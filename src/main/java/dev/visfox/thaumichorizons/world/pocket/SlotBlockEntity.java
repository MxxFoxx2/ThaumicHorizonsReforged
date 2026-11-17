package dev.visfox.thaumichorizons.world.pocket;

import dev.visfox.thaumichorizons.block.pocket.PortalTHBlock;
import dev.visfox.thaumichorizons.item.pocket.KeystoneItem;
import dev.visfox.thaumichorizons.lib.TcBlocks;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import dev.visfox.thaumichorizons.world.pocket.PocketDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandInteractable;
import thaumcraft.wand.WandItem;

public class SlotBlockEntity extends BlockEntity implements WandInteractable {
    private boolean hasKeystone;
    private boolean portalOpen;
    private int pocketID;
    private int which;
    private boolean xAligned;

    public SlotBlockEntity(BlockPos pos, BlockState state) {
        super(PocketRegistry.SLOT_BE.get(), pos, state);
        this.hasKeystone = false;
        this.portalOpen = false;
        this.pocketID = 0;
        this.which = 0;
        this.xAligned = false;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SlotBlockEntity entity) {
    }

    public boolean hasKeystone() {
        return hasKeystone;
    }

    public boolean portalOpen() {
        return portalOpen;
    }

    public int pocketID() {
        return pocketID;
    }

    public int which() {
        return which;
    }

    public boolean xAligned() {
        return xAligned;
    }

    private boolean makePortal(Player player, ItemStack wand) {
        if (!(level instanceof ServerLevel server) || PocketDimension.level(server.getServer()) == null
                || portalOpen || !hasKeystone || !PocketPlanes.validPlane(pocketID)) {
            return false;
        }
        int portalNum = PocketPlanes.firstAvailablePortal(pocketID);
        if (portalNum == 0 || !(wand.getItem() instanceof WandItem wandItem)) {
            return false;
        }

        boolean xPlane = checkPortalX();
        boolean zPlane = !xPlane && checkPortalZ();
        if (!xPlane && !zPlane) {
            return false;
        }

        AspectList cost = PocketPlanes.primalCost();
        if (!wandItem.consumeAllVisCrafting(wand, player, cost, true)) {
            return false;
        }

        if (xPlane) {
            xAligned = true;
            int md = 0;
            for (int x = -2; x <= 2; x++) {
                for (int y = 0; y >= -4; y--) {
                    BlockPos target = worldPosition.offset(x, y, 0);
                    if (!level.getBlockState(target).isAir() && (x != 0 || y != 0)) {
                        level.setBlock(target, PocketRegistry.GATEWAY_TH.get().defaultBlockState()
                            .setValue(PortalTHBlock.MD, md), 3);
                        md++;
                    } else if (x != 0 || y != 0) {
                        level.setBlock(target, PocketRegistry.PORTAL_TH.get().defaultBlockState(), 3);
                    }
                }
            }
        } else {
            xAligned = false;
            int md = 0;
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y >= -4; y--) {
                    BlockPos target = worldPosition.offset(0, y, z);
                    if (!level.getBlockState(target).isAir() && (z != 0 || y != 0)) {
                        level.setBlock(target, PocketRegistry.GATEWAY_TH.get().defaultBlockState()
                            .setValue(PortalTHBlock.MD, md), 3);
                        md++;
                    } else if (z != 0 || y != 0) {
                        level.setBlock(target, PocketRegistry.PORTAL_TH.get().defaultBlockState(), 3);
                    }
                }
            }
        }

        String sourceDimension = level.dimension().identifier().toString();
        PocketPlanes.makePortal(pocketID, portalNum, worldPosition.getX(), worldPosition.getY(),
            worldPosition.getZ(), sourceDimension, xAligned, server.getServer());
        portalOpen = true;
        which = portalNum;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    @Override
    public InteractionResult onWandUse(Level level, BlockPos pos, Player player, ItemStack wand) {
        return makePortal(player, wand) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    public InteractionResult onWandUseOn(Level level, BlockPos pos, Player player, ItemStack wand,
            net.minecraft.core.Direction side) {
        return makePortal(player, wand) ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    public static void destroyPortal(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof SlotBlockEntity slot) || level.isClientSide() || !slot.portalOpen) {
            return;
        }
        if (slot.xAligned) {
            fillFrame(level, pos, true);
        } else {
            fillFrame(level, pos, false);
        }
        PocketPlanes.destroyPortal(slot.pocketID, slot.which, level.getServer());
        level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        slot.portalOpen = false;
        slot.setChanged();
    }

    private static void fillFrame(Level level, BlockPos pos, boolean xAligned) {
        if (xAligned) {
            frameX(level, pos, -2, 0);
            frameX(level, pos, 2, 0);
            frameX(level, pos, -2, -4);
            frameX(level, pos, 2, -4);
            coreX(level, pos, -1, 0);
            coreX(level, pos, 1, 0);
            coreX(level, pos, -1, -4);
            coreX(level, pos, 0, -4);
            coreX(level, pos, 1, -4);
            for (int x = -2; x <= 2; x++) {
                coreX(level, pos, x, -1);
                coreX(level, pos, x, -2);
                coreX(level, pos, x, -3);
            }
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y >= -3; y--) {
                    level.setBlockAndUpdate(pos.offset(x, y, 0), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
                }
            }
        } else {
            frameZ(level, pos, -2, 0);
            frameZ(level, pos, 2, 0);
            frameZ(level, pos, -2, -4);
            frameZ(level, pos, 2, -4);
            coreZ(level, pos, -1, 0);
            coreZ(level, pos, 1, 0);
            coreZ(level, pos, -1, -4);
            coreZ(level, pos, 0, -4);
            coreZ(level, pos, 1, -4);
            for (int z = -2; z <= 2; z++) {
                coreZ(level, pos, z, -1);
                coreZ(level, pos, z, -2);
                coreZ(level, pos, z, -3);
            }
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y >= -3; y--) {
                    level.setBlockAndUpdate(pos.offset(0, y, z), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    private static BlockState cornerFrameState() {
        Block block = TcBlocks.block("advanced_alchemical_construct");
        return (block == null ? Blocks.IRON_BLOCK : block).defaultBlockState();
    }

    private static BlockState edgeFrameState() {
        Block block = TcBlocks.block("ancient_stone");
        return (block == null ? Blocks.STONE_BRICKS : block).defaultBlockState();
    }

    private static void frameX(Level level, BlockPos pos, int dx, int dy) {
        level.setBlockAndUpdate(pos.offset(dx, dy, 0), cornerFrameState());
    }

    private static void frameZ(Level level, BlockPos pos, int dz, int dy) {
        level.setBlockAndUpdate(pos.offset(0, dy, dz), cornerFrameState());
    }

    private static void coreX(Level level, BlockPos pos, int dx, int dy) {
        level.setBlockAndUpdate(pos.offset(dx, dy, 0), edgeFrameState());
    }

    private static void coreZ(Level level, BlockPos pos, int dz, int dy) {
        level.setBlockAndUpdate(pos.offset(0, dy, dz), edgeFrameState());
    }

    public boolean checkPortalX() {
        if (level == null) {
            return false;
        }
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y >= -3; y--) {
                if (!level.getBlockState(worldPosition.offset(x, y, 0)).isAir()) {
                    return false;
                }
            }
        }
        Block corner = TcBlocks.block("advanced_alchemical_construct");
        Block edge = TcBlocks.block("ancient_stone");
        if (corner == null || edge == null) {
            return false;
        }
        for (int x : new int[] { -2, 2 }) {
            if (!isFrameBlock(x, 0, corner) || !isFrameBlock(x, -4, corner)) {
                return false;
            }
            for (int y = -1; y >= -3; y--) {
                if (!isFrameBlock(x, y, edge)) {
                    return false;
                }
            }
        }
        return isFrameBlock(-1, 0, edge) && isFrameBlock(1, 0, edge)
            && isFrameBlock(-1, -4, edge) && isFrameBlock(0, -4, edge) && isFrameBlock(1, -4, edge);
    }

    public boolean checkPortalZ() {
        if (level == null) {
            return false;
        }
        for (int z = -1; z <= 1; z++) {
            for (int y = -1; y >= -3; y--) {
                if (!level.getBlockState(worldPosition.offset(0, y, z)).isAir()) {
                    return false;
                }
            }
        }
        Block corner = TcBlocks.block("advanced_alchemical_construct");
        Block edge = TcBlocks.block("ancient_stone");
        if (corner == null || edge == null) {
            return false;
        }
        for (int z : new int[] { -2, 2 }) {
            if (!isFrameBlockZ(z, 0, corner) || !isFrameBlockZ(z, -4, corner)) {
                return false;
            }
            for (int y = -1; y >= -3; y--) {
                if (!isFrameBlockZ(z, y, edge)) {
                    return false;
                }
            }
        }
        return isFrameBlockZ(-1, 0, edge) && isFrameBlockZ(1, 0, edge)
            && isFrameBlockZ(-1, -4, edge) && isFrameBlockZ(0, -4, edge) && isFrameBlockZ(1, -4, edge);
    }

    private boolean isFrameBlock(int xOffset, int yOffset, Block expected) {
        return level != null && level.getBlockState(worldPosition.offset(xOffset, yOffset, 0)).getBlock() == expected;
    }

    private boolean isFrameBlockZ(int zOffset, int yOffset, Block expected) {
        return level != null && level.getBlockState(worldPosition.offset(0, yOffset, zOffset)).getBlock() == expected;
    }

    public boolean insertKeystone(int planeID) {
        if (hasKeystone || !PocketPlanes.validPlane(planeID)) {
            return false;
        }
        hasKeystone = true;
        pocketID = planeID;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return true;
    }

    public int removeKeystone() {
        int removed = pocketID;
        if (portalOpen && level != null) {
            destroyPortal(level, worldPosition);
        }
        hasKeystone = false;
        pocketID = 0;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return removed;
    }

    public InteractionResult useWithoutItem(Player player) {
        if (level == null || level.isClientSide() || !hasKeystone) {
            return InteractionResult.PASS;
        }
        ItemStack keystone = KeystoneItem.create(removeKeystone());
        if (!player.getInventory().add(keystone)) {
            level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(level,
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5, keystone));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (!(level instanceof ServerLevel server)) {
            return;
        }

        // Match the legacy BlockSlot break behavior: close both portal ends and
        // drop the plane-bound Keystone instead of silently deleting it.
        if (portalOpen) {
            destroyPortal(server, pos);
        }
        if (hasKeystone) {
            int planeID = removeKeystone();
            ItemStack keystone = KeystoneItem.create(planeID);
            server.addFreshEntity(new ItemEntity(server, pos.getX() + 0.5D, pos.getY() + 0.5D,
                pos.getZ() + 0.5D, keystone));
        }
    }

    @Override
    protected void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("hasKeystone", hasKeystone);
        output.putBoolean("portalOpen", portalOpen);
        output.putInt("pocketPlaneID", pocketID);
        output.putInt("which", which);
        output.putBoolean("aligned", xAligned);
    }

    @Override
    protected void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        hasKeystone = input.getBooleanOr("hasKeystone", false);
        portalOpen = input.getBooleanOr("portalOpen", false);
        pocketID = input.getIntOr("pocketPlaneID", 0);
        which = input.getIntOr("which", 0);
        xAligned = input.getBooleanOr("aligned", false);
    }
}