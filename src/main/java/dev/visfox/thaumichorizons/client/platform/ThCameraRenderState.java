package dev.visfox.thaumichorizons.client.platform;

import net.minecraft.client.Minecraft;
import org.joml.Quaternionf;

/** Camera orientation used by billboard geometry. */
public final class ThCameraRenderState {
    public final Quaternionf orientation =
        new Quaternionf(Minecraft.getInstance().gameRenderer.getMainCamera().rotation());
}
