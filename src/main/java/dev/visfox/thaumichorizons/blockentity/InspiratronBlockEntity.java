package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.blockentity.ThBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class InspiratronBlockEntity extends ThBlockEntity implements SoulReceiver, Container, net.minecraft.world.MenuProvider {
    public static final int PROGRESS_MAX = 100;
    public static final int PAPER_SLOT = 0;
    public static final int KNOWLEDGE_SLOT = 1;
    private static final Identifier KNOWLEDGE_FRAGMENT = Identifier.fromNamespaceAndPath("thaumcraft", "knowledge_fragment");

    public final net.minecraft.world.SimpleContainer inventory = new net.minecraft.world.SimpleContainer(2) {
        @Override
        public void setChanged() {
            InspiratronBlockEntity.this.setChanged();
        }
    };
    public int progress;
    public float rota;
    public float rotb;
    public float field_40063_b;
    public float field_40061_d;
    public float field_40059_f;
    public float field_40066_q;

    public InspiratronBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.INSPIRATRON.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, InspiratronBlockEntity entity) {
        if (!level.isClientSide()) {
            return;
        }
        entity.rotb = entity.rota;
        Entity closest = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 6.0, null);
        if (closest != null) {
            double dx = closest.getX() - (pos.getX() + 0.5);
            double dz = closest.getZ() - (pos.getZ() + 0.5);
            entity.field_40066_q = (float) Math.atan2(dz, dx);
            entity.field_40059_f += 0.1F;
            if (entity.field_40059_f < 0.5F || level.getRandom().nextInt(40) == 0) {
                float previous = entity.field_40061_d;
                do {
                    entity.field_40061_d += level.getRandom().nextInt(4) - level.getRandom().nextInt(4);
                } while (previous == entity.field_40061_d);
            }
        } else {
            entity.field_40066_q += 0.01F;
        }
        while (entity.rota >= (float) Math.PI) {
            entity.rota -= (float) (Math.PI * 2);
        }
        while (entity.rota < -(float) Math.PI) {
            entity.rota += (float) (Math.PI * 2);
        }
        while (entity.field_40066_q >= (float) Math.PI) {
            entity.field_40066_q -= (float) (Math.PI * 2);
        }
        while (entity.field_40066_q < -(float) Math.PI) {
            entity.field_40066_q += (float) (Math.PI * 2);
        }
        float diff = entity.field_40066_q - entity.rota;
        while (diff < -(float) Math.PI) {
            diff += (float) (Math.PI * 2);
        }
        while (diff >= (float) Math.PI) {
            diff -= (float) (Math.PI * 2);
        }
        entity.rota += diff * 0.04F;
    }

    @Override
    public void addSoulBits(int bits) {
        for (int i = 0; i < bits; i++) {
            if (Math.random() >= 0.97) {
                if (level != null) {
                    THSounds.play(level, worldPosition, "write", SoundSource.BLOCKS, 0.2F,
                        level.getRandom().nextFloat());
                }
                progress++;
            }
        }
        if (progress >= PROGRESS_MAX) {
            progress -= PROGRESS_MAX;
            ItemStack knowledge = knowledgeFragment();
            if (inventory.getItem(KNOWLEDGE_SLOT).isEmpty()) {
                inventory.setItem(KNOWLEDGE_SLOT, knowledge);
            } else {
                inventory.getItem(KNOWLEDGE_SLOT).grow(1);
            }
            inventory.getItem(PAPER_SLOT).shrink(1);
        }
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private static ItemStack knowledgeFragment() {
        net.minecraft.world.item.Item item = Compat.port().items().lookup(KNOWLEDGE_FRAGMENT);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    @Override
    public boolean canAcceptSouls() {
        return !inventory.getItem(PAPER_SLOT).isEmpty() && inventory.getItem(PAPER_SLOT).getCount() > 0
            && (inventory.getItem(KNOWLEDGE_SLOT).isEmpty() || inventory.getItem(KNOWLEDGE_SLOT).getCount() < 64);
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        if (slot == PAPER_SLOT) {
            return stack.is(net.minecraft.world.item.Items.PAPER);
        }
        return slot == KNOWLEDGE_SLOT && ItemStack.isSameItem(stack, knowledgeFragment());
    }

    public int getTimeRemainingScaled(int scale) {
        return progress * scale / PROGRESS_MAX;
    }

    public void dropContents(Level dropLevel, Vec3 pos) {
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty()) {
                dropLevel.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(dropLevel, pos.x, pos.y, pos.z, stack));
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        progress = input.getIntOr("progress", 0);
        inventory.setItem(0, input.read("paper", ItemStack.CODEC).orElse(ItemStack.EMPTY));
        inventory.setItem(1, input.read("knowledge", ItemStack.CODEC).orElse(ItemStack.EMPTY));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("progress", progress);
        if (!inventory.getItem(0).isEmpty()) {
            output.store("paper", ItemStack.CODEC, inventory.getItem(0));
        }
        if (!inventory.getItem(1).isEmpty()) {
            output.store("knowledge", ItemStack.CODEC, inventory.getItem(1));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory,
        Player player) {
        return new dev.visfox.thaumichorizons.container.InspiratronMenu(containerId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumichorizons.inspiratron");
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inventory.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
            && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }
}
