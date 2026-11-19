package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.client.render.model.ObjModel;
import java.util.Map;
import java.util.WeakHashMap;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.VatBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import dev.visfox.thaumichorizons.client.render.VatRenderAlphaContext;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Static 3x3 Vat shell from the legacy ModelVat.
 *
 * <p>The original model is attached to the upper-centre Vat block entity and
 * spans four blocks vertically and three blocks horizontally. Internal Vat
 * blocks therefore use invisible vanilla render shapes to avoid drawing a
 * second set of full cubes underneath this model.</p>
 */
public final class VatStructureRenderer implements BlockEntityRenderer<VatBlockEntity, VatStructureRenderer.State> {
    private static final Identifier TEXTURE = ThaumicHorizons.id("textures/models/vat.png");
    private static final Identifier EFFIGY_TEXTURE = ThaumicHorizons.id("textures/block/corpseeffigy.png");
    private static final Identifier REVIVED_EFFIGY_TEXTURE =
        ThaumicHorizons.id("textures/models/corpseeffigyrevived.png");
    private static final int TEXTURE_SIZE = 512;
    private static final int[][] QUAD_VERTICES = {
        {5, 1, 2, 6},
        {0, 4, 7, 3},
        {5, 4, 0, 1},
        {2, 3, 7, 6},
        {1, 0, 3, 2},
        {4, 5, 6, 7}
    };

    private static final Box[] BOXES = {
        // ModelVat.Base: 48x16x48, pivot (-24, 8, -24), texture (0, 0).
        new Box(0, 0, -24, 8, -24, 48, 16, 48),
        // ModelVat.Water: 40x32x40, pivot (-20, -24, -20), texture (256, 0).
        new Box(256, 0, -20, -24, -20, 40, 32, 40),
        // ModelVat.Glass: 48x32x48, pivot (-24, -24, -24), texture (0, 128).
        new Box(0, 128, -24, -24, -24, 48, 32, 48),
        // ModelVat.Top: 48x14x48, pivot (-24, -38, -24), texture (256, 128).
        new Box(256, 128, -24, -38, -24, 48, 14, 48),
        // ModelVat.Hatch: 16x2x16, pivot (-8, -40, -8), texture (0, 80).
        new Box(0, 80, -8, -40, -8, 16, 2, 16)
    };

    private final EntityRenderDispatcher entityRenderer;
    private final ObjModel effigyModel = ObjModel.get(ThaumicHorizons.id("textures/models/corpseeffigy.obj"));
    private final Map<VatBlockEntity, CachedPreview> previewEntities = new WeakHashMap<>();
    private final Map<VatBlockEntity, CachedSample> sampleEntities = new WeakHashMap<>();

    public VatStructureRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.entityRenderer();
    }

    public static final class State extends BlockEntityRenderState {
        public EntityRenderState containedEntity;
        public float containedEntityBob;
        public float containedEntityAlpha = 1.0F;
        public EntityRenderState sampleItem;
        public float sampleItemBob;
        public boolean corpseEffigy;
        public boolean revivedEffigy;
        public float effigyBob;
    }

    private record Box(int u, int v, float x, float y, float z, float width, float height, float depth) {
    }

    private record CachedPreview(CompoundTag data, LivingEntity entity) {
    }

    private record CachedSample(ItemEntity entity) {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(VatBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        state.containedEntity = null;
        state.containedEntityBob = 0.0F;
        state.containedEntityAlpha = 1.0F;
        state.sampleItem = null;
        state.sampleItemBob = 0.0F;
        state.corpseEffigy = false;
        state.revivedEffigy = false;
        state.effigyBob = 0.0F;
        if (entity.getLevel() instanceof ClientLevel clientLevel) {
            float bob = (float) Math.cos(Math.toRadians(clientLevel.getGameTime() + partialTicks)) * 0.1F;
            CompoundTag previewData = entity.getEntityPreviewData();
            if (previewData != null) {
                sampleEntities.remove(entity);
                CachedPreview cached = previewEntities.get(entity);
                if (cached == null || !cached.data().equals(previewData)) {
                    Entity created = EntityNbt.load(previewData, clientLevel);
                    if (created instanceof LivingEntity living) {
                        living.snapTo(entity.getBlockPos().getX() + 0.5,
                            entity.getBlockPos().getY() - 1.75,
                            entity.getBlockPos().getZ() + 0.5, 0.0F, 0.0F);
                        cached = new CachedPreview(previewData.copy(), living);
                        previewEntities.put(entity, cached);
                    } else {
                        previewEntities.remove(entity);
                        cached = null;
                    }
                }
                if (cached != null) {
                    state.containedEntity = entityRenderer.extractEntity(cached.entity(), partialTicks);
                    state.containedEntityBob = bob;
                    // Legacy TileVatSlaveRender uses (800 - progress) / 800 in clone mode.
                    state.containedEntityAlpha = entity.mode == 1
                        ? Math.max(0.0F, Math.min(1.0F, 1.0F - entity.progress / 800.0F))
                        : 1.0F;
                }
                return;
            }

            previewEntities.remove(entity);
            state.effigyBob = bob;
            if (entity.mode == 3) {
                state.corpseEffigy = true;
                sampleEntities.remove(entity);
            } else if (entity.mode == 4 || (entity.mode == 2 && entity.recipeType == 1)) {
                state.revivedEffigy = true;
                sampleEntities.remove(entity);
            } else if (entity.sample != null && !entity.sample.isEmpty()) {
                CachedSample cached = sampleEntities.get(entity);
                if (cached == null) {
                    ItemEntity item = new ItemEntity(clientLevel,
                        entity.getBlockPos().getX() + 0.5,
                        entity.getBlockPos().getY() - 1.0,
                        entity.getBlockPos().getZ() + 0.5,
                        entity.sample.copy());
                    cached = new CachedSample(item);
                    sampleEntities.put(entity, cached);
                } else {
                    cached.entity().setItem(entity.sample.copy());
                }
                state.sampleItem = entityRenderer.extractEntity(cached.entity(), partialTicks);
                state.sampleItemBob = bob;
            } else {
                sampleEntities.remove(entity);
            }
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector,
        net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        poseStack.pushPose();
        // Match TileVatRender's local origin and ModelRenderer's 1/16 model scale.
        poseStack.translate(0.5F, -0.5F, 0.5F);
        poseStack.scale(0.0625F, 0.0625F, 0.0625F);
        collector.submitCustomGeometry(poseStack, ThRenderTypes.entityTranslucent(TEXTURE),
            (pose, buffer) -> renderModel(pose, buffer, state.lightCoords));
        poseStack.popPose();

        if (state.containedEntity != null) {
            poseStack.pushPose();
            Runnable submitContained = () -> entityRenderer.submit(state.containedEntity, camera, 0.5,
                -1.75 + state.containedEntityBob, 0.5, poseStack, collector);
            if (state.containedEntityAlpha < 1.0F) {
                VatRenderAlphaContext.run(state.containedEntityAlpha, submitContained);
            } else {
                submitContained.run();
            }
            poseStack.popPose();
        } else if (state.corpseEffigy || state.revivedEffigy) {
            Identifier effigyTexture = state.revivedEffigy ? REVIVED_EFFIGY_TEXTURE : EFFIGY_TEXTURE;
            poseStack.pushPose();
            poseStack.translate(0.5F, -1.5F + state.effigyBob, 0.5F);
            poseStack.rotateDegrees(Axis.ZP, 180.0F);
            poseStack.scale(0.1F, 0.1F, 0.1F);
            collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutout(effigyTexture),
                (pose, buffer) -> effigyModel.renderPart("effigy", pose, buffer, 0xFFFFFFFF, state.lightCoords));
            poseStack.popPose();
        } else if (state.sampleItem != null) {
            poseStack.pushPose();
            entityRenderer.submit(state.sampleItem, camera, 0.5, -1.0 + state.sampleItemBob,
                0.5, poseStack, collector);
            poseStack.popPose();
        }
    }

    private static void renderModel(PoseStack.Pose pose, VertexConsumer buffer, int light) {
        for (Box box : BOXES) {
            renderBox(pose, buffer, box, light);
        }
    }

    private static void renderBox(PoseStack.Pose pose, VertexConsumer buffer, Box box, int light) {
        float x0 = box.x;
        float y0 = box.y;
        float z0 = box.z;
        float x1 = x0 + box.width;
        float y1 = y0 + box.height;
        float z1 = z0 + box.depth;
        float[][] vertices = {
            {x0, y0, z0},
            {x1, y0, z0},
            {x1, y1, z0},
            {x0, y1, z0},
            {x0, y0, z1},
            {x1, y0, z1},
            {x1, y1, z1},
            {x0, y1, z1}
        };

        int[][] uvRects = {
            {box.u + (int) box.depth + (int) box.width, box.v + (int) box.depth,
                box.u + (int) box.depth + (int) box.width + (int) box.depth,
                box.v + (int) box.depth + (int) box.height},
            {box.u, box.v + (int) box.depth, box.u + (int) box.depth,
                box.v + (int) box.depth + (int) box.height},
            {box.u + (int) box.depth, box.v, box.u + (int) box.depth + (int) box.width,
                box.v + (int) box.depth},
            {box.u + (int) box.depth + (int) box.width, box.v,
                box.u + (int) box.depth + (int) box.width + (int) box.width,
                box.v + (int) box.depth},
            {box.u + (int) box.depth, box.v + (int) box.depth,
                box.u + (int) box.depth + (int) box.width,
                box.v + (int) box.depth + (int) box.height},
            {box.u + 2 * (int) box.depth + (int) box.width, box.v + (int) box.depth,
                box.u + 2 * (int) box.depth + 2 * (int) box.width,
                box.v + (int) box.depth + (int) box.height}
        };

        for (int face = 0; face < QUAD_VERTICES.length; face++) {
            int[] uv = uvRects[face];
            // ModelBox/TexturedQuad assigns (u2,v1),(u1,v1),(u1,v2),(u2,v2).
            float[][] faceVertices = new float[4][];
            float[][] faceUvs = new float[4][2];
            for (int order = 3; order >= 0; order--) {
                faceVertices[3 - order] = vertices[QUAD_VERTICES[face][order]];
                int uvIndex = order;
                faceUvs[3 - order][0] = (uvIndex == 0 || uvIndex == 3 ? uv[2] : uv[0]) / (float) TEXTURE_SIZE;
                faceUvs[3 - order][1] = (uvIndex == 0 || uvIndex == 1 ? uv[1] : uv[3]) / (float) TEXTURE_SIZE;
            }

            float ux = faceVertices[1][0] - faceVertices[0][0];
            float uy = faceVertices[1][1] - faceVertices[0][1];
            float uz = faceVertices[1][2] - faceVertices[0][2];
            float vx = faceVertices[3][0] - faceVertices[0][0];
            float vy = faceVertices[3][1] - faceVertices[0][1];
            float vz = faceVertices[3][2] - faceVertices[0][2];
            float nx = uy * vz - uz * vy;
            float ny = uz * vx - ux * vz;
            float nz = ux * vy - uy * vx;
            float length = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (length > 0.0F) {
                nx /= length;
                ny /= length;
                nz /= length;
            }

            for (int vertex = 0; vertex < 4; vertex++) {
                buffer.addVertex(pose, faceVertices[vertex][0], faceVertices[vertex][1], faceVertices[vertex][2])
                    .setColor(0xFFFFFFFF)
                    .setUv(faceUvs[vertex][0], faceUvs[vertex][1])
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal(pose, nx, ny, nz);
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox(VatBlockEntity entity) {
        BlockPos pos = entity.getBlockPos();
        return new AABB(pos.getX() - 2.0, pos.getY() - 3.0, pos.getZ() - 2.0,
            pos.getX() + 3.0, pos.getY() + 2.0, pos.getZ() + 3.0);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
