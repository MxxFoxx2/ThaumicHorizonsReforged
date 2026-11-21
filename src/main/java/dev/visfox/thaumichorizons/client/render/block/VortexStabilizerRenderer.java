package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.block.VortexStabilizerBlock;
import dev.visfox.thaumichorizons.blockentity.VortexStabilizerBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class VortexStabilizerRenderer
    implements BlockEntityRenderer<VortexStabilizerBlockEntity, VortexStabilizerRenderer.State> {
    private static final Identifier HEMIS_OBJ = Identifier.fromNamespaceAndPath("thaumcraft", "models/obj/hemis.obj");
    private final ObjModel hemis = ObjModel.get(HEMIS_OBJ);

    public VortexStabilizerRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public boolean redstoned;
        public BlockPos source;
        public BlockPos target;
        public Direction direction = Direction.UP;
        public long gameTime;
        public float partialTicks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(VortexStabilizerBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        Level level = entity.getLevel();
        state.redstoned = entity.redstoned;
        state.source = entity.getBlockPos();
        state.target = entity.targetPos;
        state.direction = entity.getBlockState().getValue(VortexStabilizerBlock.FACING);
        state.gameTime = level == null ? 0 : level.getGameTime();
        state.partialTicks = partialTicks;
        state.lightCoords = 220;
        if (level != null && state.redstoned && state.target != null) {
            Direction direction = state.direction;
            TcReforgedPort.fx.beam(level, state.source,
                Vec3.atCenterOf(state.source),
                new Vec3(state.target.getX() + 0.5 - direction.getStepX(),
                    state.target.getY() + 0.5 - direction.getStepY(),
                    state.target.getZ() + 0.5 - direction.getStepZ()),
                33023, 2.0F, 1);
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (!state.redstoned || state.target == null) {
            return;
        }
        float time = state.gameTime + state.partialTicks;
        for (int index = 0; index < 3; index++) {
            float age = (time + index * 4.0F) % 12.0F;
            float fade = age / 12.0F;
            int frame = Math.min(15, (int) (14.0F * fade) + 1);
            Identifier texture = Identifier.fromNamespaceAndPath("thaumcraft", "textures/model/hemis" + frame + ".png");
            poseStack.pushPose();
            poseStack.translate(state.target.getX() - state.source.getX() + 0.5F,
                state.target.getY() - state.source.getY() + 0.5F,
                state.target.getZ() - state.source.getZ() + 0.5F);
            rotate(state.direction, poseStack);
            poseStack.scale(0.5F, 0.5F, -0.5F);
            collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(texture),
                (pose, buffer) -> hemis.renderAll(pose, buffer, 0x8000FFFF, state.lightCoords));
            poseStack.popPose();
        }
    }

    private static void rotate(Direction direction, PoseStack poseStack) {
        float yaw = switch (direction) {
            case NORTH -> 180.0F;
            case WEST -> 90.0F;
            case EAST -> 270.0F;
            default -> 0.0F;
        };
        float pitch = switch (direction) {
            case DOWN -> 90.0F;
            case UP -> -90.0F;
            default -> 0.0F;
        };
        poseStack.rotateDegrees(Axis.YP, -yaw);
        poseStack.rotateDegrees(Axis.XP, pitch);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
