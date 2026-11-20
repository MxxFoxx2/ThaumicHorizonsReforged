package dev.visfox.thaumichorizons.block.pocket;

import dev.visfox.thaumichorizons.blockentity.ThBlockEntity;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import dev.visfox.thaumichorizons.world.pocket.PocketPlanes;
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
    private boolean xAligned;
    private boolean alignmentKnown;
    private boolean cleanupStarted;
    private boolean endpointReleased;

    public PortalTHBlockEntity(BlockPos pos, BlockState state) {
        super(PocketRegistry.PORTAL_BE.get(), pos, state);
    }

    public void configure(BlockPos sourceSlot, int plane, int portal, String dimension, boolean xAligned) {
        slotPos = sourceSlot.immutable();
        pocketID = plane;
        which = portal;
        sourceDimension = dimension;
        this.xAligned = xAligned;
        alignmentKnown = true;
        sync();
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

        if (!entity.cleanupStarted) {
            THSounds.play(server, pos, "craftfail", SoundSource.BLOCKS, 1.0F, 1.0F);
            if (Compat.port().fx().isAvailable()) {
                Compat.port().fx().burst(server, Vec3.atCenterOf(pos), 0xFFFFFF, 16);
            }
            entity.cleanupStarted = true;
            entity.setChanged();
        }

        entity.resolveAlignment(level, pos);
        ServerLevel sourceLevel = entity.sourceLevel(server);
        if (sourceLevel != null && entity.slotPos != null) {
            if (SlotBlockEntity.destroyPortalFromEndpoint(sourceLevel, entity.slotPos, entity.xAligned,
                entity.pocketID, entity.which)) {
                entity.endpointReleased = true;
                return;
            }

            // The source frame crosses an unloaded chunk boundary. Release the
            // Pocket Plane endpoint now, but keep this source-side block entity
            // alive so it can restore the frame once the complete footprint is loaded.
            if (!entity.endpointReleased) {
                PocketPlanes.destroyPortal(entity.pocketID, entity.which, server.getServer());
                entity.endpointReleased = true;
                entity.setChanged();
            }
            return;
        }

        if (!entity.endpointReleased) {
            PocketPlanes.destroyPortal(entity.pocketID, entity.which, server.getServer());
            entity.endpointReleased = true;
            entity.setChanged();
        }
        // If the source dimension or its Slot is no longer available, do not
        // leave a live orphan portal block behind in the currently ticking level.
        if (level.getBlockState(pos).getBlock() instanceof PortalTHBlock) {
            server.removeBlock(pos, false);
        }
    }

    private ServerLevel sourceLevel(ServerLevel current) {
        Identifier dimension = Identifier.tryParse(sourceDimension);
        if (dimension == null) {
            return null;
        }
        return current.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
    }

    private void resolveAlignment(Level level, BlockPos pos) {
        if (alignmentKnown) {
            return;
        }
        if (PocketPlanes.validPlane(pocketID)) {
            int[] endpoint = PocketPlanes.planes.get(pocketID).portal(which);
            if (endpoint != null && endpoint.length >= 4 && (endpoint[3] == 0 || endpoint[3] == 1)) {
                xAligned = endpoint[3] == 1;
                alignmentKnown = true;
                setChanged();
                return;
            }
        }
        if (level.dimension().identifier().toString().equals(sourceDimension)) {
            xAligned = GatewayPortalBlock.isXAligned(level, pos);
            alignmentKnown = true;
            setChanged();
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
        xAligned = input.getBooleanOr("aligned", false);
        alignmentKnown = input.getBooleanOr("alignment_known", false);
        cleanupStarted = input.getBooleanOr("cleanup_started", false);
        endpointReleased = input.getBooleanOr("endpoint_released", false);
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
        output.putBoolean("aligned", xAligned);
        output.putBoolean("alignment_known", alignmentKnown);
        output.putBoolean("cleanup_started", cleanupStarted);
        output.putBoolean("endpoint_released", endpointReleased);
    }
}
