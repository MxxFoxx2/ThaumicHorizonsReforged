package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import thaumcraft.client.platform.ItemGeometry;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import thaumcraft.client.render.LegacyItemView;

import java.util.function.Consumer;

/**
 * Reuses the ModelBiped-derived Corpse Effigy mesh with the original legacy item transforms.
 * The OBJ keeps ModelBiped's Y-down coordinates; the source renderer's two 180-degree
 * rotations and per-context offsets establish its upright pose and placement.
 */
public final class CorpseEffigyItemSpecialModelRenderer implements SpecialModelRenderer<Boolean> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/corpseeffigy.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/block/corpseeffigy.png");
    /** ModelBiped.render used 0.125; the item renderer applied an additional 0.5 in GUI and hands. */
    private static final float MODEL_SCALE = 0.125F;
    private static final float ITEM_SCALE = 0.5F;

    private final ObjModel model = ObjModel.get(MODEL);
    private final LegacyItemView view;

    public CorpseEffigyItemSpecialModelRenderer(LegacyItemView view) {
        this.view = view;
    }

    @Override
    public @Nullable Boolean extractArgument(ItemStack stack) {
        return stack.is(ThItems.CORPSE_EFFIGY.get()) ? Boolean.TRUE : null;
    }

    private static final Vector3f[] MESH_CORNERS = {
        new Vector3f(-8.0F, -8.0F, -4.0F),
        new Vector3f(-8.0F, -8.0F, 4.0F),
        new Vector3f(-8.0F, 24.0F, -4.0F),
        new Vector3f(-8.0F, 24.0F, 4.0F),
        new Vector3f(8.0F, -8.0F, -4.0F),
        new Vector3f(8.0F, -8.0F, 4.0F),
        new Vector3f(8.0F, 24.0F, -4.0F),
        new Vector3f(8.0F, 24.0F, 4.0F)
    };

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        for (Vector3f corner : MESH_CORNERS) {
            PoseStack poseStack = new PoseStack();
            transformModel(poseStack);
            output.accept(poseStack.last().pose().transformPosition(corner.x, corner.y, corner.z, new Vector3f()));
        }
    }

    private void transformModel(PoseStack poseStack) {
        view.apply(poseStack);
        poseStack.rotateDegrees(Axis.ZP, 180.0F);
        poseStack.rotateDegrees(Axis.YP, 180.0F);
        switch (view) {
            case INVENTORY -> {
                poseStack.translate(0.0F, -0.9F, 0.0F);
                poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
            }
            case ENTITY -> poseStack.translate(0.0F, -3.0F, 0.0F);
            case EQUIPPED, FIRST_PERSON -> {
                poseStack.scale(ITEM_SCALE, ITEM_SCALE, ITEM_SCALE);
                poseStack.translate(-1.0F, -4.0F, -1.0F);
            }
        }
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);
    }

    @Override
    public void submit(@Nullable Boolean effigy, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (effigy == null) {
            return;
        }

        poseStack.pushPose();
        transformModel(poseStack);
        ItemGeometry.solid(collector.order(0), poseStack, TEXTURE, false,
            sink -> model.renderSink("effigy", sink, 0xFFFFFFFF, lightCoords));
        poseStack.popPose();
    }


    public record Unbaked(LegacyItemView view) implements SpecialModelRenderer.Unbaked<Boolean> {
        public static final MapCodec<Unbaked> MAP_CODEC = LegacyItemView.FIELD.xmap(Unbaked::new, Unbaked::view);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Boolean> bake(SpecialModelRenderer.BakingContext context) {
            return new CorpseEffigyItemSpecialModelRenderer(view);
        }
    }
}
