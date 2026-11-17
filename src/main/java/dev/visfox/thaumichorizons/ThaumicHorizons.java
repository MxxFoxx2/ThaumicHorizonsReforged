package dev.visfox.thaumichorizons;

import com.mojang.logging.LogUtils;
import dev.visfox.thaumichorizons.aspect.ConfigAspects;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import dev.visfox.thaumichorizons.platform.DataAttachments;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThCreativeTabs;
import dev.visfox.thaumichorizons.registry.ThEffects;
import dev.visfox.thaumichorizons.registry.ThEntities;
import dev.visfox.thaumichorizons.network.ToggleInfusionPayload;
import dev.visfox.thaumichorizons.research.ConfigResearch;
import dev.visfox.thaumichorizons.registry.ThItems;
import dev.visfox.thaumichorizons.registry.ThMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(ThaumicHorizons.MODID)
public class ThaumicHorizons {
    public static final String MODID = "thaumichorizons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumicHorizons(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modContainer.registerConfig(ModConfig.Type.STARTUP, ThaumicHorizonsConfig.SPEC);
        CreatureInfusion.init();
        dev.visfox.thaumichorizons.infusion.CreatureInfusions.init();
        dev.visfox.thaumichorizons.infusion.ThInfusionRecipes.init();
        DataAttachments.register(modEventBus);
        dev.visfox.thaumichorizons.platform.StackData.register(modEventBus);
        ThEffects.register(modEventBus);
        ThBlocks.register(modEventBus);
        ThItems.register(modEventBus);
        ThBlockEntities.register(modEventBus);
        ThEntities.register(modEventBus);
        dev.visfox.thaumichorizons.registry.PocketRegistry.register(modEventBus);
        ThMenus.register(modEventBus);
        ThCreativeTabs.register(modEventBus);
        ConfigResearch.init();
        ConfigAspects.init();
        dev.visfox.thaumichorizons.crafting.ThRecipes.init();
        ToggleInfusionPayload.register();
        dev.visfox.thaumichorizons.network.OpenFingersPayload.register();
        dev.visfox.thaumichorizons.item.lenses.LensChangePayload.register();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Thaumic Horizons: Reforged loaded");
    }

    public static net.minecraft.resources.Identifier id(String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(MODID, path);
    }
}
