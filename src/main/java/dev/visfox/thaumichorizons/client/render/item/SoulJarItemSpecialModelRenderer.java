package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.item.SoulJarKeys;
import dev.visfox.thaumichorizons.item.SoulJarKeys.SoulJarData;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.lib.SoulJarFrames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/** Adds the legacy creature or animated soul preview inside a Soul Jar ItemStack. */
public final class SoulJarItemSpecialModelRenderer implements SpecialModelRenderer<SoulJarItemSpecialModelRenderer.State> {
    private static final Identifier SOUL_TEXTURE = ThaumicHorizons.id("textures/misc/soul.png");
    private static final Map<SoulJarData, LivingEntity> PREVIEW_CACHE = new WeakHashMap<>();

    public record State(boolean soul, @Nullable EntityRenderState preview, int frame) {
    }

    @Override
    public @Nullable State extractArgument(ItemStack stack) {
        SoulJarData data = SoulJarKeys.DATA.get(stack);
        if (data == null) {
            return null;
        }
        if (data.soul()) {
            return new State(true, null, SoulJarFrames.frame(System.nanoTime()));
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (!(minecraft.level instanceof ClientLevel clientLevel)) {
            return null;
        }

        LivingEntity preview = PREVIEW_CACHE.get(data);
        if (preview == null) {
            Entity loaded = EntityNbt.load(data.entity(), clientLevel);
            if (!(loaded instanceof LivingEntity living)) {
                return null;
            }
            living.snapTo(0.0, 0.0, 0.0, 0.0F, 0.0F);
            preview = living;
            PREVIEW_CACHE.put(data, preview);
        }

        EntityRenderDispatcher dispatcher = minecraft.getEntityRenderDispatcher();
        EntityRenderState renderState = dispatcher.extractEntity(preview, 0.0F);
        return new State(false, renderState, SoulJarFrames.frame(System.nanoTime()));
    }

    @Override
    public void getExtents(Consumer<Vector3fc> output) {
        output.accept(new Vector3f(-0.5F, -0.5F, -0.5F));
        output.accept(new Vector3f(-0.5F, -0.5F, 0.5F));
        output.accept(new Vector3f(-0.5F, 0.5F, -0.5F));
        output.accept(new Vector3f(-0.5F, 0.5F, 0.5F));
        output.accept(new Vector3f(0.5F, -0.5F, -0.5F));
        output.accept(new Vector3f(0.5F, -0.5F, 0.5F));
        output.accept(new Vector3f(0.5F, 0.5F, -0.5F));
        output.accept(new Vector3f(0.5F, 0.5F, 0.5F));
    }

    @Override
    public void submit(@Nullable State state, PoseStack poseStack, SubmitNodeCollector collector,
        int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
        if (state == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        CameraRenderState camera = minecraft.gameRenderer.gameRenderState().levelRenderState.cameraRenderState;
        if (state.soul()) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.4F, 0.5F);
            poseStack.rotate(camera.orientation);
            collector.submitCustomGeometry(poseStack, ThRenderTypes.textSeeThrough(SOUL_TEXTURE),
                (pose, buffer) -> renderSoul(pose, buffer, state.frame(), lightCoords));
            poseStack.popPose();
            return;
        }

        if (state.preview() == null) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.01F, 0.5F);
        poseStack.scale(0.25F, 0.25F, 0.25F);
        minecraft.getEntityRenderDispatcher().submit(state.preview(), camera,
            0.0F, 0.0F, 0.0F, poseStack, collector);
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

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer,
        float x, float y, float u, float v, int light) {
        buffer.addVertex(pose, x, y, 0.0F)
            .setColor(0xFFFFFFFF)
            .setUv(u, v)
            .setLight(light);
    }

    public record Unbaked() implements SpecialModelRenderer.Unbaked<State> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public SpecialModelRenderer<State> bake(SpecialModelRenderer.BakingContext context) {
            return new SoulJarItemSpecialModelRenderer();
        }
    }
}
