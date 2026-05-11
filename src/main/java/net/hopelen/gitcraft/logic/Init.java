package net.hopelen.gitcraft.logic;


import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Init {
    public static void execute(FabricClientCommandSource source, String repoName, BlockPos pos1, BlockPos pos2) {
        // normalize so pos1 is always the lesser corner
        BlockPos min = new BlockPos(
                Math.min(pos1.getX(), pos2.getX()),
                Math.min(pos1.getY(), pos2.getY()),
                Math.min(pos1.getZ(), pos2.getZ())
        );
        BlockPos max = new BlockPos(
                Math.max(pos1.getX(), pos2.getX()),
                Math.max(pos1.getY(), pos2.getY()),
                Math.max(pos1.getZ(), pos2.getZ())
        );


        try {
            Path repoRoot = getRepoPath(repoName);

            if (Files.exists(repoRoot)) {
                source.sendFeedback(Component.literal("Repo '" + repoName + "' already exists."));
                return;
            }

            // create all folders in one go
            Files.createDirectories(repoRoot.resolve("objects"));
            Files.createDirectories(repoRoot.resolve("refs/heads"));
            Files.createDirectories(repoRoot.resolve("refs/tags"));
            Files.createDirectories(repoRoot.resolve("commits"));

            // get world info from the client
            Minecraft client = Minecraft.getInstance();
            String worldId;
            String worldName;
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

// build and write repo.json
            RepoJson.RepoData repoData = new RepoJson.RepoData(repoName);
            repoData.placements.add(new RepoJson.Placement(worldName, worldId, dimension, min, max, "main"));
            RepoJson.write(repoRoot, repoData);


            source.sendFeedback(Component.literal("Initialized repo '" + repoName + "'"));
        } catch (IOException e) {
            source.sendFeedback(Component.literal("Failed to initialize repo: " + e.getMessage()));
        }
    }

    public static Path getRepoPath(String repoName) {
        return getReposRoot().resolve(repoName);
    }

    public static Path getReposRoot() {
        return FabricLoader.getInstance().getGameDir().resolve("gitcraft/repos");
    }
}
