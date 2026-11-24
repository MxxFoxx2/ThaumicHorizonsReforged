package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

public final class ThHorseArmorLayer<T extends AbstractHorse> extends RenderLayer<T, ThHorseModel<T>> {
    private final HorseModel<T> model;

    public ThHorseArmorLayer(RenderLayerParent<T, ThHorseModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new HorseModel<>(modelSet.bakeLayer(ModelLayers.HORSE_ARMOR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight,
        T entity, float limbSwing, float limbSwingAmount, float partialTicks,
        float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack stack = entity.getBodyArmorItem();
        if (!(stack.getItem() instanceof AnimalArmorItem armor)
            || armor.getBodyType() != AnimalArmorItem.BodyType.EQUESTRIAN) {
            return;
        }

        ThHorseModel<T> parent = getParentModel();
        parent.copyPropertiesTo(model);
        model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        int color = stack.is(ItemTags.DYEABLE)
            ? FastColor.ARGB32.opaque(DyedItemColor.getOrDefault(stack, -6265536))
            : -1;
        VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutoutNoCull(armor.getTexture()));
        model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, color);
    }
}
