package net.hopelen.gitcraft.logic;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Place {

    public static void execute(FabricClientCommandSource source, String repoName, BlockPos origin) {

        Path repoRoot = Init.getRepoPath(repoName);

        // make sure the repo exists
        if (!Files.exists(repoRoot)) {
            source.sendFeedback(Component.literal("Repo '" + repoName + "' does not exist."));
            return;
        }
        try {
            RepoJson.RepoData repoData = RepoJson.read(repoRoot);

            BlockPos max = new BlockPos(
                    origin.getX() + repoData.size.x,
                    origin.getY() + repoData.size.y,
                    origin.getZ() + repoData.size.z
            );

            RepoJson.Placement newPlace = createPlacement(source, origin);


            for (RepoJson.Placement existing : repoData.placements) {
                if (existing.worldId.equals(newPlace.worldId)
                        && existing.origin.x == origin.getX()
                        && existing.origin.y == origin.getY()
                        && existing.origin.z == origin.getZ()) {
                    source.sendFeedback(Component.literal("A placement already exists at this position."));
                    return;
                }
            }

            repoData.placements.add(newPlace);
            RepoJson.write(repoRoot, repoData);

            source.sendFeedback(Component.literal(
                    "Added placement for '" + repoName + "' at " + origin + " to " + max
            ));
        } catch (IOException e) {
            source.sendFeedback(Component.literal("Failed to read/write repo: " + e.getMessage()));
        }

    }

    public static RepoJson.Placement createPlacement(FabricClientCommandSource source, BlockPos origin) {
        Minecraft client = Minecraft.getInstance();

        String worldName;
        String worldId;
        String dimension = client.level.dimension().identifier().toString();

        if (client.hasSingleplayerServer()) {
            worldId = client.getSingleplayerServer().getWorldPath(LevelResource.ROOT)
                    .getParent().getFileName().toString();
            worldName = client.getSingleplayerServer().getWorldData().getLevelName();
        } else {
            ServerData server = client.getCurrentServer();
            worldId = server.ip;
            worldName = server.name;
        }

        return new RepoJson.Placement(worldName, worldId, dimension, origin, "main");

    }


}
