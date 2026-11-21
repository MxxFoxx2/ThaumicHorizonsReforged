package dev.visfox.thaumichorizons.research;

import java.util.List;
import net.minecraft.resources.Identifier;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.compat.CompatAspectList;
import dev.visfox.thaumichorizons.compat.ResearchCompat.Flag;
import dev.visfox.thaumichorizons.compat.ResearchCompat.PageKind;
import dev.visfox.thaumichorizons.compat.ResearchCompat.PageSpec;
import dev.visfox.thaumichorizons.compat.ResearchCompat.ResearchSpec;

public final class ConfigResearch {
    private static final String CATEGORY = "ThaumicHorizons";

    private ConfigResearch() {
    }

    public static void init() {
        TcReforgedPort.research.registerCategory(
            CATEGORY,
            th("textures/misc/vat.png"),
            Identifier.parse("thaumcraft:textures/gui/gui_researchback.png"));
        registerAlternateGolemBell();
        registerAlcheponics();
        registerAlchIncubation();
        registerMeatGrowth();
        registerFleshLeather();
        registerShardTrans();
        registerEtherealShard();
        registerAlchemite();
        registerVisDynamo();
        registerEssentiaDynamo();
        registerNodeMonitor();
        registerSoulExtractor();
        registerSoulKnowledge();
        registerSoulAssembler();
        registerFocusContainment();
        registerMirrorAmulet();
        registerEssentiaInjection();
        registerBlastPhial();
        registerInjector();
        registerHealingVat();
        registerIncarnationVat();
        registerInfusionVat();
        registerCloneVillager();
        registerNightmare();
        registerCloneSelf();
        registerLensFire();
        registerLensWater();
        registerLensEarth();
        registerLensAir();
        registerLensOrderEntropy();
        registerLensCase();
        registerFocusLiquefaction();
        registerFocusDisintegration();
        registerFocusIllumination();
        registerFocusAnimation();
        registerGreatwoodBoat();
        registerThaumiumBoat();
        registerGolemPowder();
        registerPlanarTheory();
        registerTransductionAmplifier();
        registerVortexStabilizer();
        registerRecombinator();
        registerPlanarRift();
        registerWispSpawn();
        registerCrystalWand();
        registerVoidPutty();
        registerVoidGolem();
        registerPocketPlane();
        registerPlanarClouds();
        registerLeviathan();
        registerPlanarKeystone();
        registerGeneralInfusion();
        registerInstilledLoyalty();
        registerRunicCreature();
        registerEldritchFangs();
        registerPortability();
        registerSheepInfusion();
        registerCatInfusion();
        registerCowInfusion();
        registerChickenInfusion();
        registerPigInfusion();
        registerDogInfusion();
        registerHorseInfusion();
        registerNightmareInfusion();
        registerSheederInfusion();
        registerSelfInfusion();
        registerHumanInfusion();
        registerMorphicFingers();
        registerSilverHeart();
        registerSynthSkin();
        registerAmphibious();
        registerWarpedTumor();
        registerSpiderClimb();
        registerChameleonSkin();
    }

    private static Identifier th(String path) {
        return Identifier.fromNamespaceAndPath("thaumichorizons", path);
    }

    private static Identifier tc(String path) {
        return Identifier.fromNamespaceAndPath("thaumcraft", path);
    }

    private static Identifier mc(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    private static CompatAspectList tags(Object... pairs) {
        CompatAspectList list = new CompatAspectList();
        for (int index = 0; index < pairs.length; index += 2) {
            list.add((String) pairs[index], (Integer) pairs[index + 1]);
        }
        return list;
    }

    private static void register(String key, CompatAspectList tags, int column, int row, int complexity,
        Identifier icon, List<Flag> flags, List<String> parents, List<String> parentsHidden,
        List<String> siblings, List<String> itemTriggers, int warp, PageSpec... pages) {
        TcReforgedPort.research.registerResearch(new ResearchSpec(
            key, CATEGORY, tags, column, row, complexity, icon, flags, parents, parentsHidden,
            siblings, itemTriggers, List.of(), List.of(), warp, List.of(pages)));
    }

    private static void registerAlternateGolemBell() {
        register("alternateGolemBell", tags("ordo", 2, "potentia", 2, "vitreus", 3),
            -2, 0, 1, th("textures/item/newbell.png"),
            List.of(Flag.CONCEALED), List.of(), List.of(), List.of("GOLEMBELL"), List.of(), 0,
            PageSpec.text("alternateGolemBell1"),
            PageSpec.recipes(PageKind.ARCANE, "GOLEMBELL"));
    }

    private static void registerAlcheponics() {
        register("alcheponics", tags("messis", 5, "herba", 5, "lux", 5),
            0, 0, 1, mc("textures/item/wheat.png"),
            List.of(), List.of(), List.of(), List.of(), List.of(), 0,
            PageSpec.text("alcheponics1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "alcheponics"),
            PageSpec.text("alcheponics2"),
            PageSpec.recipes(PageKind.CRUCIBLE, "alcheponics1", "alcheponics2"),
            PageSpec.recipes(PageKind.CRUCIBLE, "alcheponics3", "alcheponics4"),
            PageSpec.recipes(PageKind.CRUCIBLE, "alcheponics"),
            PageSpec.recipes(PageKind.CRUCIBLE, "alcheponics1", "alcheponics2", "alcheponics3", "alcheponics4"));
    }

    private static void registerAlchIncubation() {
        register("alchIncubation", tags("bestia", 4, "victus", 2, "ignis", 2),
            2, 0, 1, th("textures/item/eggincubated.png"),
            List.of(Flag.SECONDARY), List.of("alcheponics"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("alchIncubation1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "alchIncubation"));
    }

    private static void registerMeatGrowth() {
        register("meatGrowth", tags("corpus", 6, "victus", 4, "bestia", 4, "fabrico", 4),
            4, 0, 2, mc("textures/item/beef.png"),
            List.of(), List.of("alchIncubation"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("meatGrowth1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "meatGrowth", "meatGrowth1", "meatGrowth2"));
    }

    private static void registerFleshLeather() {
        register("fleshLeather", tags("corpus", 4, "pannus", 6, "fabrico", 4),
            6, -1, 1, mc("textures/item/leather.png"),
            List.of(Flag.SECONDARY), List.of("meatGrowth"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("fleshLeather1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "fleshLeather", "fleshLeather1", "fleshLeather2"));
    }

    private static void registerShardTrans() {
        register("shardTrans", tags("permutatio", 4, "vitreus", 4, "ignis", 2, "aqua", 2, "terra", 2,
            "aer", 2, "ordo", 2, "perditio", 2),
            1, 2, 2, tc("textures/item/shard.png"),
            List.of(Flag.SECONDARY), List.of(), List.of(), List.of(), List.of(), 0,
            PageSpec.text("shardTrans1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "shardTrans", "shardTrans1", "shardTrans2", "shardTrans3", "shardTrans4", "shardTrans5"));
    }

    private static void registerEtherealShard() {
        register("etherealShard", tags("auram", 8, "potentia", 16, "vacuos", 16, "praecantatio", 8),
            1, 4, 3, th("textures/block/synthnode.png"),
            List.of(Flag.CONCEALED), List.of("shardTrans"), List.of("INFUSION", "VISPOWER"),
            List.of(), List.of(), 0,
            PageSpec.text("etherealShard1"),
            PageSpec.recipes(PageKind.INFUSION, "etherealShard"),
            PageSpec.text("etherealShard2"));
    }

    private static void registerAlchemite() {
        register("alchemite", tags("ignis", 2, "perditio", 2, "potentia", 4, "vitreus", 4, "permutatio", 2),
            3, 6, 2, th("textures/block/alchemite_side.png"),
            List.of(), List.of(), List.of("ESSENTIACRYSTAL"), List.of(), List.of(), 0,
            PageSpec.text("alchemite1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "alchemite"));
    }

    private static void registerVisDynamo() {
        register("visDynamo", tags("auram", 5, "machina", 5, "potentia", 5),
            3, 2, 2, th("textures/misc/vat.png"),
            List.of(), List.of(), List.of("INFUSION", "VISPOWER"), List.of(), List.of(), 0,
            PageSpec.text("visDynamo1"),
            PageSpec.recipes(PageKind.INFUSION, "visDynamo"));
    }

    private static void registerEssentiaDynamo() {
        register("essentiaDynamo", tags("auram", 5, "machina", 5, "potentia", 5, "aqua", 5),
            3, 4, 2, th("textures/misc/vat.png"),
            List.of(Flag.SECONDARY, Flag.CONCEALED), List.of("visDynamo"),
            List.of("INFUSION", "VISPOWER", "THAUMIUM"), List.of(), List.of(), 0,
            PageSpec.text("essentiaDynamo1"),
            PageSpec.recipes(PageKind.INFUSION, "essentiaDynamo"),
            PageSpec.text("essentiaDynamo2"));
    }

    private static void registerNodeMonitor() {
        register("nodeMonitor", tags("auram", 3, "machina", 3, "sensus", 3),
            1, 6, 1, th("textures/misc/vat.png"),
            List.of(), List.of(), List.of(), List.of(), List.of(), 0,
            PageSpec.text("nodeMonitor1"),
            PageSpec.recipes(PageKind.INFUSION, "nodeMonitor"));
    }

    private static void registerSoulExtractor() {
        register("soulExtractor", tags("spiritus", 4, "vinculum", 4, "terra", 4, "permutatio", 4),
            5, 6, 1, th("textures/misc/vat.png"),
            List.of(Flag.HIDDEN), List.of("JARBRAIN", "ENCHFABRIC"), List.of(), List.of(),
            List.of("minecraft:soul_sand"), 3,
            PageSpec.text("soulExtractor1"),
            PageSpec.recipes(PageKind.INFUSION, "soulExtractor"),
            PageSpec.text("soulExtractor2"));
    }

    private static void registerSoulKnowledge() {
        register("soulKnowledge", tags("spiritus", 4, "sano", 4, "cognitio", 4),
            7, 6, 2, th("textures/misc/vat.png"),
            List.of(Flag.CONCEALED), List.of("soulExtractor"), List.of(), List.of(), List.of(), 2,
            PageSpec.text("soulKnowledge1"),
            PageSpec.recipes(PageKind.INFUSION, "soulKnowledge"),
            PageSpec.text("soulKnowledge2"));
    }

    private static void registerSoulAssembler() {
        register("soulAssembler", tags("spiritus", 4, "sano", 4, "cognitio", 4),
            9, 6, 3, th("textures/misc/vat.png"),
            List.of(Flag.CONCEALED), List.of("soulKnowledge"), List.of(), List.of(), List.of(), 2,
            PageSpec.text("soulAssembler1"),
            PageSpec.recipes(PageKind.INFUSION, "soulAssembler"),
            PageSpec.text("soulAssembler2"),
            PageSpec.text("soulAssembler3"));
    }

    private static void registerFocusContainment() {
        register("focusContainment", tags("vinculum", 6, "vacuos", 6, "iter", 2),
            8, -3, 3, th("textures/item/focus_containment.png"),
            List.of(Flag.CONCEALED), List.of(), List.of("DISTILESSENTIA", "FOCUSPORTABLEHOLE"),
            List.of(), List.of(), 0,
            PageSpec.text("focusContainment1"),
            PageSpec.recipes(PageKind.INFUSION, "focusContainment"),
            PageSpec.text("focusContainment2"));
    }

    private static void registerMirrorAmulet() {
        register("mirrorAmulet", tags("mortuus", 5, "perditio", 5, "vacuos", 5),
            17, 4, 2, th("textures/item/amuletmirror.png"),
            List.of(Flag.CONCEALED), List.of("cloneSelf"), List.of("MIRROR"), List.of(), List.of(), 0,
            PageSpec.text("mirrorAmulet1"),
            PageSpec.recipes(PageKind.INFUSION, "mirrorAmulet"));
    }

    private static void registerEssentiaInjection() {
        register("essentiaInjection", tags("victus", 4, "humanus", 2, "permutatio", 2, "praecantatio", 2),
            6, 1, 3, th("textures/item/syringe.png"),
            List.of(), List.of("meatGrowth"), List.of("THAUMATORIUM", "INFUSION"), List.of(),
            List.of(), 1,
            PageSpec.text("essentiaInjection1"),
            PageSpec.recipes(PageKind.ARCANE, "essentiaInjectionSyringe"),
            PageSpec.text("essentiaInjection2"),
            PageSpec.recipes(PageKind.INFUSION, "essentiaInjection"),
            PageSpec.text("essentiaInjection3"),
            PageSpec.text("essentiaInjection4"));
    }

    private static void registerBlastPhial() {
        register("blastPhial", tags("victus", 2, "ignis", 2, "perditio", 2),
            6, 3, 1, th("textures/item/phialblood.png"),
            List.of(Flag.SECONDARY, Flag.CONCEALED), List.of("essentiaInjection"), List.of(),
            List.of(), List.of(), 0,
            PageSpec.text("blastPhial1"),
            PageSpec.recipes(PageKind.ARCANE, "blastPhial"));
    }

    private static void registerInjector() {
        register("injector", tags("telum", 3, "machina", 3, "volatus", 3),
            8, 3, 1, th("textures/item/injector.png"),
            List.of(Flag.CONCEALED), List.of("blastPhial", "BONEBOW"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("injector1"),
            PageSpec.recipes(PageKind.INFUSION, "injector"));
    }

    private static void registerHealingVat() {
        register("healingVat", tags("victus", 2, "sano", 4, "machina", 2, "aqua", 2),
            8, 0, 3, th("textures/misc/vat.png"),
            List.of(Flag.SPECIAL), List.of("essentiaInjection", "fleshLeather"), List.of(),
            List.of(), List.of(), 0,
            PageSpec.text("healingVat1"),
            PageSpec.text("healingVat2"),
            PageSpec.concealedText("focusContainment", "healingVatContainment"));
    }

    private static void registerIncarnationVat() {
        register("incarnationVat", tags("victus", 3, "fabrico", 3, "bestia", 3, "machina", 2),
            10, 1, 2, mc("textures/item/cow_spawn_egg.png"),
            List.of(Flag.SECONDARY, Flag.CONCEALED), List.of("healingVat"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("incarnationVat1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "incarnationVat"),
            PageSpec.text("incarnationVat2"));
    }

    private static void registerInfusionVat() {
        register("infusionVat", tags("praecantatio", 5, "victus", 5, "fabrico", 5, "bestia", 5),
            10, -1, 3, th("textures/misc/vat.png"),
            List.of(Flag.CONCEALED), List.of("healingVat", "focusContainment"), List.of(),
            List.of("generalInfusion"), List.of(), 0,
            PageSpec.text("infusionVat1"),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/1.png"), null),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/2.png"), null),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/3.png"), null),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/4.png"), null),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/5.png"), null),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/6.png"), null),
            PageSpec.image(th("textures/misc/creature_page/infusionvat/7.png"), null),
            PageSpec.recipes(PageKind.INFUSION, "infusionVat"),
            PageSpec.text("infusionVat2"),
            PageSpec.text("infusionVat3"),
            PageSpec.concealedText("incarnationVat", "infusionVatClone"));
    }

    private static void registerCloneVillager() {
        register("cloneVillager", tags("spiritus", 8, "humanus", 8, "sano", 8),
            11, 4, 3, th("textures/misc/villager.png"),
            List.of(Flag.CONCEALED), List.of("incarnationVat", "soulAssembler"), List.of(),
            List.of("nightmare"), List.of(), 3,
            PageSpec.text("cloneVillager1"),
            PageSpec.recipes(PageKind.INFUSION, "cloneVillager"),
            PageSpec.text("cloneVillager2"));
    }

    private static void registerNightmare() {
        register("nightmare", tags("spiritus", 1),
            13, 4, 1, th("textures/misc/skull.png"),
            List.of(Flag.ROUND, Flag.CONCEALED), List.of("cloneVillager"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("nightmare1"),
            PageSpec.text("nightmare2"));
    }

    private static void registerCloneSelf() {
        register("cloneSelf", tags("spiritus", 4, "vinculum", 4, "humanus", 4, "sano", 4),
            15, 4, 2, th("textures/misc/steve.png"),
            List.of(Flag.CONCEALED), List.of("nightmare", "PRIMPEARL"), List.of(), List.of(),
            List.of(), 5,
            PageSpec.text("cloneSelf1"),
            PageSpec.recipes(PageKind.INFUSION, "cloneSelf"),
            PageSpec.text("cloneSelf2"));
    }

    private static void registerLensFire() {
        register("lensFire", tags("sensus", 3, "lux", 3, "potentia", 2),
            2, -2, 2, th("textures/item/lensfire.png"),
            List.of(), List.of("GOGGLES"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("lensFire1"),
            PageSpec.recipes(PageKind.INFUSION, "lensFire"));
    }

    private static void registerLensWater() {
        register("lensWater", tags("sensus", 4, "permutatio", 2),
            1, -4, 1, th("textures/item/lenswater.png"),
            List.of(Flag.CONCEALED), List.of("lensFire", "ARCANEEAR"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("lensWater1"),
            PageSpec.recipes(PageKind.INFUSION, "lensWater"));
    }

    private static void registerLensEarth() {
        register("lensEarth", tags("sensus", 4, "vacuos", 2, "lux", 2),
            3, -4, 1, th("textures/item/lensearth.png"),
            List.of(Flag.CONCEALED), List.of("lensFire"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("lensEarth1"),
            PageSpec.recipes(PageKind.INFUSION, "lensEarth"));
    }

    private static void registerLensAir() {
        register("lensAir", tags("sensus", 4, "auram", 2, "victus", 2, "exanimis", 2),
            -1, -4, 1, th("textures/item/lensair.png"),
            List.of(Flag.CONCEALED), List.of("lensFire"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("lensAir1"),
            PageSpec.recipes(PageKind.INFUSION, "lensAir"));
    }

    private static void registerLensOrderEntropy() {
        register("lensOrderEntropy", tags("sensus", 4, "cognitio", 4, "praecantatio", 2),
            5, -4, 1, th("textures/item/lensorderentropy.png"),
            List.of(Flag.CONCEALED), List.of("lensFire"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("lensOrderEntropy1"),
            PageSpec.recipes(PageKind.INFUSION, "lensOrderEntropy"));
    }

    private static void registerLensCase() {
        register("lensCase", tags("vitreus", 2, "pannus", 4, "vacuos", 2),
            2, -6, 1, th("textures/item/lenscase.png"),
            List.of(Flag.SECONDARY, Flag.CONCEALED),
            List.of("lensAir", "lensWater", "lensEarth", "lensOrderEntropy", "FOCUSPOUCH"),
            List.of(), List.of(), List.of(), 0,
            PageSpec.text("lensCase1"),
            PageSpec.recipes(PageKind.CRAFTING, "lensCase"));
    }

    private static void registerFocusLiquefaction() {
        register("focusLiquefaction", tags("ignis", 3, "metallum", 3, "permutatio", 2),
            1, 8, 2, th("textures/item/focus_liquefaction.png"),
            List.of(Flag.CONCEALED), List.of("focusIllumination", "FOCUSEXCAVATION"), List.of(),
            List.of(), List.of(), 0,
            PageSpec.text("focusLiquefaction1"),
            PageSpec.recipes(PageKind.INFUSION, "focusLiquefaction"));
    }

    private static void registerFocusDisintegration() {
        register("focusDisintegration", tags("praecantatio", 3, "vitreus", 2, "perditio", 3),
            3, 8, 2, th("textures/item/focus_disintegration.png"),
            List.of(Flag.CONCEALED), List.of("focusLiquefaction", "alchemite", "LIQUIDDEATH"),
            List.of(), List.of(), List.of(), 0,
            PageSpec.text("focusDisintegration1"),
            PageSpec.recipes(PageKind.INFUSION, "focusDisintegration"));
    }

    private static void registerFocusIllumination() {
        register("focusIllumination", tags("lux", 2, "potentia", 2, "praecantatio", 1),
            -1, 6, 1, th("textures/item/focus_illumination.png"),
            List.of(Flag.CONCEALED), List.of("FOCUSFIRE"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("focusIllumination1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "focusIllumination"));
    }

    private static void registerFocusAnimation() {
        register("focusAnimation", tags("humanus", 5, "motus", 5, "spiritus", 5, "fabrico", 3),
            12, -1, 3, th("textures/item/focus_animation.png"),
            List.of(Flag.CONCEALED), List.of("PRIMPEARL", "golemPowder"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("focusAnimation1"),
            PageSpec.recipes(PageKind.INFUSION, "focusAnimation"),
            PageSpec.text("focusAnimation2"));
    }

    private static void registerGreatwoodBoat() {
        register("greatwoodBoat", tags("aqua", 1, "iter", 1, "fabrico", 1),
            5, 8, 1, th("textures/item/boatgreatwood.png"),
            List.of(Flag.SECONDARY), List.of(), List.of(), List.of(), List.of(), 0,
            PageSpec.text("greatwoodBoat1"),
            PageSpec.recipes(PageKind.ARCANE, "greatwoodBoat"));
    }

    private static void registerThaumiumBoat() {
        register("thaumiumBoat", tags("ignis", 2, "metallum", 2, "praecantatio", 2),
            7, 8, 1, th("textures/item/boatthaumium.png"),
            List.of(), List.of("greatwoodBoat", "THAUMIUM"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("thaumiumBoat1"),
            PageSpec.recipes(PageKind.ARCANE, "thaumiumBoat"));
    }

    private static void registerGolemPowder() {
        register("golemPowder", tags("humanus", 3, "spiritus", 3, "motus", 3, "fabrico", 3),
            12, 1, 3, th("textures/item/golempowder.png"),
            List.of(Flag.CONCEALED), List.of("GOLEMTHAUMIUM", "ADVANCEDGOLEM", "incarnationVat"),
            List.of(), List.of(), List.of(), 0,
            PageSpec.text("golemPowder1"),
            PageSpec.recipes(PageKind.CRUCIBLE, "golemPowder"),
            PageSpec.text("golemPowder2"),
            PageSpec.text("golemPowder3"));
    }

    private static void registerPlanarTheory() {
        register("planarTheory", tags("vacuos", 5, "praecantatio", 5, "alienis", 5),
            10, 8, 3, th("textures/item/planarconduit.png"),
            List.of(Flag.CONCEALED), List.of("VISPOWER", "VOIDMETAL"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("planarTheory1"),
            PageSpec.recipes(PageKind.CRAFTING, "planarTheory"),
            PageSpec.text("planarTheory2"));
    }

    private static void registerTransductionAmplifier() {
        register("transductionAmplifier", tags("auram", 4, "potentia", 4, "vacuos", 3),
            12, 6, 3, th("textures/misc/vat.png"),
            List.of(Flag.CONCEALED), List.of("planarTheory"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("transductionAmplifier1"),
            PageSpec.recipes(PageKind.CRAFTING, "transductionAmplifier"),
            PageSpec.text("transductionAmplifier2"));
    }

    private static void registerVortexStabilizer() {
        register("vortexStabilizer", tags("auram", 3, "fames", 3, "vinculum", 3),
            12, 10, 2, th("textures/misc/vortex.png"),
            List.of(Flag.CONCEALED), List.of("planarTheory"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("vortexStabilizer1"),
            PageSpec.recipes(PageKind.INFUSION, "vortexStabilizer"));
    }

    private static void registerRecombinator() {
        register("recombinator", tags("auram", 4, "permutatio", 4, "fabrico", 4),
            12, 8, 3, th("textures/misc/vat.png"),
            List.of(Flag.CONCEALED), List.of("planarTheory", "PRIMPEARL"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("recombinator1"),
            PageSpec.recipes(PageKind.INFUSION, "recombinator"),
            PageSpec.text("recombinator2"),
            PageSpec.text("recombinator3"));
    }

    private static void registerPlanarRift() {
        register("planarRift", tags("vacuos", 4, "praecantatio", 4, "alienis", 2),
            14, 8, 2, th("textures/misc/rift.png"),
            List.of(Flag.SPECIAL, Flag.CONCEALED), List.of("vortexStabilizer", "transductionAmplifier"),
            List.of(), List.of(), List.of(), 3,
            PageSpec.text("planarRift1"),
            PageSpec.text("planarRift2"),
            PageSpec.text("planarRift3"));
    }

    private static void registerWispSpawn() {
        register("wispSpawn", tags("praecantatio", 2, "vitreus", 2, "victus", 2),
            15, 6, 1, th("textures/misc/wisp.png"),
            List.of(Flag.CONCEALED), List.of("planarRift"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("wispSpawn1"));
    }

    private static void registerCrystalWand() {
        register("crystalWand", tags("praecantatio", 4, "vitreus", 4, "potentia", 4),
            15, 10, 2, th("textures/item/crystalwand.png"),
            List.of(Flag.CONCEALED), List.of("planarRift"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("crystalWand1"),
            PageSpec.recipes(PageKind.CRAFTING, "crystalWand"));
    }

    private static void registerVoidPutty() {
        register("voidPutty", tags("permutatio", 3, "fabrico", 3, "metallum", 3),
            16, 9, 2, th("textures/item/voidputty.png"),
            List.of(Flag.CONCEALED), List.of("planarRift"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("voidPutty1"));
    }

    private static void registerVoidGolem() {
        register("voidGolem", tags("vacuos", 2, "motus", 2, "spiritus", 2, "humanus", 2),
            16, 7, 1, th("textures/misc/voidgolem.png"),
            List.of(Flag.CONCEALED), List.of("wispSpawn"), List.of("golemPowder"), List.of(),
            List.of(), 1,
            PageSpec.text("voidGolem1"));
    }

    private static void registerPocketPlane() {
        register("pocketPlane", tags("iter", 24, "vacuos", 21, "fabrico", 18, "auram", 15,
            "machina", 21, "alienis", 6, "praecantatio", 6, "cognitio", 3),
            18, 8, 4, th("textures/misc/pocketplane.png"),
            List.of(Flag.CONCEALED), List.of("planarRift", "PRIMPEARL"), List.of(), List.of(),
            List.of(), 4,
            PageSpec.text("pocketPlane1"),
            PageSpec.text("pocketPlane2"),
            PageSpec.image(th("textures/misc/planar_gateway_page.png"),
                "thaumichorizons.research.pocketPlane.gateway.caption"),
            PageSpec.recipes(PageKind.INFUSION, "pocketPlane"),
            PageSpec.text("pocketPlane3"));
    }

    private static void registerPlanarClouds() {
        register("planarClouds", new CompatAspectList(),
            20, 6, 1, th("textures/block/cloud.png"),
            List.of(Flag.LOST, Flag.ROUND), List.of("pocketPlane"), List.of(), List.of(),
            List.of("thaumichorizons:cloudth", "thaumichorizons:cloudglowingth"), 0,
            PageSpec.text("planarClouds1"),
            PageSpec.text("planarClouds2"),
            PageSpec.text("planarClouds3"));
    }

    private static void registerLeviathan() {
        register("leviathan", new CompatAspectList(),
            20, 10, 1, th("textures/block/leviathan.png"),
            List.of(Flag.LOST, Flag.ROUND), List.of("pocketPlane"), List.of(), List.of(),
            List.of("thaumichorizons:leviathanth"), 0,
            PageSpec.text("leviathan1"),
            PageSpec.text("leviathan2"));
    }

    private static void registerPlanarKeystone() {
        register("planarKeystone", tags("iter", 15, "vacuos", 12, "auram", 9, "alienis", 6, "praecantatio", 3),
            20, 8, 4, th("textures/item/keystone.png"),
            List.of(Flag.CONCEALED), List.of("pocketPlane"), List.of(), List.of(), List.of(), 5,
            PageSpec.text("planarKeystone1"),
            PageSpec.recipes(PageKind.INFUSION, "planarKeystone"),
            PageSpec.text("planarKeystone2"),
            PageSpec.recipes(PageKind.INFUSION, "planarKeystoneSlot"));
    }

    private static void registerGeneralInfusion() {
        register("generalInfusion", tags("bestia", 1),
            12, -3, 1, th("textures/misc/vat.png"),
            List.of(Flag.ROUND, Flag.CONCEALED), List.of("infusionVat"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("generalInfusion1"),
            PageSpec.text("generalInfusion2"),
            PageSpec.text("generalInfusion3"),
            PageSpec.text("generalInfusion4"),
            PageSpec.text("generalInfusion5"),
            PageSpec.text("generalInfusion6"));
    }

    private static void registerInstilledLoyalty() {
        register("instilledLoyalty", tags("cognitio", 4, "bestia", 3, "permutatio", 3),
            15, -1, 2, th("textures/item/instilledloyalty.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("generalInfusion7"),
            PageSpec.text("generalInfusion7b"));
    }

    private static void registerRunicCreature() {
        register("runicCreature", tags("potentia", 4, "tutamen", 4, "praecantatio", 4),
            15, -3, 2, tc("textures/misc/r_runicupg.png"),
            List.of(Flag.SECONDARY, Flag.CONCEALED), List.of("generalInfusion", "RUNICAUGMENTATION"),
            List.of(), List.of(), List.of(), 0,
            PageSpec.text("generalInfusion8"),
            PageSpec.image(th("textures/misc/creature_page/runiccreature/0.png"), null));
    }

    private static void registerEldritchFangs() {
        register("eldritchFangs", tags("alienis", 4, "telum", 4, "venenum", 4),
            15, -2, 2, th("textures/item/eldritchfangs.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion", "VOIDMETAL"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("generalInfusion9"),
            PageSpec.image(th("textures/misc/creature_page/eldritchfangs/0.png"), null));
    }

    private static void registerPortability() {
        register("portability", tags("vacuos", 4, "vinculum", 4),
            15, -4, 2, th("textures/item/portability.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("portability1"));
    }

    private static void registerSheepInfusion() {
        register("sheepInfusion", tags("bestia", 3, "pannus", 3, "sensus", 3),
            10, -5, 1, th("textures/item/sheep.png"),
            List.of(Flag.SECONDARY, Flag.CONCEALED), List.of("generalInfusion"), List.of(),
            List.of(), List.of(), 0,
            PageSpec.text("sheepInfusion1"),
            PageSpec.image(th("textures/misc/creature_page/sheepinfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/sheepinfusion/1.png"), null));
    }

    private static void registerCatInfusion() {
        register("catInfusion", tags("bestia", 5, "praecantatio", 3, "exanimis", 3),
            13, -5, 1, th("textures/item/cat.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("catInfusion1"),
            PageSpec.text("catInfusion2"),
            PageSpec.text("catInfusion3"),
            PageSpec.image(th("textures/misc/creature_page/catinfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/catinfusion/1.png"), null),
            PageSpec.image(th("textures/misc/creature_page/catinfusion/2.png"), null));
    }

    private static void registerCowInfusion() {
        register("cowInfusion", tags("bestia", 5, "limus", 5, "permutatio", 5),
            9, -5, 1, th("textures/item/cow.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("cowInfusion1"),
            PageSpec.image(th("textures/misc/creature_page/cowinfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/cowinfusion/1.png"), null),
            PageSpec.recipes(PageKind.CRAFTING, "cowInfusion"),
            PageSpec.recipes(PageKind.ARCANE, "cowInfusionIceCream"),
            PageSpec.text("cowInfusion2"));
    }

    private static void registerChickenInfusion() {
        register("chickenInfusion", tags("bestia", 5, "limus", 5, "lucrum", 3, "tenebrae", 3),
            11, -5, 1, th("textures/item/chicken.png"),
            List.of(Flag.SECONDARY, Flag.CONCEALED), List.of("generalInfusion"), List.of(),
            List.of(), List.of(), 0,
            PageSpec.text("chickenInfusion1"),
            PageSpec.image(th("textures/misc/creature_page/chickeninfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/chickeninfusion/1.png"), null),
            PageSpec.text("chickenInfusion2"));
    }

    private static void registerPigInfusion() {
        register("pigInfusion", tags("bestia", 5, "fames", 5, "metallum", 3, "vitium", 3, "sano", 3),
            12, -5, 1, th("textures/item/pig.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("pigInfusion1"),
            PageSpec.image(th("textures/misc/creature_page/piginfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/piginfusion/1.png"), null),
            PageSpec.text("pigInfusion2"));
    }

    private static void registerDogInfusion() {
        register("dogInfusion", tags("bestia", 5, "lux", 3, "ignis", 3, "aqua", 3, "praecantatio", 3),
            14, -5, 1, th("textures/item/wolf.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("dogInfusion1"),
            PageSpec.text("dogInfusion2"),
            PageSpec.text("dogInfusion3"),
            PageSpec.image(th("textures/misc/creature_page/doginfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/doginfusion/1.png"), null),
            PageSpec.image(th("textures/misc/creature_page/doginfusion/2.png"), null));
    }

    private static void registerHorseInfusion() {
        register("horseInfusion", tags("bestia", 4, "iter", 5, "alienis", 4),
            15, -5, 2, th("textures/item/horse.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("horseInfusion2"),
            PageSpec.text("horseInfusion3"),
            PageSpec.image(th("textures/misc/creature_page/horseinfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/horseinfusion/1.png"), null),
            PageSpec.image(th("textures/misc/creature_page/horseinfusion/2.png"), null));
    }

    private static void registerNightmareInfusion() {
        register("nightmareInfusion", tags("ignis", 4, "vacuos", 3, "iter", 3),
            17, -7, 2, th("textures/item/horse.png"),
            List.of(Flag.CONCEALED), List.of("horseInfusion", "LEVITATOR"), List.of(), List.of(),
            List.of(), 0,
            PageSpec.text("nightmareInfusion1"),
            PageSpec.text("nightmareInfusion2"),
            PageSpec.image(th("textures/misc/creature_page/nightmareinfusion/0.png"), null));
    }

    private static void registerSheederInfusion() {
        register("sheederInfusion", tags("pannus", 4, "bestia", 3, "permutatio", 3),
            10, -7, 2, th("textures/item/sheep.png"),
            List.of(Flag.CONCEALED), List.of("sheepInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("sheederInfusion1"),
            PageSpec.image(th("textures/misc/creature_page/sheederinfusion/0.png"), null),
            PageSpec.image(th("textures/misc/creature_page/sheederinfusion/1.png"), null));
    }

    private static void registerSelfInfusion() {
        register("selfInfusion", tags("humanus", 6, "praecantatio", 6, "fabrico", 6),
            14, 1, 3, th("textures/item/humaninfusion.png"),
            List.of(Flag.CONCEALED), List.of("generalInfusion", "cloneSelf"), List.of(),
            List.of("humanInfusion"), List.of(), 0,
            PageSpec.text("selfInfusion1"),
            PageSpec.text("selfInfusion2"),
            PageSpec.text("selfInfusion3"),
            PageSpec.image(th("textures/misc/self_page/selfinfusion/0.png"), null),
            PageSpec.image(th("textures/misc/self_page/selfinfusion/1.png"), null),
            PageSpec.image(th("textures/misc/self_page/selfinfusion/2.png"), null));
    }

    private static void registerHumanInfusion() {
        register("humanInfusion", tags("humanus", 1),
            16, 1, 1, th("textures/item/quicksilverlimbs.png"),
            List.of(Flag.CONCEALED), List.of("selfInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("humanInfusion1"),
            PageSpec.text("humanInfusion3"),
            PageSpec.text("humanInfusion4"));
    }

    private static void registerMorphicFingers() {
        register("morphicFingers", tags("humanus", 3, "fabrico", 3, "instrumentum", 3, "permutatio", 2),
            19, 2, 2, th("textures/item/morphicfingers.png"),
            List.of(Flag.CONCEALED), List.of("humanInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("morphicFingers1"),
            PageSpec.image(th("textures/misc/self_page/morphicfingers/0.png"), null));
    }

    private static void registerSilverHeart() {
        register("silverHeart", tags("humanus", 3, "ordo", 3, "sano", 3),
            19, -1, 2, th("textures/item/silverwoodheart.png"),
            List.of(Flag.CONCEALED), List.of("humanInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("silverHeart1"),
            PageSpec.text("silverHeart2"),
            PageSpec.image(th("textures/misc/self_page/silverheart/0.png"), null));
    }

    private static void registerSynthSkin() {
        register("synthSkin", tags("humanus", 3, "herba", 3, "fames", 3),
            19, 0, 2, th("textures/item/synthskin.png"),
            List.of(Flag.CONCEALED), List.of("humanInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("synthSkin1"),
            PageSpec.image(th("textures/misc/self_page/synthskin/0.png"), null));
    }

    private static void registerAmphibious() {
        register("amphibious", tags("humanus", 3, "aer", 3, "aqua", 3),
            19, 1, 2, mc("textures/item/cod.png"),
            List.of(Flag.CONCEALED), List.of("humanInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("amphibious1"),
            PageSpec.image(th("textures/misc/self_page/amphibious/0.png"), null));
    }

    private static void registerWarpedTumor() {
        register("warpedTumor", tags("humanus", 15, "vitium", 12, "alienis", 9, "corpus", 6, "permutatio", 3),
            19, 4, 3, th("textures/item/warpedtumor.png"),
            List.of(Flag.CONCEALED), List.of("humanInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("warpedTumor1"),
            PageSpec.image(th("textures/misc/self_page/warpedtumor/0.png"), null));
    }

    private static void registerSpiderClimb() {
        register("spiderClimb", tags("humanus", 3, "bestia", 3, "motus", 3, "limus", 3),
            19, 3, 2, th("textures/item/spiderclimb.png"),
            List.of(Flag.CONCEALED), List.of("humanInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("spiderClimb1"),
            PageSpec.image(th("textures/misc/self_page/spiderclimb/0.png"), null));
    }

    private static void registerChameleonSkin() {
        register("chameleonSkin", tags("sensus", 4, "permutatio", 4, "vacuos", 4),
            19, -2, 2, th("textures/item/chameleonskin.png"),
            List.of(Flag.CONCEALED), List.of("humanInfusion"), List.of(), List.of(), List.of(), 0,
            PageSpec.text("chameleonSkin1"),
            PageSpec.image(th("textures/misc/self_page/chameleonskin/0.png"), null));
    }
}
