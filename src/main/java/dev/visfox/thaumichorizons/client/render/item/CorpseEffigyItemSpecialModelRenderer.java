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
 * Reuses the ModelBiped-derived Corpse Effigy mesh for item, ground and hand rendering.
 * The mesh (legacy Y-down, ~2.1 tall) is flipped upright, re-based to the same
 * "base at origin, +Y up" frame TC4R's {@code WandSpecialRenderer} expects and scaled
 * to wand height, then placed per legacy view: inventory isometric, held, ground entity.
 */
public final class CorpseEffigyItemSpecialModelRenderer implements SpecialModelRenderer<Boolean> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/corpseeffigy.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/block/corpseeffigy.png");
    private static final float SCALE = 0.65F;

    private final ObjModel model = ObjModel.get(MODEL);
    private final LegacyItemView view;

    public CorpseEffigyItemSpecialModelRenderer(LegacyItemView view) {
        this.view = view;
    }

    @Override
    public @Nullable Boolean extractArgument(ItemStack stack) {
        return stack.is(ThItems.CORPSE_EFFIGY.get()) ? Boolean.TRUE : null;
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.55F, -1.10F, -0.30F));
        output.accept(new Vector3f(-0.55F, -1.10F, 0.30F));
        output.accept(new Vector3f(-0.55F, 1.00F, -0.30F));
        output.accept(new Vector3f(-0.55F, 1.00F, 0.30F));
        output.accept(new Vector3f(0.55F, -1.10F, -0.30F));
        output.accept(new Vector3f(0.55F, -1.10F, 0.30F));
        output.accept(new Vector3f(0.55F, 1.00F, -0.30F));
        output.accept(new Vector3f(0.55F, 1.00F, 0.30F));
    }

    @Override
    public void submit(@Nullable Boolean effigy, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (effigy == null) {
            return;
        }

        poseStack.pushPose();
        transform(poseStack);
        // the OBJ is legacy Y-down (head at -1.10): flip upright and put the feet on the frame origin
        poseStack.rotateDegrees(Axis.ZP, 180.0F);
        poseStack.rotateDegrees(Axis.YP, 180.0F);
        poseStack.translate(0.0F, 1.00F, 0.0F);
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
