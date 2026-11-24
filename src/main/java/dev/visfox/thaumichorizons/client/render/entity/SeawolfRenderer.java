package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.resources.ResourceLocation;

public class SeawolfRenderer extends WolfRenderer {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/seawolf.png");

    public SeawolfRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Wolf entity) {
        return TEXTURE;
    }
}
