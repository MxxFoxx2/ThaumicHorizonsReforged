package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.AlchemitePrimedEntity;
import dev.visfox.thaumichorizons.entity.BlastPhialProjectile;
import dev.visfox.thaumichorizons.entity.SyringeProjectile;
import dev.visfox.thaumichorizons.entity.ChocolateCow;
import dev.visfox.thaumichorizons.entity.ChromaticSheep;
import dev.visfox.thaumichorizons.entity.Endersteed;
import dev.visfox.thaumichorizons.entity.HorseSkeleton;
import dev.visfox.thaumichorizons.entity.HorseZombie;
import dev.visfox.thaumichorizons.entity.NightmareTH;
import dev.visfox.thaumichorizons.entity.EggIncubatedEntity;
import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import dev.visfox.thaumichorizons.entity.Familiar;
import dev.visfox.thaumichorizons.entity.GoldChicken;
import dev.visfox.thaumichorizons.entity.Gravekeeper;
import dev.visfox.thaumichorizons.entity.GuardianPanther;
import dev.visfox.thaumichorizons.entity.LostSoul;
import dev.visfox.thaumichorizons.entity.LunarWolf;
import dev.visfox.thaumichorizons.entity.MeatSlime;
import dev.visfox.thaumichorizons.entity.MedSlime;
import dev.visfox.thaumichorizons.entity.MercurialSlime;
import dev.visfox.thaumichorizons.entity.NetherHound;
import dev.visfox.thaumichorizons.entity.OrePig;
import dev.visfox.thaumichorizons.entity.ScholarChicken;
import dev.visfox.thaumichorizons.entity.Seawolf;
import dev.visfox.thaumichorizons.entity.SelfShearingSheep;
import dev.visfox.thaumichorizons.entity.Sheeder;
import dev.visfox.thaumichorizons.entity.TaintEaterPig;
import dev.visfox.thaumichorizons.entity.VoltSlime;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThEntities {
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(ThaumicHorizons.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<AlchemitePrimedEntity>> ALCHEMITE_PRIMED =
        ENTITIES.registerEntityType("alchemiteprimed", AlchemitePrimedEntity::new, MobCategory.MISC,
            builder -> builder.sized(0.98F, 0.98F).clientTrackingRange(10).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<SyringeProjectile>> SYRINGE =
        ENTITIES.registerEntityType("syringe_projectile", SyringeProjectile::new, MobCategory.MISC,
            builder -> builder.sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<BlastPhialProjectile>> BLAST_PHIAL =
        ENTITIES.registerEntityType("blast_phial", BlastPhialProjectile::new, MobCategory.MISC,
            builder -> builder.sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(10));

    public static final DeferredHolder<EntityType<?>, EntityType<EggIncubatedEntity>> EGG_INCUBATED =
        ENTITIES.registerEntityType("eggincubated", EggIncubatedEntity::new, MobCategory.MISC,
            builder -> builder.sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(20).fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<ChromaticSheep>> CHROMATIC_SHEEP = creature(
        "chromaticsheep", ChromaticSheep::new, builder -> builder.sized(0.9F, 1.3F).eyeHeight(1.213F));
    public static final DeferredHolder<EntityType<?>, EntityType<SelfShearingSheep>> SELF_SHEARING_SHEEP = creature(
        "selfshearingsheep", SelfShearingSheep::new, builder -> builder.sized(0.9F, 1.3F).eyeHeight(1.213F));
    public static final DeferredHolder<EntityType<?>, EntityType<GuardianPanther>> GUARDIAN_PANTHER = creature(
        "guardianpanther", GuardianPanther::new, builder -> builder.sized(1.2F, 1.6F).eyeHeight(1.44F));
    public static final DeferredHolder<EntityType<?>, EntityType<Gravekeeper>> GRAVEKEEPER = creature(
        "gravekeeper", Gravekeeper::new, builder -> builder.sized(0.6F, 0.7F).eyeHeight(0.63F));
    public static final DeferredHolder<EntityType<?>, EntityType<Familiar>> FAMILIAR = creature(
        "familiar", Familiar::new, builder -> builder.sized(0.6F, 0.7F).eyeHeight(0.63F));
    public static final DeferredHolder<EntityType<?>, EntityType<ChocolateCow>> CHOCOLATE_COW = creature(
        "chocolatecow", ChocolateCow::new, builder -> builder.sized(0.9F, 1.4F).eyeHeight(1.268F));
    public static final DeferredHolder<EntityType<?>, EntityType<ScholarChicken>> SCHOLAR_CHICKEN = creature(
        "scholarchicken", ScholarChicken::new, builder -> builder.sized(0.4F, 0.7F).eyeHeight(0.6445F));
    public static final DeferredHolder<EntityType<?>, EntityType<GoldChicken>> GOLD_CHICKEN = creature(
        "goldchicken", GoldChicken::new, builder -> builder.sized(0.4F, 0.7F).eyeHeight(0.6445F));
    public static final DeferredHolder<EntityType<?>, EntityType<OrePig>> ORE_PIG = creature(
        "orepig", OrePig::new, builder -> builder.sized(0.9F, 0.9F).eyeHeight(0.78F));
    public static final DeferredHolder<EntityType<?>, EntityType<TaintEaterPig>> TAINT_EATER_PIG = creature(
        "tainteaterpig", TaintEaterPig::new, builder -> builder.sized(0.9F, 0.9F).eyeHeight(0.78F));
    public static final DeferredHolder<EntityType<?>, EntityType<Endersteed>> ENDERSTEED = creature(
        "endersteed", Endersteed::new, builder -> builder.sized(1.3965F, 1.6F).eyeHeight(1.473F));
    public static final DeferredHolder<EntityType<?>, EntityType<HorseZombie>> HORSE_ZOMBIE =
        ENTITIES.registerEntityType("horse_zombie", HorseZombie::new, MobCategory.CREATURE,
            builder -> builder.sized(1.3965F, 1.6F).clientTrackingRange(64).updateInterval(3));
    public static final DeferredHolder<EntityType<?>, EntityType<HorseSkeleton>> HORSE_SKELETON =
        ENTITIES.registerEntityType("horse_skeleton", HorseSkeleton::new, MobCategory.CREATURE,
            builder -> builder.sized(1.3965F, 1.6F).clientTrackingRange(64).updateInterval(3));
    public static final DeferredHolder<EntityType<?>, EntityType<NightmareTH>> NIGHTMARE =
        ENTITIES.registerEntityType("nightmare", NightmareTH::new, MobCategory.CREATURE,
            builder -> builder.sized(1.3965F, 1.6F).clientTrackingRange(64).updateInterval(3).fireImmune());

    public static final DeferredHolder<EntityType<?>, EntityType<Seawolf>> SEAWOLF = creature(
        "seawolf", Seawolf::new, builder -> builder.sized(0.6F, 0.85F).eyeHeight(0.425F));
    public static final DeferredHolder<EntityType<?>, EntityType<NetherHound>> NETHER_HOUND = creature(
        "netherhound", NetherHound::new, builder -> builder.sized(0.6F, 0.85F).eyeHeight(0.425F));
    public static final DeferredHolder<EntityType<?>, EntityType<LunarWolf>> LUNAR_WOLF = creature(
        "lunarwolf", LunarWolf::new, builder -> builder.sized(0.6F, 0.85F).eyeHeight(0.425F));
    public static final DeferredHolder<EntityType<?>, EntityType<MeatSlime>> MEAT_SLIME = creature(
        "meatslime", MeatSlime::new, builder -> builder.sized(2.04F, 2.04F).eyeHeight(1.02F).notInPeaceful());
    public static final DeferredHolder<EntityType<?>, EntityType<MercurialSlime>> MERCURIAL_SLIME = creature(
        "mercurialslime", MercurialSlime::new, builder -> builder.sized(2.04F, 2.04F).eyeHeight(1.02F).notInPeaceful());
    public static final DeferredHolder<EntityType<?>, EntityType<VoltSlime>> VOLT_SLIME = creature(
        "voltslime", VoltSlime::new, builder -> builder.sized(2.04F, 2.04F).eyeHeight(1.02F).notInPeaceful());
    public static final DeferredHolder<EntityType<?>, EntityType<MedSlime>> MED_SLIME = creature(
        "medslime", MedSlime::new, builder -> builder.sized(2.04F, 2.04F).eyeHeight(1.02F).notInPeaceful());
    public static final DeferredHolder<EntityType<?>, EntityType<Sheeder>> SHEEDER = creature(
        "sheeder", Sheeder::new, builder -> builder.sized(1.4F, 0.9F).eyeHeight(0.65F));
    public static final DeferredHolder<EntityType<?>, EntityType<LostSoul>> LOST_SOUL = creature(
        "lostsoul", LostSoul::new, builder -> builder.sized(0.9F, 0.9F).eyeHeight(0.45F).notInPeaceful());
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGolemTH>> GOLEM_TH =
        ENTITIES.registerEntityType("golemth", EntityGolemTH::new, MobCategory.MISC,
            builder -> builder.sized(0.4F, 0.95F).clientTrackingRange(4).updateInterval(3));

    private ThEntities() {
    }

    private static <T extends net.minecraft.world.entity.Mob> DeferredHolder<EntityType<?>, EntityType<T>> creature(
        String name, EntityType.EntityFactory<T> factory, java.util.function.UnaryOperator<EntityType.Builder<T>> customize) {
        return ENTITIES.registerEntityType(name, factory, MobCategory.CREATURE,
            builder -> customize.apply(builder).clientTrackingRange(64).updateInterval(3));
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        bus.addListener(ThEntities::createAttributes);
    }

    private static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(CHROMATIC_SHEEP.get(), ChromaticSheep.createAttributes().build());
        event.put(SELF_SHEARING_SHEEP.get(), SelfShearingSheep.createAttributes().build());
        event.put(GUARDIAN_PANTHER.get(), GuardianPanther.createAttributes().build());
        event.put(GRAVEKEEPER.get(), Gravekeeper.createAttributes().build());
        event.put(FAMILIAR.get(), Familiar.createAttributes().build());
        event.put(CHOCOLATE_COW.get(), ChocolateCow.createAttributes().build());
        event.put(SCHOLAR_CHICKEN.get(), ScholarChicken.createAttributes().build());
        event.put(GOLD_CHICKEN.get(), GoldChicken.createAttributes().build());
        event.put(ORE_PIG.get(), OrePig.createAttributes().build());
        event.put(TAINT_EATER_PIG.get(), TaintEaterPig.createAttributes().build());
        event.put(ENDERSTEED.get(), Endersteed.createAttributes().build());
        event.put(HORSE_ZOMBIE.get(), HorseZombie.createAttributes().build());
        event.put(HORSE_SKELETON.get(), HorseSkeleton.createAttributes().build());
        event.put(NIGHTMARE.get(), NightmareTH.createAttributes().build());
        event.put(SEAWOLF.get(), Seawolf.createAttributes().build());
        event.put(NETHER_HOUND.get(), NetherHound.createAttributes().build());
        event.put(LUNAR_WOLF.get(), LunarWolf.createAttributes().build());
        event.put(MEAT_SLIME.get(), MeatSlime.createAttributes().build());
        event.put(MERCURIAL_SLIME.get(), MercurialSlime.createAttributes().build());
        event.put(VOLT_SLIME.get(), VoltSlime.createAttributes().build());
        event.put(MED_SLIME.get(), MedSlime.createAttributes().build());
        event.put(SHEEDER.get(), Sheeder.createAttributes().build());
        event.put(LOST_SOUL.get(), LostSoul.createAttributes().build());
        event.put(GOLEM_TH.get(), thaumcraft.entity.golem.GolemBase.createAttributes().build());
    }
}
