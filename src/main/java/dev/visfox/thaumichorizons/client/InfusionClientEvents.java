package dev.visfox.thaumichorizons.client;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class InfusionClientEvents {
    private InfusionClientEvents() {
    }

    @SubscribeEvent
    static void onRenderPlayerPre(RenderPlayerEvent.Pre<?> event) {
        Entity entity = Minecraft.getInstance().level == null ? null : Minecraft.getInstance().level.getEntity(event.getRenderState().id);
        if (entity instanceof Player player
            && CreatureInfusion.hasChameleon(player)
            && CreatureInfusion.isChameleonActive(player)) {
            event.setCanceled(true);
        }
    }
}
