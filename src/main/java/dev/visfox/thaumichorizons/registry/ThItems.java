package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.compat.tc.BaubleAmuletMirrorItem;
import dev.visfox.thaumichorizons.item.BucketChocolateItem;
import dev.visfox.thaumichorizons.item.CloudBlockItem;
import dev.visfox.thaumichorizons.block.CloudBlock;
import dev.visfox.thaumichorizons.item.CorpseEffigyItem;
import dev.visfox.thaumichorizons.item.THBoatItem;
import dev.visfox.thaumichorizons.item.EggIncubatedItem;
import dev.visfox.thaumichorizons.item.FocusContainmentItem;
import dev.visfox.thaumichorizons.item.InjectorItem;
import dev.visfox.thaumichorizons.item.InfusionCheatItem;
import dev.visfox.thaumichorizons.item.SelfInfusionCheatItem;
import dev.visfox.thaumichorizons.item.NodeCheatItem;
import dev.visfox.thaumichorizons.item.NutrientsItem;
import dev.visfox.thaumichorizons.item.SyringeBloodItem;
import dev.visfox.thaumichorizons.item.SyringeBloodSampleItem;
import dev.visfox.thaumichorizons.item.SyringeEmptyItem;
import dev.visfox.thaumichorizons.item.SyringeInjectionItem;
import dev.visfox.thaumichorizons.item.SoulJarBlockItem;
import dev.visfox.thaumichorizons.item.VortexBlockItem;
import dev.visfox.thaumichorizons.item.focus.FocusAnimationItem;
import dev.visfox.thaumichorizons.item.focus.FocusDisintegrationItem;
import dev.visfox.thaumichorizons.item.focus.FocusIlluminationItem;
import dev.visfox.thaumichorizons.item.focus.FocusLiquefactionItem;
import dev.visfox.thaumichorizons.item.golem.CrystalWandItem;
import dev.visfox.thaumichorizons.item.golem.DisposableWandItem;
import dev.visfox.thaumichorizons.item.golem.GolemBellTHItem;
import dev.visfox.thaumichorizons.item.golem.GolemPlacerTHItem;
import dev.visfox.thaumichorizons.item.golem.GolemPowderItem;
import dev.visfox.thaumichorizons.item.lenses.ItemLensAir;
import dev.visfox.thaumichorizons.item.lenses.ItemLensCase;
import dev.visfox.thaumichorizons.item.lenses.ItemLensEarth;
import dev.visfox.thaumichorizons.item.lenses.ItemLensFire;
import dev.visfox.thaumichorizons.item.lenses.ItemLensOrderEntropy;
import dev.visfox.thaumichorizons.item.lenses.ItemLensWater;
import thaumcraft.entity.golem.GolemType;
import thaumcraft.wand.focus.FocusItem;
import dev.visfox.thaumichorizons.item.SuicidePillItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import java.util.function.Supplier;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThaumicHorizons.MODID);

    public static final DeferredItem<Item> BAR_CHOCOLATE = ITEMS.registerItem("barchocolate",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(1).saturationModifier(2.0F).build())));
    public static final DeferredItem<Item> ICE_CREAM = ITEMS.registerItem("icecream",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(3).saturationModifier(4.0F).build())));
    public static final DeferredItem<BucketChocolateItem> BUCKET_CHOCOLATE = ITEMS.registerItem("bucketchocolate",
        BucketChocolateItem::new,THRegistryProps.item(properties -> properties.stacksTo(1).craftRemainder(Items.BUCKET)));
    public static final DeferredItem<Item> INK_EGG = ITEMS.registerItem("inkegg",
        Item::new,THRegistryProps.item(properties -> properties.stacksTo(16)));
    public static final DeferredItem<Item> GOLD_EGG = ITEMS.registerItem("goldegg",
        Item::new,THRegistryProps.item(properties -> properties.stacksTo(16)));
    public static final DeferredItem<THBoatItem> BOAT_GREATWOOD = ITEMS.registerItem("boatgreatwood",
        properties -> new THBoatItem(ThEntities.BOAT_GREATWOOD, properties.stacksTo(1)));
    public static final DeferredItem<THBoatItem> BOAT_THAUMIUM = ITEMS.registerItem("boatthaumium",
        properties -> new THBoatItem(ThEntities.BOAT_THAUMIUM, properties.stacksTo(1)));
    public static final DeferredItem<Item> VOID_PUTTY = ITEMS.registerItem("voidputty",
        Item::new,THRegistryProps.item(properties -> properties.stacksTo(16)));
    public static final DeferredItem<BaubleAmuletMirrorItem> AMULET_MIRROR = ITEMS.registerItem("amuletmirror",
        BaubleAmuletMirrorItem::new,THRegistryProps.item(properties -> properties.stacksTo(1).rarity(Rarity.RARE)));
    public static final DeferredItem<SuicidePillItem> SUICIDE_PILL = ITEMS.registerItem("suicidepill", SuicidePillItem::new);
    public static final DeferredItem<Item> DUMMY = ITEMS.registerItem("dummy", Item::new);
    public static final DeferredItem<Item> DUMMY_VAT = ITEMS.registerItem("dummyvat", Item::new);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_1 = infusionCheat(1);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_2 = infusionCheat(2);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_3 = infusionCheat(3);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_4 = infusionCheat(4);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_5 = infusionCheat(5);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_6 = infusionCheat(6);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_7 = infusionCheat(7);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_8 = infusionCheat(8);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_9 = infusionCheat(9);
    public static final DeferredItem<InfusionCheatItem> INFUSION_CHEAT_10 = infusionCheat(10);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_1 = selfInfusionCheat(1);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_2 = selfInfusionCheat(2);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_3 = selfInfusionCheat(3);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_4 = selfInfusionCheat(4);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_5 = selfInfusionCheat(5);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_6 = selfInfusionCheat(6);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_7 = selfInfusionCheat(7);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_8 = selfInfusionCheat(8);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_9 = selfInfusionCheat(9);
    public static final DeferredItem<SelfInfusionCheatItem> SELF_INFUSION_CHEAT_10 = selfInfusionCheat(10);
    public static final DeferredItem<NodeCheatItem> NODE_CHEAT = ITEMS.registerItem("nodecheat", NodeCheatItem::new);
    public static final DeferredItem<Item> MEAT = ITEMS.registerItem("meatth",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build())));
    public static final DeferredItem<Item> MEAT_COOKED = ITEMS.registerItem("meatcookedth",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.7F).build())));
    public static final DeferredItem<Item> MEAT_NUGGET = ITEMS.registerItem("meatnuggetth",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build())));

    public static final DeferredItem<CloudBlockItem> CLOUD = cloudItem("cloudth", () -> ThBlocks.CLOUD.get(), 0);
    public static final DeferredItem<CloudBlockItem> CLOUD_FIRE = cloudItem("cloudglowingth", () -> ThBlocks.CLOUD_GLOWING.get(), 1);
    public static final DeferredItem<CloudBlockItem> CLOUD_THUNDER = cloudItem("cloudth_thunder", () -> ThBlocks.CLOUD.get(), 2);
    public static final DeferredItem<CloudBlockItem> CLOUD_ACID = cloudItem("cloudth_acid", () -> ThBlocks.CLOUD.get(), 3);
    public static final DeferredItem<CloudBlockItem> CLOUD_ALLOY = cloudItem("cloudglowingth_alloy", () -> ThBlocks.CLOUD_GLOWING.get(), 4);
    public static final DeferredItem<CloudBlockItem> CLOUD_FLESH = cloudItem("cloudth_flesh", () -> ThBlocks.CLOUD.get(), 5);
    public static final DeferredItem<CloudBlockItem> CLOUD_VIS = cloudItem("cloudglowingth_vis", () -> ThBlocks.CLOUD_GLOWING.get(), 6);
    public static final DeferredItem<CloudBlockItem> CLOUD_GLYPH = cloudItem("cloudglowingth_glyph", () -> ThBlocks.CLOUD_GLOWING.get(), 7);
    public static final DeferredItem<CloudBlockItem> CLOUD_SPORE = cloudItem("cloudth_spore", () -> ThBlocks.CLOUD.get(), 8);
    public static final DeferredItem<CloudBlockItem> CLOUD_ANIMUS = cloudItem("cloudglowingth_animus", () -> ThBlocks.CLOUD_GLOWING.get(), 9);


    public static final DeferredItem<SoulJarBlockItem> SOUL_JAR_ITEM = ITEMS.registerItem("souljar",
        properties -> new SoulJarBlockItem(ThBlocks.SOUL_JAR.get(), properties.stacksTo(1)));
    public static final DeferredItem<SyringeInjectionItem> SYRINGE_INJECTION = ITEMS.registerItem("syringeinjection",
        SyringeInjectionItem::new,THRegistryProps.item(properties -> properties.stacksTo(1)));
    public static final DeferredItem<SyringeEmptyItem> SYRINGE_EMPTY = ITEMS.registerItem("syringeempty",
        SyringeEmptyItem::new);
    public static final DeferredItem<SyringeBloodItem> SYRINGE_BLOOD = ITEMS.registerItem("syringeblood",
        SyringeBloodItem::new);
    public static final DeferredItem<SyringeBloodSampleItem> SYRINGE_BLOOD_SAMPLE = ITEMS.registerItem("syringebloodsample",
        SyringeBloodSampleItem::new);
    public static final DeferredItem<InjectorItem> INJECTOR = ITEMS.registerItem("injector", InjectorItem::new,THRegistryProps.item(properties -> properties.stacksTo(1)));
    public static final DeferredItem<SpawnEggItem> CHROMATIC_SHEEP_SPAWN_EGG = spawnEgg("chromatic_sheep", ThEntities.CHROMATIC_SHEEP, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> SELF_SHEARING_SHEEP_SPAWN_EGG = spawnEgg("self_shearing_sheep", ThEntities.SELF_SHEARING_SHEEP, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> GUARDIAN_PANTHER_SPAWN_EGG = spawnEgg("guardian_panther", ThEntities.GUARDIAN_PANTHER, 0xFFC300, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> GRAVEKEEPER_SPAWN_EGG = spawnEgg("gravekeeper", ThEntities.GRAVEKEEPER, 0xFFFF00, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> FAMILIAR_SPAWN_EGG = spawnEgg("familiar", ThEntities.FAMILIAR, 0x000000, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> CHOCOLATE_COW_SPAWN_EGG = spawnEgg("chocolate_cow", ThEntities.CHOCOLATE_COW, 0x844000, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> SCHOLAR_CHICKEN_SPAWN_EGG = spawnEgg("scholar_chicken", ThEntities.SCHOLAR_CHICKEN, 0x000000, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> GOLD_CHICKEN_SPAWN_EGG = spawnEgg("gold_chicken", ThEntities.GOLD_CHICKEN, 0xD3B000, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> ORE_PIG_SPAWN_EGG = spawnEgg("ore_pig", ThEntities.ORE_PIG, 0x939393, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> TAINT_EATER_PIG_SPAWN_EGG = spawnEgg("taint_eater_pig", ThEntities.TAINT_EATER_PIG, 0x3A005B, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> ENDERSTEED_SPAWN_EGG = spawnEgg("endersteed", ThEntities.ENDERSTEED, 0xA500FF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> SEAWOLF_SPAWN_EGG = spawnEgg("seawolf", ThEntities.SEAWOLF, 0x000020, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> NETHER_HOUND_SPAWN_EGG = spawnEgg("nether_hound", ThEntities.NETHER_HOUND, 0xFF0C00, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> LUNAR_WOLF_SPAWN_EGG = spawnEgg("lunar_wolf", ThEntities.LUNAR_WOLF, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> MEAT_SLIME_SPAWN_EGG = spawnEgg("meat_slime", ThEntities.MEAT_SLIME, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> MERCURIAL_SLIME_SPAWN_EGG = spawnEgg("mercurial_slime", ThEntities.MERCURIAL_SLIME, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> VOLT_SLIME_SPAWN_EGG = spawnEgg("volt_slime", ThEntities.VOLT_SLIME, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> MED_SLIME_SPAWN_EGG = spawnEgg("med_slime", ThEntities.MED_SLIME, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> SHEEDER_SPAWN_EGG = spawnEgg("sheeder", ThEntities.SHEEDER, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> LOST_SOUL_SPAWN_EGG = spawnEgg("lost_soul", ThEntities.LOST_SOUL, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> HORSE_ZOMBIE_SPAWN_EGG = spawnEgg("horse_zombie", ThEntities.HORSE_ZOMBIE, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> HORSE_SKELETON_SPAWN_EGG = spawnEgg("horse_skeleton", ThEntities.HORSE_SKELETON, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<SpawnEggItem> NIGHTMARE_SPAWN_EGG = spawnEgg("nightmare", ThEntities.NIGHTMARE, 0xFFFFFF, 0xA500FF);
    public static final DeferredItem<CorpseEffigyItem> CORPSE_EFFIGY = ITEMS.registerItem("corpseeffigy",
        CorpseEffigyItem::new);
    public static final DeferredItem<NutrientsItem> NUTRIENTS = ITEMS.registerItem("nutrients", NutrientsItem::new);
    public static final DeferredItem<FocusContainmentItem> FOCUS_CONTAINMENT = ITEMS.registerItem("focuscontainment",
        FocusContainmentItem::new, THRegistryProps.item(FocusItem::properties));
    public static final DeferredItem<FocusAnimationItem> FOCUS_ANIMATION = ITEMS.registerItem("focusanimation",
        FocusAnimationItem::new, THRegistryProps.item(FocusItem::properties));
    public static final DeferredItem<FocusDisintegrationItem> FOCUS_DISINTEGRATION = ITEMS.registerItem("focusdisintegration",
        FocusDisintegrationItem::new, THRegistryProps.item(FocusItem::properties));
    public static final DeferredItem<FocusIlluminationItem> FOCUS_ILLUMINATION = ITEMS.registerItem("focusillumination",
        FocusIlluminationItem::new, THRegistryProps.item(FocusItem::properties));
    public static final DeferredItem<FocusLiquefactionItem> FOCUS_LIQUEFACTION = ITEMS.registerItem("focusliquefaction",
        FocusLiquefactionItem::new, THRegistryProps.item(FocusItem::properties));

    public static final DeferredItem<GolemPowderItem> GOLEM_POWDER = ITEMS.registerItem("golempowder",
        GolemPowderItem::new,THRegistryProps.item(properties -> properties.stacksTo(64)));
    public static final DeferredItem<GolemPlacerTHItem> GOLEM_PLACER = ITEMS.registerItem("golemplacer",
        properties -> new GolemPlacerTHItem(properties, GolemType.WOOD),THRegistryProps.item(properties -> properties.stacksTo(1)));
    public static final DeferredItem<GolemBellTHItem> GOLEM_BELL_TH = ITEMS.registerItem("golembellth",
        GolemBellTHItem::new,THRegistryProps.item(properties -> properties.stacksTo(1)));
    public static final DeferredItem<CrystalWandItem> CRYSTAL_WAND = ITEMS.registerItem("crystalwand",
        CrystalWandItem::new,THRegistryProps.item(properties -> properties.stacksTo(1)));
    // The disposable wand defaults write TC4R wand data components, and those component
    // types are only bound once the registry events run. Configuring the properties inside
    // the registration supplier keeps the lookup out of this class initializer.
    public static final DeferredItem<DisposableWandItem> WAND_CASTING_DISPOSABLE = ITEMS.registerItem("wandcastingdisposable",
        properties -> new DisposableWandItem(DisposableWandItem.properties(properties)));
    public static final DeferredItem<VortexBlockItem> VORTEX_CHEAT = ITEMS.registerItem("vortexth_cheat",
        properties -> new VortexBlockItem(ThBlocks.VORTEX.get(), properties.stacksTo(1), true));
    public static final DeferredItem<ItemLensFire> LENS_FIRE = ITEMS.registerItem("lensfire", ItemLensFire::new);
    public static final DeferredItem<ItemLensWater> LENS_WATER = ITEMS.registerItem("lenswater", ItemLensWater::new);
    public static final DeferredItem<ItemLensEarth> LENS_EARTH = ITEMS.registerItem("lensearth", ItemLensEarth::new);
    public static final DeferredItem<ItemLensAir> LENS_AIR = ITEMS.registerItem("lensair", ItemLensAir::new);
    public static final DeferredItem<ItemLensOrderEntropy> LENS_ORDER_ENTROPY = ITEMS.registerItem("lensorderentropy",
        ItemLensOrderEntropy::new);
    public static final DeferredItem<ItemLensCase> LENS_CASE = ITEMS.registerItem("lenscase",
        ItemLensCase::new, THRegistryProps.item(ItemLensCase::properties));
    public static final DeferredItem<EggIncubatedItem> EGG_INCUBATED = ITEMS.registerItem("eggincubated",
        EggIncubatedItem::new,THRegistryProps.item(properties -> properties.stacksTo(16)));

    private static DeferredItem<InfusionCheatItem> infusionCheat(int id) {
        return ITEMS.registerItem("infusioncheat" + (id == 1 ? "" : id),
            properties -> new InfusionCheatItem(properties, id));
    }

    private static DeferredItem<SelfInfusionCheatItem> selfInfusionCheat(int id) {
        return ITEMS.registerItem("infusionselfcheat" + (id == 1 ? "" : id),
            properties -> new SelfInfusionCheatItem(properties, id));
    }

    private static DeferredItem<SpawnEggItem> spawnEgg(String name,
        Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor) {
        return ITEMS.registerItem(name + "_spawn_egg",
            properties -> new SpawnEggItem(type.get(), backgroundColor, highlightColor, properties));
    }

    private static DeferredItem<CloudBlockItem> cloudItem(String name, Supplier<CloudBlock> block, int type) {
        return ITEMS.registerItem(name, properties -> new CloudBlockItem(block.get(), type, properties));
    }

    private ThItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
