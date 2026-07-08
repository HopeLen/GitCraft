package net.hopelen.gitcraft.logic;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.io.IOException;

public class PlacementLocator {

    public record Located(String repoName, RepoJson.RepoData repo, RepoJson.Placement placement) {}

    // finds the placement whose region contains the player, like git finding the repo from your cwd
    public static Located find(FabricClientCommandSource source) {
        BlockPos playerPos = Minecraft.getInstance().player.blockPosition();
        String worldId = Place.currentWorldId();
        String dimension = Place.currentDimension();

        for (String repoName : Suggestions.getRepoNames()) {
            RepoJson.RepoData repo;
            try {
                repo = RepoJson.read(Init.getRepoPath(repoName));
            } catch (IOException e) {
                continue;
            }
            for (RepoJson.Placement placement : repo.placements) {
                if (placement.worldId.equals(worldId)
                        && placement.dimension.equals(dimension)
                        && contains(placement, repo.size, playerPos)) {
                    return new Located(repoName, repo, placement);
                }
            }
        }
        return null;
    }

    private static boolean contains(RepoJson.Placement p, RepoJson.SizeData size, BlockPos pos) {
        BlockPos[] bounds = Bounds.of(p, size);
        return pos.getX() >= bounds[0].getX() && pos.getX() <= bounds[1].getX()
                && pos.getY() >= bounds[0].getY() && pos.getY() <= bounds[1].getY()
                && pos.getZ() >= bounds[0].getZ() && pos.getZ() <= bounds[1].getZ();
    }
}
