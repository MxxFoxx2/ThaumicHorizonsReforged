package dev.visfox.thaumichorizons.block.pocket;

import dev.visfox.thaumichorizons.world.pocket.PocketDimension;
import dev.visfox.thaumichorizons.world.pocket.PocketPlanes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.core.Direction;

public class PortalTHBlock extends Block {
    public static final IntegerProperty MD = IntegerProperty.create("gateway_md", 0, 14);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public PortalTHBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MD, 0));
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return Shapes.empty();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
            net.minecraft.world.entity.InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (level.isClientSide() || !(entity instanceof ServerPlayer player) || player.isSpectator()) {
            return;
        }
        ServerLevel server = (ServerLevel) level;
        PocketPlanes planes = PocketPlanes.get(server.getServer());
        int planeNum = PocketPlanes.planeIndexAt(pos.getZ());
        if (planeNum < 0 || !planes.validPlane(planeNum)) {
            return;
        }
        int which = state.getValue(MD);
        BlockPos returnPos = PocketPlanes.returnPosition(planeNum);
        if (returnPos == null) {
            return;
        }
        ServerLevel pocket = PocketDimension.level(server.getServer());
        if (pocket != null) {
            thaumcraft.platform.Teleport.to(player, pocket, player.position(), player.getYRot(), player.getXRot());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MD);
    }
}