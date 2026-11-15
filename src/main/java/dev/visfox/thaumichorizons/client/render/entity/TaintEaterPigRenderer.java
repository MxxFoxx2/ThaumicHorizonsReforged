package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import net.minecraft.client.model.animal.pig.PigModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;

public class TaintEaterPigRenderer extends PigRenderer {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/taintfeeder.png");
    private static final Identifier EYES_TEXTURE = ThaumicHorizons.id("textures/entity/taintfeeder_eyes.png");

    public TaintEaterPigRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new TaintFeederEyesLayer(this));
    }

    @Override
    public Identifier getTextureLocation(PigRenderState state) {
        return TEXTURE;
    }

    private static final class TaintFeederEyesLayer extends RenderLayer<PigRenderState, PigModel> {
        private TaintFeederEyesLayer(RenderLayerParent<PigRenderState, PigModel> parent) {
            super(parent);
        }

        @Override
        public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, PigRenderState state, float yRot, float xRot) {
            // Original shouldRenderPass blends the eyes overlay at alpha 0.32+0.32*sin, fullbright;
            // an opaque cutout pass would paint the whole body cyan.
            float pulse = Mth.sin((float) Math.toRadians((state.ageInTicks % 45) * 8.0F));
            int alpha = (int) ((0.32F + 0.32F * pulse) * 255.0F);
            // 9-arg overload: 7th is tintedColor, 9th outlineColor. The 7-arg overload
            // treats its last param as outlineColor and forces an opaque tint.
            submitNodeCollector.order(1).submitModel(this.getParentModel(), state, poseStack,
                ThRenderTypes.entityTranslucent(EYES_TEXTURE), LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                (alpha << 24) | 0xFFFFFF, null, state.outlineColor);
        }
    }
}
