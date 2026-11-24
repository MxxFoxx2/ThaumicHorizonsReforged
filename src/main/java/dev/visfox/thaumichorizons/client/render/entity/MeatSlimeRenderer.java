package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.resources.ResourceLocation;

public class MeatSlimeRenderer extends SlimeRenderer {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/meatslime.png");

    public MeatSlimeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Slime entity) {
        return TEXTURE;
    }
}
