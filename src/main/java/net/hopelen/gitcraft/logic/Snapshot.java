package net.hopelen.gitcraft.logic;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.selection.AreaSelection;
import fi.dy.masa.litematica.selection.Box;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Snapshot {

    // captures the region [min, max] (inclusive corners) as compressed litematic NBT bytes
    public static byte[] capture(Level world, BlockPos min, BlockPos max, String regionName) throws IOException {
        AreaSelection area = new AreaSelection();
        area.setName(regionName);
        area.addSubRegionBox(new Box(min, max, regionName), true);
        area.setExplicitOrigin(min);

        // visibleOnly=false, ignoreEntities=true: block tracking only for now
        LitematicaSchematic.SchematicSaveInfo info = new LitematicaSchematic.SchematicSaveInfo(false, true);
        LitematicaSchematic schematic = LitematicaSchematic.createFromWorld(world, area, info, "", s -> {});

        if (schematic == null) {
            throw new IOException("Litematica could not capture the region (is it fully loaded?)");
        }

        // author and timestamps belong to the commit, not the snapshot; zeroing them here
        // makes identical block content produce identical bytes, so hashes deduplicate
        schematic.getMetadata().setAuthor("");
        schematic.getMetadata().setTimeCreated(0);
        schematic.getMetadata().setTimeModified(0);

        CompoundTag tag = schematic.writeToNBT();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, out);
        return out.toByteArray();
    }
}
