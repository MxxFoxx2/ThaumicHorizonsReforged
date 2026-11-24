package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.blockentity.InspiratronBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import dev.visfox.thaumichorizons.client.platform.ThCameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.render.device.WardedRendering;

public final class InspiratronRenderer implements ThStateBlockRenderer<InspiratronBlockEntity, InspiratronRenderer.State> {
    public InspiratronRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public float rotation;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(InspiratronBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, Object progress) {
        float difference = entity.rota - entity.rotb;
        while (difference >= (float) Math.PI) {
            difference -= (float) (Math.PI * 2.0);
        }
        while (difference < -(float) Math.PI) {
            difference += (float) (Math.PI * 2.0);
        }
        state.rotation = entity.rotb + difference * partialTicks;
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector, ThCameraRenderState camera) {
        float bob = Mth.sin(WardedRendering.viewerTicks() / 14.0F) * 0.03F + 0.03F;
        poseStack.pushPose();
        poseStack.translate(0.5F, -0.925F + bob, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation * 180.0F / (float) Math.PI - 90.0F));
        WardedRendering.brainJar(collector.buffers, poseStack, state.lightCoords, 0.0F, WardedRendering.viewerTicks());
        poseStack.popPose();
    }
}
