package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.client.geom.FacingQuads;
import thaumcraft.client.geom.QuadSink;
import thaumcraft.client.platform.EntityQuadSink;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import thaumcraft.client.platform.TcRenderTypes;

final class MachineRenderHelper {
    static final ObjModel SCANNER = ObjModel.get(ResourceLocation.fromNamespaceAndPath("thaumcraft", "models/obj/scanner.obj"));
    static final ResourceLocation LIGHTNING_RING = ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/item/lightningringv.png");
    static final ResourceLocation NODES = ResourceLocation.fromNamespaceAndPath("thaumcraft", "textures/misc/nodes.png");

    private MachineRenderHelper() {
    }

    static void scanner(PoseStack poseStack, ThImmediateGeometry collector,
        ResourceLocation texture, float rise, float rotation, float rotation2, boolean active, int color, int light, int frame) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.2F + rise, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotation2));
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(texture),
            (pose, buffer) -> SCANNER.renderAll(pose, buffer, 0xFFFFFFFF, light));
        poseStack.mulPose(Axis.XP.rotationDegrees(-2.0F * rotation2));
        poseStack.scale(0.5F, 1.0F, 0.5F);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(texture),
            (pose, buffer) -> SCANNER.renderAll(pose, buffer, 0xFFFFFFFF, light));
        poseStack.popPose();
        if (active) {
            frame(poseStack, collector, LIGHTNING_RING, 0.5F, 0.5F, 0.5F, 0.9F,
                16, frame, color, light);
        }
    }

    static void frame(PoseStack poseStack, ThImmediateGeometry collector,
        ResourceLocation texture, float x, float y, float z, float scale, int frames, int frame, int color, int light) {
        collector.submitCustomGeometry(poseStack, TcRenderTypes.additive(texture), (pose, buffer) -> {
            QuadSink sink = new EntityQuadSink(pose, buffer);
            FacingQuads.frame(sink, camera(), x, y, z, 0.0F, scale, frames, frame, color, light);
        });
    }

    static void strip(PoseStack poseStack, ThImmediateGeometry collector,
        ResourceLocation texture, float x, float y, float z, float scale, int frames, int strip, int frame,
        int color, int light) {
        collector.submitCustomGeometry(poseStack, TcRenderTypes.additive(texture), (pose, buffer) -> {
            QuadSink sink = new EntityQuadSink(pose, buffer);
            FacingQuads.strip(sink, camera(), x, y, z, 0.0F, scale, frames, strip, frame, color, light);
        });
    }

    static void rotatingFrame(PoseStack poseStack, ThImmediateGeometry collector,
        ResourceLocation texture, float x, float y, float z, float angle, float scale, int frames, int frame,
        int color, int light) {
        collector.submitCustomGeometry(poseStack, TcRenderTypes.additive(texture), (pose, buffer) -> {
            QuadSink sink = new EntityQuadSink(pose, buffer);
            FacingQuads.frame(sink, camera(), x, y, z, angle, scale, frames, frame, color, light);
        });
    }

    static void particle(PoseStack poseStack, ThImmediateGeometry collector,
        ResourceLocation texture, float x, float y, float z, float scale, float u0, float v0, float u1, float v1,
        int color, int light) {
        collector.submitCustomGeometry(poseStack, TcRenderTypes.additive(texture), (pose, buffer) -> {
            QuadSink sink = new EntityQuadSink(pose, buffer);
            FacingQuads.particle(sink, camera(), x, y, z, scale, u0, v0, u1, v1, color, light);
        });
    }

    private static FacingQuads.Camera camera() {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        return new FacingQuads.Camera(camera.getYRot(), camera.getXRot(),
            (float) camera.getPosition().x, (float) camera.getPosition().y, (float) camera.getPosition().z);
    }
}
