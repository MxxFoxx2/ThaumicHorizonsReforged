package dev.visfox.thaumichorizons.client.platform;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public final class ThRenderTypes {
    private ThRenderTypes() {
    }

    public static RenderType eyes(Identifier texture) {
        return RenderTypes.eyes(texture);
    }

    public static RenderType entityTranslucent(Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }

    public static RenderType entityCutout(Identifier texture) {
        return RenderTypes.entityCutout(texture);
    }

    public static RenderType entityCutoutNoCull(Identifier texture) {
        return RenderTypes.entityCutout(texture, false);
    }

    public static RenderType lightning() {
        return RenderTypes.lightning();
    }
}
