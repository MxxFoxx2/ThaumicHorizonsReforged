package dev.visfox.thaumichorizons.client.platform;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

/**
 * Keeps the legacy extract/submit structure used by TH's block entity renderers
 * while implementing the 1.21.1 {@link BlockEntityRenderer} contract.
 */
public interface ThStateBlockRenderer<T extends BlockEntity, S extends ThBlockEntityRenderState>
    extends BlockEntityRenderer<T> {
    S createRenderState();

    default void extractRenderState(T entity, S state, float partial, Vec3 camera, Object progress) {
    }

    void submit(S state, PoseStack poses, ThImmediateGeometry geometry, ThCameraRenderState camera);

    @Override
    default void render(T entity, float partial, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        S state = createRenderState();
        state.lightCoords = light;
        extractRenderState(entity, state, partial,
            Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), null);
        submit(state, poses, new ThImmediateGeometry(buffers), new ThCameraRenderState());
    }
}
