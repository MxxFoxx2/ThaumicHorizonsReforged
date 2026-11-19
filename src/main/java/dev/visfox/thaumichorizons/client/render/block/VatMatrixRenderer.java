package dev.visfox.thaumichorizons.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import dev.visfox.thaumichorizons.blockentity.VatBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VatMatrixBlockEntity;
import dev.visfox.thaumichorizons.client.platform.ThRenderTypes;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.client.geom.CubeGeometry;
import thaumcraft.client.geom.InfusionHaloGeometry;
import thaumcraft.client.geom.QuadSink;

/**
 * Dynamic infusion-matrix visuals from the legacy TileVatMatrixRender.
 *
 * <p>Draws the eight animated cubes, their emissive overlay, and the additive
 * infusion halo while the Vat is in infusion mode.</p>
 */
public final class VatMatrixRenderer implements BlockEntityRenderer<VatMatrixBlockEntity, VatMatrixRenderer.State> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(
        "thaumcraft", "textures/model/infuser.png");
    private static final int FULL_BRIGHT = 0x00F000F0;

    public VatMatrixRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public float startUp;
        public float instability;
        public float craftCount;
        public int mode;
        public long gameTime;
        public float partialTicks;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(VatMatrixBlockEntity entity, State state, float partialTicks,
        Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay overlay) {
        BlockEntityRenderState.extractBase(entity, state, overlay);
        state.partialTicks = partialTicks;
        if (entity.getLevel() == null) {
            state.startUp = 0.0F;
            state.instability = 0.0F;
            state.craftCount = 0.0F;
            state.mode = 0;
            state.gameTime = 0L;
            return;
        }

        VatBlockEntity vat = entity.getVat();
        if (vat == null) {
            state.startUp = 0.0F;
            state.instability = 0.0F;
            state.craftCount = 0.0F;
            state.mode = 0;
        } else {
            state.startUp = vat.startUp;
            state.instability = vat.instability;
            state.craftCount = vat.craftCount;
            state.mode = vat.mode;
        }
        state.gameTime = entity.getLevel().getGameTime();
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector,
        net.minecraft.client.renderer.state.level.CameraRenderState camera) {
        float ticks = state.gameTime + state.partialTicks;
        float startup = Math.max(0.0F, Math.min(1.0F, state.startUp));
        float activeInstability = Math.min(6.0F,
            1.0F + state.instability * 0.66F * (Math.min(state.craftCount, 50.0F) / 50.0F));

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.rotateDegrees(Axis.YP, (ticks % 360.0F) * startup);

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    float wobbleX = (float) Math.sin((ticks + x * 10.0F) / (15.0F - activeInstability / 2.0F))
                        * 0.01F * startup * activeInstability;
                    float wobbleY = (float) Math.sin((ticks + y * 10.0F) / (14.0F - activeInstability / 2.0F))
                        * 0.01F * startup * activeInstability;
                    float wobbleZ = (float) Math.sin((ticks + z * 10.0F) / (13.0F - activeInstability / 2.0F))
                        * 0.01F * startup * activeInstability;

                    poseStack.pushPose();
                    poseStack.translate(
                        (x == 0 ? -0.25F : 0.25F) + wobbleX,
                        (y == 0 ? -0.25F : 0.25F) + wobbleY,
                        (z == 0 ? -0.25F : 0.25F) + wobbleZ);
                    if (x > 0) {
                        poseStack.rotateDegrees(Axis.XP, 90.0F);
                    }
                    if (y > 0) {
                        poseStack.rotateDegrees(Axis.YP, 90.0F);
                    }
                    if (z > 0) {
                        poseStack.rotateDegrees(Axis.ZP, 90.0F);
                    }
                    poseStack.scale(0.45F, 0.45F, 0.45F);

                    collector.submitCustomGeometry(poseStack, ThRenderTypes.entityCutoutNoCull(TEXTURE),
                        (pose, buffer) -> renderCube(pose, buffer, 0, 0xFFFFFFFF, state.lightCoords));

                    float pulse = ((float) Math.sin((ticks + x * 2.0F + y * 3.0F + z * 4.0F) / 4.0F)
                        * 0.1F + 0.2F) * startup;
                    int alpha = Math.max(0, Math.min(255, Math.round(pulse * 255.0F)));
                    int emissiveColor = (alpha << 24) | 0x00CC1AFF;
                    if (alpha > 0) {
                        collector.submitCustomGeometry(poseStack, ThRenderTypes.eyes(TEXTURE),
                            (pose, buffer) -> renderCube(pose, buffer, 32, emissiveColor, FULL_BRIGHT));
                    }
                    poseStack.popPose();
                }
            }
        }
        poseStack.popPose();

        if (state.mode == 2 && state.craftCount > 0.0F) {
            int count = Math.max(0, Math.round(state.craftCount));
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            collector.submitCustomGeometry(poseStack, ThRenderTypes.lightning(),
                (pose, buffer) -> renderHalo(pose, buffer, count));
            poseStack.popPose();
        }
    }

    private static void renderCube(PoseStack.Pose pose, VertexConsumer buffer, int textureShift,
        int argb, int light) {
        float[][] face = new float[4][6];
        int[] faceVertex = {0};

        QuadSink sink = (x, y, z, u, v, color, packedLight) -> {
            int index = faceVertex[0]++;
            face[index][0] = x;
            face[index][1] = y;
            face[index][2] = z;
            face[index][3] = u;
            face[index][4] = v;
            face[index][5] = packedLight;

            if (faceVertex[0] == 4) {
                float ux = face[1][0] - face[0][0];
                float uy = face[1][1] - face[0][1];
                float uz = face[1][2] - face[0][2];
                float vx = face[3][0] - face[0][0];
                float vy = face[3][1] - face[0][1];
                float vz = face[3][2] - face[0][2];
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
                    buffer.addVertex(pose, face[vertex][0], face[vertex][1], face[vertex][2])
                        .setColor(color)
                        .setUv(face[vertex][3], face[vertex][4])
                        .setOverlay(OverlayTexture.NO_OVERLAY)
                        .setLight(light)
                        .setNormal(pose, nx, ny, nz);
                }
                faceVertex[0] = 0;
            }
        };

        CubeGeometry.render(sink, textureShift, argb, light);
    }

    /**
     * TC4R converts the legacy triangle-fan rays to pairs of quads. The
     * lightning render type is additive and does not sample a texture.
     */
    private static void renderHalo(PoseStack.Pose pose, VertexConsumer buffer, int count) {
        float[][] positions = new float[4][3];
        int[] colors = new int[4];
        int[] vertexIndex = {0};

        QuadSink sink = (x, y, z, u, v, color, packedLight) -> {
            int index = vertexIndex[0]++ & 3;
            positions[index][0] = x;
            positions[index][1] = y;
            positions[index][2] = z;
            colors[index] = color;

            if ((vertexIndex[0] & 3) == 0) {
                float ux = positions[1][0] - positions[0][0];
                float uy = positions[1][1] - positions[0][1];
                float uz = positions[1][2] - positions[0][2];
                float vx = positions[3][0] - positions[0][0];
                float vy = positions[3][1] - positions[0][1];
                float vz = positions[3][2] - positions[0][2];
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
                    buffer.addVertex(pose, positions[vertex][0], positions[vertex][1], positions[vertex][2])
                        .setColor(colors[vertex]);
                }
            }
        };

        InfusionHaloGeometry.render(sink, count, true, 0.0F, 0.0F, FULL_BRIGHT);
    }

    @Override
    public AABB getRenderBoundingBox(VatMatrixBlockEntity entity) {
        BlockPos pos = entity.getBlockPos();
        return new AABB(pos.getX() - 2.0, pos.getY() - 2.0, pos.getZ() - 2.0,
            pos.getX() + 3.0, pos.getY() + 3.0, pos.getZ() + 3.0);
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
