package dev.visfox.thaumichorizons.client.render.block;

import dev.visfox.thaumichorizons.lib.EntityPlacement;

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
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import dev.visfox.thaumichorizons.client.platform.ThBlockEntityRenderState;
import dev.visfox.thaumichorizons.client.platform.ThCameraRenderState;
import dev.visfox.thaumichorizons.client.platform.ThImmediateGeometry;
import dev.visfox.thaumichorizons.client.platform.ThStateBlockRenderer;

/** Recreates TileJarTHRenderer's 0.25-scale creature preview and animated soul quad. */
public final class SoulJarPreviewRenderer implements ThStateBlockRenderer<SoulJarBlockEntity, SoulJarPreviewRenderer.State> {
    private static final ResourceLocation SOUL_TEXTURE = ThaumicHorizons.id("textures/misc/soul.png");

    private final EntityRenderDispatcher entityRenderer;
    private final Map<SoulJarBlockEntity, CachedPreview> previews = new WeakHashMap<>();

    public SoulJarPreviewRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.getEntityRenderer();
    }

    public static final class State extends ThBlockEntityRenderState {
        public LivingEntity preview;
        public boolean soul;
        public int frame;
        public float partialTicks;
    }

    private record CachedPreview(CompoundTag data, LivingEntity entity) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(SoulJarBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, Object progress) {
        state.preview = null;
        state.soul = false;
        state.frame = SoulJarFrames.frame(System.nanoTime());
        state.partialTicks = partialTicks;

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
            EntityPlacement.snapTo(living, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, 0.0F);
            cached = new CachedPreview(data.entity().copy(), living);
            previews.put(entity, cached);
        }
        state.preview = cached.entity();
    }

    @Override
    public void submit(State state, PoseStack poseStack, ThImmediateGeometry collector, ThCameraRenderState camera) {
        if (state.soul) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.4F, 0.5F);
            poseStack.mulPose(camera.orientation);
            int frame = state.frame;
            // Legacy disables depth testing for this quad so the soul stays visible through the jar.
            collector.submitCustomGeometry(poseStack, ThRenderTypes.textSeeThrough(SOUL_TEXTURE),
                (pose, buffer) -> renderSoul(pose, buffer, frame, state.lightCoords));
            poseStack.popPose();
            return;
        }

        if (state.preview == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.01F, 0.5F);
        poseStack.scale(0.25F, 0.25F, 0.25F);
        entityRenderer.render(state.preview, 0.0, 0.0, 0.0, 0.0F, state.partialTicks,
            poseStack, collector.buffers, state.lightCoords);
        poseStack.popPose();
    }

    private static void renderSoul(PoseStack.Pose pose, VertexConsumer buffer, int frame, int light) {
        float u0 = (float) frame / SoulJarFrames.FRAME_COUNT;
        float u1 = (float) (frame + 1) / SoulJarFrames.FRAME_COUNT;
        vertex(pose, buffer, -0.4F, -0.4F, u0, 1.0F, light);
        vertex(pose, buffer, 0.4F, -0.4F, u1, 1.0F, light);
        vertex(pose, buffer, 0.4F, 0.4F, u1, 0.0F, light);
        vertex(pose, buffer, -0.4F, 0.4F, u0, 0.0F, light);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, float x, float y, float u, float v, int light) {
        buffer.addVertex(pose, x, y, 0.0F)
            .setColor(0xFFFFFFFF)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
