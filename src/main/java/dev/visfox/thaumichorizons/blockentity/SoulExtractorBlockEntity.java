package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.BrainJarCompat;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.vis.VisNet;
import thaumcraft.vis.VisNode;

public class SoulExtractorBlockEntity extends VisNode implements net.minecraft.world.MenuProvider {
    public static final int MAX_TICKS = 1200;
    public final net.minecraft.world.SimpleContainer sieveInventory = new net.minecraft.world.SimpleContainer(1) {
        @Override
        public void setChanged() {
            SoulExtractorBlockEntity.this.setChanged();
        }
    };
    public int ticksLeft;
    public boolean extracting;
    public int sieveMotion;

    public SoulExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.SOUL_EXTRACTOR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoulExtractorBlockEntity entity) {
        entity.tick(level);
    }

    private void tick(Level tickLevel) {
        tickNetwork(tickLevel);
        extracting = false;
        if (ticksLeft <= 0) {
            if (sieveInventory.getItem(0).isEmpty()) {
                return;
            }
            sieveInventory.getItem(0).shrink(1);
            ticksLeft = MAX_TICKS;
        }
        BlockEntity above = tickLevel.getBlockEntity(worldPosition.above());
        if (above == null) {
            return;
        }
        BrainJarCompat brainJars = Compat.port().brainJars();
        boolean brainJar = brainJars.isBrainJar(above) && brainJars.xp(above) < brainJars.maxXp();
        SoulReceiver receiver = above instanceof SoulReceiver candidate && candidate.canAcceptSouls() ? candidate : null;
        if (!brainJar && receiver == null) {
            return;
        }
        extracting = true;
        if (!tickLevel.isClientSide() && ticksLeft > 0) {
            int visBoost = VisNet.drainVis(tickLevel, worldPosition, Aspect.AIR, 10);
            int bits = 1 + visBoost;
            ticksLeft -= bits;
            if (brainJar) {
                BrainJarFeed.feed(above, brainJars, bits, Math::random);
            } else {
                receiver.addSoulBits(bits);
            }
            if (ticksLeft <= 0) {
                BlockEntity below = tickLevel.getBlockEntity(worldPosition.below());
                ItemStack sand = new ItemStack(Items.SAND);
                boolean placed = false;
                if (below instanceof Container container) {
                    for (int slot = 0; slot < container.getContainerSize(); slot++) {
                        ItemStack existing = container.getItem(slot);
                        if (existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, sand)) {
                            container.setItem(slot, sand.copyWithCount(existing.getCount() + sand.getCount()));
                            placed = true;
                            break;
                        }
                    }
                }
                if (!placed) {
                    ItemEntity fallenSand = new ItemEntity(tickLevel, worldPosition.getX() + 0.5,
                        worldPosition.getY(), worldPosition.getZ() + 0.5, sand);
                    tickLevel.addFreshEntity(fallenSand);
                }
            }
            setChanged();
            if (tickLevel instanceof ServerLevel serverLevel) {
                serverLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        } else if (ticksLeft > 0 && tickLevel.isClientSide()) {
            sieveMotion++;
            if (sieveMotion >= 360) {
                sieveMotion -= 360;
                tickLevel.playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, SoundEvents.SAND_BREAK, SoundSource.BLOCKS, 1.0F, 0.0F, false);
                if (tickLevel.isClientSide()) {
                    for (int i = 0; i < 8; i++) {
                        tickLevel.addParticle(ParticleTypes.WHITE_ASH, worldPosition.getX() + 0.5,
                            worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5, 0.0, 0.05, 0.0);
                    }
                }
            }
        }
    }

    public boolean isExtracting() {
        return extracting;
    }

    public int getTimeRemainingScaled(int scale) {
        return ticksLeft * scale / MAX_TICKS;
    }

    public void dropContents(Level dropLevel, BlockPos pos) {
        ItemStack sand = sieveInventory.getItem(0);
        if (!sand.isEmpty()) {
            dropLevel.addFreshEntity(new ItemEntity(dropLevel, pos.getX(), pos.getY(), pos.getZ(), sand));
            sieveInventory.setItem(0, ItemStack.EMPTY);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ticksLeft = input.getIntOr("ticks", 0);
        extracting = input.getBooleanOr("extracting", false);
        sieveInventory.setItem(0, input.read("soulsand", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("ticks", ticksLeft);
        output.putBoolean("extracting", extracting);
        if (!sieveInventory.getItem(0).isEmpty()) {
            output.store("soulsand", ItemStack.OPTIONAL_CODEC, sieveInventory.getItem(0));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory,
        Player player) {
        return new dev.visfox.thaumichorizons.container.SoulExtractorMenu(containerId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumichorizons.soulsieve");
    }

    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
            && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public boolean isSource() {
        return false;
    }
}
