package dev.visfox.thaumichorizons.client.render.entity;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.BlastPhialProjectile;
import dev.visfox.thaumichorizons.entity.EggIncubatedEntity;
import dev.visfox.thaumichorizons.registry.ThEntities;
import dev.visfox.thaumichorizons.client.render.model.FamiliarHatModel;
import dev.visfox.thaumichorizons.client.render.entity.SyringeProjectileRenderer;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.entity.UndeadHorseRenderer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import thaumcraft.client.render.entity.golem.GolemRenderer;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class ThEntityRenderers {
    private ThEntityRenderers() {
    }

    @SubscribeEvent
    static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(FamiliarHatModel.LAYER, FamiliarHatModel::createLayer);
        event.registerLayerDefinition(ThBoatRenderer.GREATWOOD_LAYER, BoatModel::createBodyModel);
        event.registerLayerDefinition(ThBoatRenderer.THAUMIUM_LAYER, BoatModel::createBodyModel);
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ThEntities.BOAT_GREATWOOD.get(), context ->
            new ThBoatRenderer(context, ThBoatRenderer.GREATWOOD_LAYER,
                ThaumicHorizons.id("textures/entity/boatgreatwood.png")));
        event.registerEntityRenderer(ThEntities.BOAT_THAUMIUM.get(), context ->
            new ThBoatRenderer(context, ThBoatRenderer.THAUMIUM_LAYER,
                ThaumicHorizons.id("textures/entity/boatthaumium.png")));
        event.registerEntityRenderer(ThEntities.ALCHEMITE_PRIMED.get(), AlchemitePrimedRenderer::new);
        event.registerEntityRenderer(ThEntities.EGG_INCUBATED.get(), context -> new ThrownItemRenderer<EggIncubatedEntity>(context, 1.0F, true));
        event.registerEntityRenderer(ThEntities.SYRINGE.get(), SyringeProjectileRenderer::new);
        event.registerEntityRenderer(ThEntities.BLAST_PHIAL.get(), context -> new ThrownItemRenderer<BlastPhialProjectile>(context, 1.0F, true));
        event.registerEntityRenderer(ThEntities.CHROMATIC_SHEEP.get(), SheepRenderer::new);
        event.registerEntityRenderer(ThEntities.SELF_SHEARING_SHEEP.get(), SheepRenderer::new);
        event.registerEntityRenderer(ThEntities.GUARDIAN_PANTHER.get(), GuardianPantherRenderer::new);
        event.registerEntityRenderer(ThEntities.GRAVEKEEPER.get(), GravekeeperRenderer::new);
        event.registerEntityRenderer(ThEntities.FAMILIAR.get(), FamiliarRenderer::new);
        event.registerEntityRenderer(ThEntities.CHOCOLATE_COW.get(), ChocolateCowRenderer::new);
        event.registerEntityRenderer(ThEntities.SCHOLAR_CHICKEN.get(), ScholarChickenRenderer::new);
        event.registerEntityRenderer(ThEntities.GOLD_CHICKEN.get(), GoldChickenRenderer::new);
        event.registerEntityRenderer(ThEntities.ORE_PIG.get(), OrePigRenderer::new);
        event.registerEntityRenderer(ThEntities.TAINT_EATER_PIG.get(), TaintEaterPigRenderer::new);
        event.registerEntityRenderer(ThEntities.ENDERSTEED.get(), EndersteedRenderer::new);
        event.registerEntityRenderer(ThEntities.HORSE_ZOMBIE.get(), context ->
            new UndeadHorseRenderer(context, ModelLayers.ZOMBIE_HORSE));
        event.registerEntityRenderer(ThEntities.HORSE_SKELETON.get(), context ->
            new UndeadHorseRenderer(context, ModelLayers.SKELETON_HORSE));
        event.registerEntityRenderer(ThEntities.NIGHTMARE.get(), NightmareRenderer::new);
        event.registerEntityRenderer(ThEntities.SEAWOLF.get(), SeawolfRenderer::new);
        event.registerEntityRenderer(ThEntities.NETHER_HOUND.get(), NetherHoundRenderer::new);
        event.registerEntityRenderer(ThEntities.LUNAR_WOLF.get(), LunarWolfRenderer::new);
        event.registerEntityRenderer(ThEntities.MEAT_SLIME.get(), MeatSlimeRenderer::new);
        event.registerEntityRenderer(ThEntities.MERCURIAL_SLIME.get(), MercurialSlimeRenderer::new);
        event.registerEntityRenderer(ThEntities.VOLT_SLIME.get(), VoltSlimeRenderer::new);
        event.registerEntityRenderer(ThEntities.MED_SLIME.get(), MedSlimeRenderer::new);
        event.registerEntityRenderer(ThEntities.SHEEDER.get(), SheederRenderer::new);
        event.registerEntityRenderer(ThEntities.LOST_SOUL.get(), LostSoulRenderer::new);
        event.registerEntityRenderer(ThEntities.GOLEM_TH.get(), GolemRenderer::new);
    }
}
