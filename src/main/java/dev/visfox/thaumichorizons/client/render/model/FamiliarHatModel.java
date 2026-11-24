package dev.visfox.thaumichorizons.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.Familiar;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

/**
 * The Familiar's original wizard hat from the 1.7.10 ModelFamiliar.
 *
 * <p>The geometry uses the modern Ocelot head pivot. Pitch and yaw are taken
 * from the live entity, matching the old extracted feline state.</p>
 */
public final class FamiliarHatModel extends EntityModel<Familiar> {
    public static final ModelLayerLocation LAYER =
        new ModelLayerLocation(ThaumicHorizons.id("familiar_hat"), "main");

    private final ModelPart root;
    public final ModelPart head;

    public FamiliarHatModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.head = root.getChild("head");
    }

    @Override
    public void setupAnim(Familiar entity, float limbSwing, float limbSwingAmount, float ageInTicks,
        float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
    }

    @Override
    public void renderToBuffer(PoseStack poses, VertexConsumer buffer, int light, int overlay, int color) {
        root.render(poses, buffer, light, overlay, color);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // AdultOcelotModel uses the same head pivot as the 1.7.10 familiar model.
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(),
            PartPose.offset(0.0F, 15.0F, -9.0F));

        // Original ModelFamiliar UVs on textures/entity/familiar.png (64x32).
        head.addOrReplaceChild("hat_buckle",
            CubeListBuilder.create().texOffs(48, 13).addBox(-1.0F, -4.0F, -3.0F, 2.0F, 1.0F, 1.0F),
            PartPose.ZERO);
        head.addOrReplaceChild("hat_tip",
            CubeListBuilder.create().texOffs(48, 11).addBox(-0.5F, -7.0F, -1.0F, 1.0F, 1.0F, 1.0F),
            PartPose.ZERO);
        head.addOrReplaceChild("hat_crown_upper",
            CubeListBuilder.create().texOffs(48, 8).addBox(-1.0F, -6.0F, -1.5F, 2.0F, 1.0F, 2.0F),
            PartPose.ZERO);
        head.addOrReplaceChild("hat_crown_lower",
            CubeListBuilder.create().texOffs(48, 0).addBox(-1.5F, -5.0F, -2.0F, 3.0F, 2.0F, 3.0F),
            PartPose.ZERO);
        head.addOrReplaceChild("hat_brim",
            CubeListBuilder.create().texOffs(16, 24).addBox(-3.0F, -3.0F, -3.5F, 6.0F, 1.0F, 6.0F),
            PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 32);
    }
}
