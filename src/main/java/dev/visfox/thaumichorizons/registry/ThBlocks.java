package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.CrystalBlock;
import dev.visfox.thaumichorizons.block.DropsBlock;
import dev.visfox.thaumichorizons.block.DustBlock;
import dev.visfox.thaumichorizons.block.LeviathanBlock;
import java.util.function.UnaryOperator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThaumicHorizons.MODID);

    public static final DeferredBlock<Block> CHOCOLATE = withItem(BLOCKS.registerBlock("chocolate",
        Block::new, properties -> properties.mapColor(MapColor.COLOR_LIGHT_GRAY).strength(0.5F, 0.5F).sound(SoundType.WOOL)));
    public static final DeferredBlock<DustBlock> DUST = withItem(BLOCKS.registerBlock("dustth",
        DustBlock::new, properties -> properties.mapColor(MapColor.SAND).strength(0.5F).sound(SoundType.SAND)));
    public static final DeferredBlock<DropsBlock> EYES = withItem(BLOCKS.registerBlock("eyeth",
        properties -> new DropsBlock(properties, new ItemStack(Items.SPIDER_EYE), 3),
        properties -> properties.mapColor(MapColor.COLOR_RED).strength(0.5F, 0.5F).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<DropsBlock> BONE = withItem(BLOCKS.registerBlock("boneth",
        properties -> new DropsBlock(properties, new ItemStack(Items.BONE), 3),
        properties -> properties.mapColor(MapColor.STONE).strength(2.5F, 2.5F).sound(SoundType.STONE)));
    public static final DeferredBlock<DropsBlock> FLESH = withItem(BLOCKS.registerBlock("fleshth",
        properties -> new DropsBlock(properties, new ItemStack(Items.BONE), 3),
        properties -> properties.mapColor(MapColor.COLOR_RED).strength(0.5F, 0.5F).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<Block> LIGHT = BLOCKS.registerBlock("light",
        Block::new, properties -> properties.mapColor(MapColor.NONE).strength(0.0F).noCollision().noLootTable()
            .lightLevel(state -> 14).sound(SoundType.WOOL));
    public static final DeferredBlock<Block> VOID = withItem(BLOCKS.registerBlock("voidth",
        Block::new, properties -> properties.mapColor(MapColor.NONE).strength(-1.0F, 6000000.0F).noCollision()
            .lightLevel(state -> 15).sound(SoundType.WOOL)));
    public static final DeferredBlock<Block> CLOUD = withItem(BLOCKS.registerBlock("cloudth",
        Block::new, properties -> properties.mapColor(MapColor.SNOW).strength(-1.0F, 3600000.0F).noOcclusion().sound(SoundType.WOOL)));
    public static final DeferredBlock<Block> CLOUD_GLOWING = withItem(BLOCKS.registerBlock("cloudglowingth",
        Block::new, properties -> properties.mapColor(MapColor.SNOW).strength(-1.0F, 3600000.0F).noOcclusion()
            .sound(SoundType.WOOL).lightLevel(state -> 15)));
    public static final DeferredBlock<Block> BRAIN = withItem(BLOCKS.registerBlock("brainth",
        Block::new, properties -> properties.mapColor(MapColor.COLOR_PINK).strength(0.5F, 0.5F).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<CrystalBlock> CRYSTAL = withItem(BLOCKS.registerBlock("crystalth",
        CrystalBlock::new, properties -> properties.mapColor(MapColor.COLOR_RED).strength(1.75F, 0.5F)
            .sound(SoundType.GLASS).noOcclusion()));
    public static final DeferredBlock<Block> CRYSTAL_DEEP = withItem(BLOCKS.registerBlock("crystaldeep",
        Block::new, properties -> properties.mapColor(MapColor.COLOR_PURPLE).strength(2.0F).sound(SoundType.GLASS)
            .noOcclusion().lightLevel(state -> 15)));
    public static final DeferredBlock<LeviathanBlock> LEVIATHAN = withItem(BLOCKS.registerBlock("leviathanth",
        LeviathanBlock::new, properties -> properties.mapColor(MapColor.STONE).strength(10.0F, 10.0F).sound(SoundType.STONE)));

    private static <B extends Block> DeferredBlock<B> withItem(DeferredBlock<B> block) {
        ThItems.ITEMS.registerSimpleBlockItem(block);
        return block;
    }

    private static <B extends Block> DeferredBlock<B> withItem(
        DeferredBlock<B> block,
        UnaryOperator<Item.Properties> itemProperties
    ) {
        ThItems.ITEMS.registerSimpleBlockItem(block, itemProperties);
        return block;
    }

    private ThBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
