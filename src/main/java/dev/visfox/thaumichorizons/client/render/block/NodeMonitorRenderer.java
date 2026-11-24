package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.NodeMonitorBlock;
import dev.visfox.thaumichorizons.blockentity.NodeMonitorBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * Port of the 1.7.10 {@code TileNodeMonitorRender}: a hexagonal scanner ring built from
 * {@code scanner.obj} plus a screen overlay built from {@code hexagon.obj}.
 */
public final class NodeMonitorRenderer implements ThStateBlockRenderer<NodeMonitorBlockEntity, NodeMonitorRenderer.State> {
    private static final ResourceLocation SCANNER = ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/scanner.obj");
    private static final ResourceLocation SCANNER_SCREEN = ThaumicHorizons.id("textures/models/hexagon.obj");
    private static final ResourceLocation RING = ThaumicHorizons.id("textures/block/nodemon.png");
    private static final ResourceLocation SCREEN = ThaumicHorizons.id("textures/models/nodemonscreen.png");
    private static final ResourceLocation SCREEN_ACTIVE = ThaumicHorizons.id("textures/models/nodemonscreenactive.png");
    private static final int WHITE = 0xFFFFFFFF;

    private final ObjModel scanner = ObjModel.get(SCANNER);
    private final ObjModel screen = ObjModel.get(SCANNER_SCREEN);

    public NodeMonitorRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public Direction facing = Direction.UP;
        public float rotation;
        public boolean active;
        public boolean switched;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(NodeMonitorBlockEntity entity, State state, float partialTicks,
        net.minecraft.world.phys.Vec3 cameraPos,
        Object progress) {
        state.facing = entity.getBlockState().hasProperty(NodeMonitorBlock.FACING)
            ? entity.getBlockState().getValue(NodeMonitorBlock.FACING) : Direction.UP;
        state.rotation = entity.rotation;
        state.active = entity.activated;
        state.switched = entity.switchy;
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector,
        dev.visfox.thaumichorizons.client.platform.ThCameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.4F, 0.4F, 0.4F);
        switch (state.facing) {
            case DOWN -> {
                poseStack.translate(0.0F, -0.8F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
            }
            case UP -> {
                poseStack.translate(0.0F, 0.8F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
            }
            case NORTH -> {
                poseStack.translate(0.0F, 0.0F, 0.8F);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
            }
            case SOUTH -> {
                poseStack.translate(0.0F, 0.0F, -0.8F);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
            }
            case WEST -> {
                poseStack.translate(0.8F, 0.0F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
            }
            case EAST -> {
                poseStack.translate(-0.8F, 0.0F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(state.rotation));
            }
            default -> {
            }
        }
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(RING),
            (pose, buffer) -> scanner.renderAll(pose, buffer, WHITE, state.lightCoords));
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(
                state.switched ? SCREEN_ACTIVE : SCREEN),
            (pose, buffer) -> screen.renderAll(pose, buffer, WHITE, state.lightCoords));
        poseStack.popPose();
    }
}
