package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.Familiar;
import dev.visfox.thaumichorizons.client.render.model.FamiliarHatModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FamiliarRenderer extends ThFelineRenderer<Familiar> {
    public FamiliarRenderer(EntityRendererProvider.Context context) {
        super(context, ThaumicHorizons.id("textures/entity/familiar.png"));
        this.addLayer(new FamiliarHatLayer(this, context.bakeLayer(FamiliarHatModel.LAYER)));
    }
}
