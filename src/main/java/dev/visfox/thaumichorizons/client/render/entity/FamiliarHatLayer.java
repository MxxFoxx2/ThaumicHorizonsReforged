package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.render.model.FamiliarHatModel;
import dev.visfox.thaumichorizons.entity.Familiar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public final class FamiliarHatLayer extends RenderLayer<Familiar, OcelotModel<Familiar>> {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/entity/familiar.png");
    private final FamiliarHatModel model;

    public FamiliarHatLayer(RenderLayerParent<Familiar, OcelotModel<Familiar>> parent, ModelPart modelRoot) {
        super(parent);
        this.model = new FamiliarHatModel(modelRoot);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int lightCoords, Familiar entity,
        float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
        float netHeadYaw, float headPitch) {
        if (entity.isInvisible() && !Minecraft.getInstance().shouldEntityAppearGlowing(entity)) {
            return;
        }
        renderColoredCutoutModel(this.model, TEXTURE, poseStack, buffers, lightCoords, entity, 0xFFFFFFFF);
    }
}
