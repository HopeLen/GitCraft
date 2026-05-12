package net.hopelen.gitcraft.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RepoJson {


    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /* ------- Data Classes ------- */

    public static class BlockPosData {
        public int x, y, z;

        public BlockPosData(BlockPos pos) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        }
    }

    public static class SizeData {
        public int x, y, z;

        public SizeData(BlockPos min, BlockPos max) {
            this.x = max.getX() - min.getX();
            this.y = max.getY() - min.getY();
            this.z = max.getZ() - min.getZ();
        }
    }

    public static class Placement {
        public String world;
        public String worldId;
        public String dimension;
        public BlockPosData origin;
        public String branch;

        public Placement(String world, String worldId, String dimension, BlockPos origin, String branch) {
            this.world = world;
            this.worldId = worldId;
            this.dimension = dimension;
            this.origin = new BlockPosData(origin);
            this.branch = branch;
        }
    }

    public static class RepoData {
        public String name;
        public long created;
        public String currentBranch;
        public SizeData size;
        public java.util.List<Placement> placements;

        public RepoData(String name, BlockPos min, BlockPos max) {
            this.name = name;
            this.created = System.currentTimeMillis();
            this.currentBranch = "main";
            this.size = new SizeData(min, max);
            this.placements = new java.util.ArrayList<>();
        }
    }
    /* ------- Read/\Write ------- */

    public static void write(Path repoRoot, RepoData data) throws IOException {
        Path file = repoRoot.resolve("repo.json");
        Files.writeString(file, GSON.toJson(data));
    }

    public static RepoData read(Path repoRoot) throws IOException {
        Path file = repoRoot.resolve("repo.json");
        return GSON.fromJson(Files.readString(file), RepoData.class);
    }


}
