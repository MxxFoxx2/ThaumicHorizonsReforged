package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Pig;

public class TaintEaterPigRenderer extends PigRenderer {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/taintfeeder.png");
    private static final ResourceLocation EYES_TEXTURE = ThaumicHorizons.id("textures/entity/taintfeeder_eyes.png");

    public TaintEaterPigRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new TaintFeederEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Pig entity) {
        return TEXTURE;
    }

    private static final class TaintFeederEyesLayer extends RenderLayer<Pig, PigModel<Pig>> {
        private TaintFeederEyesLayer(RenderLayerParent<Pig, PigModel<Pig>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffers, int lightCoords, Pig entity,
            float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
            float netHeadYaw, float headPitch) {
            // Original shouldRenderPass blends the eyes overlay at alpha 0.32+0.32*sin, fullbright;
            // an opaque cutout pass would paint the whole body cyan.
            float pulse = Mth.sin((float) Math.toRadians((ageInTicks % 45) * 8.0F));
            int alpha = (int) ((0.32F + 0.32F * pulse) * 255.0F);
            getParentModel().renderToBuffer(poseStack,
                buffers.getBuffer(ThRenderTypes.entityTranslucent(EYES_TEXTURE)),
                0xF000F0, OverlayTexture.NO_OVERLAY, (alpha << 24) | 0xFFFFFF);
        }
    }
}
