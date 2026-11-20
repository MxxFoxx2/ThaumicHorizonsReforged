package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.lib.TcBlocks;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.block.VatBlock;
import dev.visfox.thaumichorizons.block.VatSolidBlock;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.WandTriggers;

@EventBusSubscriber(modid = dev.visfox.thaumichorizons.ThaumicHorizons.MODID)
public final class VatTrigger {
    private static final int WATER = 50;

    private VatTrigger() {
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(VatTrigger::register);
    }

    public static void register() {
        Predicate<BlockState> glass = state -> state.is(Blocks.GLASS);
        Predicate<BlockState> woodenDevice = state -> state.getBlock() == TcBlocks.block("arcane_pressure_plate");
        Predicate<BlockState> metalDevice = state -> state.getBlock() == TcBlocks.block("arcane_levitator");
        WandTriggers.register(glass.or(woodenDevice).or(metalDevice),
            (level, wand, player, pos, side) -> constructVat(level, wand, player, pos));
    }

    private static boolean constructVat(Level world, ItemStack itemstack, Player player, BlockPos pos) {
        if (!TcReforgedPort.research.isComplete(player, "healingVat")) {
            return false;
        }
        if (!(itemstack.getItem() instanceof WandItem wand)) {
            return false;
        }
        AspectList cost = new AspectList().add(Aspect.WATER, WATER).add(Aspect.EARTH, WATER).add(Aspect.ORDER, WATER);
        for (int xx = pos.getX() - 2; xx <= pos.getX(); xx++) {
            for (int yy = pos.getY() - 3; yy <= pos.getY(); yy++) {
                for (int zz = pos.getZ() - 2; zz <= pos.getZ(); zz++) {
                    if (fitVat(world, xx, yy, zz) && wand.consumeAllVisCrafting(itemstack, player, cost, true)) {
                        if (!world.isClientSide()) {
                            replaceVat(world, xx, yy, zz);
                            return true;
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private static boolean fitVat(Level world, int x, int y, int z) {
        Block wooden = TcBlocks.block("arcane_pressure_plate");
        Block metal = TcBlocks.block("arcane_levitator");
        if (wooden == null || metal == null) {
            return false;
        }
        Block[][][] blueprint = {
            { { wooden, wooden, wooden }, { wooden, metal, wooden }, { wooden, wooden, wooden } },
            { { Blocks.GLASS, Blocks.GLASS, Blocks.GLASS }, { Blocks.GLASS, Blocks.WATER, Blocks.GLASS },
                { Blocks.GLASS, Blocks.GLASS, Blocks.GLASS } },
            { { Blocks.GLASS, Blocks.GLASS, Blocks.GLASS }, { Blocks.GLASS, Blocks.WATER, Blocks.GLASS },
                { Blocks.GLASS, Blocks.GLASS, Blocks.GLASS } },
            { { wooden, wooden, wooden }, { wooden, metal, wooden }, { wooden, wooden, wooden } } };
        for (int yy = 0; yy < 4; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    BlockState state = world.getBlockState(new BlockPos(x + xx, y - yy + 3, z + zz));
                    if (state.isAir()) {
                        state = Blocks.AIR.defaultBlockState();
                    }
                    if (state.getBlock() != blueprint[yy][xx][zz]) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static void replaceVat(Level world, int x, int y, int z) {
        for (int yy = 0; yy < 4; yy++) {
            for (int zz = 0; zz < 3; zz++) {
                for (int xx = 0; xx < 3; xx++) {
                    BlockPos pos = new BlockPos(x + xx, y + yy, z + zz);
                    BlockState state = world.getBlockState(pos);
                    if (state.isAir()) {
                        continue;
                    }
                    int variant = 0;
                    Block target = null;
                    if (state.is(Blocks.GLASS)) {
                        target = ThBlocks.VAT.get();
                        variant = 10;
                    } else if (state.getBlock() == TcBlocks.block("arcane_pressure_plate")) {
                        target = ThBlocks.VAT_SOLID.get();
                        boolean edge = yy == 0 && ((xx == 1 && zz == 0) || (xx == 1 && zz == 2)
                            || (xx == 0 && zz == 1) || (xx == 2 && zz == 1));
                        variant = edge ? 4 : 5;
                    } else if (state.getBlock() == TcBlocks.block("arcane_levitator")) {
                        target = ThBlocks.VAT_SOLID.get();
                        variant = yy == 0 ? 6 : 7;
                    } else if (state.is(Blocks.WATER)) {
                        target = ThBlocks.VAT_INTERIOR.get();
                        variant = 0;
                    }
                    if (target != null) {
                        BlockState newState = target.defaultBlockState();
                        if (target instanceof VatSolidBlock) {
                            newState = newState.setValue(VatSolidBlock.VARIANT, variant);
                        } else if (target instanceof VatBlock) {
                            newState = newState.setValue(VatBlock.VARIANT, variant);
                        }
                        world.setBlock(pos, newState, 3);
                    }
                }
            }
        }
        THSounds.play(world, new BlockPos(x, y, z), "wand", SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
