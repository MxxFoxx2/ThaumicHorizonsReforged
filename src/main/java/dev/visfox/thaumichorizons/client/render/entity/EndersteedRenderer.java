package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.Endersteed;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EndersteedRenderer extends AbstractHorseRenderer<Endersteed, ThHorseModel<Endersteed>> {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/endersteed.png");

    public EndersteedRenderer(EntityRendererProvider.Context context) {
        super(context, new ThHorseModel<>(context.bakeLayer(ModelLayers.HORSE)), 0.8F);
        this.addLayer(new ThHorseArmorLayer<>(this, context.getModelSet()));
        this.addLayer(new ThHorseSaddleLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Endersteed entity) {
        // Minecraft 1.21.1 renders foals with the same horse mesh, so the adult sheet is used.
        return TEXTURE;
    }
}
