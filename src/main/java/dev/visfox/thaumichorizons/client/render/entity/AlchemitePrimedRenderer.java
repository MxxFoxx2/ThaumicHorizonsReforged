package dev.visfox.thaumichorizons.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.entity.AlchemitePrimedEntity;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlas;

public final class AlchemitePrimedRenderer extends EntityRenderer<AlchemitePrimedEntity> {
    private final BlockRenderDispatcher blockRenderer;
    private final BlockState alchemiteState;

    public AlchemitePrimedRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
        this.blockRenderer = context.getBlockRenderDispatcher();
        this.alchemiteState = ThBlocks.ALCHEMITE.get().defaultBlockState();
    }

    @Override
    public void render(AlchemitePrimedEntity entity, float entityYaw, float partialTick,
        PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.5F, 0.0F);
        int fuse = entity.getFuse();
        float remaining = fuse - partialTick + 1.0F;
        if (remaining < 10.0F) {
            float swell = Mth.clamp(1.0F - remaining / 10.0F, 0.0F, 1.0F);
            swell = swell * swell * swell;
            float scale = 1.0F + swell * 0.3F;
            poseStack.scale(scale, scale, scale);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));
        poseStack.translate(-0.5F, -0.5F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        boolean lit = fuse / 5 % 2 == 0;
        TntMinecartRenderer.renderWhiteSolidBlock(blockRenderer, alchemiteState, poseStack, buffers, packedLight, lit);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffers, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AlchemitePrimedEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
