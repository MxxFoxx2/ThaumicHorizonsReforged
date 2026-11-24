package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public final class ThHorseSaddleLayer<T extends AbstractHorse> extends RenderLayer<T, ThHorseModel<T>> {
    private static final ResourceLocation SADDLE_TEXTURE =
        ResourceLocation.withDefaultNamespace("textures/entity/horse/horse_chestnut.png");

    private final ThHorseModel<T> model;

    public ThHorseSaddleLayer(RenderLayerParent<T, ThHorseModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new ThHorseModel<>(modelSet.bakeLayer(ModelLayers.HORSE));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight,
        T entity, float limbSwing, float limbSwingAmount, float partialTicks,
        float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isSaddled()) {
            return;
        }

        ThHorseModel<T> parent = getParentModel();
        parent.copyPropertiesTo(model);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        model.showSaddle();
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(SADDLE_TEXTURE));
        for (ModelPart part : model.saddleParts()) {
            part.render(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }
}
