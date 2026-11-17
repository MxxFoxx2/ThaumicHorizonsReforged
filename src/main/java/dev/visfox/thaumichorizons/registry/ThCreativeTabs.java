package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThCreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ThaumicHorizons.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> THAUMICHORIZONS = TABS.register("thaumichorizons", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.thaumichorizons"))
        .icon(() -> new ItemStack(ThItems.MEAT.get()))
        .displayItems((parameters, output) -> {
            for (DeferredHolder<Item, ? extends Item> holder : ThItems.ITEMS.getEntries()) {
                output.accept(holder.get());
            }
            for (DeferredHolder<Item, ? extends Item> holder : PocketRegistry.ITEMS.getEntries()) {
                output.accept(holder.get());
            }
        })
        .build());

    private ThCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
