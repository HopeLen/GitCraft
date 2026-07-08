package net.hopelen.gitcraft.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class ObjectStore {

    public static String hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    // objects are content-addressed: committing identical content twice reuses the same file
    public static String store(Path repoRoot, byte[] data) throws IOException {
        String hash = hash(data);
        Path file = repoRoot.resolve("objects").resolve(hash);
        if (!Files.exists(file)) {
            Files.createDirectories(file.getParent());
            Files.write(file, data);
        }
        return hash;
    }

    public static byte[] read(Path repoRoot, String hash) throws IOException {
        return Files.readAllBytes(repoRoot.resolve("objects").resolve(hash));
    }
}
