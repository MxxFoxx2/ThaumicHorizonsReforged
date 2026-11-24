package dev.visfox.thaumichorizons.client.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.BiConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

/** 1.21.1 bridge for the legacy submit-node style geometry callbacks. */
public final class ThImmediateGeometry {
    public final MultiBufferSource buffers;

    public ThImmediateGeometry(MultiBufferSource buffers) {
        this.buffers = buffers;
    }

    public void submitCustomGeometry(PoseStack poses, RenderType type,
        BiConsumer<PoseStack.Pose, VertexConsumer> geometry) {
        geometry.accept(poses.last(), buffers.getBuffer(type));
    }
}
