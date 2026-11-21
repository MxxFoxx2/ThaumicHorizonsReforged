package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.RecombinatorBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.node.Node;

/**
 * Dynamic part of the legacy TileRecombinator renderer. The base and claws are
 * kept in the static model; this renderer moves the pearl and draws the active
 * animated ring while TC4R's BeamFx maintains the link to the node below.
 */
public final class RecombinatorRenderer implements BlockEntityRenderer<RecombinatorBlockEntity, RecombinatorRenderer.State> {
    private static final Identifier MODEL_TEXTURE = ThaumicHorizons.id("textures/models/recombinator.png");
    private static final Identifier RING_TEXTURE = Identifier.fromNamespaceAndPath(
        "thaumcraft", "textures/item/lightningringv.png");

    public RecombinatorRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public int count;
        public boolean active;
        public boolean hasNode;
        public int blockX;
        public long gameTime;
        public float partialTicks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(RecombinatorBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        Level level = entity.getLevel();
        if (level == null) {
            state.active = false;
            state.hasNode = false;
            return;
        }

        BlockPos pos = entity.getBlockPos();
        state.count = entity.getCount();
        state.active = entity.isActivated();
        state.hasNode = level.getBlockEntity(pos.below()) instanceof Node;
        state.blockX = pos.getX();
        state.gameTime = level.getGameTime();
        state.partialTicks = partialTicks;

        if (state.active && state.hasNode && level.isClientSide()) {
            TcReforgedPort.fx.beam(level, pos,
                new Vec3(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5),
                new Vec3(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5),
                0xFFFFFFFF, 0.1F, 3);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector,
        net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        float sine = (float) Math.sin(state.count / 8.0F);
        float pearlOffset = sine * 2.0F;

        poseStack.pushPose();
        poseStack.translate(0.5F, -0.5F, 0.5F);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(MODEL_TEXTURE),
            (pose, buffer) -> renderPearl(pose, buffer, state.lightCoords, pearlOffset));
        poseStack.popPose();

        if (state.active) {
            int frame = Math.floorMod((int) Math.floor(
                (state.gameTime + state.partialTicks) * 1.25D + state.blockX), 16);
            float u0 = frame / 16.0F;
            float u1 = (frame + 1) / 16.0F;
            float centerY = 1.5F + 0.2F * sine;
            poseStack.pushPose();
            poseStack.translate(0.5F, centerY, 0.5F);
            poseStack.rotateDegrees(Axis.YP, -camera.yRot);
            poseStack.rotateDegrees(Axis.XP, camera.xRot);
            collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(RING_TEXTURE),
                (pose, buffer) -> renderRing(pose, buffer, state.lightCoords, u0, u1));
            poseStack.popPose();
        }
    }

    private static void renderPearl(PoseStack.Pose pose, VertexConsumer buffer, int light, float motion) {
        float x0 = -2.0F;
        float x1 = 2.0F;
        float y0 = 14.0F + motion;
        float y1 = 18.0F + motion;
        float z0 = -2.0F;
        float z1 = 2.0F;

        // The face UVs match the 4×4×4 Pearl element in the original model JSON.
        face(pose, buffer, light,
            x0, y0, z0, 4, 28, x1, y0, z0, 8, 28,
            x1, y1, z0, 8, 24, x0, y1, z0, 4, 24,
            0.0F, 0.0F, -1.0F);
        face(pose, buffer, light,
            x1, y0, z1, 8, 28, x0, y0, z1, 12, 28,
            x0, y1, z1, 12, 24, x1, y1, z1, 8, 24,
            0.0F, 0.0F, 1.0F);
        face(pose, buffer, light,
            x0, y0, z1, 0, 28, x0, y0, z0, 4, 28,
            x0, y1, z0, 4, 24, x0, y1, z1, 0, 24,
            -1.0F, 0.0F, 0.0F);
        face(pose, buffer, light,
            x1, y0, z0, 8, 28, x1, y0, z1, 12, 28,
            x1, y1, z1, 12, 24, x1, y1, z0, 8, 24,
            1.0F, 0.0F, 0.0F);
        face(pose, buffer, light,
            x0, y1, z0, 4, 24, x1, y1, z0, 8, 24,
            x1, y1, z1, 8, 20, x0, y1, z1, 4, 20,
            0.0F, 1.0F, 0.0F);
        face(pose, buffer, light,
            x0, y0, z1, 8, 24, x1, y0, z1, 12, 24,
            x1, y0, z0, 12, 20, x0, y0, z0, 8, 20,
            0.0F, -1.0F, 0.0F);
    }

    private static void face(PoseStack.Pose pose, VertexConsumer buffer, int light,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3,
        float nx, float ny, float nz) {
        vertex(pose, buffer, light, x0, y0, z0, u0, v0, nx, ny, nz);
        vertex(pose, buffer, light, x1, y1, z1, u1, v1, nx, ny, nz);
        vertex(pose, buffer, light, x2, y2, z2, u2, v2, nx, ny, nz);
        vertex(pose, buffer, light, x3, y3, z3, u3, v3, nx, ny, nz);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, int light,
        float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        buffer.addVertex(pose, x / 16.0F, y / 16.0F, z / 16.0F)
            .setColor(0xFFFFFFFF)
            .setUv(u / 64.0F, v / 64.0F)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
    }

    private static void renderRing(PoseStack.Pose pose, VertexConsumer buffer, int light,
        float u0, float u1) {
        // One camera-facing quad, matching UtilsFX.renderFacingQuad rather than a fixed world-axis plane.
        quad(pose, buffer, light,
            -0.75F, -0.45F, 0.0F, u0, 1.0F,
            -0.75F, 0.45F, 0.0F, u0, 0.0F,
            0.75F, 0.45F, 0.0F, u1, 0.0F,
            0.75F, -0.45F, 0.0F, u1, 1.0F,
            0.0F, 0.0F, 1.0F);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer buffer, int light,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3,
        float nx, float ny, float nz) {
        rawVertex(pose, buffer, light, x0, y0, z0, u0, v0, nx, ny, nz);
        rawVertex(pose, buffer, light, x1, y1, z1, u1, v1, nx, ny, nz);
        rawVertex(pose, buffer, light, x2, y2, z2, u2, v2, nx, ny, nz);
        rawVertex(pose, buffer, light, x3, y3, z3, u3, v3, nx, ny, nz);
    }

    private static void rawVertex(PoseStack.Pose pose, VertexConsumer buffer, int light,
        float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
            .setColor(0xFFFFFFFF)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
