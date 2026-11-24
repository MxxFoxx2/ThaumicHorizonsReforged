package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.entity.Sheeder;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class SheederRenderer extends MobRenderer<Sheeder, SpiderModel<Sheeder>> {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/sheeder.png");
    private static final ResourceLocation EYES_TEXTURE = ThaumicHorizons.id("textures/entity/sheederoverlay.png");

    public SheederRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel<>(context.bakeLayer(ModelLayers.SPIDER)), 0.8F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return ThRenderTypes.eyes(EYES_TEXTURE);
            }
        });
    }

    @Override
    protected float getFlipDegrees(Sheeder entity) {
        return 180.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(Sheeder entity) {
        return TEXTURE;
    }
}
