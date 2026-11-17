package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS =
        DeferredRegister.create(Registries.SOUND_EVENT, ThaumicHorizons.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> VORTEX = SOUNDS.register(
        "vortex",
        () -> SoundEvent.createFixedRangeEvent(ThaumicHorizons.id("vortex"), 64.0F)
    );

    private ThSounds() {
    }

    public static void register(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
