package dev.visfox.thaumichorizons.block.pocket;

import com.mojang.serialization.MapCodec;
import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jspecify.annotations.Nullable;

public class GatewayPortalBlock extends Block {
    public static final MapCodec<GatewayPortalBlock> CODEC = simpleCodec(GatewayPortalBlock::new);
    public static final IntegerProperty MD = IntegerProperty.create("gateway_md", 0, 14);

    public GatewayPortalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MD, 0));
    }

    @Override
    protected MapCodec<? extends GatewayPortalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MD);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!state.is(newState.getBlock())) {
            int md = state.getValue(MD);
            int slotX = 0;
            int slotY = 0;
            int slotZ = 0;
            boolean xAligned = isXAligned(level, pos);
            if (md < 5) {
                slotY = pos.getY() + md;
                if (xAligned) {
                    slotX = pos.getX() + 2;
                    slotZ = pos.getZ();
                } else {
                    slotX = pos.getX();
                    slotZ = pos.getZ() + 2;
                }
            } else if (md == 5) {
                slotY = pos.getY();
                if (xAligned) {
                    slotX = pos.getX() + 1;
                    slotZ = pos.getZ();
                } else {
                    slotX = pos.getX();
                    slotZ = pos.getZ() + 1;
                }
            } else if (md == 8) {
                slotY = pos.getY();
                if (xAligned) {
                    slotX = pos.getX() - 1;
                    slotZ = pos.getZ();
                } else {
                    slotX = pos.getX();
                    slotZ = pos.getZ() - 1;
                }
            } else if (md == 6 || md == 7 || md == 9) {
                slotY = pos.getY() + 4;
                if (xAligned) {
                    slotZ = pos.getZ();
                    slotX = switch (md) {
                        case 6 -> pos.getX() + 1;
                        case 7 -> pos.getX();
                        default -> pos.getX() - 1;
                    };
                } else {
                    slotX = pos.getX();
                    slotZ = switch (md) {
                        case 6 -> pos.getZ() + 1;
                        case 7 -> pos.getZ();
                        default -> pos.getZ() - 1;
                    };
                }
            } else {
                slotY = pos.getY() + md - 10;
                if (xAligned) {
                    slotX = pos.getX() - 2;
                    slotZ = pos.getZ();
                } else {
                    slotX = pos.getX();
                    slotZ = pos.getZ() - 2;
                }
            }
            BlockEntity te = level.getBlockEntity(new BlockPos(slotX, slotY, slotZ));
            if (te instanceof SlotBlockEntity slot && slot.portalOpen) {
                slot.destroyPortal();
            }
        }
    }

    static boolean isXAligned(Level level, BlockPos pos) {
        return neighborIsFrame(level, pos.getX() + 1, pos.getY(), pos.getZ())
            || neighborIsFrame(level, pos.getX() - 1, pos.getY(), pos.getZ());
    }

    static boolean neighborIsFrame(Level level, int x, int y, int z) {
        BlockState state = level.getBlockState(new BlockPos(x, y, z));
        return state.getBlock() instanceof GatewayPortalBlock || state.getBlock() instanceof PortalTHBlock;
    }

    @Nullable
    protected SlotBlockEntity slotAt(Level level, int x, int y, int z) {
        BlockEntity te = level.getBlockEntity(new BlockPos(x, y, z));
        return te instanceof SlotBlockEntity slot ? slot : null;
    }
}
