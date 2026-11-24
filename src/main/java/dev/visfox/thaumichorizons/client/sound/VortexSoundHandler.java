package dev.visfox.thaumichorizons.client.sound;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class VortexSoundHandler {
    private static final int SCAN_INTERVAL_TICKS = 5;
    private static final int SCAN_CHUNK_RADIUS = 4;
    private static final double MAX_DISTANCE_SQR = 64.0D * 64.0D;

    private static final Map<BlockPos, VortexSound> SOUNDS = new HashMap<>();
    private static ClientLevel activeLevel;
    private static int tickCounter;

    private VortexSoundHandler() {
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null || minecraft.isPaused()) {
            stopAll(minecraft);
            return;
        }

        if (activeLevel != level) {
            stopAll(minecraft);
            activeLevel = level;
        }

        if (++tickCounter % SCAN_INTERVAL_TICKS != 0) {
            return;
        }

        var player = minecraft.player;
        BlockPos playerPos = player.blockPosition();
        int centerChunkX = playerPos.getX() >> 4;
        int centerChunkZ = playerPos.getZ() >> 4;
        Set<BlockPos> discovered = new HashSet<>();
        var soundManager = minecraft.getSoundManager();

        for (int chunkX = centerChunkX - SCAN_CHUNK_RADIUS; chunkX <= centerChunkX + SCAN_CHUNK_RADIUS; chunkX++) {
            for (int chunkZ = centerChunkZ - SCAN_CHUNK_RADIUS; chunkZ <= centerChunkZ + SCAN_CHUNK_RADIUS; chunkZ++) {
                LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                if (chunk == null) {
                    continue;
                }

                for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof VortexBlockEntity vortex) || vortex.isRemoved()) {
                        continue;
                    }

                    BlockPos pos = vortex.getBlockPos().immutable();
                    double distanceSqr = player.distanceToSqr(
                        pos.getX() + 0.5D,
                        pos.getY() + 0.5D,
                        pos.getZ() + 0.5D
                    );
                    if (distanceSqr > MAX_DISTANCE_SQR) {
                        continue;
                    }

                    discovered.add(pos);
                    VortexSound sound = SOUNDS.get(pos);
                    if (sound == null || sound.isStopped()) {
                        if (sound != null) {
                            soundManager.stop(sound);
                        }
                        sound = new VortexSound(level, pos);
                        SOUNDS.put(pos, sound);
                    }

                    if (!soundManager.isActive(sound)) {
                        soundManager.play(sound);
                    }
                }
            }
        }

        Iterator<Map.Entry<BlockPos, VortexSound>> iterator = SOUNDS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BlockPos, VortexSound> entry = iterator.next();
            if (!discovered.contains(entry.getKey())) {
                soundManager.stop(entry.getValue());
                iterator.remove();
            }
        }
    }

    public static void onResourceReload() {
        stopAll(Minecraft.getInstance());
    }

    private static void stopAll(Minecraft minecraft) {
        for (VortexSound sound : SOUNDS.values()) {
            minecraft.getSoundManager().stop(sound);
        }
        SOUNDS.clear();
        activeLevel = null;
        tickCounter = 0;
    }
}
