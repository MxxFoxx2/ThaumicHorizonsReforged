package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.Endersteed;
import net.minecraft.client.model.animal.equine.BabyHorseModel;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HorseRenderState;
import net.minecraft.resources.Identifier;

public class EndersteedRenderer extends AbstractHorseRenderer<Endersteed, HorseRenderState, HorseModel> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/endersteed.png");

    public EndersteedRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel(context.bakeLayer(ModelLayers.HORSE)), new BabyHorseModel(context.bakeLayer(ModelLayers.HORSE_BABY)));
    }

    @Override
    public Identifier getTextureLocation(HorseRenderState state) {
        return TEXTURE;
    }

    @Override
    public HorseRenderState createRenderState() {
        return new HorseRenderState();
    }
}
