package dev.visfox.thaumichorizons.client.render.block;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.blockentity.CloudBlockEntity;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class ThBlockRenderers {
    private ThBlockRenderers() {
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ThBlockEntities.NODE_MONITOR.get(), NodeMonitorRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.CLOUD.get(), CloudBlockRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.SPIKE.get(), SpikeBlockRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.RECOMBINATOR.get(), RecombinatorRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.VAT_MATRIX.get(), VatMatrixRenderer::new);
        event.registerBlockEntityRenderer(PocketRegistry.SLOT_BE.get(), SlotBlockRenderer::new);
    }
}
