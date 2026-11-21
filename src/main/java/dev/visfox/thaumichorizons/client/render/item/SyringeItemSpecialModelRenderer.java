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
 * The OBJ (long axis X, centered at origin) is stood upright and re-based to the same
 * "base at origin, +Y up" frame TC4R's {@code WandSpecialRenderer} expects, then placed
 * per legacy view: inventory isometric, held in hand, ground entity.
 */
public final class SyringeItemSpecialModelRenderer implements SpecialModelRenderer<Integer> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/syringe.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    private static final Vector3f MIN = new Vector3f(-0.93F, -0.18F, -0.18F);
    private static final Vector3f MAX = new Vector3f(0.93F, 0.30F, 0.30F);
    private static final float SCALE = 0.7F;

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

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(MIN.x, MIN.y, MIN.z));
        output.accept(new Vector3f(MIN.x, MIN.y, MAX.z));
        output.accept(new Vector3f(MIN.x, MAX.y, MIN.z));
        output.accept(new Vector3f(MIN.x, MAX.y, MAX.z));
        output.accept(new Vector3f(MAX.x, MIN.y, MIN.z));
        output.accept(new Vector3f(MAX.x, MIN.y, MAX.z));
        output.accept(new Vector3f(MAX.x, MAX.y, MIN.z));
        output.accept(new Vector3f(MAX.x, MAX.y, MAX.z));
    }

    @Override
    public void submit(@Nullable Integer bodyColor, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (bodyColor == null) {
            return;
        }

        poseStack.pushPose();
        transform(poseStack);
        // stand the X-long needle up and put its base on the frame origin
        poseStack.rotateDegrees(Axis.ZP, 90.0F);
        poseStack.scale(SCALE, SCALE, SCALE);
        poseStack.translate(0.0F, -MIN.x, 0.0F);
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
