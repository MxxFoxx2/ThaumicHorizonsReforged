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
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import thaumcraft.client.render.LegacyItemView;

import java.util.function.Consumer;

/**
 * Restores the legacy translucent 3D syringe model for all four syringe item families.
 * The OBJ is legacy-scale (15 units long on X, lying at y 21..24): it is stood up on its
 * base (plunger at +X, needle at -X), scaled to wand height and re-based to the
 * "base at origin, +Y up" frame TC4R's {@code WandSpecialRenderer} expects, per legacy view.
 */
public final class SyringeItemSpecialModelRenderer implements SpecialModelRenderer<Integer> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/syringe.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    /** mesh: x -7..-3 (needle) .. 6..8 (plunger), 15 units long; scale puts it at 1.3 blocks */
    private static final float SCALE = 0.0867F;

    private final ObjModel model = ObjModel.get(MODEL);
    private final LegacyItemView view;

    public SyringeItemSpecialModelRenderer(LegacyItemView view) {
        this.view = view;
    }

    @Override
    public @Nullable Integer extractArgument(ItemStack stack) {
        if (stack.is(ThItems.SYRINGE_EMPTY.get())) {
            return 0x80FFFFFF;
        }
        if (stack.is(ThItems.SYRINGE_BLOOD.get()) || stack.is(ThItems.SYRINGE_BLOOD_SAMPLE.get())) {
            return 0xBFFF0000;
        }
        if (stack.is(ThItems.SYRINGE_INJECTION.get())) {
            PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
            int rgb = contents == null ? PotionContents.BASE_POTION_COLOR : contents.getColor();
            return 0xBF000000 | (rgb & 0x00FFFFFF);
        }
        return null;
    }

    private static final Vector3f[] MESH_CORNERS = {
        new Vector3f(-7.0F, 21.0F, -1.0F),
        new Vector3f(-7.0F, 21.0F, 2.0F),
        new Vector3f(-7.0F, 24.0F, -1.0F),
        new Vector3f(-7.0F, 24.0F, 2.0F),
        new Vector3f(8.0F, 21.0F, -1.0F),
        new Vector3f(8.0F, 21.0F, 2.0F),
        new Vector3f(8.0F, 24.0F, -1.0F),
        new Vector3f(8.0F, 24.0F, 2.0F)
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
        transform(poseStack);
        // +X is the plunger end; center the OBJ cross-section and place x=8 at the frame origin.
        poseStack.rotateDegrees(Axis.ZP, 90.0F);
        poseStack.scale(SCALE, SCALE, SCALE);
        poseStack.translate(-8.0F, -22.5F, -0.5F);
    }

    @Override
    public void submit(@Nullable Integer bodyColor, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (bodyColor == null) {
            return;
        }

        poseStack.pushPose();
        transformModel(poseStack);
        ItemGeometry.solid(collector.order(0), poseStack, TEXTURE, true,
            sink -> model.renderSink("Body", sink, bodyColor, lightCoords));
        ItemGeometry.solid(collector.order(1), poseStack, TEXTURE, false,
            sink -> {
                model.renderSink("Needle", sink, 0xFFFFFFFF, lightCoords);
                model.renderSink("PlungerA", sink, 0xFFFFFFFF, lightCoords);
                model.renderSink("PlungerB", sink, 0xFFFFFFFF, lightCoords);
            });
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

    public record Unbaked(LegacyItemView view) implements SpecialModelRenderer.Unbaked<Integer> {
        public static final MapCodec<Unbaked> MAP_CODEC = LegacyItemView.FIELD.xmap(Unbaked::new, Unbaked::view);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Integer> bake(SpecialModelRenderer.BakingContext context) {
            return new SyringeItemSpecialModelRenderer(view);
        }
    }
}
