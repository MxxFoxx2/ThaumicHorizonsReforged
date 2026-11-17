package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

/** Renders the legacy ModelReceptacle's conditional, plane-tinted Keystone cube. */
public final class SlotBlockRenderer implements BlockEntityRenderer<SlotBlockEntity, SlotBlockRenderer.State> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/receptacle.png");

    public SlotBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public boolean hasKeystone;
        public int color = 0xFFFFFF;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SlotBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        state.hasKeystone = entity.hasKeystone();
        state.color = entity.keystoneColor() | 0xFF000000;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector,
        net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        if (!state.hasKeystone) {
            return;
        }
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(TEXTURE),
            (pose, buffer) -> renderKeystone(pose, buffer, state.color, state.lightCoords));
    }

    private static void renderKeystone(PoseStack.Pose pose, VertexConsumer buffer, int color, int light) {
        // ModelReceptacle.Keystone: 8×8×8 at (-4, 12, -4), after the
        // legacy renderer's (x + 0.5, y - 0.5, z + 0.5) translation.
        float min = 0.25F;
        float max = 0.75F;

        face(pose, buffer, color, light,
            min, min, min, 32, 8, max, min, min, 40, 8,
            max, max, min, 40, 16, min, max, min, 32, 16,
            0.0F, 0.0F, -1.0F);
        face(pose, buffer, color, light,
            max, min, max, 48, 8, min, min, max, 56, 8,
            min, max, max, 56, 16, max, max, max, 48, 16,
            0.0F, 0.0F, 1.0F);
        face(pose, buffer, color, light,
            min, min, max, 24, 8, min, min, min, 32, 8,
            min, max, min, 32, 16, min, max, max, 24, 16,
            -1.0F, 0.0F, 0.0F);
        face(pose, buffer, color, light,
            max, min, min, 40, 8, max, min, max, 48, 8,
            max, max, max, 48, 16, max, max, min, 40, 16,
            1.0F, 0.0F, 0.0F);
        face(pose, buffer, color, light,
            min, max, min, 32, 0, max, max, min, 40, 0,
            max, max, max, 40, 8, min, max, max, 32, 8,
            0.0F, 1.0F, 0.0F);
        face(pose, buffer, color, light,
            min, min, max, 40, 0, max, min, max, 48, 0,
            max, min, min, 48, 8, min, min, min, 40, 8,
            0.0F, -1.0F, 0.0F);
    }

    private static void face(PoseStack.Pose pose, VertexConsumer buffer, int color, int light,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3,
        float nx, float ny, float nz) {
        vertex(pose, buffer, color, light, x0, y0, z0, u0, v0, nx, ny, nz);
        vertex(pose, buffer, color, light, x1, y1, z1, u1, v1, nx, ny, nz);
        vertex(pose, buffer, color, light, x2, y2, z2, u2, v2, nx, ny, nz);
        vertex(pose, buffer, color, light, x3, y3, z3, u3, v3, nx, ny, nz);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, int color, int light,
        float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
            .setColor(color)
            .setUv(u / 64.0F, v / 64.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
