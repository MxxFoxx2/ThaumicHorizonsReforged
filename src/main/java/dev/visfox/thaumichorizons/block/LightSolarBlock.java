package dev.visfox.thaumichorizons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class LightSolarBlock extends IlluminationLightBlock {

    public LightSolarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)) {
            entity.igniteForSeconds(5.0F);
        }
    }
}
