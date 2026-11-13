package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.AlchemitePrimedEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThEntities {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(ThaumicHorizons.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AlchemitePrimedEntity>> ALCHEMITE_PRIMED =
        ENTITIES.registerEntityType("alchemiteprimed", AlchemitePrimedEntity::new, MobCategory.MISC,
            builder -> builder.sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10));

    private ThEntities() {
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
