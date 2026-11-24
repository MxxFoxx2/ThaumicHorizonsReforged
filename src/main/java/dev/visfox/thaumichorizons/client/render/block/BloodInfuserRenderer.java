package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.BloodInfuserBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import dev.visfox.thaumichorizons.client.platform.ThCameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public final class BloodInfuserRenderer implements ThStateBlockRenderer<BloodInfuserBlockEntity, BloodInfuserRenderer.State> {
    private static final ResourceLocation MODEL_ID = ThaumicHorizons.id("textures/models/syringe.obj");
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    private final ObjModel syringe = ObjModel.get(MODEL_ID);

    public BloodInfuserRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public boolean hasBlood;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(BloodInfuserBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, Object progress) {
        state.hasBlood = entity.hasBlood();
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector, ThCameraRenderState camera) {
        if (!state.hasBlood) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(1.90625F, 0.75F, 0.46875F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(TEXTURE), (pose, buffer) -> {
            syringe.renderPart("Body", pose, buffer, 0xBFFF0000, state.lightCoords);
            syringe.renderPart("Needle", pose, buffer, 0xFFFFFFFF, state.lightCoords);
            syringe.renderPart("PlungerA", pose, buffer, 0xFFFFFFFF, state.lightCoords);
            syringe.renderPart("PlungerB", pose, buffer, 0xFFFFFFFF, state.lightCoords);
        });
        poseStack.popPose();
    }
}
