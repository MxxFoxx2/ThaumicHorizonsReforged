package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.block.TransductionAmplifierBlock;
import dev.visfox.thaumichorizons.blockentity.TransductionAmplifierBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import dev.visfox.thaumichorizons.client.platform.ThCameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public final class TransductionAmplifierRenderer
    implements ThStateBlockRenderer<TransductionAmplifierBlockEntity, TransductionAmplifierRenderer.State> {
    private static final ResourceLocation MODEL_ID = ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/node_stabilizer.obj");
    private static final ResourceLocation BASE = ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/model/node_converter.png");
    private static final ResourceLocation OVERLAY = ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/model/node_converter_over.png");
    private final ObjModel model = ObjModel.get(MODEL_ID);

    public TransductionAmplifierRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public int count;
        public Direction direction = Direction.NORTH;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TransductionAmplifierBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, Object progress) {
        state.count = entity.count;
        state.direction = entity.getBlockState().getValue(TransductionAmplifierBlock.FACING);
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector, ThCameraRenderState camera) {
        float extension = Math.min(50, state.count) / 137.0F;
        orient(state.direction, poseStack);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(BASE),
            (pose, buffer) -> model.renderPart("lock", pose, buffer, 0xFFFFFFFF, state.lightCoords));
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(OVERLAY),
            (pose, buffer) -> model.renderPart("lock", pose, buffer, 0xFFCCCC00, 220));
        for (int index = 0; index < 4; index++) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZP.rotationDegrees(index * 90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
            poseStack.translate(0.0F, 0.0F, extension);
            collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(BASE),
                (pose, buffer) -> model.renderPart("piston", pose, buffer, 0xFFFFFFFF, state.lightCoords));
            collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(OVERLAY),
                (pose, buffer) -> model.renderPart("piston", pose, buffer, 0xFFCCCC00, 220));
            poseStack.popPose();
        }
    }

    private static void orient(Direction direction, PoseStack poseStack) {
        switch (direction) {
            case SOUTH -> {
                poseStack.translate(0.5F, 0.5F, 1.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            }
            case WEST -> {
                poseStack.translate(0.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case EAST -> {
                poseStack.translate(1.0F, 0.5F, 0.5F);
                poseStack.mulPose(Axis.YP.rotationDegrees(270.0F));
            }
            default -> poseStack.translate(0.5F, 0.5F, 0.0F);
        }
    }
}
