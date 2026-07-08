package net.hopelen.gitcraft.logic;

import fi.dy.masa.litematica.util.PositionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Mirror;

public class Bounds {

    // world-space {min, max} corners of a placement's region, accounting for rotation.
    // litematica rotates around the placement origin, so a rotated box can extend
    // into negative directions from the origin
    public static BlockPos[] of(RepoJson.Placement placement, RepoJson.SizeData size) {
        BlockPos origin = new BlockPos(placement.origin.x, placement.origin.y, placement.origin.z);
        BlockPos farOffset = PositionUtils.getTransformedBlockPos(
                new BlockPos(size.x, size.y, size.z), Mirror.NONE, Rotations.of(placement));
        BlockPos far = origin.offset(farOffset);

        BlockPos min = new BlockPos(
                Math.min(origin.getX(), far.getX()),
                Math.min(origin.getY(), far.getY()),
                Math.min(origin.getZ(), far.getZ()));
        BlockPos max = new BlockPos(
                Math.max(origin.getX(), far.getX()),
                Math.max(origin.getY(), far.getY()),
                Math.max(origin.getZ(), far.getZ()));
        return new BlockPos[]{min, max};
    }
}
