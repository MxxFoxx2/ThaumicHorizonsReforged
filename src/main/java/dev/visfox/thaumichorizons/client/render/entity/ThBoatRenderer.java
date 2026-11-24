package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.datafixers.util.Pair;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

public final class ThBoatRenderer extends BoatRenderer {
    public static final ModelLayerLocation GREATWOOD_LAYER =
        new ModelLayerLocation(ThaumicHorizons.id("boat_greatwood"), "main");
    public static final ModelLayerLocation THAUMIUM_LAYER =
        new ModelLayerLocation(ThaumicHorizons.id("boat_thaumium"), "main");

    private final ListModel<Boat> model;
    private final ResourceLocation texture;

    public ThBoatRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, ResourceLocation texture) {
        super(context, false);
        this.model = new BoatModel(context.bakeLayer(layer));
        this.texture = texture;
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        return Pair.of(texture, model);
    }
}
