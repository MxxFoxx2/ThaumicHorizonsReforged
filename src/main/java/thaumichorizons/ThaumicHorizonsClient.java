package thaumichorizons;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;

@Mod(value = ThaumicHorizons.MODID, dist = Dist.CLIENT)
public class ThaumicHorizonsClient {
    public ThaumicHorizonsClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
    }

    private void clientSetup(FMLClientSetupEvent event) {
    }
}
