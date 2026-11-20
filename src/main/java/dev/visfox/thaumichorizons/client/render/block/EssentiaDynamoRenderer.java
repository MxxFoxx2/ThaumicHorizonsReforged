package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.EssentiaDynamoBlockEntity;
import dev.visfox.thaumichorizons.compat.Compat;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class EssentiaDynamoRenderer implements BlockEntityRenderer<EssentiaDynamoBlockEntity, EssentiaDynamoRenderer.State> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/thaumiumring.png");
    private final Map<BlockPos, Long> trailTicks = new HashMap<>();

    public EssentiaDynamoRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public float rise;
        public float rotation;
        public float rotation2;
        public boolean active;
        public int color;
        public int frame;
        public int aspectColor;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EssentiaDynamoBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        Level level = entity.getLevel();
        state.rise = entity.rise;
        state.rotation = entity.rotation;
        state.rotation2 = entity.rotation2;
        state.active = entity.rise >= 0.3F && entity.ticksProvided > 0;
        state.aspectColor = entity.essentia == null ? 0xFFFFFF : entity.essentia.getColor();
        state.color = 0xFF000000 | state.aspectColor;
        state.frame = level == null ? 0 : Math.floorMod((int) (level.getGameTime() * 1.25F + entity.getBlockPos().getX()), 16);
        if (level != null && entity.essentia != null && entity.ticksProvided > 0 && level.isClientSide()
            && Compat.port().fx().isAvailable()) {
            long gameTime = level.getGameTime();
            if (gameTime % 4 == 0 && trailTicks.put(entity.getBlockPos(), gameTime) == null) {
                Compat.port().fx().essentiaTrail(level, entity.getBlockPos(),
                    new Vec3(entity.getBlockPos().getX() + 0.5, entity.getBlockPos().getY() + 0.5,
                        entity.getBlockPos().getZ() + 0.5), state.aspectColor);
            }
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        MachineRenderHelper.scanner(poseStack, collector, camera, TEXTURE, state.rise, state.rotation,
            state.rotation2, state.active, state.color, state.lightCoords, state.frame);
    }
}
