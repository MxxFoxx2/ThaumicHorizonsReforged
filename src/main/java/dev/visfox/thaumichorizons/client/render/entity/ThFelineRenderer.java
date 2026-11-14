package dev.visfox.thaumichorizons.client.render.entity;

import net.minecraft.client.model.animal.feline.AbstractFelineModel;
import net.minecraft.client.model.animal.feline.AdultOcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.TamableAnimal;

public abstract class ThFelineRenderer<T extends TamableAnimal> extends MobRenderer<T, FelineRenderState, AbstractFelineModel<FelineRenderState>> {
    private final Identifier texture;

    protected ThFelineRenderer(EntityRendererProvider.Context context, Identifier texture) {
        super(context, new AdultOcelotModel(context.bakeLayer(ModelLayers.OCELOT)), 0.4F);
        this.texture = texture;
    }

    @Override
    public Identifier getTextureLocation(FelineRenderState state) {
        return this.texture;
    }

    @Override
    public FelineRenderState createRenderState() {
        return new FelineRenderState();
    }
}
