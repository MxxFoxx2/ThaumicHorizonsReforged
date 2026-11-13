package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.effect.ThMobEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThEffects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ThaumicHorizons.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> VIS_BOOST = register("vis_boost", ThMobEffect.Kind.VIS_BOOST, 6697847);
    public static final DeferredHolder<MobEffect, MobEffect> VIS_REGEN = register("vis_regen", ThMobEffect.Kind.VIS_REGEN, 5701759);
    public static final DeferredHolder<MobEffect, MobEffect> VACUUM = register("vacuum", ThMobEffect.Kind.VACUUM, 4210752);
    public static final DeferredHolder<MobEffect, MobEffect> SHOCK = register("shock", ThMobEffect.Kind.SHOCK, 11513861);
    public static final DeferredHolder<MobEffect, MobEffect> SYNTHESIS = register("synthesis", ThMobEffect.Kind.SYNTHESIS, 3253280);

    private ThEffects() {
    }

    private static DeferredHolder<MobEffect, MobEffect> register(String name, ThMobEffect.Kind kind, int color) {
        return EFFECTS.register(name, () -> new ThMobEffect(kind, color));
    }

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
