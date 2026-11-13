package dev.visfox.thaumichorizons.infusion;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import dev.visfox.thaumichorizons.ThaumicHorizons;

@EventBusSubscriber(modid = ThaumicHorizons.MODID)
public final class InfusionEvents {
    private InfusionEvents() {
    }

    @SubscribeEvent
    static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof ServerPlayer target && CreatureInfusion.hasChameleon(target)) {
            CreatureInfusion.DATA.sync(target);
        }
    }

    @SubscribeEvent
    static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            CreatureInfusion.applyVisualState(player);
            if (CreatureInfusion.hasChameleon(player)) {
                CreatureInfusion.DATA.sync(player);
            }
        }
    }
}
