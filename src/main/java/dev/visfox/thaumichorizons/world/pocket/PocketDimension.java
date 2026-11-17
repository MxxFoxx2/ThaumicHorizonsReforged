package dev.visfox.thaumichorizons.world.pocket;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class PocketDimension {
    public static final ResourceKey<Level> POCKET_PLANE = ResourceKey.create(Registries.DIMENSION, ThaumicHorizons.id("pocket_plane"));

    private PocketDimension() {
    }

    public static @Nullable ServerLevel level(@Nullable MinecraftServer server) {
        if (server == null) {
            return null;
        }
        return server.getLevel(POCKET_PLANE);
    }

    public static ServerLevel levelOrThrow(MinecraftServer server) {
        ServerLevel level = server.getLevel(POCKET_PLANE);
        if (level == null) {
            throw new IllegalStateException("Pocket plane dimension not present");
        }
        return level;
    }
}
