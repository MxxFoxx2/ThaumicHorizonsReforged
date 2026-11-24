package dev.visfox.thaumichorizons.client.render.entity;

import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

public final class ThHorseModel<T extends AbstractHorse> extends HorseModel<T> {
    public ThHorseModel(ModelPart root) {
        super(root);
    }

    public ModelPart[] saddleParts() {
        return new ModelPart[] {
            body.getChild("saddle"),
            headParts.getChild("left_saddle_mouth"),
            headParts.getChild("right_saddle_mouth"),
            headParts.getChild("head_saddle"),
            headParts.getChild("mouth_saddle_wrap")
        };
    }

    public void showSaddle() {
        for (ModelPart part : saddleParts()) {
            part.visible = true;
        }
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
        float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // Custom sheets do not carry the vanilla saddle UVs; the saddle is drawn by ThHorseSaddleLayer.
        for (ModelPart part : saddleParts()) {
            part.visible = false;
        }
    }
}
