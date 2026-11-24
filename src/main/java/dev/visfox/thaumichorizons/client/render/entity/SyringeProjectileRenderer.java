package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.entity.SyringeProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.alchemy.PotionContents;

/** Restores the four-part legacy syringe dart model and its potion tint. */
public final class SyringeProjectileRenderer extends EntityRenderer<SyringeProjectile> {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    private final ObjModel model = ObjModel.get(ThaumicHorizons.id("textures/models/syringe.obj"));

    public SyringeProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(SyringeProjectile entity, float entityYaw, float partialTick,
        PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        PotionContents contents = entity.getItem().get(DataComponents.POTION_CONTENTS);
        int rgb = contents == null ? PotionContents.EMPTY.getColor() : contents.getColor();
        int bodyColor = 0xBF000000 | (rgb & 0x00FFFFFF);
        float yaw = Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw + 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-pitch));
        poseStack.translate(0.0F, -1.36F, 0.0F);
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);
        var buffer = buffers.getBuffer(ThRenderTypes.entityTranslucent(TEXTURE));
        model.renderPart("Body", poseStack.last(), buffer, bodyColor, packedLight);
        model.renderPart("Needle", poseStack.last(), buffer, 0xFFFFFFFF, packedLight);
        model.renderPart("PlungerA", poseStack.last(), buffer, 0xFFFFFFFF, packedLight);
        model.renderPart("PlungerB", poseStack.last(), buffer, 0xFFFFFFFF, packedLight);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SyringeProjectile entity) {
        return TEXTURE;
    }
}
