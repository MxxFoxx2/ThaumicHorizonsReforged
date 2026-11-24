package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.item.SoulJarKeys;
import dev.visfox.thaumichorizons.item.SoulJarKeys.SoulJarData;
import dev.visfox.thaumichorizons.blockentity.ThBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.essentia.Essentia;
import thaumcraft.essentia.EssentiaTransport;

public class SoulforgeBlockEntity extends ThBlockEntity implements SoulReceiver, EssentiaTransport {
    public static final int PROGRESS_MAX = 9600;
    public static final int MAX_SOULS = 16;
    public static final int ESSENTIA_MAX = 4000;
    public static final int ESSENTIA_DRAW_THRESHOLD = 3000;
    public static final int ESSENTIA_PER_ASPECT = 1000;
    public static final int SUCTION_PRESSURE = 128;
    public static final double PLAYER_REACH_SQ = 64.0;

    int progress;
    public int souls;
    int essentia;
    public float rota;
    public int forging = 3;

    public SoulforgeBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.SOULFORGE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoulforgeBlockEntity entity) {
        entity.tick(level);
    }

    private void tick(Level tickLevel) {
        if (!tickLevel.isClientSide() && essentia < ESSENTIA_DRAW_THRESHOLD) {
            drawEssentia();
        }
        if (forging > 0) {
            rota++;
            if (rota > 360.0F) {
                rota -= 360.0F;
            }
            forging--;
        }
    }

    public boolean activate(Level level, Player player, ItemStack held) {
        if (!TcReforgedPort.items.isEssentiaJar(held)) {
            return false;
        }
        if (souls > 0) {
            ItemStack soul = new ItemStack(dev.visfox.thaumichorizons.registry.ThBlocks.SOUL_JAR.get());
            List<VillagerProfession> professions =
                List.copyOf(BuiltInRegistries.VILLAGER_PROFESSION.stream().toList());
            if (professions.isEmpty()) {
                return false;
            }
            VillagerProfession profession = professions.get(level.getRandom().nextInt(professions.size()));
            String professionId = String.valueOf(BuiltInRegistries.VILLAGER_PROFESSION.getKey(profession));
            SoulJarData data = new SoulJarData(new CompoundTag(), true, "villager", professionId);
            SoulJarKeys.DATA.set(soul, data);
            held.shrink(1);
            if (!player.getInventory().add(soul)) {
                player.drop(soul, false);
            }
            souls--;
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return true;
        }
        return false;
    }

    @Override
    public void addSoulBits(int bits) {
        progress += bits;
        essentia -= bits;
        while (progress >= PROGRESS_MAX) {
            progress -= PROGRESS_MAX;
            souls++;
        }
        forging = 3;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public boolean canAcceptSouls() {
        return essentia > 0 && souls < MAX_SOULS;
    }

    public boolean isStillUsableByPlayer(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
            && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= PLAYER_REACH_SQ;
    }

    void drawEssentia() {
        if (level == null) {
            return;
        }
        EssentiaTransport ic = Essentia.getConnectable(level, worldPosition, Direction.UP);
        if (ic == null) {
            return;
        }
        Direction opposite = Direction.DOWN;
        if (!ic.canOutputTo(opposite)) {
            return;
        }
        Aspect ta = null;
        if (ic.getEssentiaAmount(opposite) > 0 && ic.getSuctionAmount(opposite) < getSuctionAmount(Direction.UP)
            && getSuctionAmount(Direction.UP) >= ic.getMinimumSuction()) {
            ta = ic.getEssentiaType(opposite);
        }
        if (ta != null && ta.getTag().equals(Aspect.MIND.getTag()) && ic.takeEssentia(ta, 1, opposite) == 1) {
            addEssentia(ta, 1, Direction.UP);
        }
    }

    @Override
    public boolean isConnectable(@Nullable Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canInputFrom(@Nullable Direction face) {
        return face == Direction.UP;
    }

    @Override
    public boolean canOutputTo(@Nullable Direction face) {
        return false;
    }

    @Override
    public void setSuction(@Nullable Aspect aspect, int amount) {
    }

    @Override
    public @Nullable Aspect getSuctionType(@Nullable Direction face) {
        return Aspect.MIND;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        return essentia < ESSENTIA_DRAW_THRESHOLD ? SUCTION_PRESSURE : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (essentia < ESSENTIA_DRAW_THRESHOLD) {
            essentia += ESSENTIA_PER_ASPECT;
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
            return 1;
        }
        return 0;
    }

    @Override
    public @Nullable Aspect getEssentiaType(@Nullable Direction face) {
        return Aspect.MIND;
    }

    @Override
    public int getEssentiaAmount(@Nullable Direction face) {
        return essentia / ESSENTIA_PER_ASPECT;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        souls = input.getIntOr("souls", 0);
        progress = input.getIntOr("progress", 0);
        essentia = input.getIntOr("essentia", 0);
        forging = input.getIntOr("forging", 3);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("souls", souls);
        output.putInt("progress", progress);
        output.putInt("essentia", essentia);
        output.putInt("forging", forging);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }
}
