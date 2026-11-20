package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.block.TransductionAmplifierBlock;
import dev.visfox.thaumichorizons.blockentity.TransductionAmplifierBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class TransductionAmplifierRenderer
    implements BlockEntityRenderer<TransductionAmplifierBlockEntity, TransductionAmplifierRenderer.State> {
    private static final Identifier MODEL_ID = Identifier.fromNamespaceAndPath("thaumcraft", "models/obj/node_stabilizer.obj");
    private static final Identifier BASE = Identifier.fromNamespaceAndPath("thaumcraft", "textures/model/node_converter.png");
    private static final Identifier OVERLAY = Identifier.fromNamespaceAndPath("thaumcraft", "textures/model/node_converter_over.png");
    private final ObjModel model = ObjModel.get(MODEL_ID);

    public TransductionAmplifierRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public int count;
        public Direction direction = Direction.NORTH;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TransductionAmplifierBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        state.count = entity.count;
        state.direction = entity.getBlockState().getValue(TransductionAmplifierBlock.FACING);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float extension = Math.min(50, state.count) / 137.0F;
        orient(state.direction, poseStack);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(BASE),
            (pose, buffer) -> model.renderPart("lock", pose, buffer, 0xFFFFFFFF, state.lightCoords));
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(OVERLAY),
            (pose, buffer) -> model.renderPart("lock", pose, buffer, 0xFFCCCC00, 220));
        for (int index = 0; index < 4; index++) {
            poseStack.pushPose();
            poseStack.rotateDegrees(Axis.ZP, index * 90.0F);
            poseStack.rotateDegrees(Axis.YP, 45.0F);
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
                poseStack.rotateDegrees(Axis.YP, 180.0F);
            }
            case WEST -> {
                poseStack.translate(0.0F, 0.5F, 0.5F);
                poseStack.rotateDegrees(Axis.YP, 90.0F);
            }
            case EAST -> {
                poseStack.translate(1.0F, 0.5F, 0.5F);
                poseStack.rotateDegrees(Axis.YP, 270.0F);
            }
            default -> poseStack.translate(0.5F, 0.5F, 0.0F);
        }
    }
}
