package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.Familiar;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class FamiliarRenderer extends ThFelineRenderer<Familiar> {
    public FamiliarRenderer(EntityRendererProvider.Context context) {
        super(context, ThaumicHorizons.id("textures/entity/familiar.png"));
    }
}
