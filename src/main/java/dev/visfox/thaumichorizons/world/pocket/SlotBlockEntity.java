package dev.visfox.thaumichorizons.world.pocket;
import dev.visfox.thaumichorizons.registry.PocketRegistry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.ThaumicHorizonsConfig;
import dev.visfox.thaumichorizons.block.pocket.SlotBlock;
import dev.visfox.thaumichorizons.item.pocket.KeystoneItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.platform.Teleport;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import dev.visfox.thaumichorizons.block.pocket.PortalTHBlock;

public class SlotBlockEntity extends BlockEntity {
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

    public static void makePortal(Level level, BlockPos pos, Player player) {
        if (!(level.getBlockEntity(pos) instanceof SlotBlockEntity slot) || level.isClientSide()) {
            return;
        }
        if (slot.portalOpen) {
            return;
        }
        int portalNum = PocketPlanes.firstAvailablePortal(slot.pocketID);
        if (portalNum == 0) {
            return;
        }
        ItemStack wand = player.getMainHandItem();
        if (!Wands.isWand(wand)) {
            return;
        }
        AspectList cost = new AspectList()
            .add(Aspect.WATER, 250).add(Aspect.EARTH, 250).add(Aspect.ORDER, 250)
            .add(Aspect.FIRE, 250).add(Aspect.AIR, 250).add(Aspect.ENTROPY, 250);
        if (!(level instanceof ServerLevel server) || !thaumcraft.wand.WandManager.consumeVisFromInventory(player, cost)) {
            return;
        }
        boolean made = false;
        if (slot.checkPortalX()) {
            slot.xAligned = true;
            int md = 0;
            for (int x = -2; x <= 2; x++) {
                for (int y = 0; y >= -4; y--) {
                    BlockPos target = pos.offset(x, y, 0);
                    if (!level.getBlockState(target).isAir() && (x != 0 || y != 0)) {
                        level.setBlock(target, PocketRegistry.GATEWAY_TH.get().defaultBlockState()
                            .setValue(PortalTHBlock.MD, md), 3);
                        md++;
                    } else if (x != 0 || y != 0) {
                        level.setBlock(target, PocketRegistry.PORTAL_TH.get().defaultBlockState(), 3);
                    }
                }
            }
            made = true;
        } else if (slot.checkPortalZ()) {
            slot.xAligned = false;
            int md = 0;
            for (int z = -2; z <= 2; z++) {
                for (int y = 0; y >= -4; y--) {
                    BlockPos target = pos.offset(0, y, z);
                    if (!level.getBlockState(target).isAir() && (z != 0 || y != 0)) {
                        level.setBlock(target, PocketRegistry.GATEWAY_TH.get().defaultBlockState()
                            .setValue(PortalTHBlock.MD, md), 3);
                        md++;
                    } else if (z != 0 || y != 0) {
                        level.setBlock(target, PocketRegistry.PORTAL_TH.get().defaultBlockState(), 3);
                    }
                }
            }
            made = true;
        }
        if (made) {
            slot.portalOpen = true;
            slot.which = portalNum;
            slot.setChanged();
            PocketPlanes.makePortal(slot.pocketID, portalNum, pos.getX(), pos.getY(), pos.getZ(), server.getServer());
            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
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
            coreZ(level, pos, 0, -1);
            coreZ(level, pos, 1, -1);
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

    private static void frameX(Level level, BlockPos pos, int dx, int dy) {
        level.setBlockAndUpdate(pos.offset(dx, dy, 0), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState());
    }

    private static void frameZ(Level level, BlockPos pos, int dz, int dy) {
        level.setBlockAndUpdate(pos.offset(0, dy, dz), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState());
    }

    private static void coreX(Level level, BlockPos pos, int dx, int dy) {
        level.setBlockAndUpdate(pos.offset(dx, dy, 0), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState());
    }

    private static void coreZ(Level level, BlockPos pos, int dz, int dy) {
        level.setBlockAndUpdate(pos.offset(0, dy, dz), net.minecraft.world.level.block.Blocks.IRON_BARS.defaultBlockState());
    }

    public boolean checkPortalX() {
        for (int x = -2; x <= 2; x++) {
            for (int y = 0; y >= -4; y--) {
                if (x != 0 || y != 0) {
                    if (level == null || level.getBlockState(worldPosition.offset(x, y, 0)).isAir()) {
                        return false;
                    }
                }
            }
        }
        for (int y = 0; y >= -4; y--) {
            for (int z = -2; z <= 2; z++) {
                if (level == null || !level.getBlockState(worldPosition.offset(0, y, z)).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean checkPortalZ() {
        for (int z = -2; z <= 2; z++) {
            for (int y = 0; y >= -4; y--) {
                if (z != 0 || y != 0) {
                    if (level == null || level.getBlockState(worldPosition.offset(0, y, z)).isAir()) {
                        return false;
                    }
                }
            }
        }
        for (int y = 0; y >= -4; y--) {
            for (int x = -2; x <= 2; x++) {
                if (level == null || !level.getBlockState(worldPosition.offset(x, y, 0)).isAir()) {
                    return false;
                }
            }
        }
        return true;
    }

    public InteractionResult useWithoutItem(Player player) {
        if (!(level instanceof ServerLevel server) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof KeystoneItem && !hasKeystone) {
            hasKeystone = true;
            pocketID = KeystoneItem.getPlaneID(main);
            setChanged();
            return InteractionResult.SUCCESS;
        }
        if (hasKeystone && portalOpen) {
            Teleport.to(serverPlayer, server.getLevel(PocketDimension.POCKET), serverPlayer.position(), 0, 0);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
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