package net.hopelen.gitcraft.logic;

import net.minecraft.world.level.block.Rotation;

// the repo's committed orientation is defined as facing north; a placement's
// facing says which absolute direction that north side points in the world
public class Rotations {

    public static Rotation of(RepoJson.Placement placement) {
        if (placement.facing == null) return Rotation.NONE;
        return switch (placement.facing) {
            case "east" -> Rotation.CLOCKWISE_90;
            case "south" -> Rotation.CLOCKWISE_180;
            case "west" -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE; // north
        };
    }

    public static String facing(Rotation rotation) {
        return switch (rotation) {
            case CLOCKWISE_90 -> "east";
            case CLOCKWISE_180 -> "south";
            case COUNTERCLOCKWISE_90 -> "west";
            default -> "north";
        };
    }
}
