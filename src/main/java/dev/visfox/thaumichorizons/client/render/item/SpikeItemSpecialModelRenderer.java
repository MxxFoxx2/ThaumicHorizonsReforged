package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.SpikeBlock;
import thaumcraft.client.platform.ItemGeometry;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;
import thaumcraft.client.render.LegacyItemView;

import java.util.function.Consumer;

/**
 * Uses the reference OBJ and matching material texture for Spike block items.
 * The mesh (centered, ~1.05 tall) is re-based to the same "base at origin, +Y up"
 * frame TC4R's {@code WandSpecialRenderer} expects, then placed per legacy view:
 * inventory isometric, held in hand, ground entity.
 */
public final class SpikeItemSpecialModelRenderer implements SpecialModelRenderer<Integer> {
    private static final Identifier MODEL_ID = ThaumicHorizons.id("textures/models/spike.obj");
    private static final Identifier[] TEXTURES = {
        ThaumicHorizons.id("textures/models/metalspike.png"),
        ThaumicHorizons.id("textures/models/woodenspike.png"),
        ThaumicHorizons.id("textures/models/toothspike.png")
    };

    private static final float MIN_Y = -0.5075F;
    private static final float MAX_Y = 0.5425F;

    private final ObjModel model = ObjModel.get(MODEL_ID);
    private final LegacyItemView view;

    public SpikeItemSpecialModelRenderer(LegacyItemView view) {
        this.view = view;
    }

    @Override
    public @Nullable Integer extractArgument(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem
            && blockItem.getBlock() instanceof SpikeBlock spikeBlock) {
            return spikeBlock.getSpikeType();
        }
        return null;
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.45304F, MIN_Y, -0.539455F));
        output.accept(new Vector3f(-0.45304F, MIN_Y, 0.510545F));
        output.accept(new Vector3f(-0.45304F, MAX_Y, -0.539455F));
        output.accept(new Vector3f(-0.45304F, MAX_Y, 0.510545F));
        output.accept(new Vector3f(0.456295F, MIN_Y, -0.539455F));
        output.accept(new Vector3f(0.456295F, MIN_Y, 0.510545F));
        output.accept(new Vector3f(0.456295F, MAX_Y, -0.539455F));
        output.accept(new Vector3f(0.456295F, MAX_Y, 0.510545F));
    }

    @Override
    public void submit(@Nullable Integer spikeType, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (spikeType == null || spikeType < 0 || spikeType >= TEXTURES.length) {
            return;
        }

        poseStack.pushPose();
        transform(poseStack);
        // legacy mesh: centered at origin, so shift its base onto the frame origin
        poseStack.translate(0.0F, -MIN_Y, 0.0F);
        poseStack.rotateDegrees(Axis.YP, 90.0F);

        ItemGeometry.solid(collector.order(0), poseStack, TEXTURES[spikeType], false,
            sink -> model.renderSink(sink, 0xFFFFFFFF, lightCoords));
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
            return new SpikeItemSpecialModelRenderer(view);
        }
    }
}
