package dev.visfox.thaumichorizons;

import com.mojang.logging.LogUtils;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import dev.visfox.thaumichorizons.platform.DataAttachments;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThCreativeTabs;
import dev.visfox.thaumichorizons.registry.ThEffects;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(ThaumicHorizons.MODID)
public class ThaumicHorizons {
    public static final String MODID = "thaumichorizons";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ThaumicHorizons(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        CreatureInfusion.init();
        DataAttachments.register(modEventBus);
        ThEffects.register(modEventBus);
        ThBlocks.register(modEventBus);
        ThItems.register(modEventBus);
        ThCreativeTabs.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Thaumic Horizons: Reforged loaded");
    }
}
