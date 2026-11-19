package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
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

/** Restores the legacy translucent 3D syringe model for all four syringe item families. */
public final class SyringeItemSpecialModelRenderer implements SpecialModelRenderer<Integer> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/syringe.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/syringe.png");
    private static final Vector3f MIN = new Vector3f(-0.93F, -0.18F, -0.18F);
    private static final Vector3f MAX = new Vector3f(0.93F, 0.30F, 0.30F);

    private final ObjModel model = ObjModel.get(MODEL);

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
        // ModelSyringe's old inventory renderer used scale 0.125 and y=-2.75.
        poseStack.scale(0.125F, 0.125F, 0.125F);
        poseStack.translate(0.0F, -22.0F, 0.0F);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(TEXTURE), (pose, buffer) -> {
            model.renderPart("Body", pose, buffer, bodyColor, lightCoords);
            model.renderPart("Needle", pose, buffer, 0xFFFFFFFF, lightCoords);
            model.renderPart("PlungerA", pose, buffer, 0xFFFFFFFF, lightCoords);
            model.renderPart("PlungerB", pose, buffer, 0xFFFFFFFF, lightCoords);
        });
        poseStack.popPose();
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Integer> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Integer> bake(SpecialModelRenderer.BakingContext context) {
            return new SyringeItemSpecialModelRenderer();
        }
    }
}
