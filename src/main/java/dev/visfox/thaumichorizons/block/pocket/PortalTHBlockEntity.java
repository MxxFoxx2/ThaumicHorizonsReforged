package dev.visfox.thaumichorizons.block.pocket;

import dev.visfox.thaumichorizons.blockentity.ThBlockEntity;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class PortalTHBlockEntity extends ThBlockEntity {
    private int count;
    private int openCount = -1;
    private @Nullable BlockPos slotPos;
    private int pocketID;
    private int which;
    private String sourceDimension = "minecraft:overworld";

    public PortalTHBlockEntity(BlockPos pos, BlockState state) {
        super(PocketRegistry.PORTAL_BE.get(), pos, state);
    }

    public void configure(BlockPos sourceSlot, int plane, int portal, String dimension) {
        slotPos = sourceSlot.immutable();
        pocketID = plane;
        which = portal;
        sourceDimension = dimension;
        setChanged();
    }

    public int openCount() {
        return openCount;
    }

    public String sourceDimension() {
        return sourceDimension;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PortalTHBlockEntity entity) {
        entity.count++;
        if (level.isClientSide()) {
            if (entity.openCount < 30) {
                entity.openCount++;
            }
            if (entity.count % 250 == 0) {
                var sound = THSounds.sound("evilportal");
                if (sound != null) {
                    level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        sound, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
            }
            return;
        }
        if (entity.count <= 250 || !(level instanceof ServerLevel server)) {
            return;
        }
        THSounds.play(server, pos, "craftfail", SoundSource.BLOCKS, 1.0F, 1.0F);
        if (Compat.port().fx().isAvailable()) {
            Compat.port().fx().burst(server, Vec3.atCenterOf(pos), 0xFFFFFF, 16);
        }
        ServerLevel slotLevel = server;
        Identifier dimension = Identifier.tryParse(entity.sourceDimension);
        if (dimension != null) {
            ServerLevel source = server.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
            if (source != null) {
                slotLevel = source;
            }
        }
        if (entity.slotPos != null
            && slotLevel.getBlockEntity(entity.slotPos) instanceof SlotBlockEntity slot && slot.portalOpen()) {
            SlotBlockEntity.destroyPortal(slotLevel, entity.slotPos);
        } else {
            server.removeBlock(pos, false);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        count = input.getIntOr("count", 0);
        openCount = input.getIntOr("opencount", -1);
        pocketID = input.getIntOr("pocket", 0);
        which = input.getIntOr("which", 0);
        sourceDimension = input.getStringOr("dimension", "minecraft:overworld");
        long slot = input.getLongOr("slot", Long.MIN_VALUE);
        slotPos = slot == Long.MIN_VALUE ? null : BlockPos.of(slot);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("count", count);
        output.putInt("opencount", openCount);
        output.putInt("pocket", pocketID);
        output.putInt("which", which);
        output.putString("dimension", sourceDimension);
        if (slotPos != null) {
            output.putLong("slot", slotPos.asLong());
        }
    }
}
