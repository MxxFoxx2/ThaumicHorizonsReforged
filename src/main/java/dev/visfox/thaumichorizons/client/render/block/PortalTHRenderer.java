package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.pocket.PortalTHBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PortalTHRenderer implements BlockEntityRenderer<PortalTHBlockEntity, PortalTHRenderer.State> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/misc/vortex.png");

    public PortalTHRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public int openCount;
        public int color;
        public int frame;
        public float partialTicks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(PortalTHBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        Level level = entity.getLevel();
        state.openCount = entity.openCount();
        state.color = switch (entity.sourceDimension()) {
            case "minecraft:the_nether" -> 0xFFFF1A1A;
            case "minecraft:overworld" -> 0xFF1ACCCC;
            case "minecraft:the_end" -> 0xFF404040;
            default -> 0xFF80801A;
        };
        state.frame = level == null ? 0 : Math.floorMod((int) level.getGameTime(), 16);
        state.partialTicks = partialTicks;
        state.lightCoords = 220;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float open = state.openCount + state.partialTicks;
        float horizontal = Math.min(5.0F, open) / 5.0F;
        float vertical = Math.min(30.0F, open) / 30.0F;
        if (horizontal <= 0.0F || vertical <= 0.0F) {
            return;
        }
        float u0 = state.frame / 16.0F;
        float u1 = (state.frame + 1) / 16.0F;
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.rotate(camera.orientation);
        poseStack.scale(horizontal, vertical, horizontal);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(TEXTURE),
            (pose, buffer) -> quad(buffer, pose, state.color, state.lightCoords, u0, u1));
        poseStack.popPose();
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose pose, int color, int light, float u0, float u1) {
        vertex(buffer, pose, -0.5F, -0.5F, 0.0F, u0, 1.0F, color, light);
        vertex(buffer, pose, 0.5F, -0.5F, 0.0F, u1, 1.0F, color, light);
        vertex(buffer, pose, 0.5F, 0.5F, 0.0F, u1, 0.0F, color, light);
        vertex(buffer, pose, -0.5F, 0.5F, 0.0F, u0, 0.0F, color, light);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z,
        float u, float v, int color, int light) {
        buffer.addVertex(pose, x, y, z).setColor(color).setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    @Override
    public AABB getRenderBoundingBox(PortalTHBlockEntity entity) {
        return new AABB(entity.getBlockPos()).inflate(1.0);
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
