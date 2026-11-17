package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.AlchemiteBlock;
import dev.visfox.thaumichorizons.block.BloodInfuserBlock;
import dev.visfox.thaumichorizons.block.CrystalBlock;
import dev.visfox.thaumichorizons.block.CloudBlock;
import dev.visfox.thaumichorizons.block.DropsBlock;
import dev.visfox.thaumichorizons.block.DustBlock;
import dev.visfox.thaumichorizons.block.EssentiaDynamoBlock;
import dev.visfox.thaumichorizons.block.InspiratronBlock;
import dev.visfox.thaumichorizons.block.IlluminationLightBlock;
import dev.visfox.thaumichorizons.block.LeviathanBlock;
import dev.visfox.thaumichorizons.block.LightSolarBlock;
import dev.visfox.thaumichorizons.block.ModifiedMatrixBlock;
import dev.visfox.thaumichorizons.block.NodeMonitorBlock;
import dev.visfox.thaumichorizons.block.RecombinatorBlock;
import dev.visfox.thaumichorizons.block.SoulBeaconBlock;
import dev.visfox.thaumichorizons.block.SoulExtractorBlock;
import dev.visfox.thaumichorizons.block.SoulforgeBlock;
import dev.visfox.thaumichorizons.block.SoulJarBlock;
import dev.visfox.thaumichorizons.block.SyntheticNodeBlock;
import dev.visfox.thaumichorizons.block.TransductionAmplifierBlock;
import dev.visfox.thaumichorizons.block.VisDynamoBlock;
import dev.visfox.thaumichorizons.block.VatBlock;
import dev.visfox.thaumichorizons.block.VatInteriorBlock;
import dev.visfox.thaumichorizons.block.VatSolidBlock;
import dev.visfox.thaumichorizons.block.VortexBlock;
import dev.visfox.thaumichorizons.block.VortexStabilizerBlock;
import java.util.function.UnaryOperator;
import net.minecraft.world.item.Item;
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
        properties -> new DropsBlock(properties, Items.SPIDER_EYE, 3),
        properties -> properties.mapColor(MapColor.COLOR_RED).strength(0.5F, 0.5F).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<DropsBlock> BONE = withItem(BLOCKS.registerBlock("boneth",
        properties -> new DropsBlock(properties, Items.BONE, 3),
        properties -> properties.mapColor(MapColor.STONE).strength(2.5F, 2.5F).sound(SoundType.STONE)));
    public static final DeferredBlock<DropsBlock> FLESH = withItem(BLOCKS.registerBlock("fleshth",
        properties -> new DropsBlock(properties, Items.BONE, 3),
        properties -> properties.mapColor(MapColor.COLOR_RED).strength(0.5F, 0.5F).sound(SoundType.SLIME_BLOCK)));
    public static final DeferredBlock<IlluminationLightBlock> LIGHT = BLOCKS.registerBlock("light",
        IlluminationLightBlock::new, properties -> properties.mapColor(MapColor.NONE).strength(0.0F).noCollision().noLootTable()
            .lightLevel(state -> 14).sound(SoundType.WOOL));
    public static final DeferredBlock<LightSolarBlock> LIGHT_SOLAR = BLOCKS.registerBlock("lightsolar",
        LightSolarBlock::new, properties -> properties.mapColor(MapColor.NONE).strength(0.0F).noCollision().noLootTable()
            .lightLevel(state -> 14).sound(SoundType.WOOL));
    public static final DeferredBlock<Block> VOID = withItem(BLOCKS.registerBlock("voidth",
        Block::new, properties -> properties.mapColor(MapColor.NONE).strength(-1.0F, 6000000.0F).noCollision()
            .lightLevel(state -> 15).sound(SoundType.WOOL)));
    public static final DeferredBlock<CloudBlock> CLOUD = withItem(BLOCKS.registerBlock("cloudth",
        properties -> new CloudBlock(properties, 0), properties -> properties.mapColor(MapColor.SNOW)
            .strength(-1.0F, 3600000.0F).noOcclusion().noCollision().sound(SoundType.WOOL)));
    public static final DeferredBlock<CloudBlock> CLOUD_GLOWING = withItem(BLOCKS.registerBlock("cloudglowingth",
        properties -> new CloudBlock(properties, 1), properties -> properties.mapColor(MapColor.SNOW)
            .strength(-1.0F, 3600000.0F).noOcclusion().noCollision().sound(SoundType.WOOL).lightLevel(state -> 15)));
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
    public static final DeferredBlock<VisDynamoBlock> VIS_DYNAMO = withItem(BLOCKS.registerBlock("visdynamo",
        VisDynamoBlock::new, properties -> properties.mapColor(MapColor.METAL).strength(0.7F, 1.0F)
            .lightLevel(state -> 7).noOcclusion().sound(SoundType.STONE)));
    public static final DeferredBlock<EssentiaDynamoBlock> ESSENTIA_DYNAMO = withItem(BLOCKS.registerBlock("essentiadynamo",
        EssentiaDynamoBlock::new, properties -> properties.mapColor(MapColor.METAL).strength(0.7F, 1.0F)
            .lightLevel(state -> 7).noOcclusion().sound(SoundType.STONE)));
    public static final DeferredBlock<NodeMonitorBlock> NODE_MONITOR = withItem(BLOCKS.registerBlock("nodemonitor",
        NodeMonitorBlock::new, properties -> properties.mapColor(MapColor.COLOR_YELLOW).strength(0.7F, 1.0F)
            .lightLevel(state -> 7).noOcclusion().sound(SoundType.GLASS)));
    public static final DeferredBlock<SyntheticNodeBlock> SYNTH_NODE = withItem(BLOCKS.registerBlock("synthnode",
        SyntheticNodeBlock::new, properties -> properties.mapColor(MapColor.COLOR_CYAN).strength(0.7F, 1.0F)
            .lightLevel(state -> 7).noOcclusion().sound(SoundType.GLASS)));
    public static final DeferredBlock<TransductionAmplifierBlock> TRANSDUCTION_AMPLIFIER =
        withItem(BLOCKS.registerBlock("transductionamplifier", TransductionAmplifierBlock::new,
            properties -> properties.mapColor(MapColor.METAL).strength(0.0F, 0.0F).sound(SoundType.STONE)));
    public static final DeferredBlock<RecombinatorBlock> RECOMBINATOR = withItem(BLOCKS.registerBlock(
        "recombinator", RecombinatorBlock::new,
        properties -> properties.mapColor(MapColor.STONE).strength(3.0F, 5.0F).noOcclusion().sound(SoundType.STONE)));
    public static final DeferredBlock<VortexBlock> VORTEX = withItem(BLOCKS.registerBlock("vortexth",
        VortexBlock::new, properties -> properties.mapColor(MapColor.COLOR_PURPLE).strength(-1.0F, 20000.0F)
            .noLootTable().noCollision().lightLevel(state -> 15).sound(SoundType.WOOL)));
    public static final DeferredBlock<VortexStabilizerBlock> VORTEX_STABILIZER = withItem(BLOCKS.registerBlock("vortexstabilizer",
        VortexStabilizerBlock::new, properties -> properties.mapColor(MapColor.METAL).strength(10.0F, 0.0F)
            .sound(SoundType.METAL)));
    public static final DeferredBlock<AlchemiteBlock> ALCHEMITE = withItem(BLOCKS.registerBlock("alchemite",
        AlchemiteBlock::new, properties -> properties.mapColor(MapColor.COLOR_CYAN).strength(0.7F, 1.0F)
            .lightLevel(state -> 7).sound(SoundType.GRASS)));
    public static final DeferredBlock<SoulJarBlock> SOUL_JAR = BLOCKS.registerBlock("souljar",
        SoulJarBlock::new, properties -> properties.mapColor(MapColor.NONE).strength(0.3F, 1.0F)
            .sound(SoundType.GLASS).noOcclusion().lightLevel(state -> 9));
    public static final DeferredBlock<SoulExtractorBlock> SOUL_EXTRACTOR = withItem(BLOCKS.registerBlock("soulsieve",
        SoulExtractorBlock::new, properties -> properties.mapColor(MapColor.METAL).strength(0.7F, 1.0F)
            .sound(SoundType.WOOD).noOcclusion()));
    public static final DeferredBlock<InspiratronBlock> INSPIRATRON = withItem(BLOCKS.registerBlock("inspiratron",
        InspiratronBlock::new, properties -> properties.mapColor(MapColor.NONE).strength(0.7F, 1.0F)
            .sound(SoundType.GLASS).noOcclusion()));
    public static final DeferredBlock<SoulforgeBlock> SOULFORGE = withItem(BLOCKS.registerBlock("soulforge",
        SoulforgeBlock::new, properties -> properties.mapColor(MapColor.NONE).strength(0.7F, 1.0F)
            .sound(SoundType.GLASS).noOcclusion()));
    public static final DeferredBlock<BloodInfuserBlock> BLOOD_INFUSER = withItem(BLOCKS.registerBlock("bloodinfuser",
        BloodInfuserBlock::new, properties -> properties.mapColor(MapColor.METAL).strength(1.0F, 1.0F)
            .sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<SoulBeaconBlock> SOUL_BEACON = withItem(BLOCKS.registerBlock("soulbeacon",
        SoulBeaconBlock::new, properties -> properties.mapColor(MapColor.METAL).strength(7.0F, 7.0F)
            .sound(SoundType.METAL).noOcclusion().lightLevel(state -> 15)));
    public static final DeferredBlock<ModifiedMatrixBlock> MOD_MATRIX = withItem(BLOCKS.registerBlock("modmatrix",
        ModifiedMatrixBlock::new, properties -> properties.mapColor(MapColor.STONE).strength(2.0F, 2.0F)
            .noOcclusion()));
    public static final DeferredBlock<VatBlock> VAT = BLOCKS.registerBlock("vat", VatBlock::new,
        properties -> properties.mapColor(MapColor.WOOD).strength(3.0F, 15.0F).noOcclusion().lightLevel(state -> 8));
    public static final DeferredBlock<VatSolidBlock> VAT_SOLID = BLOCKS.registerBlock("vatsolid", VatSolidBlock::new,
        properties -> properties.mapColor(MapColor.WOOD).strength(3.0F, 15.0F).noOcclusion().lightLevel(state -> 8));
    public static final DeferredBlock<VatInteriorBlock> VAT_INTERIOR = BLOCKS.registerBlock("vatinterior",
        VatInteriorBlock::new, properties -> properties.mapColor(MapColor.WATER).strength(3.0F, 15.0F)
            .noOcclusion().lightLevel(state -> 8));

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
