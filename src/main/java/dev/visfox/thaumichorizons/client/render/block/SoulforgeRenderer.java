package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.SoulforgeBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import dev.visfox.thaumichorizons.client.platform.ThCameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.render.device.WardedRendering;

public final class SoulforgeRenderer implements ThStateBlockRenderer<SoulforgeBlockEntity, SoulforgeRenderer.State> {
    private static final ResourceLocation SOUL = ThaumicHorizons.id("textures/misc/soul.png");

    public SoulforgeRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public float rotation;
        public int forging;
        public int souls;
        public int frame;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SoulforgeBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, Object progress) {
        Level level = entity.getLevel();
        state.rotation = entity.rota;
        state.forging = entity.forging;
        state.souls = entity.souls;
        state.frame = level == null ? 0 : Math.floorMod((int) (level.getGameTime() * 1.25F + entity.getBlockPos().getX()), 16);
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector, ThCameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, -1.25F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation - 90.0F));
        WardedRendering.brainJar(collector.buffers, poseStack, state.lightCoords, 0.0F, WardedRendering.viewerTicks());
        poseStack.popPose();

        if (state.forging > 0) {
            for (int x = 0; x < 2; x++) {
                for (int z = 0; z < 2; z++) {
                    MachineRenderHelper.frame(poseStack, collector, MachineRenderHelper.LIGHTNING_RING,
                        x == 0 ? 0.025F : 0.975F, 0.75F, z == 0 ? 0.025F : 0.975F,
                        0.9F, 16, state.frame, 0xFFFFFFFF, state.lightCoords);
                }
            }
        }
        if (state.souls > 0) {
            double radian = Math.toRadians(state.frame * 1.25F);
            double distance = 0.1 + 0.1 * Math.cos(radian);
            double offset = Math.PI * 2.0 / state.souls;
            float v0 = state.frame / 16.0F;
            float v1 = (state.frame + 1) / 16.0F;
            for (int index = 0; index < state.souls; index++) {
                float x = (float) (0.5 + distance * Math.sin(2.0 * radian + offset * index));
                float z = (float) (0.5 + distance * Math.cos(2.0 * radian + offset * index));
                MachineRenderHelper.particle(poseStack, collector, SOUL, x, 0.85F, z,
                    0.1F, 0.0F, v0, 1.0F, v1, 0xFFFFFFFF, state.lightCoords);
            }
        }
    }
}
