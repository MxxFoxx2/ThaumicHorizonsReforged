package dev.visfox.thaumichorizons.client.lens;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.LensEffectsHandler;
import dev.visfox.thaumichorizons.client.platform.GuiBlit;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class AirLensHandler {
    private static final Identifier RIPPLE = ThaumicHorizons.id("textures/fx/ripple.png");
    private static final Identifier VORTEX = ThaumicHorizons.id("textures/fx/vortex.png");
    private static final double RANGE = 24.0;

    private AirLensHandler() {
    }

    public static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null || !minecraft.options.getCameraType().isFirstPerson()
            || !LensEffectsHandler.hasLens(player, "LensAir")) {
            return;
        }
        Camera camera = minecraft.gameRenderer.getMainCamera();
        Matrix4f viewRotation = camera.getViewRotationMatrix(new Matrix4f());
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        double aspect = (double) width / height;
        double verticalScale = Math.tan(Math.toRadians(minecraft.options.fov().get()) / 2.0);
        double horizontalScale = verticalScale * aspect;
        for (LivingEntity entity : minecraft.level.getEntitiesOfClass(LivingEntity.class,
            player.getBoundingBox().inflate(RANGE), entity -> entity != player)) {
            double entityX = Mth.lerp(partialTick, entity.xOld, entity.getX());
            double entityY = Mth.lerp(partialTick, entity.yOld, entity.getY()) + entity.getBbHeight() / 2.0;
            double entityZ = Mth.lerp(partialTick, entity.zOld, entity.getZ());
            Vector3f relative = new Vector3f(
                (float) (entityX - camera.position().x),
                (float) (entityY - camera.position().y),
                (float) (entityZ - camera.position().z));
            viewRotation.transformPosition(relative);
            float depth = -relative.z;
            if (depth <= 0.05F) {
                continue;
            }
            int x = Mth.floor(width * (0.5 + relative.x / (depth * horizontalScale * 2.0)));
            int y = Mth.floor(height * (0.5 - relative.y / (depth * verticalScale * 2.0)));
            if (x < -64 || y < -64 || x > width + 64 || y > height + 64) {
                continue;
            }
            double distance = Math.sqrt(relative.lengthSquared());
            double size = Mth.clamp(Math.min(entity.getBbWidth(), entity.getBbHeight())
                * Math.min(width, height) / Math.max(distance, 0.5), 4.0, 64.0);
            boolean undead = entity.getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.UNDEAD);
            if (undead) {
                drawVortex(graphics, x, y, size * 2.0, entity.tickCount + partialTick,
                    withAlpha(Mth.clamp(0.1F - (float) distance / 240.0F, 0.02F, 0.1F)));
            } else {
                drawRipples(graphics, x, y, size, distance, entity.tickCount + partialTick,
                    withAlpha(Mth.clamp(0.3F - (float) distance / 80.0F, 0.03F, 0.3F)));
            }
        }
    }

    private static int withAlpha(float alpha) {
        return ARGB.color(Mth.floor(alpha * 255.0F), 255, 255, 255);
    }

    private static void drawRipples(GuiGraphicsExtractor graphics, int x, int y, double size, double distance,
        float age, int color) {
        int count = Math.min(4, (int) (48.0 / Math.max(distance, 0.5) + 1.0));
        float offset = age % 16.0F;
        for (int index = 0; index < count; index++) {
            double rippleSize = size * ((index * 16.0 / (count + 1) + offset) % 16.0) / 12.0;
            blitCentered(graphics, RIPPLE, x, y, rippleSize, 0.0F, color);
        }
    }

    private static void drawVortex(GuiGraphicsExtractor graphics, int x, int y, double size, float age, int color) {
        blitCentered(graphics, VORTEX, x, y, size, (float) Math.toRadians(age % 360.0F), color);
        blitCentered(graphics, VORTEX, x, y, size / 2.0, (float) -Math.toRadians(age % 360.0F), color);
    }

    private static void blitCentered(GuiGraphicsExtractor graphics, Identifier texture, int x, int y, double size,
        float rotation, int color) {
        int side = Math.max(1, Mth.ceil(size));
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        if (rotation != 0.0F) {
            graphics.pose().rotate(rotation);
        }
        GuiBlit.blit(graphics, texture, -side / 2, -side / 2, side, side, 0, 0, 64, 64, 64, 64, color);
        graphics.pose().popMatrix();
    }
}
