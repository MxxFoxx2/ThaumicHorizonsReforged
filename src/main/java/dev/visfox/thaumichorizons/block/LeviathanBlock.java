package dev.visfox.thaumichorizons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class LeviathanBlock extends Block {

    public LeviathanBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        double d0 = 0.0625;
        for (int l = 0; l < 6; ++l) {
            double d2 = pos.getX() + random.nextFloat();
            double d3 = pos.getY() + random.nextFloat();
            double d4 = pos.getZ() + random.nextFloat();
            if (l == 0 && !level.getBlockState(pos.above()).isSolidRender(level, pos.above())) {
                d3 = pos.getY() + 1 + d0;
            }
            if (l == 1 && !level.getBlockState(pos.below()).isSolidRender(level, pos.below())) {
                d3 = pos.getY() - d0;
            }
            if (l == 2 && !level.getBlockState(pos.south()).isSolidRender(level, pos.south())) {
                d4 = pos.getZ() + 1 + d0;
            }
            if (l == 3 && !level.getBlockState(pos.north()).isSolidRender(level, pos.north())) {
                d4 = pos.getZ() - d0;
            }
            if (l == 4 && !level.getBlockState(pos.east()).isSolidRender(level, pos.east())) {
                d2 = pos.getX() + 1 + d0;
            }
            if (l == 5 && !level.getBlockState(pos.west()).isSolidRender(level, pos.west())) {
                d2 = pos.getX() - d0;
            }
            if (random.nextInt(10) == 0 && (d2 < pos.getX() || d2 > pos.getX() + 1
                || d3 < 0.0
                || d3 > pos.getY() + 1
                || d4 < pos.getZ()
                || d4 > pos.getZ() + 1)) {
                level.addParticle(ParticleTypes.SMOKE, d2, d3, d4, 0.0, 0.0, 0.0);
            }
        }
    }
}
