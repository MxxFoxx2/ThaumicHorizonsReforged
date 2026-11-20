package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.block.IlluminationLightBlock;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.item.focus.FocusIlluminationItem;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Client-side ambient visual for an invisible Illumination Focus light. */
public final class IlluminationLightBlockEntity extends BlockEntity {
    public IlluminationLightBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.ILLUMINATION_LIGHT.get(), pos, state);
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, IlluminationLightBlockEntity entity) {
        if (level.getRandom().nextInt(9) != 0 || !state.hasProperty(IlluminationLightBlock.COLOR)) {
            return;
        }
        var fx = TcReforgedPort.fx;
        if (!fx.isAvailable()) {
            return;
        }
        int color = FocusIlluminationItem.COLORS[state.getValue(IlluminationLightBlock.COLOR)];
        fx.sparkle(level, pos.getX() + 0.55F, pos.getY() + 0.5F, pos.getZ() + 0.55F, 0.5F, color, 0.0F);
    }
}
