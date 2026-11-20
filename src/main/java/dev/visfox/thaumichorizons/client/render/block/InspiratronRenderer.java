package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.blockentity.InspiratronBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.render.device.WardedRendering;

public final class InspiratronRenderer implements BlockEntityRenderer<InspiratronBlockEntity, InspiratronRenderer.State> {
    public InspiratronRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public float rotation;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(InspiratronBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
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
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float bob = Mth.sin(WardedRendering.viewerTicks() / 14.0F) * 0.03F + 0.03F;
        poseStack.pushPose();
        poseStack.translate(0.5F, -0.925F + bob, 0.5F);
        poseStack.rotateDegrees(Axis.XP, 180.0F);
        poseStack.rotateDegrees(Axis.YP, state.rotation * 180.0F / (float) Math.PI - 90.0F);
        WardedRendering.brainJar(collector, poseStack, state.lightCoords, 0.0F, WardedRendering.viewerTicks());
        poseStack.popPose();
    }
}
