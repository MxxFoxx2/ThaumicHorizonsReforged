package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SlimeRenderer;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.resources.ResourceLocation;

public class VoltSlimeRenderer extends SlimeRenderer {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/voltslime.png");

    public VoltSlimeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Slime entity) {
        return TEXTURE;
    }
}
