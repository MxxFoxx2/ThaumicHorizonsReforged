package dev.visfox.thaumichorizons;

import com.mojang.logging.LogUtils;
import dev.visfox.thaumichorizons.registry.ThEffects;
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
        ThEffects.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Thaumic Horizons: Reforged loaded");
    }
}
