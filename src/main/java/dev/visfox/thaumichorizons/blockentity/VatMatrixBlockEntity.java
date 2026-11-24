package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.vis.VisNode;
import thaumcraft.wand.WandInteractable;

public class VatMatrixBlockEntity extends VisNode implements WandInteractable {
    public VatMatrixBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.VAT_MATRIX.get(), pos, state);
    }

    public @Nullable VatBlockEntity getVat() {
        if (level == null) {
            return null;
        }
        BlockEntity below = level.getBlockEntity(worldPosition.below());
        return below instanceof VatBlockEntity vat ? vat : null;
    }

    public @Nullable AspectList getAspects() {
        VatBlockEntity vat = getVat();
        if (vat != null) {
            return vat.getAspects();
        }
        return null;
    }

    @Override
    public InteractionResult onWandUse(Level level, BlockPos pos, Player player, ItemStack wand) {
        VatBlockEntity vat = getVat();
        if (vat != null && ((vat.isValidInfusionTarget() && vat.mode == 0) || vat.mode == 4)) {
            vat.startInfusion(player);
        }
        return InteractionResult.SUCCESS;
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
