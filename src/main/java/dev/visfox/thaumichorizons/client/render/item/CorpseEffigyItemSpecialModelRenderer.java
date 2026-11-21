package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ItemGeometry;
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
 * Reuses the ModelBiped-derived Corpse Effigy mesh for item, ground and hand rendering.
 * The OBJ is legacy-scale (32 units = 2 blocks, Y-down, head at y -8): it is flipped
 * upright, scaled to wand height and re-based to the "base at origin, +Y up" frame
 * TC4R's {@code WandSpecialRenderer} expects, per legacy view.
 */
public final class CorpseEffigyItemSpecialModelRenderer implements SpecialModelRenderer<Boolean> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/corpseeffigy.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/block/corpseeffigy.png");
    /** mesh: y -8 (head) .. 24 (feet), x +-8, z +-4; scale puts 32 units at 1.4 blocks */
    private static final float SCALE = 0.04375F;

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
            transform(poseStack);
            poseStack.scale(1.0F, -1.0F, 1.0F);
            poseStack.translate(0.0F, 24.0F, 0.0F);
            poseStack.scale(SCALE, SCALE, SCALE);
            output.accept(corner.transform(poseStack.last().pose()));
        }
    }

    @Override
    public void submit(@Nullable Boolean effigy, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (effigy == null) {
            return;
        }

        poseStack.pushPose();
        transform(poseStack);
        // the OBJ is legacy Y-down with the head above the origin: flip upright, feet on the frame origin
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.translate(0.0F, 24.0F, 0.0F);
        poseStack.scale(SCALE, SCALE, SCALE);
        ItemGeometry.solid(collector.order(0), poseStack, TEXTURE, false,
            sink -> model.renderSink("effigy", sink, 0xFFFFFFFF, lightCoords));
        poseStack.popPose();
    }

    private void transform(PoseStack poseStack) {
        view.apply(poseStack);
        switch (view) {
            case INVENTORY -> poseStack.rotateDegrees(Axis.ZP, 66.0F);
            case EQUIPPED, FIRST_PERSON -> poseStack.translate(0.5F, 1.5F, 0.5F);
            case ENTITY -> poseStack.translate(0.0F, 1.0F, 0.0F);
        }
        poseStack.rotateDegrees(Axis.XP, 180.0F);
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
