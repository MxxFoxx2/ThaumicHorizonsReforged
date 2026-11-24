package dev.visfox.thaumichorizons.client.render.entity;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.TamableAnimal;

public abstract class ThFelineRenderer<T extends TamableAnimal> extends MobRenderer<T, OcelotModel<T>> {
    private final ResourceLocation texture;

    protected ThFelineRenderer(EntityRendererProvider.Context context, ResourceLocation texture) {
        super(context, new OcelotModel<>(context.bakeLayer(ModelLayers.OCELOT)), 0.4F);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return this.texture;
    }
}
