package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.SoulExtractorBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.geom.ModelBoxGeometry;
import thaumcraft.client.geom.QuadSink;
import thaumcraft.client.platform.EntityQuadSink;

public final class SoulSieveRenderer implements BlockEntityRenderer<SoulExtractorBlockEntity, SoulSieveRenderer.State> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/soulsieve.png");

    public SoulSieveRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public float sieveMotion;
        public int ticksLeft;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SoulExtractorBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        state.sieveMotion = entity.sieveMotion + partialTicks;
        state.ticksLeft = entity.ticksLeft;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.rotateDegrees(Axis.XP, 180.0F);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(TEXTURE), (pose, buffer) -> {
            QuadSink sink = new EntityQuadSink(pose, buffer);
            renderStationary(sink, state.lightCoords);
            float movement = 0.15625F * (float) (Math.cos(Math.toRadians(state.sieveMotion)) - 1.0F);
            pose.translate(0.0F, -movement, 0.0F);
            renderMoving(sink, state.lightCoords);
            if (state.ticksLeft > 0) {
                box(sink, -7, 10, -7, 14, 3, 14, 72, 16, 64, 32, state.lightCoords);
            }
        });
        poseStack.popPose();
    }

    private static void renderStationary(QuadSink sink, int light) {
        box(sink, -8, 8, 7, 16, 6, 1, 0, 0, 64, 32, light);
        box(sink, -8, 8, -8, 16, 6, 1, 0, 9, 64, 32, light);
        box(sink, 7, 8, -7, 1, 6, 14, 0, 44, 64, 32, light);
        box(sink, -8, 8, -7, 1, 6, 14, 0, 18, 64, 32, light);
        box(sink, -7, 13, -7, 14, 1, 14, 48, 0, 64, 32, light);
        box(sink, -4, 14, -4, 8, 6, 8, 32, 32, 64, 32, light);
        box(sink, -2, 20, -2, 4, 4, 4, 32, 18, 64, 32, light);
    }

    private static void renderMoving(QuadSink sink, int light) {
        box(sink, -1, 8, 2, 2, 1, 5, 64, 36, 64, 32, light);
        box(sink, -1, 8, -7, 2, 1, 5, 64, 36, 64, 32, light);
        box(sink, -7, 8, -1, 5, 1, 2, 48, 18, 64, 32, light);
        box(sink, 2, 8, -1, 5, 1, 2, 48, 18, 64, 32, light);
        box(sink, 1, 8, -1, 1, 1, 2, 36, 0, 64, 32, light);
        box(sink, -2, 8, -1, 1, 1, 2, 36, 0, 64, 32, light);
        box(sink, -2, 8, 1, 4, 1, 1, 32, 28, 64, 32, light);
        box(sink, -2, 8, -2, 4, 1, 1, 32, 28, 64, 32, light);
        box(sink, -5, 8, -5, 4, 1, 1, 62, 24, 128, 64, light);
        box(sink, -5, 8, -4, 1, 1, 3, 64, 32, 128, 64, light);
        box(sink, 1, 8, -5, 4, 1, 1, 62, 24, 128, 64, light);
        box(sink, 4, 8, -4, 1, 1, 3, 64, 32, 128, 64, light);
        box(sink, 4, 8, 1, 1, 1, 3, 64, 32, 128, 64, light);
        box(sink, 1, 8, 4, 4, 1, 1, 62, 24, 128, 64, light);
        box(sink, -5, 8, 4, 4, 1, 1, 62, 24, 128, 64, light);
        box(sink, -5, 8, 1, 1, 1, 3, 64, 32, 128, 64, light);
    }

    private static void box(QuadSink sink, float x, float y, float z, int width, int height, int depth,
        int u, int v, float textureWidth, float textureHeight, int light) {
        ModelBoxGeometry.box(sink, x, y, z, width, height, depth, u, v, textureWidth, textureHeight,
            true, 0xFFFFFFFF, light);
    }
}
