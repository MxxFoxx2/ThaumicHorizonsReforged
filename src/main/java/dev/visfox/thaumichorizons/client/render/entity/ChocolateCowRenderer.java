package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.resources.ResourceLocation;

public class ChocolateCowRenderer extends CowRenderer {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/chocolatecow.png");

    public ChocolateCowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Cow entity) {
        return TEXTURE;
    }
}
