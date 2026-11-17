package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.CloudBlock;
import dev.visfox.thaumichorizons.blockentity.CloudBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Recreates TileCloudRender's animated vertical weather column.
 *
 * <p>The original renderer uses a four-sided textured tube, scrolling UVs and a
 * cloud-variant tint. Rain state and column depth are synchronized by the
 * block entity; the biome determines rain versus snow locally.</p>
 */
public final class CloudBlockRenderer implements BlockEntityRenderer<CloudBlockEntity, CloudBlockRenderer.State> {
    private static final Identifier RAIN = ThaumicHorizons.id("textures/environment/rain.png");
    private static final Identifier FIRE_RAIN = ThaumicHorizons.id("textures/environment/firerain.png");
    private static final Identifier SNOW = Identifier.fromNamespaceAndPath("minecraft", "textures/environment/snow.png");

    public CloudBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public boolean raining;
        public boolean warmBiome;
        public int cloudType;
        public int columnDepth;
        public int worldX;
        public int worldZ;
        public long gameTime;
        public float partialTicks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(CloudBlockEntity entity, State state, float partialTicks,
        net.minecraft.world.phys.Vec3 cameraPosition,
        net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        Level level = entity.getLevel();
        if (level == null) {
            state.raining = false;
            return;
        }

        BlockPos pos = entity.getBlockPos();
        state.raining = entity.isRaining();
        state.warmBiome = level.getBiome(pos).value().warmEnoughToRain(pos, level.getSeaLevel());
        state.cloudType = entity.getBlockState().getValue(CloudBlock.TYPE);
        state.columnDepth = Math.max(0, entity.getColumnDepth() - 1);
        state.worldX = pos.getX();
        state.worldZ = pos.getZ();
        state.gameTime = level.getGameTime();
        state.partialTicks = partialTicks;
        state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector,
        net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        if (!state.raining || state.columnDepth <= 0) {
            return;
        }

        Identifier texture = state.cloudType == 1 ? FIRE_RAIN : state.warmBiome ? RAIN : SNOW;
        int color = tint(state.cloudType);
        double time = state.gameTime + state.partialTicks + state.worldX + 16.0D * state.worldZ;
        float scroll = (float) (-time * 0.2D - Math.floor(-time * 0.1D));
        float topV = -1.0F + scroll;
        float bottomV = (state.columnDepth + 1) / 4.0F + topV;
        float bottomY = -state.columnDepth;

        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(texture),
            (pose, buffer) -> renderColumn(pose, buffer, color, state.lightCoords, bottomY, topV, bottomV));
    }

    @Override
    public AABB getRenderBoundingBox(CloudBlockEntity entity) {
        BlockPos pos = entity.getBlockPos();
        double bottom = pos.getY() - Math.max(1, entity.getColumnDepth());
        return new AABB(pos.getX(), bottom, pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    private static int tint(int type) {
        return switch (type) {
            case 1, 7 -> 0xFFFFFFFF;
            case 3 -> 0xFF20FF40;
            case 4 -> 0xFF404040;
            case 5 -> 0xFFFF4020;
            case 6 -> 0xFFAA40C8;
            case 8 -> 0xFFA0FFA0;
            case 9 -> 0xFFFFE640;
            default -> 0xFF2040FF;
        };
    }

    private static void renderColumn(PoseStack.Pose pose, VertexConsumer buffer, int color, int light,
        float bottomY, float topV, float bottomV) {
        quad(pose, buffer, color, light,
            0.0F, bottomY, 0.0F, 1.0F, bottomV,
            0.0F, 0.0F, 0.0F, 1.0F, topV,
            1.0F, 0.0F, 0.0F, 0.0F, topV,
            1.0F, bottomY, 0.0F, 0.0F, bottomV,
            0.0F, 0.0F, -1.0F);
        quad(pose, buffer, color, light,
            1.0F, bottomY, 1.0F, 1.0F, bottomV,
            1.0F, 0.0F, 1.0F, 1.0F, topV,
            0.0F, 0.0F, 1.0F, 0.0F, topV,
            0.0F, bottomY, 1.0F, 0.0F, bottomV,
            0.0F, 0.0F, 1.0F);
        quad(pose, buffer, color, light,
            0.0F, bottomY, 1.0F, 1.0F, bottomV,
            0.0F, 0.0F, 1.0F, 1.0F, topV,
            0.0F, 0.0F, 0.0F, 0.0F, topV,
            0.0F, bottomY, 0.0F, 0.0F, bottomV,
            -1.0F, 0.0F, 0.0F);
        quad(pose, buffer, color, light,
            1.0F, bottomY, 0.0F, 1.0F, bottomV,
            1.0F, 0.0F, 0.0F, 1.0F, topV,
            1.0F, 0.0F, 1.0F, 0.0F, topV,
            1.0F, bottomY, 1.0F, 0.0F, bottomV,
            1.0F, 0.0F, 0.0F);
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer buffer, int color, int light,
        float x0, float y0, float z0, float u0, float v0,
        float x1, float y1, float z1, float u1, float v1,
        float x2, float y2, float z2, float u2, float v2,
        float x3, float y3, float z3, float u3, float v3,
        float nx, float ny, float nz) {
        vertex(pose, buffer, color, light, x0, y0, z0, u0, v0, nx, ny, nz);
        vertex(pose, buffer, color, light, x1, y1, z1, u1, v1, nx, ny, nz);
        vertex(pose, buffer, color, light, x2, y2, z2, u2, v2, nx, ny, nz);
        vertex(pose, buffer, color, light, x3, y3, z3, u3, v3, nx, ny, nz);
    }

    private static void vertex(PoseStack.Pose pose, VertexConsumer buffer, int color, int light,
        float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        buffer.addVertex(pose, x, y, z)
            .setColor(color)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(light)
            .setNormal(pose, nx, ny, nz);
    }
}
