package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.essentia.EssentiaTransport;

public class VatConnectorBlockEntity extends VatSlaveBlockEntity implements EssentiaTransport {
    public VatConnectorBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.VAT_CONNECTOR.get(), pos, state);
    }

    @Override
    public boolean isConnectable(@Nullable Direction face) {
        return true;
    }

    @Override
    public boolean canInputFrom(@Nullable Direction face) {
        return true;
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
        VatBlockEntity boss = getBoss(-1);
        if (boss != null) {
            return boss.getSuctionType(face);
        }
        return null;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        VatBlockEntity boss = getBoss(-1);
        if (boss != null) {
            return boss.getSuctionAmount(face);
        }
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        VatBlockEntity boss = getBoss(-1);
        if (boss != null) {
            return boss.addEssentia(aspect, amount, face);
        }
        return 0;
    }

    @Override
    public @Nullable Aspect getEssentiaType(@Nullable Direction face) {
        return null;
    }

    @Override
    public int getEssentiaAmount(@Nullable Direction face) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }
}
