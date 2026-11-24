package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.VisDynamoBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import dev.visfox.thaumichorizons.client.platform.ThCameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class VisDynamoRenderer implements ThStateBlockRenderer<VisDynamoBlockEntity, VisDynamoRenderer.State> {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/models/goldring.png");

    public VisDynamoRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public float rise;
        public float rotation;
        public float rotation2;
        public boolean active;
        public int color;
        public int frame;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(VisDynamoBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, Object progress) {
        Level level = entity.getLevel();
        state.rise = entity.rise;
        state.rotation = entity.rotation;
        state.rotation2 = entity.rotation2;
        state.active = entity.rise >= 0.3F && entity.ticksProvided > 0;
        state.color = entity.color;
        state.frame = level == null ? 0 : Math.floorMod((int) (level.getGameTime() * 1.25F + entity.getBlockPos().getX()), 16);
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector, ThCameraRenderState camera) {
        MachineRenderHelper.scanner(poseStack, collector, TEXTURE, state.rise, state.rotation,
            state.rotation2, state.active, state.color, state.lightCoords, state.frame);
    }
}
