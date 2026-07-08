package net.hopelen.gitcraft.logic;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.hopelen.gitcraft.render.PlacementRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

import java.io.IOException;

public class Unplace {

    public static void execute(FabricClientCommandSource source, boolean clearBlocks) {
        PlacementLocator.Located located = PlacementLocator.find(source);
        if (located == null) {
            source.sendFeedback(Component.literal("Not inside a repo placement. Stand inside one to unplace."));
            return;
        }

        try {
            located.repo().placements.remove(located.placement());
            RepoJson.write(Init.getRepoPath(located.repoName()), located.repo());
            PlacementRenderer.invalidate();

            RepoJson.BlockPosData origin = located.placement().origin;
            String feedback = "Removed placement of '" + located.repoName() + "' at ("
                    + origin.x + ", " + origin.y + ", " + origin.z + ").";

            if (clearBlocks) {
                feedback += clearRegion(located)
                        ? " Cleared the region."
                        : " Blocks not cleared: clearing needs singleplayer.";
            } else {
                feedback += " Blocks were left untouched.";
            }

            source.sendFeedback(Component.literal(feedback));
        } catch (IOException e) {
            source.sendFeedback(Component.literal("Failed to remove placement: " + e.getMessage()));
        }
    }

    private static boolean clearRegion(PlacementLocator.Located located) {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.hasSingleplayerServer()) return false;

        ServerLevel level = mc.getSingleplayerServer().getLevel(mc.level.dimension());
        if (level == null) return false;

        BlockPos[] bounds = Bounds.of(located.placement(), located.repo().size);
        // flag 2|16 = send to clients without neighbor updates, so clearing doesn't
        // trigger physics cascades or item drops from attached blocks
        mc.getSingleplayerServer().execute(() -> {
            for (BlockPos pos : BlockPos.betweenClosed(bounds[0], bounds[1])) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2 | 16);
            }
        });
        return true;
    }
}
