package dev.visfox.thaumichorizons.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A legacy cloud block. The variant is kept in block state rather than legacy metadata.
 * Server-side weather processing is implemented by CloudBlockEntity in the follow-up pass.
 */
public class CloudBlock extends Block {
    public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 9);

    public CloudBlock(Properties properties, int defaultType) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TYPE, defaultType));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TYPE);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                           CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos,
                                  CollisionContext context) {
        return Shapes.block();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
                                InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        int type = state.getValue(TYPE);
        if (type == 1 || type == 4) {
            entity.setRemainingFireTicks(6 * 20);
        } else if (type == 3) {
            entity.hurt(level.damageSources().magic(), 1.0F);
        }
    }
}
