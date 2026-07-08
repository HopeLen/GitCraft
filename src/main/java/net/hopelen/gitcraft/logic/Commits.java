package net.hopelen.gitcraft.logic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Commits {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class CommitData {
        public String snapshot;   // hash of the region snapshot in objects/
        public String parent;     // hash of the previous commit, null for the first commit
        public String message;
        public String author;
        public long timestamp;

        public CommitData(String snapshot, String parent, String message, String author) {
            this.snapshot = snapshot;
            this.parent = parent;
            this.message = message;
            this.author = author;
            this.timestamp = System.currentTimeMillis();
        }
    }

    // the commit's hash is the hash of its own JSON, so commits are tamper-evident like git's
    public static String write(Path repoRoot, CommitData commit) throws IOException {
        byte[] json = GSON.toJson(commit).getBytes(StandardCharsets.UTF_8);
        String hash = ObjectStore.hash(json);
        Path dir = repoRoot.resolve("commits");
        Files.createDirectories(dir);
        Files.write(dir.resolve(hash + ".json"), json);
        return hash;
    }

    public static CommitData read(Path repoRoot, String hash) throws IOException {
        Path file = repoRoot.resolve("commits").resolve(hash + ".json");
        return GSON.fromJson(Files.readString(file), CommitData.class);
    }
}
