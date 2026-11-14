package dev.visfox.thaumichorizons.lib;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import thaumcraft.Thaumcraft;

public final class THSounds {
    private THSounds() {
    }

    public static @Nullable SoundEvent sound(String path) {
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.fromNamespaceAndPath("thaumcraft", path));
        return event == null ? null : event;
    }

    public static void play(Level level, BlockPos pos, String path, SoundSource source, float volume, float pitch) {
        SoundEvent event = sound(path);
        if (event != null) {
            level.playSound(null, pos, event, source, volume, pitch);
        }
    }

    public static void play(Level level, Vec3 pos, String path, SoundSource source, float volume, float pitch) {
        SoundEvent event = sound(path);
        if (event != null && level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, pos.x, pos.y, pos.z, event, source, volume, pitch);
        }
    }
}
