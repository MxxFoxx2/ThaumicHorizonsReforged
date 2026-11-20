package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.blockentity.SoulBeaconBlockEntity;
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

public final class SoulBeaconRenderer implements BlockEntityRenderer<SoulBeaconBlockEntity, SoulBeaconRenderer.State> {
    private static final Identifier BEAM = Identifier.withDefaultNamespace("textures/entity/beacon_beam.png");
    private static final int COLOR = 0x20FFFFFF;

    public SoulBeaconRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public boolean visible;
        public float time;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SoulBeaconBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        Level level = entity.getLevel();
        state.visible = level != null && level.canSeeSky(entity.getBlockPos());
        state.time = (level == null ? 0L : level.getGameTime()) + partialTicks;
        state.lightCoords = 240;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.visible) {
            return;
        }
        collector.submitCustomGeometry(poseStack, ThRenderTypes.beaconBeam(BEAM, true),
            (pose, buffer) -> renderBeams(buffer, pose, state.time, state.lightCoords));
    }

    private static void renderBeams(VertexConsumer buffer, PoseStack.Pose pose, float time, int light) {
        float angle = time * 0.025F * (1.0F - 2.5F);
        float scroll = -time * 0.2F - (float) Math.floor(-time * 0.1F);
        float outerTop = 256.0F * (0.5F / 0.2F) - 1.0F + scroll;
        float bottom = -1.0F + scroll;
        float[] x = new float[4];
        float[] z = new float[4];
        double[] angles = {2.356194490192345, 0.7853981633974483, 3.9269908169872414, 5.497787143782138};
        for (int index = 0; index < 4; index++) {
            x[index] = (float) (0.5 + Math.cos(angle + angles[index]) * 0.2);
            z[index] = (float) (0.5 + Math.sin(angle + angles[index]) * 0.2);
        }
        for (int index = 0; index < 4; index++) {
            int next = (index + 1) % 4;
            side(buffer, pose, x[index], z[index], x[next], z[next], 256.0F, bottom, outerTop, COLOR, light);
        }
        float innerTop = 256.0F + bottom;
        side(buffer, pose, 0.2F, 0.2F, 0.8F, 0.2F, 256.0F, bottom, innerTop, COLOR, light);
        side(buffer, pose, 0.8F, 0.2F, 0.8F, 0.8F, 256.0F, bottom, innerTop, COLOR, light);
        side(buffer, pose, 0.8F, 0.8F, 0.2F, 0.8F, 256.0F, bottom, innerTop, COLOR, light);
        side(buffer, pose, 0.2F, 0.8F, 0.2F, 0.2F, 256.0F, bottom, innerTop, COLOR, light);
    }

    private static void side(VertexConsumer buffer, PoseStack.Pose pose, float x0, float z0, float x1, float z1,
        float height, float bottomV, float topV, int color, int light) {
        vertex(buffer, pose, x0, height, z0, 1.0F, topV, color, light);
        vertex(buffer, pose, x0, 0.0F, z0, 1.0F, bottomV, color, light);
        vertex(buffer, pose, x1, 0.0F, z1, 0.0F, bottomV, color, light);
        vertex(buffer, pose, x1, height, z1, 0.0F, topV, color, light);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z,
        float u, float v, int color, int light) {
        buffer.addVertex(pose, x, y, z).setColor(color).setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    @Override
    public AABB getRenderBoundingBox(SoulBeaconBlockEntity entity) {
        return new AABB(entity.getBlockPos()).expandTowards(0.0, 256.0, 0.0);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
