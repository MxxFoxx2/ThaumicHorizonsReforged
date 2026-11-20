package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.blockentity.SyntheticNodeBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.client.geom.CubeGeometry;
import thaumcraft.client.platform.EntityQuadSink;
import thaumcraft.client.platform.TcRenderTypes;

public final class SyntheticNodeRenderer implements BlockEntityRenderer<SyntheticNodeBlockEntity, SyntheticNodeRenderer.State> {
    private static final Identifier CRYSTAL = Identifier.fromNamespaceAndPath("thaumcraft", "textures/model/crystal.png");

    public SyntheticNodeRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public float rotation;
        public int color;
        public List<Aspect> aspects = List.of();
        public AspectList current = new AspectList();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SyntheticNodeBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        Level level = entity.getLevel();
        state.rotation = entity.rotation + partialTicks;
        state.current = entity.getAspects().copy();
        AspectList base = entity.getAspectsBase();
        List<Aspect> aspects = new ArrayList<>();
        long red = 255;
        long green = 255;
        long blue = 255;
        long points = 0;
        for (Aspect aspect : base.getAspects()) {
            aspects.add(aspect);
            int amount = base.getAmount(aspect);
            red += (aspect.getColor() >> 16 & 255) * (long) amount;
            green += (aspect.getColor() >> 8 & 255) * (long) amount;
            blue += (aspect.getColor() & 255) * (long) amount;
            points += amount;
        }
        state.aspects = List.copyOf(aspects);
        long divisor = points + 1;
        state.color = 0xFF000000 | (int) Math.min(255, red / divisor) << 16
            | (int) Math.min(255, green / divisor) << 8 | (int) Math.min(255, blue / divisor);
        state.lightCoords = 220;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.rotateDegrees(Axis.YP, state.rotation);
        poseStack.scale(0.165F, 0.55F, 0.165F);
        poseStack.rotateDegrees(Axis.ZP, 40.5F);
        poseStack.rotateDegrees(Axis.XP, 40.5F);
        collector.submitCustomGeometry(poseStack, TcRenderTypes.entityTranslucent(CRYSTAL),
            (pose, buffer) -> CubeGeometry.render(new EntityQuadSink(pose, buffer), 0, state.color, state.lightCoords));
        poseStack.popPose();

        double radian = Math.toRadians(state.rotation);
        double distance = 0.4 + 0.1 * Math.cos(radian);
        double offset = state.aspects.isEmpty() ? 0.0 : Math.PI * 2.0 / state.aspects.size();
        int frame = Math.floorMod((int) state.rotation, 32);
        for (int index = 0; index < state.aspects.size(); index++) {
            Aspect aspect = state.aspects.get(index);
            float x = (float) (0.5 + distance * Math.sin(2.0 * radian + offset * index));
            float y = (float) (0.64 + 0.1 * Math.sin(radian));
            float z = (float) (0.5 + distance * Math.cos(2.0 * radian + offset * index));
            float scale = 0.1F + 0.005F * state.current.getAmount(aspect);
            MachineRenderHelper.strip(poseStack, collector, camera, MachineRenderHelper.NODES,
                x, y, z, scale, 32, 1, frame, 0xFF000000 | aspect.getColor(), state.lightCoords);
        }
    }
}
