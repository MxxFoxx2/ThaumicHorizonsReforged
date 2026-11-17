package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID)
public final class SyntheticNodeEvents {
    private SyntheticNodeEvents() {
    }

    @SubscribeEvent
    static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel().getBlockEntity(event.getPos()) instanceof SyntheticNodeBlockEntity node)
                || !node.addEssence(event.getItemStack())) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
