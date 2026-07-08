package net.hopelen.gitcraft.logic;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Rotation;

import java.io.IOException;
import java.nio.file.Path;

public class Commit {

    public static void execute(FabricClientCommandSource source, String message) {
        PlacementLocator.Located located = PlacementLocator.find(source);
        if (located == null) {
            source.sendFeedback(Component.literal("Not inside a repo placement. Stand inside one to commit."));
            return;
        }

        Path repoRoot = Init.getRepoPath(located.repoName());

        try {
            String commitHash = createCommit(repoRoot, located.repo(), located.placement(), message);
            if (commitHash == null) {
                source.sendFeedback(Component.literal("Nothing to commit - region matches HEAD."));
                return;
            }

            RepoJson.write(repoRoot, located.repo());
            source.sendFeedback(Component.literal(
                    "[" + located.placement().branch + " " + commitHash.substring(0, 7) + "] " + message));
        } catch (IOException e) {
            source.sendFeedback(Component.literal("Commit failed: " + e.getMessage()));
        }
    }

    // snapshots the placement's region and advances its branch; returns the new commit
    // hash, or null when the region is identical to HEAD. The caller is responsible for
    // persisting repo.json afterwards (placement.headCommit is updated here).
    public static String createCommit(Path repoRoot, RepoJson.RepoData repo, RepoJson.Placement placement,
                                      String message) throws IOException {
        // a rotated capture would hash differently than the repo's canonical orientation,
        // so history from rotated placements would never line up - reject until we can
        // reverse-rotate snapshots back to canonical
        if (Rotations.of(placement) != Rotation.NONE) {
            throw new IOException("this placement is rotated; commit from an unrotated placement of this repo");
        }

        BlockPos[] bounds = Bounds.of(placement, repo.size);
        byte[] snapshotBytes = Snapshot.capture(Minecraft.getInstance().level, bounds[0], bounds[1], repo.name);
        String snapshotHash = ObjectStore.store(repoRoot, snapshotBytes);

        String parent = Refs.readHead(repoRoot, placement.branch);
        if (parent != null && snapshotHash.equals(Commits.read(repoRoot, parent).snapshot)) {
            return null;
        }

        String author = Minecraft.getInstance().player.getName().getString();
        Commits.CommitData commit = new Commits.CommitData(snapshotHash, parent, message, author);
        String commitHash = Commits.write(repoRoot, commit);

        Refs.writeHead(repoRoot, placement.branch, commitHash);
        placement.headCommit = commitHash;
        return commitHash;
    }
}
