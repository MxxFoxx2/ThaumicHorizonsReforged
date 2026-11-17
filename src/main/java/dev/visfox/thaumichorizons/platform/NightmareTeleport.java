package dev.visfox.thaumichorizons.platform;

import dev.visfox.thaumichorizons.entity.NightmareTH;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.TeleportTransition;

/**
 * Isolates Minecraft's version-sensitive cross-dimension entity transfer API
 * from common entity behavior. Entity#teleport carries the passenger tree.
 */
public final class NightmareTeleport {
    private NightmareTeleport() {
    }

    public static boolean transfer(NightmareTH nightmare, ServerLevel destination, Vec3 position) {
        return nightmare.teleport(new TeleportTransition(
            destination,
            position,
            Vec3.ZERO,
            nightmare.getYRot(),
            nightmare.getXRot(),
            TeleportTransition.PLAY_PORTAL_SOUND
        )) != null;
    }
}
