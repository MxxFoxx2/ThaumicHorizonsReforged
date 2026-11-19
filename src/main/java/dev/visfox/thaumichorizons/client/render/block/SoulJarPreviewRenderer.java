package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.SoulJarBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.item.SoulJarKeys.SoulJarData;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.lib.SoulJarFrames;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

/** Recreates TileJarTHRenderer's 0.25-scale creature preview and animated soul quad. */
public final class SoulJarPreviewRenderer implements BlockEntityRenderer<SoulJarBlockEntity, SoulJarPreviewRenderer.State> {
    private static final Identifier SOUL_TEXTURE = ThaumicHorizons.id("textures/misc/soul.png");

    private final EntityRenderDispatcher entityRenderer;
    private final Map<SoulJarBlockEntity, CachedPreview> previews = new WeakHashMap<>();

    public SoulJarPreviewRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    public static final class State extends BlockEntityRenderState {
        public EntityRenderState preview;
        public boolean soul;
        public int frame;
    }

    private record CachedPreview(CompoundTag data, LivingEntity entity) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SoulJarBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        state.preview = null;
        state.soul = false;
        state.frame = SoulJarFrames.frame(System.nanoTime());

        SoulJarData data = entity.jarData;
        if (data == null) {
            previews.remove(entity);
            return;
        }
        if (data.soul()) {
            state.soul = true;
            previews.remove(entity);
            return;
        }
        if (!(entity.getLevel() instanceof ClientLevel clientLevel)) {
            return;
        }

        CachedPreview cached = previews.get(entity);
        if (cached == null || !cached.data().equals(data.entity())) {
            Entity created = EntityNbt.load(data.entity(), clientLevel);
            if (!(created instanceof LivingEntity living)) {
                previews.remove(entity);
                return;
            }
            BlockPos pos = entity.getBlockPos();
            living.snapTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
            cached = new CachedPreview(data.entity().copy(), living);
            previews.put(entity, cached);
        }
        state.preview = entityRenderer.extractEntity(cached.entity(), partialTicks);
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector,
        net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        if (state.soul) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.4F, 0.5F);
            poseStack.rotate(camera.orientation);
            int frame = state.frame;
            collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(SOUL_TEXTURE),
                (pose, buffer) -> renderSoul(pose, buffer, frame));
            poseStack.popPose();
            return;
        }

        if (state.preview == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.01F, 0.5F);
        poseStack.scale(0.25F, 0.25F, 0.25F);
        entityRenderer.submit(state.preview, camera, 0.0F, 0.0F, 0.0F, poseStack, collector);
        poseStack.popPose();
    }

    private static void renderSoul(PoseStack.Pose pose, VertexConsumer buffer, int frame) {
        float u0 = (float) frame / SoulJarFrames.FRAME_COUNT;
        float u1 = (float) (frame + 1) / SoulJarFrames.FRAME_COUNT;
        vertex(pose, buffer, -0.4F, -0.4F, u0, 1.0F);
        vertex(pose, buffer, 0.4F, -0.4F, u1, 1.0F);
        vertex(pose, buffer, 0.4F, 0.4F, u1, 0.0F);
        vertex(pose, buffer, -0.4F, 0.4F, u0, 0.0F);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float u, float v) {
        buffer.addVertex(pose, x, y, 0.0F)
            .setColor(0xFFFFFFFF)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(LightCoordsUtil.FULL_BRIGHT)
            .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
