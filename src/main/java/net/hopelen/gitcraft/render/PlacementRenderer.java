package net.hopelen.gitcraft.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import fi.dy.masa.malilib.interfaces.IRenderer;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.data.Color4f;
import net.hopelen.gitcraft.logic.Bounds;
import net.hopelen.gitcraft.logic.Init;
import net.hopelen.gitcraft.logic.Place;
import net.hopelen.gitcraft.logic.RepoJson;
import net.hopelen.gitcraft.logic.Suggestions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlacementRenderer implements IRenderer {

    private static final Color4f OUTLINE_COLOR = Color4f.fromColor(0x40FF88, 1.0f);
    private static final long REFRESH_INTERVAL_MS = 5000;

    private record Region(String repoName, BlockPos min, BlockPos max, String worldId, String dimension) {}

    private static List<Region> regions = List.of();
    private static long lastRefresh = 0;

    // called after init/place write new placements so the outline shows up immediately
    public static void invalidate() {
        lastRefresh = 0;
    }

    @Override
    public void onRenderWorldLast(RenderTarget fb, Matrix4fc modelViewMatrix, CameraRenderState cameraState,
                                  Frustum culling, RenderBuffers buffers, GpuBufferSlice terrainFog,
                                  Vector4f fogColor, ProfilerFiller profiler) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        refreshIfStale();

        String worldId;
        String dimension;
        try {
            worldId = Place.currentWorldId();
            dimension = Place.currentDimension();
        } catch (Exception e) {
            return;
        }

        for (Region region : regions) {
            if (region.worldId().equals(worldId) && region.dimension().equals(dimension)) {
                RenderUtils.renderAreaOutline(region.min(), region.max(), 1.5f,
                        OUTLINE_COLOR, OUTLINE_COLOR, OUTLINE_COLOR);
            }
        }
    }

    private static void refreshIfStale() {
        long now = System.currentTimeMillis();
        if (now - lastRefresh < REFRESH_INTERVAL_MS) return;
        lastRefresh = now;

        List<Region> result = new ArrayList<>();
        for (String repoName : Suggestions.getRepoNames()) {
            try {
                RepoJson.RepoData repo = RepoJson.read(Init.getRepoPath(repoName));
                for (RepoJson.Placement p : repo.placements) {
                    BlockPos[] bounds = Bounds.of(p, repo.size);
                    result.add(new Region(repoName, bounds[0], bounds[1], p.worldId, p.dimension));
                }
            } catch (IOException ignored) {
            }
        }
        regions = result;
    }
}
