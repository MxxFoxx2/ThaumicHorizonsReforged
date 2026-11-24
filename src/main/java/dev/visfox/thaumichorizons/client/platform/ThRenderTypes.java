package dev.visfox.thaumichorizons.client.platform;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class ThRenderTypes {
    private ThRenderTypes() {
    }

    public static RenderType eyes(ResourceLocation texture) {
        return RenderType.eyes(texture);
    }

    public static RenderType entityTranslucent(ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }

    public static RenderType entityCutout(ResourceLocation texture) {
        return RenderType.entityCutout(texture);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(texture);
    }

    public static RenderType textSeeThrough(ResourceLocation texture) {
        return RenderType.textSeeThrough(texture);
    }

    public static RenderType lightning() {
        return RenderType.lightning();
    }

    public static RenderType beaconBeam(ResourceLocation texture, boolean translucent) {
        return RenderType.beaconBeam(texture, translucent);
    }
}
