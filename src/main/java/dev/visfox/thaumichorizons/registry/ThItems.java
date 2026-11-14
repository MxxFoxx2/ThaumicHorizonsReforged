package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.item.BucketChocolateItem;
import dev.visfox.thaumichorizons.item.CorpseEffigyItem;
import dev.visfox.thaumichorizons.item.FocusContainmentItem;
import dev.visfox.thaumichorizons.item.InjectorItem;
import dev.visfox.thaumichorizons.item.NutrientsItem;
import dev.visfox.thaumichorizons.item.SyringeBloodItem;
import dev.visfox.thaumichorizons.item.SyringeBloodSampleItem;
import dev.visfox.thaumichorizons.item.SyringeEmptyItem;
import dev.visfox.thaumichorizons.item.SyringeInjectionItem;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThaumicHorizons.MODID);

    public static final DeferredItem<Item> BAR_CHOCOLATE = ITEMS.registerItem("barchocolate",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(1).saturationModifier(2.0F).build())));
    public static final DeferredItem<Item> ICE_CREAM = ITEMS.registerItem("icecream",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(3).saturationModifier(4.0F).build())));
    public static final DeferredItem<BucketChocolateItem> BUCKET_CHOCOLATE = ITEMS.registerItem("bucketchocolate",
        BucketChocolateItem::new, properties -> properties.stacksTo(1).craftRemainder(Items.BUCKET));
    public static final DeferredItem<Item> INK_EGG = ITEMS.registerItem("inkegg",
        Item::new, properties -> properties.stacksTo(16));
    public static final DeferredItem<Item> GOLD_EGG = ITEMS.registerItem("goldegg",
        Item::new, properties -> properties.stacksTo(16));
    public static final DeferredItem<Item> VOID_PUTTY = ITEMS.registerItem("voidputty",
        Item::new, properties -> properties.stacksTo(16));
    public static final DeferredItem<SuicidePillItem> SUICIDE_PILL = ITEMS.registerItem("suicidepill", SuicidePillItem::new);
    public static final DeferredItem<Item> DUMMY = ITEMS.registerItem("dummy", Item::new);
    public static final DeferredItem<Item> DUMMY_VAT = ITEMS.registerItem("dummyvat", Item::new);
    public static final DeferredItem<Item> MEAT = ITEMS.registerItem("meatth",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.3F).build())));
    public static final DeferredItem<Item> MEAT_COOKED = ITEMS.registerItem("meatcookedth",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.7F).build())));
    public static final DeferredItem<Item> MEAT_NUGGET = ITEMS.registerItem("meatnuggetth",
        properties -> new Item(properties.food(new FoodProperties.Builder().nutrition(1).saturationModifier(0.1F).build())));


    public static final DeferredItem<SyringeInjectionItem> SYRINGE_INJECTION = ITEMS.registerItem("syringeInjection",
        SyringeInjectionItem::new, properties -> properties.stacksTo(1));
    public static final DeferredItem<SyringeEmptyItem> SYRINGE_EMPTY = ITEMS.registerItem("syringeEmpty",
        SyringeEmptyItem::new);
    public static final DeferredItem<SyringeBloodItem> SYRINGE_BLOOD = ITEMS.registerItem("syringeBlood",
        SyringeBloodItem::new);
    public static final DeferredItem<SyringeBloodSampleItem> SYRINGE_BLOOD_SAMPLE = ITEMS.registerItem("syringeBloodSample",
        SyringeBloodSampleItem::new);
    public static final DeferredItem<InjectorItem> INJECTOR = ITEMS.registerItem("injector", InjectorItem::new);
    public static final DeferredItem<CorpseEffigyItem> CORPSE_EFFIGY = ITEMS.registerItem("corpseEffigy",
        CorpseEffigyItem::new);
    public static final DeferredItem<NutrientsItem> NUTRIENTS = ITEMS.registerItem("nutrients", NutrientsItem::new);
    public static final DeferredItem<FocusContainmentItem> FOCUS_CONTAINMENT = ITEMS.registerItem("focus_containment",
        FocusContainmentItem::new, FocusItem::properties);

    public static final DeferredItem<GolemPowderItem> GOLEM_POWDER = ITEMS.registerItem("golempowder",
        GolemPowderItem::new, properties -> properties.stacksTo(64));
    public static final DeferredItem<GolemPlacerTHItem> GOLEM_PLACER = ITEMS.registerItem("golemplacer",
        properties -> new GolemPlacerTHItem(properties, GolemType.VOID), properties -> properties.stacksTo(1));
    public static final DeferredItem<GolemBellTHItem> GOLEM_BELL_TH = ITEMS.registerItem("golembellth",
        GolemBellTHItem::new, properties -> properties.stacksTo(1));
    public static final DeferredItem<CrystalWandItem> CRYSTAL_WAND = ITEMS.registerItem("crystalwand",
        CrystalWandItem::new, properties -> properties.stacksTo(1));
    public static final DeferredItem<DisposableWandItem> WAND_CASTING_DISPOSABLE = ITEMS.registerItem("wandcastingdisposable",
        DisposableWandItem::new, properties -> properties.stacksTo(1));
    public static final DeferredItem<ItemLensFire> LENS_FIRE = ITEMS.registerItem("lensfire", ItemLensFire::new);
    public static final DeferredItem<ItemLensWater> LENS_WATER = ITEMS.registerItem("lenswater", ItemLensWater::new);
    public static final DeferredItem<ItemLensEarth> LENS_EARTH = ITEMS.registerItem("lensearth", ItemLensEarth::new);
    public static final DeferredItem<ItemLensAir> LENS_AIR = ITEMS.registerItem("lensair", ItemLensAir::new);
    public static final DeferredItem<ItemLensOrderEntropy> LENS_ORDER_ENTROPY = ITEMS.registerItem("lensorderentropy",
        ItemLensOrderEntropy::new);
    public static final DeferredItem<ItemLensCase> LENS_CASE = ITEMS.registerItem("lenscase",
        ItemLensCase::new, ItemLensCase::properties);

    private ThItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
