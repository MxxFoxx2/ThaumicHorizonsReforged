package dev.visfox.thaumichorizons.client.sound;

import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import dev.visfox.thaumichorizons.registry.ThSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;

public final class VortexSound extends AbstractTickableSoundInstance {
    private final ClientLevel level;
    private final BlockPos pos;

    public VortexSound(ClientLevel level, BlockPos pos) {
        super(ThSounds.VORTEX.get(), SoundSource.AMBIENT, RandomSource.create());
        this.level = level;
        this.pos = pos.immutable();
        this.looping = true;
        this.delay = 0;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        updatePosition();
        this.volume = 1.0F;
    }

    @Override
    public void tick() {
        if (!level.hasChunkAt(pos)) {
            stop();
            return;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof VortexBlockEntity) || blockEntity.isRemoved()) {
            stop();
            return;
        }

        updatePosition();

        var player = Minecraft.getInstance().player;
        if (player == null || player.level() != level) {
            volume = 0.0F;
            return;
        }

        double distance = Math.sqrt(player.distanceToSqr(x, y, z));
        volume = (float) Math.max(0.0D, 1.0D / (1.0D + distance / 8.0D));
    }

    private void updatePosition() {
        Vec3 center = Vec3.atCenterOf(pos);
        x = center.x;
        y = center.y;
        z = center.z;
    }
}
