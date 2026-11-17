package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.render.model.FamiliarHatModel;
import net.minecraft.client.model.animal.feline.AbstractFelineModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.FelineRenderState;
import net.minecraft.resources.Identifier;

public final class FamiliarHatLayer extends RenderLayer<FelineRenderState, AbstractFelineModel<FelineRenderState>> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/entity/familiar.png");
    private final FamiliarHatModel model;

    public FamiliarHatLayer(
        RenderLayerParent<FelineRenderState, AbstractFelineModel<FelineRenderState>> parent,
        ModelPart modelRoot
    ) {
        super(parent);
        this.model = new FamiliarHatModel(modelRoot);
    }

    @Override
    public void submit(
        PoseStack poseStack,
        SubmitNodeCollector collector,
        int lightCoords,
        FelineRenderState state,
        float yRot,
        float xRot
    ) {
        if (state.isInvisible && !state.appearsGlowing()) {
            return;
        }

        renderColoredCutoutModel(
            this.model,
            TEXTURE,
            poseStack,
            collector,
            lightCoords,
            state,
            0xFFFFFFFF,
            LivingEntityRenderer.getOverlayCoords(state, 0.0F)
        );
    }
}
