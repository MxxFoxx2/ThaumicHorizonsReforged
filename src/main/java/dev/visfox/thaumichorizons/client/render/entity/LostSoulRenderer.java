package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.entity.LostSoul;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public final class LostSoulRenderer extends EntityRenderer<LostSoul, EntityRenderState> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/misc/soul.png");

    public LostSoulRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.0F;
    }

    @Override
    public EntityRenderState createRenderState() {
        return new EntityRenderState();
    }

    @Override
    public void submit(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float bob = Mth.sin(state.ageInTicks / 6.0F) * 0.15F;
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.45F + bob, 0.0F);
        poseStack.mulPose(camera.orientation);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(TEXTURE), (pose, buffer) -> {
            quad(buffer, pose, 0.55F);
        });
        poseStack.popPose();
        super.submit(state, poseStack, collector, camera);
    }

    private static void quad(VertexConsumer buffer, PoseStack.Pose pose, float size) {
        float half = size * 0.5F;
        vertex(buffer, pose, -half, -half, 0.0F, 0.0F, 1.0F);
        vertex(buffer, pose, half, -half, 0.0F, 1.0F, 1.0F);
        vertex(buffer, pose, half, half, 0.0F, 1.0F, 0.0F);
        vertex(buffer, pose, -half, half, 0.0F, 0.0F, 0.0F);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, float u, float v) {
        buffer.addVertex(pose, x, y, z)
            .setColor(0xFFFFFFFF)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(240)
            .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }
}
