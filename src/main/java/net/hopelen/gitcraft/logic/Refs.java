package net.hopelen.gitcraft.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Refs {

    public static Path refFile(Path repoRoot, String branch) {
        return repoRoot.resolve("refs/heads").resolve(branch);
    }

    // returns the commit hash the branch points to, or null if the branch has no commits yet
    public static String readHead(Path repoRoot, String branch) throws IOException {
        Path file = refFile(repoRoot, branch);
        if (!Files.exists(file)) return null;
        String hash = Files.readString(file).trim();
        return hash.isEmpty() ? null : hash;
    }

    public static void writeHead(Path repoRoot, String branch, String commitHash) throws IOException {
        Path file = refFile(repoRoot, branch);
        Files.createDirectories(file.getParent());
        Files.writeString(file, commitHash);
    }
}
