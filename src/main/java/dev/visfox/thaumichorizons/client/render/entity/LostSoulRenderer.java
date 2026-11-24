package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.entity.LostSoul;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class LostSoulRenderer extends EntityRenderer<LostSoul> {
    private static final ResourceLocation TEXTURE = ThaumicHorizons.id("textures/misc/soul.png");

    public LostSoulRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(LostSoul entity, float entityYaw, float partialTick,
        PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        float bob = Mth.sin((entity.tickCount + partialTick) / 6.0F) * 0.15F;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.45F + bob, 0.0F);
        poseStack.mulPose(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
        collectorGeometry(poseStack, buffers, packedLight);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    private static void collectorGeometry(PoseStack poseStack, MultiBufferSource buffers, int light) {
        VertexConsumer buffer = buffers.getBuffer(ThRenderTypes.entityTranslucent(TEXTURE));
        quad(buffer, poseStack.last(), 0.55F, light);
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose pose, float size, int light) {
        float half = size * 0.5F;
        vertex(buffer, pose, -half, -half, 0.0F, 1.0F, light);
        vertex(buffer, pose, half, -half, 1.0F, 1.0F, light);
        vertex(buffer, pose, half, half, 1.0F, 0.0F, light);
        vertex(buffer, pose, -half, half, 0.0F, 0.0F, light);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float u, float v, int light) {
        buffer.addVertex(pose, x, y, 0.0F)
            .setColor(0xFFFFFFFF)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(LostSoul entity) {
        return TEXTURE;
    }
}
