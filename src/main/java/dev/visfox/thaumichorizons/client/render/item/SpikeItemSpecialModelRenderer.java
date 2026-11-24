package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.SpikeBlock;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

/**
 * Uses the reference OBJ and matching material texture for Spike block items.
 * In GUI and hand views, the view frame presents the legacy +Y direction inverted,
 * so the mesh is flipped around its midpoint and centered in the visible item band.
 */
public final class SpikeItemSpecialModelRenderer {
    private static final ResourceLocation MODEL_ID = ThaumicHorizons.id("textures/models/spike.obj");
    private static final ResourceLocation[] TEXTURES = {
        ThaumicHorizons.id("textures/models/metalspike.png"),
        ThaumicHorizons.id("textures/models/woodenspike.png"),
        ThaumicHorizons.id("textures/models/toothspike.png")
    };
    private static final float SCALE = 0.35F;

    private static final ObjModel MODEL_DATA = ObjModel.get(MODEL_ID);

    private SpikeItemSpecialModelRenderer() {
    }

    public static void render(ItemStack stack, ThItemView view, PoseStack poseStack,
        MultiBufferSource buffers, int lightCoords, int overlayCoords) {
        Integer spikeType = extractArgument(stack);
        if (spikeType == null || spikeType < 0 || spikeType >= TEXTURES.length) {
            return;
        }

        poseStack.pushPose();
        transformModel(poseStack, view);
        // spike.obj is triangle-only: EntityQuadSink derives normals from a duplicated
        // fourth vertex and would render zero normals, so emit the OBJ faces directly
        // with their authored normals and no backface culling.
        MODEL_DATA.renderAll(poseStack.last(),
            buffers.getBuffer(ThRenderTypes.entityCutoutNoCull(TEXTURES[spikeType])), 0xFFFFFFFF, lightCoords);
        poseStack.popPose();
    }

    private static Integer extractArgument(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem
            && blockItem.getBlock() instanceof SpikeBlock spikeBlock) {
            return spikeBlock.getSpikeType();
        }
        return null;
    }

    private static void transformModel(PoseStack poseStack, ThItemView view) {
        view.apply(poseStack);
        switch (view) {
            case INVENTORY -> {
                poseStack.translate(0.0F, -0.55F, 0.0F);
                poseStack.scale(SCALE, SCALE, SCALE);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case EQUIPPED, FIRST_PERSON -> {
                poseStack.translate(0.0F, -3.0F, 0.0F);
                poseStack.scale(SCALE, SCALE, SCALE);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
            case ENTITY -> {
                poseStack.scale(SCALE, SCALE, SCALE);
                poseStack.translate(0.0F, -1.45F, 0.0F);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            }
        }
    }
}
