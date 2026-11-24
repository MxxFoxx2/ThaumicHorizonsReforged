package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.resources.ResourceLocation;

public class ScholarChickenRenderer extends ChickenRenderer {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/scholarchicken.png");

    public ScholarChickenRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Chicken entity) {
        return TEXTURE;
    }
}
