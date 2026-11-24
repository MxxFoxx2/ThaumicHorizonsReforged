package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.entity.SyringeProjectile;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.alchemy.PotionContents;

/** Restores the four-part legacy syringe dart model and its potion tint. */
public final class SyringeProjectileRenderer extends EntityRenderer<SyringeProjectile, SyringeProjectileRenderer.State> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    private final ObjModel model = ObjModel.get(ThaumicHorizons.id("textures/models/syringe.obj"));

    public SyringeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    public static final class State extends EntityRenderState {
        public float yaw;
        public float pitch;
        public int potionColor;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SyringeProjectile entity, State state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.yaw = entity.getYRot(partialTicks);
        state.pitch = entity.getXRot(partialTicks);
        PotionContents contents = entity.getItem().get(DataComponents.POTION_CONTENTS);
        state.potionColor = contents == null ? PotionContents.BASE_POTION_COLOR : contents.getColor();
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        int bodyColor = 0xBF000000 | (state.potionColor & 0x00FFFFFF);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yaw + 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-state.pitch));
        poseStack.translate(0.0F, -1.36F, 0.0F);
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(TEXTURE), (pose, buffer) -> {
            model.renderPart("Body", pose, buffer, bodyColor, state.lightCoords);
            model.renderPart("Needle", pose, buffer, 0xFFFFFFFF, state.lightCoords);
            model.renderPart("PlungerA", pose, buffer, 0xFFFFFFFF, state.lightCoords);
            model.renderPart("PlungerB", pose, buffer, 0xFFFFFFFF, state.lightCoords);
        });
        poseStack.popPose();
    }
}
