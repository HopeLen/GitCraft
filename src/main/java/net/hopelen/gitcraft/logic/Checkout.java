package net.hopelen.gitcraft.logic;

import fi.dy.masa.litematica.schematic.LitematicaSchematic;
import fi.dy.masa.litematica.schematic.placement.SchematicPlacement;
import fi.dy.masa.litematica.util.FileType;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.gui.interfaces.IMessageConsumer;
import net.hopelen.gitcraft.GitCraft;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class Checkout {

    private static final IMessageConsumer LOG_MESSAGES = new IMessageConsumer() {
        @Override
        public void addMessage(MessageType type, String messageKey, Object... args) {
            GitCraft.LOGGER.info("litematica [{}]: {}", type, messageKey);
        }

        @Override
        public void addMessage(MessageType type, int lifeTime, String messageKey, Object... args) {
            GitCraft.LOGGER.info("litematica [{}]: {}", type, messageKey);
        }
    };

    // pastes the given commit's snapshot into the world at the placement's origin;
    // writing blocks needs the integrated server, so this only works in singleplayer
    public static boolean pasteCommit(Path repoRoot, String repoName, RepoJson.Placement placement, String commitHash) throws IOException {
        Minecraft mc = Minecraft.getInstance();
        if (!mc.hasSingleplayerServer()) return false;

        Commits.CommitData commit = Commits.read(repoRoot, commitHash);
        byte[] bytes = ObjectStore.read(repoRoot, commit.snapshot);
        CompoundTag tag = NbtIo.readCompressed(new ByteArrayInputStream(bytes), NbtAccounter.unlimitedHeap());
        LitematicaSchematic schematic = new LitematicaSchematic(null, tag, FileType.LITEMATICA_SCHEMATIC);

        BlockPos origin = new BlockPos(placement.origin.x, placement.origin.y, placement.origin.z);
        SchematicPlacement schematicPlacement = SchematicPlacement.createFor(schematic, origin, repoName, true, false);

        Rotation rotation = Rotations.of(placement);
        if (rotation != Rotation.NONE) {
            schematicPlacement.setRotation(rotation, LOG_MESSAGES);
        }

        ServerLevel level = mc.getSingleplayerServer().getLevel(mc.level.dimension());
        if (level == null) return false;

        // block writes must happen on the server thread, not the command thread
        mc.getSingleplayerServer().execute(() ->
                schematic.placeToWorld(level, schematicPlacement, false, true));
        return true;
    }
}
