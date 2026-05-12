package net.hopelen.gitcraft.logic;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Suggestions {


    public static final SuggestionProvider<FabricClientCommandSource> REPO_NAMES =
            (context, builder) -> {
                getRepoNames().forEach(builder::suggest);
                return builder.buildFuture();
            };

    public static List<String> getRepoNames() {
        Path reposRoot = Init.getReposRoot();

        if (!Files.exists(reposRoot)) return List.of();

        try (Stream<Path> dirs = Files.list(reposRoot)) {
            return dirs
                    .filter(Files::isDirectory)
                    .map(path -> path.getFileName().toString())
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }
}

