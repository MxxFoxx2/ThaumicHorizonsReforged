package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.Gravekeeper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class GravekeeperRenderer extends ThFelineRenderer<Gravekeeper> {
    public GravekeeperRenderer(EntityRendererProvider.Context context) {
        super(context, ThaumicHorizons.id("textures/entity/gravekeeper.png"));
    }
}
