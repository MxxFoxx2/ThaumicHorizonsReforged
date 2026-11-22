package dev.visfox.thaumichorizons.client.render.block;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.render.item.SpikeItemSpecialModelRenderer;
import dev.visfox.thaumichorizons.client.render.item.SyringeItemSpecialModelRenderer;
import dev.visfox.thaumichorizons.client.render.item.CorpseEffigyItemSpecialModelRenderer;
import dev.visfox.thaumichorizons.client.render.item.InjectorItemSpecialModelRenderer;
import dev.visfox.thaumichorizons.client.render.item.SoulJarItemSpecialModelRenderer;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.blockentity.CloudBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VatBlockEntity;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class ThBlockRenderers {
    private ThBlockRenderers() {
    }

    @SubscribeEvent
    static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(ThaumicHorizons.id("spike"), SpikeItemSpecialModelRenderer.Unbaked.MAP_CODEC);
        event.register(ThaumicHorizons.id("syringe"), SyringeItemSpecialModelRenderer.Unbaked.MAP_CODEC);
        event.register(ThaumicHorizons.id("corpse_effigy"), CorpseEffigyItemSpecialModelRenderer.Unbaked.MAP_CODEC);
        event.register(ThaumicHorizons.id("injector"), InjectorItemSpecialModelRenderer.Unbaked.MAP_CODEC);
        event.register(ThaumicHorizons.id("soul_jar"), SoulJarItemSpecialModelRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ThBlockEntities.NODE_MONITOR.get(), NodeMonitorRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.SOUL_EXTRACTOR.get(), SoulSieveRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.SYNTH_NODE.get(), SyntheticNodeRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.INSPIRATRON.get(), InspiratronRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.SOULFORGE.get(), SoulforgeRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.BLOOD_INFUSER.get(), BloodInfuserRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.SOUL_BEACON.get(), SoulBeaconRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.VIS_DYNAMO.get(), VisDynamoRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.ESSENTIA_DYNAMO.get(), EssentiaDynamoRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.TRANSDUCTION_AMPLIFIER.get(), TransductionAmplifierRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.VORTEX.get(), VortexRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.CLOUD.get(), CloudBlockRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.SPIKE.get(), SpikeBlockRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.RECOMBINATOR.get(), RecombinatorRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.VORTEX_STABILIZER.get(), VortexStabilizerRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.VAT.get(), VatStructureRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.VAT_MATRIX.get(), VatMatrixRenderer::new);
        event.registerBlockEntityRenderer(ThBlockEntities.SOUL_JAR.get(), SoulJarPreviewRenderer::new);
        event.registerBlockEntityRenderer(PocketRegistry.SLOT_BE.get(), SlotBlockRenderer::new);
        event.registerBlockEntityRenderer(PocketRegistry.PORTAL_BE.get(), PortalTHRenderer::new);
    }
}
