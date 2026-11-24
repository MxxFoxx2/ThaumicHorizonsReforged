package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import thaumcraft.client.platform.ItemGeometry;

/**
 * Restores the legacy translucent syringe model using its original per-context item transforms.
 * The OBJ retains the ModelSyringe axes: the needle points toward -X and the plunger toward +X.
 */
public final class SyringeItemSpecialModelRenderer {
    private static final ResourceLocation MODEL = ThaumicHorizons.id("textures/models/syringe.obj");
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    /** ModelSyringe.render used 0.125 for all four parts. */
    private static final float MODEL_SCALE = 0.125F;

    private static final ObjModel MODEL_DATA = ObjModel.get(MODEL);

    private SyringeItemSpecialModelRenderer() {
    }

    public static void render(ItemStack stack, ThItemView view, PoseStack poseStack,
        MultiBufferSource buffers, int lightCoords, int overlayCoords) {
        Integer bodyColor = extractArgument(stack);
        if (bodyColor == null) {
            return;
        }

        poseStack.pushPose();
        transformModel(poseStack, view);
        ItemGeometry.solid(buffers, poseStack, TEXTURE, true,
            sink -> MODEL_DATA.renderSink("Body", sink, bodyColor, lightCoords));
        ItemGeometry.solid(buffers, poseStack, TEXTURE, false,
            sink -> {
                MODEL_DATA.renderSink("Needle", sink, 0xFFFFFFFF, lightCoords);
                MODEL_DATA.renderSink("PlungerA", sink, 0xFFFFFFFF, lightCoords);
                MODEL_DATA.renderSink("PlungerB", sink, 0xFFFFFFFF, lightCoords);
            });
        poseStack.popPose();
    }

    private static Integer extractArgument(ItemStack stack) {
        if (stack.is(ThItems.SYRINGE_EMPTY.get())) {
            return 0x80FFFFFF;
        }
        if (stack.is(ThItems.SYRINGE_BLOOD.get()) || stack.is(ThItems.SYRINGE_BLOOD_SAMPLE.get())) {
            return 0xBFFF0000;
        }
        if (stack.is(ThItems.SYRINGE_INJECTION.get())) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            int rgb = contents == null ? PotionContents.EMPTY.getColor() : contents.getColor();
            return 0xBF000000 | (rgb & 0x00FFFFFF);
        }
        return null;
    }

    private static void transformModel(PoseStack poseStack, ThItemView view) {
        view.apply(poseStack);
        switch (view) {
            case INVENTORY -> poseStack.translate(0.0F, -2.75F, 0.0F);
            case ENTITY -> poseStack.translate(0.0F, -2.0F, 0.0F);
            case EQUIPPED, FIRST_PERSON -> {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-66.0F));
                poseStack.translate(-1.0F, -2.25F, 0.75F);
            }
        }
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }
}
