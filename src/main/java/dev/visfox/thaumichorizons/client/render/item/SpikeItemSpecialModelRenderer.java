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
import thaumcraft.client.render.LegacyItemView;

import java.util.function.Consumer;

/**
 * Uses the reference OBJ and matching material texture for Spike block items.
 * The OBJ is legacy-scale (3 units tall, base at y 0): it is scaled to ~1 block and
 * re-based to the "base at origin, +Y up" frame TC4R's {@code WandSpecialRenderer}
 * expects, per legacy view.
 */
public final class SpikeItemSpecialModelRenderer implements SpecialModelRenderer<Integer> {
    private static final Identifier MODEL_ID = ThaumicHorizons.id("textures/models/spike.obj");
    private static final Identifier[] TEXTURES = {
        ThaumicHorizons.id("textures/models/metalspike.png"),
        ThaumicHorizons.id("textures/models/woodenspike.png"),
        ThaumicHorizons.id("textures/models/toothspike.png")
    };
    /** mesh: y 0 (base) .. 3 (tip), x -1.46 .. 1.54, z +-1.3; scale puts 3 units at 1.05 blocks */
    private static final float SCALE = 0.35F;

    private static final Vector3f[] MESH_CORNERS = {
        new Vector3f(-1.46F, 0.0F, -1.29F),
        new Vector3f(-1.46F, 0.0F, 1.30F),
        new Vector3f(-1.46F, 3.0F, -1.29F),
        new Vector3f(-1.46F, 3.0F, 1.30F),
        new Vector3f(1.54F, 0.0F, -1.29F),
        new Vector3f(1.54F, 0.0F, 1.30F),
        new Vector3f(1.54F, 3.0F, -1.29F),
        new Vector3f(1.54F, 3.0F, 1.30F)
    };

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
        for (Vector3f corner : MESH_CORNERS) {
            PoseStack poseStack = new PoseStack();
            transformModel(poseStack);
            output.accept(poseStack.last().pose().transformPosition(corner.x, corner.y, corner.z, new Vector3f()));
        }
    }

    @Override
    public void submit(@Nullable Integer spikeType, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (spikeType == null || spikeType < 0 || spikeType >= TEXTURES.length) {
            return;
        }

        poseStack.pushPose();
        transformModel(poseStack);

        // spike.obj is triangle-only: EntityQuadSink derives normals from a duplicated
        // fourth vertex and would render zero normals, so emit the OBJ faces directly
        // with their authored normals and no backface culling.
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(TEXTURES[spikeType]),
            (pose, buffer) -> model.renderAll(pose, buffer, 0xFFFFFFFF, lightCoords));
        poseStack.popPose();
    }

    private void transformModel(PoseStack poseStack) {
        view.apply(poseStack);
        poseStack.scale(SCALE, SCALE, SCALE);
        poseStack.rotateDegrees(Axis.YP, 90.0F);
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
