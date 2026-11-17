package dev.visfox.thaumichorizons.block.pocket;

import dev.visfox.thaumichorizons.world.pocket.PocketDimension;
import dev.visfox.thaumichorizons.world.pocket.PocketPlaneData;
import dev.visfox.thaumichorizons.world.pocket.PocketPlanes;
import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import thaumcraft.platform.Teleport;

public class PortalTHBlock extends Block {
    public static final IntegerProperty MD = IntegerProperty.create("gateway_md", 0, 14);
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);
    private static final int PORTAL_COOLDOWN = 10;

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
        if (level.isClientSide() || !(entity instanceof ServerPlayer player)
            || player.isPassenger() || player.hasPassengers()) {
            return;
        }
        if (player.getPortalCooldown() > 0) {
            player.setPortalCooldown(PORTAL_COOLDOWN);
            return;
        }

        ServerLevel source = (ServerLevel) level;
        if (source.dimension().equals(PocketDimension.POCKET_PLANE)) {
            leavePocketPlane(source, player, state, pos);
        } else {
            enterPocketPlane(source, player, pos);
        }
    }

    private static void leavePocketPlane(ServerLevel source, ServerPlayer player, BlockState state, BlockPos portalPos) {
        int planeID = PocketPlanes.planeIndexAt(portalPos.getZ());
        if (!PocketPlanes.validPlane(planeID)) {
            return;
        }

        int which = switch (state.getValue(MD)) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 2;
            case 3 -> 4;
            default -> 0;
        };
        if (which == 0) {
            return;
        }

        PocketPlaneData data = PocketPlanes.planes.get(planeID);
        int[] endpoint = data.portal(which);
        String dimensionName = data.portalDimension(which);
        if (endpoint == null || endpoint.length < 4 || dimensionName.isBlank()) {
            return;
        }
        Identifier dimensionId = Identifier.tryParse(dimensionName);
        if (dimensionId == null) {
            return;
        }
        ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, dimensionId);
        ServerLevel target = source.getServer().getLevel(dimensionKey);
        if (target == null) {
            return;
        }

        float yaw = endpoint[3] != 0 ? 0.0F : 270.0F;
        Vec3 destination = new Vec3(endpoint[0] + 0.5, endpoint[1] - 2.0, endpoint[2] + 0.5);
        teleport(player, target, destination, yaw);
    }

    private static void enterPocketPlane(ServerLevel source, ServerPlayer player, BlockPos portalPos) {
        SlotBlockEntity slot = findSourceSlot(source, portalPos);
        if (slot == null || !slot.hasKeystone() || !slot.portalOpen()
            || !PocketPlanes.validPlane(slot.pocketID()) || slot.which() < 1 || slot.which() > 4) {
            return;
        }

        int planeID = slot.pocketID();
        PocketPlaneData data = PocketPlanes.planes.get(planeID);
        int radius = data.radius;
        int centerZ = planeID * 256;
        BlockPos destination;
        float yaw;
        switch (slot.which()) {
            case 1 -> {
                destination = new BlockPos(0, 128, centerZ + radius);
                yaw = 180.0F;
            }
            case 2 -> {
                destination = new BlockPos(0, 128, centerZ - radius);
                yaw = 0.0F;
            }
            case 3 -> {
                destination = new BlockPos(radius, 128, centerZ);
                yaw = 90.0F;
            }
            case 4 -> {
                destination = new BlockPos(-radius, 128, centerZ);
                yaw = 270.0F;
            }
            default -> {
                return;
            }
        }

        ServerLevel pocket = PocketDimension.level(source.getServer());
        if (pocket == null) {
            return;
        }
        teleport(player, pocket, Vec3.atBottomCenterOf(destination), yaw);
    }

    private static SlotBlockEntity findSourceSlot(ServerLevel level, BlockPos portalPos) {
        BlockPos upper = portalPos;
        int limit = 5;
        while (limit-- > 0 && level.getBlockState(upper).getBlock() instanceof PortalTHBlock) {
            upper = upper.above();
        }

        BlockEntity direct = level.getBlockEntity(upper);
        if (direct instanceof SlotBlockEntity slot) {
            return slot;
        }
        if (!(level.getBlockState(upper).getBlock() instanceof GatewayPortalBlock)) {
            return null;
        }
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockEntity adjacent = level.getBlockEntity(upper.relative(direction));
            if (adjacent instanceof SlotBlockEntity slot) {
                return slot;
            }
        }
        return null;
    }

    private static void teleport(ServerPlayer player, ServerLevel target, Vec3 destination, float yaw) {
        Entity teleported = Teleport.to(player, target, destination, yaw, 0.0F);
        if (teleported != null) {
            teleported.setPortalCooldown(PORTAL_COOLDOWN);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MD);
    }
}
