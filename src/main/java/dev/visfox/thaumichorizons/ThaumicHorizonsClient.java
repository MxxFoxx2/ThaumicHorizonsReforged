package dev.visfox.thaumichorizons;

import dev.visfox.thaumichorizons.client.screen.BloodInfuserScreen;
import dev.visfox.thaumichorizons.client.screen.FingersScreen;
import dev.visfox.thaumichorizons.client.screen.InspiratronScreen;
import dev.visfox.thaumichorizons.client.screen.InjectorScreen;
import dev.visfox.thaumichorizons.client.screen.SoulExtractorScreen;
import dev.visfox.thaumichorizons.client.screen.SoulforgeScreen;
import dev.visfox.thaumichorizons.client.screen.VatScreen;
import dev.visfox.thaumichorizons.client.screen.VisDynamoScreen;
import dev.visfox.thaumichorizons.client.render.item.KeystoneTintSource;
import dev.visfox.thaumichorizons.registry.ThMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = ThaumicHorizons.MODID, dist = Dist.CLIENT)
public class ThaumicHorizonsClient {
    public ThaumicHorizonsClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::registerScreens);
        modEventBus.addListener(this::registerItemTintSources);
    }

    private void clientSetup(FMLClientSetupEvent event) {
    }

    private void registerItemTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(ThaumicHorizons.id("keystone"), KeystoneTintSource.MAP_CODEC);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ThMenus.VAT.get(), VatScreen::new);
        event.register(ThMenus.SOUL_SIEVE.get(), SoulExtractorScreen::new);
        event.register(ThMenus.INSPIRATRON.get(), InspiratronScreen::new);
        event.register(ThMenus.SOULFORGE.get(), SoulforgeScreen::new);
        event.register(ThMenus.BLOOD_INFUSER.get(), BloodInfuserScreen::new);
        event.register(ThMenus.INJECTOR.get(), InjectorScreen::new);
        event.register(ThMenus.FINGERS.get(), FingersScreen::new);
        event.register(ThMenus.VIS_DYNAMO.get(), VisDynamoScreen::new);
        event.register(ThMenus.CASE.get(), dev.visfox.thaumichorizons.client.screen.GuiCase::new);
    }
}
