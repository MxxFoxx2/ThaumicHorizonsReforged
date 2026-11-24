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

import java.util.function.Consumer;

/**
 * Restores the legacy translucent syringe model using its original per-context item transforms.
 * The OBJ retains the ModelSyringe axes: the needle points toward -X and the plunger toward +X.
 */
public final class SyringeItemSpecialModelRenderer implements SpecialModelRenderer<Integer> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/syringe.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    /** ModelSyringe.render used 0.125 for all four parts. */
    private static final float MODEL_SCALE = 0.125F;

    private final ObjModel model = ObjModel.get(MODEL);
    private final ThItemView view;

    public SyringeItemSpecialModelRenderer(ThItemView view) {
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


    public record Unbaked(ThItemView view) implements SpecialModelRenderer.Unbaked<Integer> {
        public static final MapCodec<Unbaked> MAP_CODEC = ThItemView.FIELD.xmap(Unbaked::new, Unbaked::view);

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
