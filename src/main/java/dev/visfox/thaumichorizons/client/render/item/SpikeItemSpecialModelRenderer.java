package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.SpikeBlock;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Uses the reference OBJ and matching material texture for Spike block items.
 * The mesh uses the same scale and vertical offset as the legacy inventory renderer.
 */
public final class SpikeItemSpecialModelRenderer implements SpecialModelRenderer<Integer> {
    private static final Identifier MODEL_ID = ThaumicHorizons.id("textures/models/spike.obj");
    private static final Identifier[] TEXTURES = {
        ThaumicHorizons.id("textures/models/metalspike.png"),
        ThaumicHorizons.id("textures/models/woodenspike.png"),
        ThaumicHorizons.id("textures/models/toothspike.png")
    };

    private static final float MIN_X = -0.37F;
    private static final float MAX_X = 0.47F;
    private static final float MIN_Y = -0.52F;
    private static final float MAX_Y = 0.55F;
    private static final float MIN_Z = -0.44F;
    private static final float MAX_Z = 0.41F;

    private final ObjModel model = ObjModel.get(MODEL_ID);

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
        output.accept(new Vector3f(MIN_X, MIN_Y, MIN_Z));
        output.accept(new Vector3f(MIN_X, MIN_Y, MAX_Z));
        output.accept(new Vector3f(MIN_X, MAX_Y, MIN_Z));
        output.accept(new Vector3f(MIN_X, MAX_Y, MAX_Z));
        output.accept(new Vector3f(MAX_X, MIN_Y, MIN_Z));
        output.accept(new Vector3f(MAX_X, MIN_Y, MAX_Z));
        output.accept(new Vector3f(MAX_X, MAX_Y, MIN_Z));
        output.accept(new Vector3f(MAX_X, MAX_Y, MAX_Z));
    }

    @Override
    public void submit(@Nullable Integer spikeType, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (spikeType == null || spikeType < 0 || spikeType >= TEXTURES.length) {
            return;
        }

        poseStack.pushPose();
        poseStack.rotateDegrees(Axis.YP, 90.0F);
        poseStack.scale(0.35F, 0.35F, 0.35F);
        poseStack.translate(0.0F, -1.45F, 0.0F);

        Identifier texture = TEXTURES[spikeType];
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(texture),
            (pose, buffer) -> model.renderAll(pose, buffer, 0xFFFFFFFF, lightCoords));
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
            return new SpikeItemSpecialModelRenderer();
        }
    }
}
