package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import dev.visfox.thaumichorizons.item.SyringeKeys;
import dev.visfox.thaumichorizons.item.SyringeKeys.InjectorVisualData;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Rebuilds the original nine-part ModelInjector with its draw and cylinder animation. */
public final class InjectorItemSpecialModelRenderer {
    private static final ResourceLocation MODEL_ID = ThaumicHorizons.id("textures/models/injector.obj");
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/models/injector.png");
    private static final float MODEL_SCALE = 0.125F;

    private static final ObjModel MODEL_DATA = ObjModel.get(MODEL_ID);

    private InjectorItemSpecialModelRenderer() {
    }

    public record State(float draw, float cylinderRotation) {
    }

    public static void render(ItemStack stack, ThItemView view, PoseStack poseStack,
        MultiBufferSource buffers, int lightCoords, int overlayCoords) {
        State state = extractArgument(stack);
        if (state == null) {
            return;
        }

        float draw = state.draw();
        float drawAngle1 = draw * 11.25F;
        float drawAngle2 = draw * 45.0F;

        poseStack.pushPose();
        // Keep the legacy orientation while letting the active item display transform
        // control inventory, ground, first-person and third-person placement.
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate(0.38F, -0.1F, 0.38F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

        renderPart("Drum", 2.5F, 2.25F, 0.0F, 0.0F, 0.0F, state.cylinderRotation() * 2.0F,
            poseStack, buffers, lightCoords);
        renderPart("Front", 1.0F, 1.0F, -8.0F, 0.0F, 0.0F, 0.0F,
            poseStack, buffers, lightCoords);
        renderPart("BowL1", 2.5F, 1.0F, -8.0F, 0.0F, -drawAngle1, 0.0F,
            poseStack, buffers, lightCoords);
        renderPart("BowR1", 2.5F, 1.0F, -8.0F, 0.0F, 180.0F + drawAngle1, 0.0F,
            poseStack, buffers, lightCoords);
        renderPart("BowL2", 6.0F, 1.0F, -7.5F, 0.0F, -20.0F - drawAngle2, 0.0F,
            poseStack, buffers, lightCoords);
        renderPart("BowR2", -1.0F, 1.0F, -7.5F, 0.0F, 200.0F + drawAngle2, 0.0F,
            poseStack, buffers, lightCoords);
        renderPart("Thingy", 2.5F, 0.0F, -6.5F + 5.0F * draw, 0.0F, 0.0F, 0.0F,
            poseStack, buffers, lightCoords);
        renderPart("Stock", 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F,
            poseStack, buffers, lightCoords);
        renderPart("Grip", 1.0F, 2.0F, 3.0F, 0.0F, 0.0F, 0.0F,
            poseStack, buffers, lightCoords);
        renderString(poseStack,
            buffers.getBuffer(ThRenderTypes.entityCutoutNoCull(TEXTURE)), draw, lightCoords);
        poseStack.popPose();
    }

    private static State extractArgument(ItemStack stack) {
        if (!stack.is(ThItems.INJECTOR.get())) {
            return null;
        }
        InjectorVisualData visual = SyringeKeys.INJECTOR_VISUAL.getOrDefault(stack, InjectorVisualData.EMPTY);
        int gameTick = Minecraft.getInstance().level == null
            ? visual.rotationTarget()
            : (int) (Minecraft.getInstance().level.getGameTime() & 0x7FFFFFFFL);
        return new State(visual.drawProgress(), visual.rotationAt(gameTick));
    }

    private static void renderPart(String name, float pivotX, float pivotY, float pivotZ,
        float rotateX, float rotateY, float rotateZ, PoseStack poseStack,
        MultiBufferSource buffers, int lightCoords) {
        poseStack.pushPose();
        poseStack.translate(pivotX, pivotY, pivotZ);
        if (rotateZ != 0.0F) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(rotateZ));
        }
        if (rotateY != 0.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(rotateY));
        }
        if (rotateX != 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(rotateX));
        }
        MODEL_DATA.renderPart(name, poseStack.last(),
            buffers.getBuffer(ThRenderTypes.entityCutoutNoCull(TEXTURE)), 0xFFFFFFFF, lightCoords);
        poseStack.popPose();
    }

    private static void renderString(PoseStack poseStack, VertexConsumer buffer, float draw, int light) {
        float rightX = -0.4F + draw * 0.25F;
        float middleX = 0.325F;
        float leftX = 1.05F - draw * 0.25F;
        float endZ = -0.6875F + draw * 0.125F;
        float middleZ = -0.6875F + draw * 0.65F;
        float y = 1.0F - 0.9375F;

        stringSegment(poseStack.last(), buffer, rightX, y, endZ, middleX, y, middleZ, light);
        stringSegment(poseStack.last(), buffer, middleX, y, middleZ, leftX, y, endZ, light);
    }

    private static void stringSegment(PoseStack.Pose pose, VertexConsumer buffer,
        float x1, float y1, float z1, float x2, float y2, float z2, int light) {
        float dx = x2 - x1;
        float dz = z2 - z1;
        float length = (float) Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-5F) {
            return;
        }
        float halfWidth = 0.018F;
        float ox = -dz / length * halfWidth;
        float oz = dx / length * halfWidth;
        float yOffset = 0.018F;
        int color = 0xFF181818;
        float u = 0.25F;
        float v = 0.25F;

        vertex(pose, buffer, x1 + ox, y1 - yOffset, z1 + oz, color, u, v, light);
        vertex(pose, buffer, x2 + ox, y2 - yOffset, z2 + oz, color, u, v, light);
        vertex(pose, buffer, x2 - ox, y2 + yOffset, z2 - oz, color, u, v, light);
        vertex(pose, buffer, x1 - ox, y1 + yOffset, z1 - oz, color, u, v, light);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float z,
        int color, float u, float v, int light) {
        buffer.addVertex(pose, x, y, z)
            .setColor(color)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }
}
