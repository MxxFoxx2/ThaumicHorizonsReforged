package dev.visfox.thaumichorizons.world.pocket;

import dev.visfox.thaumichorizons.lib.TagInput;

import dev.visfox.thaumichorizons.block.pocket.PortalTHBlock;
import dev.visfox.thaumichorizons.block.pocket.GatewayPortalBlock;
import dev.visfox.thaumichorizons.block.pocket.PortalTHBlockEntity;
import dev.visfox.thaumichorizons.item.pocket.KeystoneItem;
import dev.visfox.thaumichorizons.lib.TcBlocks;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import dev.visfox.thaumichorizons.world.pocket.PocketDimension;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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
    private int keystoneColor = 0xFFFFFF;
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

    public int keystoneColor() {
        return keystoneColor;
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
                            .setValue(PortalTHBlock.MD, md).setValue(GatewayPortalBlock.X_ALIGNED, true), 3);
                        md++;
                    } else if (x != 0 || y != 0) {
                        level.setBlock(target, PocketRegistry.PORTAL_TH.get().defaultBlockState(), 3);
                        configurePortal(target, pocketID, portalNum, level.dimension().location().toString());
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
                            .setValue(PortalTHBlock.MD, md).setValue(GatewayPortalBlock.X_ALIGNED, false), 3);
                        md++;
                    } else if (z != 0 || y != 0) {
                        level.setBlock(target, PocketRegistry.PORTAL_TH.get().defaultBlockState(), 3);
                        configurePortal(target, pocketID, portalNum, level.dimension().location().toString());
                    }
                }
            }
        }

        String sourceDimension = level.dimension().location().toString();
        PocketPlanes.makePortal(pocketID, portalNum, worldPosition.getX(), worldPosition.getY(),
            worldPosition.getZ(), sourceDimension, xAligned, server.getServer());
        portalOpen = true;
        which = portalNum;
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        level.playSound(null, worldPosition, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return true;
    }

    private void configurePortal(BlockPos target, int plane, int portal, String dimension) {
        if (level != null && level.getBlockEntity(target) instanceof PortalTHBlockEntity portalEntity) {
            portalEntity.configure(worldPosition, plane, portal, dimension, xAligned);
        }
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
        // Close the source endpoint before replacing GatewayPortal blocks.
        // Their affectNeighborsAfterRemoval callback also requests teardown;
        // leaving portalOpen true here would re-enter this method while the
        // frame is being restored with setBlockAndUpdate().
        slot.portalOpen = false;
        slot.setChanged();

        if (slot.xAligned) {
            fillFrame(level, pos, true);
        } else {
            fillFrame(level, pos, false);
        }
        PocketPlanes.destroyPortal(slot.pocketID, slot.which, level.getServer());
        level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.sendBlockUpdated(pos, slot.getBlockState(), slot.getBlockState(), 3);
    }

    /**
     * Cleans a portal when its source endpoint outlives or loses its Slot block entity.
     *
     * @return false when part of the frame is in an unloaded chunk; the caller
     *         should retry later instead of deleting the last recovery point.
     */
    public static boolean destroyPortalFromEndpoint(ServerLevel level, BlockPos pos, boolean xAligned,
            int pocketID, int which) {
        if (level.isClientSide() || !portalFootprintLoaded(level, pos, xAligned)) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SlotBlockEntity slot && slot.portalOpen) {
            destroyPortal(level, pos);
            return true;
        }

        if (hasPortalStructure(level, pos, xAligned)) {
            fillFrame(level, pos, xAligned);
        }
        PocketPlanes.destroyPortal(pocketID, which, level.getServer());
        return true;
    }

    private static boolean portalFootprintLoaded(Level level, BlockPos pos, boolean xAligned) {
        for (int offset = -2; offset <= 2; offset++) {
            for (int y = 0; y >= -4; y--) {
                BlockPos framePos = xAligned ? pos.offset(offset, y, 0) : pos.offset(0, y, offset);
                if (!level.hasChunkAt(framePos)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean hasPortalStructure(Level level, BlockPos pos, boolean xAligned) {
        for (int offset = -2; offset <= 2; offset++) {
            for (int y = 0; y >= -4; y--) {
                BlockPos framePos = xAligned ? pos.offset(offset, y, 0) : pos.offset(0, y, offset);
                Block block = level.getBlockState(framePos).getBlock();
                if (block instanceof GatewayPortalBlock || block instanceof PortalTHBlock) {
                    return true;
                }
            }
        }
        return false;
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
            for (int y = -1; y >= -3; y--) {
                coreX(level, pos, -2, y);
                coreX(level, pos, 2, y);
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
            for (int y = -1; y >= -3; y--) {
                coreZ(level, pos, -2, y);
                coreZ(level, pos, 2, y);
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

    public boolean insertKeystone(int planeID, int color) {
        if (hasKeystone || !PocketPlanes.validPlane(planeID)) {
            return false;
        }
        hasKeystone = true;
        pocketID = planeID;
        keystoneColor = color & 0xFFFFFF;
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
        keystoneColor = 0xFFFFFF;
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
        int planeID = pocketID;
        int color = keystoneColor;
        removeKeystone();
        ItemStack keystone = KeystoneItem.create(planeID, color);
        if (!player.getInventory().add(keystone)) {
            level.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(level,
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5, keystone));
        }
        return InteractionResult.SUCCESS;
    }

    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel server)) {
            return;
        }

        // Match the legacy BlockSlot break behavior: close both portal ends and
        // drop the plane-bound Keystone instead of silently deleting it.
        if (portalOpen) {
            destroyPortal(server, pos);
        }
        if (hasKeystone) {
            int planeID = pocketID;
            int color = keystoneColor;
            removeKeystone();
            ItemStack keystone = KeystoneItem.create(planeID, color);
            server.addFreshEntity(new ItemEntity(server, pos.getX() + 0.5D, pos.getY() + 0.5D,
                pos.getZ() + 0.5D, keystone));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveAdditional(dev.visfox.thaumichorizons.compat.ValueOutput.of(tag, registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadAdditional(dev.visfox.thaumichorizons.compat.ValueInput.of(tag, registries));
    }

    protected void saveAdditional(dev.visfox.thaumichorizons.compat.ValueOutput output) {
        super.saveAdditional(output.tag(), output.lookup());
        output.putBoolean("hasKeystone", hasKeystone);
        output.putBoolean("portalOpen", portalOpen);
        output.putInt("pocketPlaneID", pocketID);
        output.putInt("keystoneColor", keystoneColor);
        output.putInt("which", which);
        output.putBoolean("aligned", xAligned);
    }

    protected void loadAdditional(dev.visfox.thaumichorizons.compat.ValueInput input) {
        super.loadAdditional(input.tag(), input.lookup());
        hasKeystone = input.getBooleanOr("hasKeystone", false);
        portalOpen = input.getBooleanOr("portalOpen", false);
        pocketID = input.getIntOr("pocketPlaneID", 0);
        keystoneColor = input.getIntOr("keystoneColor", 0xFFFFFF) & 0xFFFFFF;
        which = input.getIntOr("which", 0);
        xAligned = input.getBooleanOr("aligned", false);
    }
}