package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.item.BucketChocolateItem;
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

    private ThItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
