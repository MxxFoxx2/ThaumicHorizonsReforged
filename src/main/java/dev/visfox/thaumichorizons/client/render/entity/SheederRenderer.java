package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.entity.Sheeder;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.spider.SpiderModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

public class SheederRenderer extends MobRenderer<Sheeder, LivingEntityRenderState, SpiderModel> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/sheeder.png");
    private static final Identifier EYES_TEXTURE = ThaumicHorizons.id("textures/entity/sheederoverlay.png");

    public SheederRenderer(EntityRendererProvider.Context context) {
        super(context, new SpiderModel(context.bakeLayer(ModelLayers.SPIDER)), 0.8F);
        this.addLayer(new SheederEyesLayer(this));
    }

    @Override
    protected float getFlipDegrees() {
        return 180.0F;
    }

    @Override
    public Identifier getTextureLocation(LivingEntityRenderState state) {
        return TEXTURE;
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    private static final class SheederEyesLayer extends RenderLayer<LivingEntityRenderState, SpiderModel> {
        private SheederEyesLayer(RenderLayerParent<LivingEntityRenderState, SpiderModel> parent) {
            super(parent);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, LivingEntityRenderState state, float yRot, float xRot) {
            submitNodeCollector.order(1).submitModel(this.getParentModel(), state, poseStack, ThRenderTypes.eyes(EYES_TEXTURE),
                lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        }
    }
}
