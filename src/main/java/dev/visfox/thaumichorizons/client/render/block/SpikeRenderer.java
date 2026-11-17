package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.SpikeBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

/** Restores the legacy spike OBJ with its metal, greatwood and bone material variants. */
public final class SpikeRenderer implements BlockEntityRenderer<SpikeBlockEntity, SpikeRenderer.State> {
    private static final ObjModel MODEL = ObjModel.get(ThaumicHorizons.id("textures/models/spike.obj"));
    private static final Identifier[] TEXTURES = {
        ThaumicHorizons.id("textures/models/metalspike.png"),
        ThaumicHorizons.id("textures/models/woodenspike.png"),
        ThaumicHorizons.id("textures/models/toothspike.png")
    };
    private static final int WHITE = 0xFFFFFFFF;

    public SpikeRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public Direction facing = Direction.UP;
        public int spikeType;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SpikeBlockEntity entity, State state, float partialTicks,
        net.minecraft.world.phys.Vec3 cameraPos,
        net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        state.facing = entity.getBlockState().getValue(dev.visfox.thaumichorizons.block.SpikeBlock.FACING);
        state.spikeType = entity.getSpikeType();
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector,
        net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        switch (state.facing) {
            case DOWN -> {
                poseStack.translate(0.0F, 1.0F, 0.0F);
                poseStack.rotateDegrees(Axis.XP, 180.0F);
            }
            case NORTH -> poseStack.rotateDegrees(Axis.XP, -90.0F);
            case SOUTH -> poseStack.rotateDegrees(Axis.XP, 90.0F);
            case WEST -> poseStack.rotateDegrees(Axis.ZP, 90.0F);
            case EAST -> poseStack.rotateDegrees(Axis.ZP, -90.0F);
            default -> {
            }
        }
        poseStack.scale(0.30F, 0.30F, 0.30F);
        Identifier texture = TEXTURES[Math.max(0, Math.min(state.spikeType, TEXTURES.length - 1))];
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(texture),
            (pose, buffer) -> MODEL.renderAll(pose, buffer, WHITE, state.lightCoords));
        poseStack.popPose();
    }
}
