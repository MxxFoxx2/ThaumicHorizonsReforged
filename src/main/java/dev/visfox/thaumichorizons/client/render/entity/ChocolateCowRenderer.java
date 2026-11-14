package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.CowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.CowRenderState;
import net.minecraft.resources.Identifier;

public class ChocolateCowRenderer extends CowRenderer {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/chocolatecow.png");

    public ChocolateCowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(CowRenderState state) {
        return TEXTURE;
    }
}
