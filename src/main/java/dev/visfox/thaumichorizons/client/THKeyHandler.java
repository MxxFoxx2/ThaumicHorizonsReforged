package dev.visfox.thaumichorizons.client;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.platform.ClientNetwork;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import dev.visfox.thaumichorizons.network.OpenFingersPayload;
import dev.visfox.thaumichorizons.network.ToggleInfusionPayload;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class THKeyHandler {
    private THKeyHandler() {
    }

    public static final KeyMapping ARCANE_LENS = new KeyMapping("key.thaumichorizons.arcane_lens", InputConstants.KEY_V, KeyMapping.Category.MISC);
    public static final KeyMapping MORPHIC_FINGERS = new KeyMapping("key.thaumichorizons.morphic_fingers", InputConstants.KEY_M, KeyMapping.Category.MISC);
    public static final KeyMapping TOGGLE_CLIMB = new KeyMapping("key.thaumichorizons.toggle_climb", InputConstants.KEY_C, KeyMapping.Category.MISC);
    public static final KeyMapping TOGGLE_INVISIBLE = new KeyMapping("key.thaumichorizons.toggle_invisible", InputConstants.KEY_X, KeyMapping.Category.MISC);

    @SubscribeEvent
    static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(ARCANE_LENS);
        event.register(MORPHIC_FINGERS);
        event.register(TOGGLE_CLIMB);
        event.register(TOGGLE_INVISIBLE);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        while (TOGGLE_CLIMB.consumeClick()) {
            ClientNetwork.sendToServer(new ToggleInfusionPayload(CreatureInfusion.SPIDER_CLIMB));
        }
        while (TOGGLE_INVISIBLE.consumeClick()) {
            ClientNetwork.sendToServer(new ToggleInfusionPayload(CreatureInfusion.CHAMELEON_SKIN));
        }
        while (MORPHIC_FINGERS.consumeClick()) {
            ClientNetwork.sendToServer(new OpenFingersPayload());
        }
        ARCANE_LENS.consumeClick();
    }
}
