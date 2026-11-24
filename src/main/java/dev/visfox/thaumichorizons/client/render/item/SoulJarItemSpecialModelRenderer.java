package dev.visfox.thaumichorizons.client.render.item;

import dev.visfox.thaumichorizons.lib.EntityPlacement;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.item.SoulJarKeys;
import dev.visfox.thaumichorizons.item.SoulJarKeys.SoulJarData;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.lib.SoulJarFrames;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/** Adds the legacy creature or animated soul preview inside a Soul Jar ItemStack. */
public final class SoulJarItemSpecialModelRenderer {
    private static final ResourceLocation SOUL_TEXTURE = ThaumicHorizons.id("textures/misc/soul.png");
    private static final Map<SoulJarData, LivingEntity> PREVIEW_CACHE = new WeakHashMap<>();

    private SoulJarItemSpecialModelRenderer() {
    }

    public static void render(ItemStack stack, ThItemView view, PoseStack poseStack,
        MultiBufferSource buffers, int lightCoords, int overlayCoords) {
        SoulJarData data = SoulJarKeys.DATA.get(stack);
        if (data == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (data.soul()) {
            int frame = SoulJarFrames.frame(System.nanoTime());
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.4F, 0.5F);
            poseStack.mulPose(minecraft.gameRenderer.getMainCamera().rotation());
            renderSoul(poseStack, buffers.getBuffer(ThRenderTypes.textSeeThrough(SOUL_TEXTURE)), frame, lightCoords);
            poseStack.popPose();
            return;
        }

        if (!(minecraft.level instanceof ClientLevel clientLevel)) {
            return;
        }
        LivingEntity preview = PREVIEW_CACHE.get(data);
        if (preview == null) {
            Entity loaded = EntityNbt.load(data.entity(), clientLevel);
            if (!(loaded instanceof LivingEntity living)) {
                return;
            }
            EntityPlacement.snapTo(living, 0.0, 0.0, 0.0, 0.0F, 0.0F);
            preview = living;
            PREVIEW_CACHE.put(data, preview);
        }

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.01F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.scale(0.25F, 0.25F, 0.25F);
        minecraft.getEntityRenderDispatcher().getRenderer(preview)
            .render(preview, 0.0F, 0.0F, poseStack, buffers, lightCoords);
        poseStack.popPose();
    }

    private static void renderSoul(PoseStack poseStack, VertexConsumer buffer, int frame, int light) {
        float u0 = (float) frame / SoulJarFrames.FRAME_COUNT;
        float u1 = (float) (frame + 1) / SoulJarFrames.FRAME_COUNT;
        PoseStack.Pose pose = poseStack.last();
        vertex(pose, buffer, -0.15F, -0.45F, u0, 1.0F, light);
        vertex(pose, buffer, 0.15F, -0.45F, u1, 1.0F, light);
        vertex(pose, buffer, 0.15F, 0.45F, u1, 0.0F, light);
        vertex(pose, buffer, -0.15F, 0.45F, u0, 0.0F, light);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer,
        float x, float y, float u, float v, int light) {
        buffer.addVertex(pose, x, y, 0.0F)
            .setColor(0xFFFFFFFF)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}
