package dev.visfox.thaumichorizons;

import com.mojang.logging.LogUtils;
import dev.visfox.thaumichorizons.aspect.ConfigAspects;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import dev.visfox.thaumichorizons.platform.DataAttachments;
import dev.visfox.thaumichorizons.platform.ThDispenserBehaviors;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThCreativeTabs;
import dev.visfox.thaumichorizons.registry.ThEffects;
import dev.visfox.thaumichorizons.registry.ThEntities;
import dev.visfox.thaumichorizons.network.ToggleInfusionPayload;
import dev.visfox.thaumichorizons.research.ConfigResearch;
import dev.visfox.thaumichorizons.registry.ThItems;
import dev.visfox.thaumichorizons.registry.ThMenus;
import dev.visfox.thaumichorizons.registry.ThSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
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
        NeoForge.EVENT_BUS.addListener(this::serverStarted);
        NeoForge.EVENT_BUS.addListener(this::serverStopped);
        modContainer.registerConfig(ModConfig.Type.STARTUP, ThaumicHorizonsConfig.SPEC);
        CreatureInfusion.init();
        dev.visfox.thaumichorizons.infusion.CreatureInfusions.init();
        dev.visfox.thaumichorizons.infusion.ThInfusionRecipes.init();
        DataAttachments.register(modEventBus);
        dev.visfox.thaumichorizons.platform.StackData.register(modEventBus);
        dev.visfox.thaumichorizons.item.SyringeKeys.initialize();
        dev.visfox.thaumichorizons.item.SoulJarKeys.initialize();
        dev.visfox.thaumichorizons.blockentity.SoulBeaconKeys.initialize();
        ThEffects.register(modEventBus);
        ThSounds.register(modEventBus);
        ThBlocks.register(modEventBus);
        ThItems.register(modEventBus);
        dev.visfox.thaumichorizons.registry.ThRecipeSerializers.register(modEventBus);
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
        dev.visfox.thaumichorizons.network.LensScanPayload.register();
    }

    private void serverStarted(ServerStartedEvent event) {
        dev.visfox.thaumichorizons.world.pocket.PocketPlanes.get(event.getServer());
    }

    private void serverStopped(ServerStoppedEvent event) {
        dev.visfox.thaumichorizons.world.pocket.PocketPlanes.onServerStopped(event.getServer());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        dev.visfox.thaumichorizons.compat.tc.TcReforgedPort.logCompatibility();
        event.enqueueWork(ThDispenserBehaviors::register);
        LOGGER.info("Thaumic Horizons: Reforged loaded");
    }

    public static net.minecraft.resources.Identifier id(String path) {
        return net.minecraft.resources.Identifier.fromNamespaceAndPath(MODID, path);
    }
}
