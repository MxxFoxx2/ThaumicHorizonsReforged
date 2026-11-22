package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/** Reuses the ModelBiped-derived Corpse Effigy mesh for item, ground and hand rendering. */
public final class CorpseEffigyItemSpecialModelRenderer implements SpecialModelRenderer<Boolean> {
    private static final Identifier MODEL = ThaumicHorizons.id("textures/models/corpseeffigy.obj");
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/block/corpseeffigy.png");
    private final ObjModel model = ObjModel.get(MODEL);

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
        poseStack.rotateDegrees(Axis.ZP, 180.0F);
        poseStack.rotateDegrees(Axis.YP, 180.0F);
        // Match the legacy GUI/hand path: 0.125 ModelBiped scale, halved by the item renderer.
        poseStack.translate(0.0F, -14.4F, 0.0F);
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(TEXTURE),
            (pose, buffer) -> model.renderPart("effigy", pose, buffer, 0xFFFFFFFF, lightCoords));
        poseStack.popPose();
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<Boolean> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<Boolean> bake(SpecialModelRenderer.BakingContext context) {
            return new CorpseEffigyItemSpecialModelRenderer();
        }
    }
}
