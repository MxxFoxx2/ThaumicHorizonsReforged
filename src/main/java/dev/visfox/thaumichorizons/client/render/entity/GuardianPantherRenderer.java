package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.entity.GuardianPanther;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class GuardianPantherRenderer extends ThFelineRenderer<GuardianPanther> {
    public GuardianPantherRenderer(EntityRendererProvider.Context context) {
        super(context, Identifier.withDefaultNamespace("textures/entity/cat/ocelot.png"));
    }
}
