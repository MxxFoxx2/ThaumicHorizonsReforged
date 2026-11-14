package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID)
public final class GolemEventsTH {
    private GolemEventsTH() {
    }

    @SubscribeEvent
    static void golemDies(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof EntityGolemTH golem) {
            golem.die();
        }
    }
}
