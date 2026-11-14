package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.resources.Identifier;

public class OrePigRenderer extends PigRenderer {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/oreboar.png");

    public OrePigRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(PigRenderState state) {
        return TEXTURE;
    }
}
