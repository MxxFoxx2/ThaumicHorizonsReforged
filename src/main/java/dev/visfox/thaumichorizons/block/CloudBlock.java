package dev.visfox.thaumichorizons.block;

import dev.visfox.thaumichorizons.blockentity.CloudBlockEntity;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.entity.InvulnerableItemEntity;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import thaumcraft.aspect.Aspect;
import thaumcraft.wand.WandInteractable;

/**
 * A legacy cloud block. The variant is kept in block state rather than legacy metadata.
 * Server-side weather processing is implemented by CloudBlockEntity in the follow-up pass.
 */
public class CloudBlock extends Block implements EntityBlock, WandInteractable {
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
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CloudBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        if (type != ThBlockEntities.CLOUD.get()) {
            return null;
        }
        return (tickLevel, pos, tickState, entity) ->
            CloudBlockEntity.tick(tickLevel, pos, tickState, (CloudBlockEntity) entity);
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, ItemStack wand, Direction side) {
        var items = Compat.port().items();
        var research = Compat.port().research();
        if (!items.isAvailable() || !research.isAvailable()
                || !items.isWand(wand) || !research.isComplete(player, "planarClouds")) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!Compat.port().items().consumeVis(wand, player, Aspect.AIR, 100, false)) {
            return InteractionResult.PASS;
        }

        int type = level.getBlockState(pos).getValue(TYPE);
        ItemStack cloud = new ItemStack(cloudItem(type));
        InvulnerableItemEntity item = new InvulnerableItemEntity(level,
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, cloud);
        level.addFreshEntity(item);
        level.removeBlock(pos, false);
        return InteractionResult.SUCCESS;
    }

    private static net.minecraft.world.item.Item cloudItem(int type) {
        return switch (type) {
            case 0 -> ThItems.CLOUD.get();
            case 1 -> ThItems.CLOUD_FIRE.get();
            case 2 -> ThItems.CLOUD_THUNDER.get();
            case 3 -> ThItems.CLOUD_ACID.get();
            case 4 -> ThItems.CLOUD_ALLOY.get();
            case 5 -> ThItems.CLOUD_FLESH.get();
            case 6 -> ThItems.CLOUD_VIS.get();
            case 7 -> ThItems.CLOUD_GLYPH.get();
            case 8 -> ThItems.CLOUD_SPORE.get();
            case 9 -> ThItems.CLOUD_ANIMUS.get();
            default -> throw new IllegalStateException("Unknown cloud type: " + type);
        };
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
