package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import java.util.List;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import dev.visfox.thaumichorizons.client.platform.ThCameraRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;

public final class VortexRenderer implements ThStateBlockRenderer<VortexBlockEntity, VortexRenderer.State> {
    private static final ResourceLocation VORTEX = ThaumicHorizons.id("textures/misc/vortex.png");

    public VortexRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends ThBlockEntityRenderState {
        public List<Aspect> aspects = List.of();
        public AspectList amounts = new AspectList();
        public int count;
        public int beams;
        public boolean collapsing;
        public boolean plane;
        public boolean cheat;
        public int frame;
        public float partialTicks;
        public float alpha;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(VortexBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, Object progress) {
        Level level = entity.getLevel();
        state.aspects = List.copyOf(entity.aspects.getAspects());
        state.amounts = entity.aspects.copy();
        state.count = entity.count;
        state.beams = entity.beams;
        state.collapsing = entity.collapsing;
        state.plane = entity.createdDimension;
        state.cheat = entity.cheat;
        state.frame = level == null ? 0 : Math.floorMod((int) (level.getGameTime() * 1.25F + entity.getBlockPos().getX()), 32);
        state.partialTicks = partialTicks;
        double distance = cameraPosition.distanceTo(Vec3.atCenterOf(entity.getBlockPos()));
        state.alpha = Mth.clamp((float) ((64.0 - distance) / 64.0), 0.0F, 1.0F);
        state.lightCoords = 220;
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector, ThCameraRenderState camera) {
        if (state.aspects.isEmpty() || state.alpha <= 0.0F) {
            return;
        }
        float ticks = state.frame + state.partialTicks;
        float angle = 0.0F;
        float alpha = state.alpha / Math.max(1.0F, state.aspects.size() / 2.0F);
        for (int index = 0; index < state.aspects.size(); index++) {
            Aspect aspect = state.aspects.get(index);
            float period = 500.0F + 50.0F * index;
            angle = (ticks % period) / period * (float) (Math.PI * 2.0);
            float scale = state.beams < 6 || state.count < 50 ? 4.0F : 4.0F / 3.0F;
            MachineRenderHelper.strip(poseStack, collector, MachineRenderHelper.NODES,
                0.5F, 0.5F, 0.5F, scale, 32, 0, state.frame, withAlpha(aspect.getColor(), alpha), state.lightCoords);
        }

        float coreScale = 1.0F;
        if (state.count < 50 && !state.collapsing) {
            coreScale = state.count / 50.0F;
        } else if (state.collapsing) {
            coreScale = 1.0F - state.count / 25.0F;
        }
        if (!state.cheat && (state.beams < 6 || state.count < 50)) {
            MachineRenderHelper.rotatingFrame(poseStack, collector, VORTEX,
                0.5F, 0.5F, 0.5F, angle * 20.0F * coreScale / (1 + 2 * state.beams),
                0.8F * coreScale, 16, state.frame % 16, withAlpha(0xFFFFFF, state.alpha * 0.8F), state.lightCoords);
        } else {
            MachineRenderHelper.strip(poseStack, collector, MachineRenderHelper.NODES,
                0.5F, 0.5F, 0.5F, 3.0F, 32, 2, state.frame, withAlpha(0xFFFFFF, state.alpha), state.lightCoords);
        }
        if (state.plane) {
            for (int index = 0; index < state.aspects.size(); index++) {
                Aspect aspect = state.aspects.get(index);
                MachineRenderHelper.strip(poseStack, collector, MachineRenderHelper.NODES,
                    0.5F, 0.5F, 0.5F, 0.5F, 32, 0, state.frame,
                    withAlpha(aspect.getColor(), alpha), state.lightCoords);
            }
        }
    }

    private static int withAlpha(int rgb, float alpha) {
        return Mth.clamp((int) (alpha * 255.0F), 0, 255) << 24 | rgb & 0x00FFFFFF;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
