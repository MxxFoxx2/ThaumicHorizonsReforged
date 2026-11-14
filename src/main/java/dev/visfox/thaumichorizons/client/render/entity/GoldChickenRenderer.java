package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.resources.Identifier;

public class GoldChickenRenderer extends ChickenRenderer {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/goldchicken.png");

    public GoldChickenRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(ChickenRenderState state) {
        return TEXTURE;
    }
}
