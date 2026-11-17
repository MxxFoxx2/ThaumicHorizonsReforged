package dev.visfox.thaumichorizons.client.render.model;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.FelineRenderState;

/**
 * The Familiar's original wizard hat from the 1.7.10 ModelFamiliar.
 *
 * <p>The geometry is kept relative to the feline head pivot. The render layer
 * copies the live parent model's head transform before submitting this model.</p>
 */
public final class FamiliarHatModel extends EntityModel<FelineRenderState> {
    public static final ModelLayerLocation LAYER =
        new ModelLayerLocation(ThaumicHorizons.id("familiar_hat"), "main");

    public final ModelPart head;

    public FamiliarHatModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

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
