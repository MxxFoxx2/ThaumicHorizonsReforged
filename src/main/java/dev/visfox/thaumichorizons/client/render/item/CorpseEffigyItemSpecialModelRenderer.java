package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.platform.ItemGeometry;

/**
 * Reuses the ModelBiped-derived Corpse Effigy mesh with the original legacy item transforms.
 * The OBJ keeps ModelBiped's Y-down coordinates; the two 180-degree rotations and
 * per-context offsets establish its upright pose and placement.
 */
public final class CorpseEffigyItemSpecialModelRenderer {
    private static final ResourceLocation MODEL = ThaumicHorizons.id("textures/models/corpseeffigy.obj");
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/block/corpseeffigy.png");
    private static final float MODEL_SCALE = 0.0625F;
    private static final float ITEM_SCALE = 0.5F;

    private static final ObjModel MODEL_DATA = ObjModel.get(MODEL);

    private CorpseEffigyItemSpecialModelRenderer() {
    }

    public static void render(ItemStack stack, ThItemView view, PoseStack poseStack,
        MultiBufferSource buffers, int lightCoords, int overlayCoords) {
        if (!stack.is(ThItems.CORPSE_EFFIGY.get())) {
            return;
        }

        poseStack.pushPose();
        transformModel(poseStack, view);
        ItemGeometry.solid(buffers, poseStack, TEXTURE, false,
            sink -> MODEL_DATA.renderSink("effigy", sink, 0xFFFFFFFF, lightCoords));
        poseStack.popPose();
    }

    private static void transformModel(PoseStack poseStack, ThItemView view) {
        view.apply(poseStack);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        switch (view) {
            case INVENTORY -> {
                // center the 1.0-block effigy on the wand band (0.06..1.31)
                poseStack.translate(0.0F, 0.44F, 0.0F);
                poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            }
            case ENTITY -> poseStack.translate(0.0F, 0.0F, 0.0F);
            case EQUIPPED, FIRST_PERSON -> {
                // hold by the middle, like the wand band (0.06..1.31)
                poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
                poseStack.translate(0.0F, 0.44F, 0.0F);
            }
        }
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }
}
