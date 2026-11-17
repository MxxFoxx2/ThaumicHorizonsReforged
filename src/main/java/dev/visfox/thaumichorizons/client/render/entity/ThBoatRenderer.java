package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.object.boat.BoatModel;
import net.minecraft.client.renderer.entity.AbstractBoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.BoatRenderState;
import net.minecraft.resources.Identifier;

public final class ThBoatRenderer extends AbstractBoatRenderer {
    public static final ModelLayerLocation GREATWOOD_LAYER =
        new ModelLayerLocation(ThaumicHorizons.id("boat_greatwood"), "main");
    public static final ModelLayerLocation THAUMIUM_LAYER =
        new ModelLayerLocation(ThaumicHorizons.id("boat_thaumium"), "main");

    private final BoatModel model;

    public ThBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, Identifier texture) {
        super(context, texture);
        this.model = new BoatModel(context.bakeLayer(layer));
    }

    @Override
    protected EntityModel<BoatRenderState> model() {
        return this.model;
    }
}
