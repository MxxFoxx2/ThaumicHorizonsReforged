package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.SpikeBlock;
import dev.visfox.thaumichorizons.blockentity.SpikeBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;

/** Renders the original spike.obj mesh with the legacy metal/wood/tooth texture set. */
public final class SpikeBlockRenderer implements ThStateBlockRenderer<SpikeBlockEntity, SpikeBlockRenderer.State> {
    private static final ResourceLocation MODEL_ID = ThaumicHorizons.id("textures/models/spike.obj");
    private static final ResourceLocation[] TEXTURES = {
        ThaumicHorizons.id("textures/models/metalspike.png"),
        ThaumicHorizons.id("textures/models/woodenspike.png"),
        ThaumicHorizons.id("textures/models/toothspike.png")
    };

    private final ObjModel model = ObjModel.get(MODEL_ID);

    public SpikeBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public Direction facing = Direction.UP;
        public int spikeType;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SpikeBlockEntity entity, State state, float partialTicks,
        net.minecraft.world.phys.Vec3 cameraPosition,
        Object progress) {
        state.facing = entity.getBlockState().getValue(SpikeBlock.FACING);
        state.spikeType = entity.getSpikeType();
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector,
        dev.visfox.thaumichorizons.client.platform.ThCameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.35F, 0.35F, 0.35F);
        switch (state.facing) {
            case UP -> poseStack.translate(0.0F, -1.45F, 0.0F);
            case DOWN -> {
                poseStack.translate(0.0F, 1.45F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
            }
            case NORTH -> {
                poseStack.translate(0.0F, 0.0F, 1.45F);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
            }
            case SOUTH -> {
                poseStack.translate(0.0F, 0.0F, -1.45F);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            }
            case WEST -> {
                poseStack.translate(1.45F, 0.0F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
            }
            case EAST -> {
                poseStack.translate(-1.45F, 0.0F, 0.0F);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
            }
        }
        ResourceLocation texture = TEXTURES[Math.max(0, Math.min(2, state.spikeType))];
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(texture),
            (pose, buffer) -> model.renderAll(pose, buffer, 0xFFFFFFFF, state.lightCoords));
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(SpikeBlockEntity entity) {
        var pos = entity.getBlockPos();
        return new AABB(pos.getX() - 1.0, pos.getY() - 1.0, pos.getZ() - 1.0,
            pos.getX() + 2.0, pos.getY() + 2.0, pos.getZ() + 2.0);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
