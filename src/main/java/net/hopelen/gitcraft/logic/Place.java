package net.hopelen.gitcraft.logic;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.hopelen.gitcraft.render.PlacementRenderer;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Place {

    public static void execute(FabricClientCommandSource source, String repoName, BlockPos origin, Rotation rotation) {

        Path repoRoot = Init.getRepoPath(repoName);

        // make sure the repo exists
        if (!Files.exists(repoRoot)) {
            source.sendFeedback(Component.literal("Repo '" + repoName + "' does not exist."));
            return;
        }
        try {
            RepoJson.RepoData repoData = RepoJson.read(repoRoot);

            RepoJson.Placement newPlace = createPlacement(source, origin);
            newPlace.rotation = rotation.name();

            BlockPos[] bounds = Bounds.of(newPlace, repoData.size);
            BlockPos max = bounds[1];

            for (RepoJson.Placement existing : repoData.placements) {
                if (existing.worldId.equals(newPlace.worldId)
                        && existing.dimension.equals(newPlace.dimension)
                        && existing.origin.x == origin.getX()
                        && existing.origin.y == origin.getY()
                        && existing.origin.z == origin.getZ()) {
                    source.sendFeedback(Component.literal("A placement already exists at this position."));
                    return;
                }
            }

            repoData.placements.add(newPlace);

            // a placement of a repo with commits should start as a checkout of its branch,
            // like a fresh clone checks out the files
            String head = Refs.readHead(repoRoot, newPlace.branch);
            String feedback = "Added placement for '" + repoName + "' at " + bounds[0] + " to " + max
                    + (rotation != Rotation.NONE ? " (rotated " + Rotations.degrees(rotation) + "°)" : "");
            if (head != null) {
                if (Checkout.pasteCommit(repoRoot, repoName, newPlace, head)) {
                    newPlace.headCommit = head;
                    feedback += ", checked out " + newPlace.branch + " (" + head.substring(0, 7) + ")";
                } else {
                    feedback += " (blocks not pasted: checkout needs singleplayer)";
                }
            }

            RepoJson.write(repoRoot, repoData);
            PlacementRenderer.invalidate();

            source.sendFeedback(Component.literal(feedback));
        } catch (IOException e) {
            source.sendFeedback(Component.literal("Failed to read/write repo: " + e.getMessage()));
        }

    }

    public static RepoJson.Placement createPlacement(FabricClientCommandSource source, BlockPos origin) {
        return new RepoJson.Placement(currentWorldName(), currentWorldId(), currentDimension(), origin, "main");
    }

    public static String currentDimension() {
        return Minecraft.getInstance().level.dimension().identifier().toString();
    }

    public static String currentWorldId() {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer()) {
            return client.getSingleplayerServer().getWorldPath(LevelResource.ROOT)
                    .getParent().getFileName().toString();
        }
        ServerData server = client.getCurrentServer();
        return server.ip;
    }

    public static String currentWorldName() {
        Minecraft client = Minecraft.getInstance();
        if (client.hasSingleplayerServer()) {
            return client.getSingleplayerServer().getWorldData().getLevelName();
        }
        return client.getCurrentServer().name;
    }


}
