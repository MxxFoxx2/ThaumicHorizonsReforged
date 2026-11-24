package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.resources.ResourceLocation;

public class OrePigRenderer extends PigRenderer {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/oreboar.png");

    public OrePigRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Pig entity) {
        return TEXTURE;
    }
}
