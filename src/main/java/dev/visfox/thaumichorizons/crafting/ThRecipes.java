package dev.visfox.thaumichorizons.crafting;

import dev.visfox.thaumichorizons.item.SyringeKeys;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.crafting.CraftingRecipeRef;
import thaumcraft.crafting.CrucibleRecipe;
import thaumcraft.crafting.InfusionRecipe;
import thaumcraft.crafting.ShapedArcaneRecipe;
import thaumcraft.crafting.ShapelessArcaneRecipe;
import thaumcraft.crafting.TcIngredient;
import thaumcraft.crafting.TcStack;

public final class ThRecipes {
    private ThRecipes() {
    }

    public static void init() {
        registerPhialData();
        normal0();
        compound0();
        crucible0();
        crucible1();
        crucible2();
        crucible3();
        crucible4();
        crucible5();
        arcane0();
        arcane1();
        arcaneBoats();
        infusion0();
        infusion1();
        infusion2();
        infusion3();
        infusion4();
        infusionPocketPlane();
        infusionRecombinator();
        mirrorAmulet();
        advancedGolem();
    }

    private static void registerPhialData() {
        TcStack.registerDataHandler("phial", (stack, value) ->
            SyringeKeys.BLOOD.set(stack, new SyringeKeys.BloodData(Boolean.parseBoolean(value))));
        TcStack.registerDataMatcher("phial", (stack, value) -> {
            SyringeKeys.BloodData data = SyringeKeys.BLOOD.get(stack);
            return data != null && data.phial() == Boolean.parseBoolean(value);
        });
    }

    private static void normal0() {
        TcReforgedPort.registerRecipeUnique(new CraftingRecipeRef("cowInfusion", "cowInfusion", ResourceLocation.parse("thaumichorizons:barchocolate"), TcStack.of("thaumichorizons:barchocolate")));
    }

    private static void compound0() {
        String planks = "thaumcraft:greatwood_planks";
        String construct = "thaumcraft:alchemical_construct";
        String glass = "minecraft:glass";
        String water = "minecraft:water_bucket";
        TcReforgedPort.registerCompoundRecipe("CurativeVat", "healingVat",
            new AspectList().add(Aspect.WATER, 50).add(Aspect.EARTH, 50).add(Aspect.ORDER, 50), 3, 4, 3, List.of(
                planks, planks, planks, planks, construct, planks, planks, planks, planks,
                glass, glass, glass, glass, water, glass, glass, glass, glass,
                glass, glass, glass, glass, water, glass, glass, glass, glass,
                planks, planks, planks, planks, construct, planks, planks, planks, planks));
    }

    private static void crucible0() {
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("alcheponics", "alcheponics", TcStack.of("minecraft:wheat", 2), TcIngredient.item("minecraft:wheat_seeds"),
            new AspectList().add(Aspect.LIGHT, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("alcheponics1", "alcheponics", TcStack.of("minecraft:carrot", 2), TcIngredient.item("minecraft:carrot"),
            new AspectList().add(Aspect.LIGHT, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("alcheponics2", "alcheponics", TcStack.of("minecraft:potato", 2), TcIngredient.item("minecraft:potato"),
            new AspectList().add(Aspect.LIGHT, 2).add(Aspect.EARTH, 2).add(Aspect.WATER, 2)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("alcheponics3", "alcheponics", TcStack.of("minecraft:melon"), TcIngredient.item("minecraft:melon_seeds"),
            new AspectList().add(Aspect.LIGHT, 3).add(Aspect.EARTH, 3).add(Aspect.WATER, 3)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("alcheponics4", "alcheponics", TcStack.of("minecraft:pumpkin"), TcIngredient.item("minecraft:pumpkin_seeds"),
            new AspectList().add(Aspect.LIGHT, 3).add(Aspect.EARTH, 3).add(Aspect.WATER, 3)));
    }

    private static void crucible1() {
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("meatGrowth", "meatGrowth", TcStack.of("minecraft:porkchop", 2), TcIngredient.item("minecraft:porkchop"),
            new AspectList().add(Aspect.LIFE, 3)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("meatGrowth1", "meatGrowth", TcStack.of("minecraft:beef", 2), TcIngredient.item("minecraft:beef"),
            new AspectList().add(Aspect.LIFE, 3)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("meatGrowth2", "meatGrowth", TcStack.of("minecraft:chicken", 2), TcIngredient.item("minecraft:chicken"),
            new AspectList().add(Aspect.LIFE, 3)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("fleshLeather", "fleshLeather", TcStack.of("minecraft:leather"), TcIngredient.item("minecraft:beef"),
            new AspectList().add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 1)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("fleshLeather1", "fleshLeather", TcStack.of("minecraft:leather"), TcIngredient.item("minecraft:porkchop"),
            new AspectList().add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 1)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("fleshLeather2", "fleshLeather", TcStack.of("minecraft:leather"), TcIngredient.item("minecraft:rotten_flesh"),
            new AspectList().add(Aspect.CRAFT, 1).add(Aspect.CLOTH, 1).add(Aspect.ORDER, 1)));
    }

    private static void crucible2() {
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("shardTrans", "shardTrans", TcStack.of("thaumcraft:balanced_shard"), TcIngredient.item("thaumcraft:fire_shard"),
            new AspectList().add(Aspect.FIRE, 2).add(Aspect.EXCHANGE, 1)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("shardTrans1", "shardTrans", TcStack.of("thaumcraft:balanced_shard"), TcIngredient.item("thaumcraft:water_shard"),
            new AspectList().add(Aspect.WATER, 2).add(Aspect.EXCHANGE, 1)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("shardTrans2", "shardTrans", TcStack.of("thaumcraft:balanced_shard"), TcIngredient.item("thaumcraft:air_shard"),
            new AspectList().add(Aspect.AIR, 2).add(Aspect.EXCHANGE, 1)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("shardTrans3", "shardTrans", TcStack.of("thaumcraft:balanced_shard"), TcIngredient.item("thaumcraft:earth_shard"),
            new AspectList().add(Aspect.EARTH, 2).add(Aspect.EXCHANGE, 1)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("shardTrans4", "shardTrans", TcStack.of("thaumcraft:balanced_shard"), TcIngredient.item("thaumcraft:order_shard"),
            new AspectList().add(Aspect.ORDER, 2).add(Aspect.EXCHANGE, 1)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("shardTrans5", "shardTrans", TcStack.of("thaumcraft:balanced_shard"), TcIngredient.item("thaumcraft:entropy_shard"),
            new AspectList().add(Aspect.ENTROPY, 2).add(Aspect.EXCHANGE, 1)));
    }

    private static void crucible3() {
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("alchemite", "alchemite", TcStack.of("thaumichorizons:alchemite"), TcIngredient.item("minecraft:tnt"),
            new AspectList().add(Aspect.ENERGY, 4).add(Aspect.CRYSTAL, 4).add(Aspect.EXCHANGE, 4)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("golemPowder", "golemPowder", TcStack.of("thaumichorizons:golempowder"), TcIngredient.item("thaumcraft:salis_mundus"),
            new AspectList().add(Aspect.MAN, 4).add(Aspect.SOUL, 4).add(Aspect.MOTION, 4)));
    }

    private static void arcane0() {
        TcReforgedPort.registerRecipeUnique(new ShapelessArcaneRecipe("cowInfusionIceCream", "cowInfusion", TcStack.of("thaumichorizons:icecream"),
            new AspectList().add(Aspect.WATER, 1), List.of(
                TcIngredient.item("thaumichorizons:bucketchocolate"), TcIngredient.item("minecraft:wheat"))));
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("GOLEMBELL", "GOLEMBELL", TcStack.of("thaumichorizons:golembellth"),
            new AspectList().add(Aspect.ORDER, 5), 3, 3, List.of(
                TcIngredient.item("minecraft:quartz"), TcIngredient.item("minecraft:quartz"), TcIngredient.EMPTY,
                TcIngredient.item("minecraft:quartz"), TcIngredient.item("minecraft:quartz"), TcIngredient.EMPTY,
                TcIngredient.EMPTY, TcIngredient.EMPTY, TcIngredient.item("minecraft:stick"))));
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("essentiaInjectionSyringe", "essentiaInjection", TcStack.of("thaumichorizons:syringeinjection"),
            new AspectList().add(Aspect.WATER, 1).add(Aspect.ORDER, 1), 1, 2, List.of(
                TcIngredient.item("thaumcraft:glass_phial"), TcIngredient.item("minecraft:iron_nugget"))));
        TcReforgedPort.registerRecipeUnique(new ShapelessArcaneRecipe("blastPhial", "blastPhial", TcStack.withData("thaumichorizons:syringeblood", 4, Map.of("phial", "true")),
            new AspectList().add(Aspect.FIRE, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1), List.of(
                TcIngredient.item("thaumcraft:glass_phial"), TcIngredient.item("thaumcraft:glass_phial"),
                TcIngredient.item("minecraft:gunpowder"), TcIngredient.item("thaumcraft:glass_phial"),
                TcIngredient.item("thaumcraft:glass_phial"), TcIngredient.item("thaumichorizons:syringeblood"))));
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("lensCase", "lensCase", TcStack.of("thaumichorizons:lenscase"),
            new AspectList().add(Aspect.EARTH, 10).add(Aspect.ORDER, 10).add(Aspect.ENTROPY, 10), 3, 3, List.of(
                TcIngredient.item("minecraft:leather"), TcIngredient.item("minecraft:iron_ingot"), TcIngredient.item("minecraft:leather"),
                TcIngredient.item("minecraft:leather"), TcIngredient.item("thaumcraft:mundane_belt"), TcIngredient.item("minecraft:leather"),
                TcIngredient.item("minecraft:leather"), TcIngredient.item("minecraft:iron_ingot"), TcIngredient.item("minecraft:leather"))));
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("crystalWand", "crystalWand", TcStack.of("thaumichorizons:crystalwand"),
            new AspectList().add(Aspect.AIR, 25).add(Aspect.EARTH, 25).add(Aspect.FIRE, 25).add(Aspect.WATER, 25).add(Aspect.ORDER, 25).add(Aspect.ENTROPY, 25), 3, 3, List.of(
                TcIngredient.EMPTY, TcIngredient.EMPTY, TcIngredient.item("thaumcraft:balanced_shard"),
                TcIngredient.EMPTY, TcIngredient.item("thaumcraft:mixed_crystal_cluster"), TcIngredient.EMPTY,
                TcIngredient.item("thaumcraft:balanced_shard"), TcIngredient.EMPTY, TcIngredient.EMPTY)));
    }

    private static void infusion0() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("etherealShard", "etherealShard", TcStack.of("thaumichorizons:synthnode"), 7,
            new AspectList().add(Aspect.AURA, 8).add(Aspect.ENERGY, 32).add(Aspect.VOID, 32),
            TcIngredient.item("thaumcraft:balanced_shard"), List.of(
                TcIngredient.item("thaumcraft:thaumium_ingot"), TcIngredient.item("thaumcraft:thaumium_ingot"),
                TcIngredient.item("thaumcraft:thaumium_ingot"), TcIngredient.item("thaumcraft:thaumium_ingot"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("visDynamo", "visDynamo", TcStack.of("thaumichorizons:visdynamo"), 5,
            new AspectList().add(Aspect.AURA, 4).add(Aspect.ENERGY, 16).add(Aspect.EXCHANGE, 16).add(Aspect.MECHANISM, 16),
            TcIngredient.item("minecraft:piston"), List.of(
                TcIngredient.item("thaumcraft:primal_charm"), TcIngredient.item("minecraft:gold_ingot"),
                TcIngredient.item("thaumcraft:greatwood_planks"), TcIngredient.item("minecraft:gold_ingot"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("essentiaDynamo", "essentiaDynamo", TcStack.of("thaumichorizons:essentiadynamo"), 5,
            new AspectList().add(Aspect.AURA, 4).add(Aspect.ENERGY, 16).add(Aspect.EXCHANGE, 16).add(Aspect.MECHANISM, 16).add(Aspect.WATER, 32),
            TcIngredient.item("minecraft:piston"), List.of(
                TcIngredient.item("thaumcraft:thaumium_ingot"), TcIngredient.item("thaumcraft:alchemical_construct"),
                TcIngredient.item("thaumcraft:greatwood_planks"), TcIngredient.item("thaumcraft:thaumium_ingot"),
                TcIngredient.item("thaumcraft:alchemical_centrifuge"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("nodeMonitor", "nodeMonitor", TcStack.of("thaumichorizons:nodemonitor"), 2,
            new AspectList().add(Aspect.AURA, 2).add(Aspect.SENSES, 8).add(Aspect.MECHANISM, 8),
            TcIngredient.item("thaumcraft:thaumometer"), List.of(
                TcIngredient.item("thaumcraft:zombie_brain"), TcIngredient.item("minecraft:comparator"),
                TcIngredient.item("thaumcraft:air_shard"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("soulExtractor", "soulExtractor", TcStack.of("thaumichorizons:soulsieve"), 4,
            new AspectList().add(Aspect.SOUL, 16).add(Aspect.MECHANISM, 8).add(Aspect.EXCHANGE, 8),
            TcIngredient.item("minecraft:hopper"), List.of(
                TcIngredient.item("thaumcraft:amber"), TcIngredient.item("thaumcraft:enchanted_fabric"),
                TcIngredient.item("minecraft:piston"), TcIngredient.item("thaumcraft:amber"),
                TcIngredient.item("thaumcraft:enchanted_fabric"), TcIngredient.item("minecraft:piston"))));
    }

    private static void infusion1() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("soulKnowledge", "soulKnowledge", TcStack.of("thaumichorizons:inspiratron"), 5,
            new AspectList().add(Aspect.HEAL, 8).add(Aspect.MIND, 16).add(Aspect.ORDER, 8).add(Aspect.SOUL, 8),
            TcIngredient.item("thaumcraft:brain_jar"), List.of(
                TcIngredient.item("thaumcraft:scribing_tools"), TcIngredient.item("thaumcraft:thaumium_ingot"),
                TcIngredient.item("minecraft:golden_apple"), TcIngredient.item("thaumcraft:thaumium_ingot"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("soulAssembler", "soulAssembler", TcStack.of("thaumichorizons:soulforge"), 6,
            new AspectList().add(Aspect.SOUL, 16).add(Aspect.CRAFT, 16).add(Aspect.EXCHANGE, 16).add(Aspect.MIND, 32).add(Aspect.HEAL, 16),
            TcIngredient.item("thaumcraft:warded_jar"), List.of(
                TcIngredient.item("thaumcraft:brain_jar"), TcIngredient.item("minecraft:golden_apple"),
                TcIngredient.item("thaumcraft:vis_filter"), TcIngredient.item("thaumcraft:brain_jar"),
                TcIngredient.item("minecraft:golden_apple"), TcIngredient.item("thaumcraft:vis_filter"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("focusContainment", "focusContainment", TcStack.of("thaumichorizons:focuscontainment"), 6,
            new AspectList().add(Aspect.VOID, 32).add(Aspect.TRAP, 32).add(Aspect.TRAVEL, 8),
            TcIngredient.item("thaumcraft:focus_portable_hole"), List.of(
                TcIngredient.item("thaumcraft:amber"), TcIngredient.item("minecraft:cobweb"),
                TcIngredient.item("thaumcraft:amber"), TcIngredient.item("minecraft:iron_bars"),
                TcIngredient.item("thaumcraft:amber"), TcIngredient.item("minecraft:cobweb"),
                TcIngredient.item("thaumcraft:amber"), TcIngredient.item("minecraft:iron_bars"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("essentiaInjection", "essentiaInjection", TcStack.of("thaumichorizons:bloodinfuser"), 4,
            new AspectList().add(Aspect.MAN, 8).add(Aspect.EXCHANGE, 8).add(Aspect.LIFE, 16).add(Aspect.MAGIC, 16),
            TcIngredient.item("thaumcraft:crucible"), List.of(
                TcIngredient.item("minecraft:comparator"), TcIngredient.item("thaumcraft:alchemical_construct"),
                TcIngredient.item("thaumcraft:essentia_buffer"), TcIngredient.item("thaumcraft:alchemical_construct"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("injector", "injector", TcStack.of("thaumichorizons:injector"), 2,
            new AspectList().add(Aspect.WEAPON, 8).add(Aspect.MECHANISM, 8).add(Aspect.FLIGHT, 8),
            TcIngredient.item("thaumcraft:bone_bow"), List.of(
                TcIngredient.item("thaumcraft:greatwood_planks"), TcIngredient.item("minecraft:feather"),
                TcIngredient.item("thaumcraft:thaumium_ingot"), TcIngredient.item("thaumcraft:air_shard"))));
    }

    private static void infusion2() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("focusLiquefaction", "focusLiquefaction", TcStack.of("thaumichorizons:focusliquefaction"), 3,
            new AspectList().add(Aspect.FIRE, 12).add(Aspect.METAL, 8).add(Aspect.EXCHANGE, 4),
            TcIngredient.item("minecraft:diamond"), List.of(
                TcIngredient.item("thaumcraft:focus_fire"),
                TcIngredient.item("thaumcraft:salis_mundus"),
                TcIngredient.item("thaumcraft:focus_excavation"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("focusDisintegration", "focusDisintegration", TcStack.of("thaumichorizons:focusdisintegration"), 7,
            new AspectList().add(Aspect.ENTROPY, 32).add(Aspect.MAGIC, 24).add(Aspect.CRYSTAL, 32).add(Aspect.EXCHANGE, 16),
            TcIngredient.item("thaumcraft:focus_excavation"), List.of(
                TcIngredient.item("thaumcraft:liquid_death_bucket"),
                TcIngredient.item("thaumcraft:primal_charm"),
                TcIngredient.item("thaumcraft:liquid_death_bucket"),
                TcIngredient.item("thaumcraft:alumentum"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("focusAnimation", "focusAnimation", TcStack.of("thaumichorizons:focusanimation"), 10,
            new AspectList().add(Aspect.MAN, 64).add(Aspect.MOTION, 64).add(Aspect.SOUL, 64).add(Aspect.CRAFT, 64),
            TcIngredient.item("thaumcraft:primordial_pearl"), List.of(
                TcIngredient.item("thaumcraft:golem_core_blank"),
                TcIngredient.item("thaumichorizons:syringeblood"),
                TcIngredient.item("thaumcraft:golem_upgrade_air"),
                TcIngredient.item("thaumcraft:golem_upgrade_fire"),
                TcIngredient.item("thaumcraft:golem_upgrade_order"),
                TcIngredient.item("thaumcraft:golem_core_blank"),
                TcIngredient.item("thaumichorizons:syringeblood"),
                TcIngredient.item("thaumcraft:golem_upgrade_earth"),
                TcIngredient.item("thaumcraft:golem_upgrade_water"),
                TcIngredient.item("thaumcraft:golem_upgrade_entropy"))));
    }

    private static void arcane1() {
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("planarTheory", "planarTheory", TcStack.of("thaumichorizons:planarconduit"),
            new AspectList().add(Aspect.FIRE, 50).add(Aspect.WATER, 50).add(Aspect.ORDER, 50)
                .add(Aspect.ENTROPY, 50).add(Aspect.EARTH, 50).add(Aspect.AIR, 50), 3, 3, List.of(
                TcIngredient.item("thaumcraft:void_metal_ingot"), TcIngredient.item("thaumcraft:wisp_essence"),
                TcIngredient.item("thaumcraft:void_metal_ingot"),
                TcIngredient.item("thaumcraft:wisp_essence"), TcIngredient.item("minecraft:ender_pearl"),
                TcIngredient.item("thaumcraft:wisp_essence"),
                TcIngredient.item("thaumcraft:void_metal_ingot"), TcIngredient.item("thaumcraft:wisp_essence"),
                TcIngredient.item("thaumcraft:void_metal_ingot"))));
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("transductionAmplifier", "transductionAmplifier",
            TcStack.of("thaumichorizons:transductionamplifier"),
            new AspectList().add(Aspect.AIR, 10).add(Aspect.FIRE, 10).add(Aspect.ORDER, 10), 3, 3, List.of(
                TcIngredient.EMPTY, TcIngredient.item("thaumichorizons:planarconduit"), TcIngredient.EMPTY,
                TcIngredient.item("thaumcraft:amber"), TcIngredient.item("thaumcraft:node_transducer"),
                TcIngredient.item("thaumcraft:amber"),
                TcIngredient.item("minecraft:redstone_block"), TcIngredient.item("thaumcraft:nitor"),
                TcIngredient.item("minecraft:redstone_block"))));
    }

    private static void arcaneBoats() {
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("greatwoodBoat", "greatwoodBoat",
            TcStack.of("thaumichorizons:boatgreatwood"),
            new AspectList().add(Aspect.ORDER, 5).add(Aspect.EARTH, 5).add(Aspect.WATER, 5),
            3, 2, List.of(
                TcIngredient.item("thaumcraft:greatwood_log"), TcIngredient.EMPTY,
                TcIngredient.item("thaumcraft:greatwood_log"),
                TcIngredient.item("thaumcraft:greatwood_log"), TcIngredient.item("thaumcraft:greatwood_log"),
                TcIngredient.item("thaumcraft:greatwood_log"))));
        TcReforgedPort.registerRecipeUnique(new ShapedArcaneRecipe("thaumiumBoat", "thaumiumBoat",
            TcStack.of("thaumichorizons:boatthaumium"),
            new AspectList().add(Aspect.FIRE, 10).add(Aspect.WATER, 10)
                .add(Aspect.ORDER, 10).add(Aspect.EARTH, 10),
            3, 3, List.of(
                TcIngredient.item("thaumcraft:water_shard"), TcIngredient.item("thaumcraft:thaumium_ingot"),
                TcIngredient.item("thaumcraft:water_shard"),
                TcIngredient.item("thaumcraft:thaumium_ingot"), TcIngredient.item("thaumichorizons:boatgreatwood"),
                TcIngredient.item("thaumcraft:thaumium_ingot"),
                TcIngredient.item("thaumcraft:water_shard"), TcIngredient.item("thaumcraft:thaumium_ingot"),
                TcIngredient.item("thaumcraft:water_shard"))));
    }

    private static void infusionPocketPlane() {
        // Blank keystone: imprint it by right-clicking a generated Pocket Plane vortex.
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("pocketPlane", "pocketPlane",
            TcStack.of("thaumichorizons:keystoneth"), 6,
            new AspectList().add(Aspect.TRAVEL, 32).add(Aspect.VOID, 32).add(Aspect.EXCHANGE, 16),
            TcIngredient.item("thaumichorizons:planarconduit"), List.of(
                TcIngredient.item("minecraft:quartz"), TcIngredient.item("thaumcraft:balanced_shard"),
                TcIngredient.item("minecraft:quartz"), TcIngredient.item("thaumcraft:balanced_shard"),
                TcIngredient.item("minecraft:quartz"), TcIngredient.item("thaumcraft:balanced_shard"),
                TcIngredient.item("minecraft:quartz"), TcIngredient.item("thaumcraft:balanced_shard"))));

        // Secondary Keystone recipe unlocked by Planar Gateways research.
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("planarKeystone", "planarKeystone",
            TcStack.of("thaumichorizons:keystoneth"), 6,
            new AspectList().add(Aspect.TRAVEL, 24).add(Aspect.VOID, 24).add(Aspect.ELDRITCH, 12),
            TcIngredient.item("minecraft:diamond"), List.of(
                TcIngredient.item("thaumcraft:quicksilver"), TcIngredient.item("thaumcraft:balanced_shard"),
                TcIngredient.item("thaumcraft:quicksilver"), TcIngredient.item("thaumcraft:balanced_shard"))));

        // The receptacle that anchors a gateway frame around the inserted Keystone.
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("planarKeystoneSlot", "planarKeystone",
            TcStack.of("thaumichorizons:slotth"), 8,
            new AspectList().add(Aspect.VOID, 32).add(Aspect.TRAVEL, 24)
                .add(Aspect.MECHANISM, 24).add(Aspect.ELDRITCH, 24),
            TcIngredient.item("thaumichorizons:planarconduit"), List.of(
                TcIngredient.item("minecraft:gold_ingot"), TcIngredient.item("thaumcraft:void_metal_ingot"),
                TcIngredient.item("minecraft:gold_ingot"), TcIngredient.item("thaumcraft:void_metal_ingot"),
                TcIngredient.item("minecraft:gold_ingot"), TcIngredient.item("thaumcraft:void_metal_ingot"),
                TcIngredient.item("minecraft:gold_ingot"), TcIngredient.item("thaumcraft:void_metal_ingot"))));
    }

    private static void infusion4() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("vortexStabilizer", "vortexStabilizer",
            TcStack.of("thaumichorizons:vortexstabilizer"), 6,
            new AspectList().add(Aspect.AURA, 8).add(Aspect.TRAP, 32).add(Aspect.HUNGER, 32).add(Aspect.ENERGY, 16),
            TcIngredient.item("thaumcraft:node_stabilizer"), List.of(
                TcIngredient.item("thaumichorizons:planarconduit"),
                TcIngredient.item("thaumcraft:thaumium_ingot"),
                TcIngredient.item("thaumcraft:amber"),
                TcIngredient.item("thaumcraft:thaumium_ingot"))));
    }

    private static void infusionRecombinator() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("recombinator", "recombinator",
            TcStack.of("thaumichorizons:recombinator"), 9,
            new AspectList().add(Aspect.EXCHANGE, 32).add(Aspect.AURA, 20).add(Aspect.CRAFT, 32)
                .add(Aspect.ENERGY, 32),
            TcIngredient.item("thaumcraft:primordial_pearl"), List.of(
                TcIngredient.item("thaumcraft:vis_filter"),
                TcIngredient.item("thaumichorizons:planarconduit"),
                TcIngredient.item("thaumcraft:vis_filter"),
                TcIngredient.item("thaumcraft:node_transducer"),
                TcIngredient.item("thaumcraft:vis_filter"),
                TcIngredient.item("thaumichorizons:planarconduit"),
                TcIngredient.item("thaumcraft:vis_filter"),
                TcIngredient.item("thaumcraft:node_stabilizer"))));
    }

    private static void crucible5() {
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("alchIncubation", "alchIncubation", TcStack.of("thaumichorizons:eggincubated"),
            TcIngredient.item("minecraft:egg"),
            new AspectList().add(Aspect.FIRE, 2).add(Aspect.LIFE, 2)));
    }

    private static void crucible4() {
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("focusIllumination", "focusIllumination", TcStack.of("thaumichorizons:focusillumination"),
            TcIngredient.item("minecraft:glowstone"),
            new AspectList().add(Aspect.LIGHT, 12).add(Aspect.ENERGY, 8).add(Aspect.FIRE, 8)));
        TcReforgedPort.registerRecipeUnique(new CrucibleRecipe("incarnationVat", "incarnationVat", TcStack.of("thaumichorizons:nutrients"),
            TcIngredient.item("minecraft:sugar"),
            new AspectList().add(Aspect.HUNGER, 2)));
    }

    private static void infusion3() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("infusionVat", "infusionVat", TcStack.of("thaumichorizons:modmatrix"), 10,
            new AspectList().add(Aspect.LIFE, 32).add(Aspect.CRAFT, 32).add(Aspect.MAGIC, 32).add(Aspect.BEAST, 32),
            TcIngredient.item("thaumcraft:runic_matrix"), List.of(
                TcIngredient.item("thaumcraft:salis_mundus"), TcIngredient.item("thaumichorizons:syringeblood"),
                TcIngredient.item("thaumcraft:salis_mundus"), TcIngredient.item("thaumichorizons:syringeblood"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("cloneVillager", "cloneVillager", TcStack.of("thaumichorizons:corpseeffigy"), 3,
            new AspectList().add(Aspect.CRAFT, 8).add(Aspect.MAN, 8),
            TcIngredient.item("thaumcraft:zombie_brain"), List.of(
                TcIngredient.item("minecraft:rotten_flesh"), TcIngredient.item("minecraft:bone"),
                TcIngredient.item("minecraft:rotten_flesh"), TcIngredient.item("minecraft:bone"),
                TcIngredient.item("minecraft:rotten_flesh"), TcIngredient.item("minecraft:bone"),
                TcIngredient.item("minecraft:rotten_flesh"), TcIngredient.item("minecraft:bone"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("cloneSelf", "cloneSelf", TcStack.of("thaumichorizons:soulbeacon"), 8,
            new AspectList().add(Aspect.SOUL, 16).add(Aspect.TRAP, 32).add(Aspect.TRAVEL, 32).add(Aspect.DEATH, 32),
            TcIngredient.item("thaumcraft:primordial_pearl"), List.of(
                TcIngredient.item("thaumcraft:warded_jar"), TcIngredient.item("thaumcraft:void_metal_ingot"),
                TcIngredient.item("minecraft:ender_eye"), TcIngredient.item("minecraft:beacon"),
                TcIngredient.item("minecraft:ender_eye"), TcIngredient.item("thaumcraft:void_metal_ingot"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("lensFire", "lensFire", TcStack.of("thaumichorizons:lensfire"), 1,
            new AspectList().add(Aspect.LIGHT, 20).add(Aspect.ENERGY, 12).add(Aspect.SENSES, 20),
            TcIngredient.item("minecraft:glass_pane"), List.of(
                TcIngredient.item("minecraft:golden_carrot"), TcIngredient.item("thaumcraft:fire_shard"),
                TcIngredient.item("minecraft:golden_carrot"), TcIngredient.item("thaumcraft:fire_shard"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("lensWater", "lensWater", TcStack.of("thaumichorizons:lenswater"), 1,
            new AspectList().add(Aspect.SENSES, 24).add(Aspect.EXCHANGE, 12).add(Aspect.WATER, 12),
            TcIngredient.item("minecraft:glass_pane"), List.of(
                TcIngredient.item("thaumcraft:arcane_ear"), TcIngredient.item("thaumcraft:water_shard"),
                TcIngredient.item("minecraft:note_block"), TcIngredient.item("thaumcraft:water_shard"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("lensEarth", "lensEarth", TcStack.of("thaumichorizons:lensearth"), 1,
            new AspectList().add(Aspect.SENSES, 20).add(Aspect.VOID, 12).add(Aspect.LIGHT, 12),
            TcIngredient.item("minecraft:glass_pane"), List.of(
                TcIngredient.item("minecraft:glowstone_dust"), TcIngredient.item("thaumcraft:earth_shard"),
                TcIngredient.item("minecraft:ender_eye"), TcIngredient.item("thaumcraft:earth_shard"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("lensAir", "lensAir", TcStack.of("thaumichorizons:lensair"), 1,
            new AspectList().add(Aspect.SENSES, 18).add(Aspect.AURA, 4).add(Aspect.LIFE, 12).add(Aspect.UNDEAD, 12),
            TcIngredient.item("minecraft:glass_pane"), List.of(
                TcIngredient.item("minecraft:bone"), TcIngredient.item("thaumcraft:air_shard"),
                TcIngredient.item("minecraft:egg"), TcIngredient.item("thaumcraft:air_shard"))));
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("lensOrderEntropy", "lensOrderEntropy", TcStack.of("thaumichorizons:lensorderentropy"), 1,
            new AspectList().add(Aspect.SENSES, 24).add(Aspect.MIND, 12).add(Aspect.MAGIC, 12),
            TcIngredient.item("minecraft:glass_pane"), List.of(
                TcIngredient.item("thaumcraft:scribing_tools"), TcIngredient.item("thaumcraft:entropy_shard"),
                TcIngredient.item("minecraft:book"), TcIngredient.item("thaumcraft:order_shard"))));
    }

    private static void mirrorAmulet() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("mirrorAmulet", "mirrorAmulet", TcStack.of("thaumichorizons:amuletmirror"), 6,
            new AspectList().add(Aspect.DEATH, 16).add(Aspect.ENTROPY, 32).add(Aspect.VOID, 32),
            TcIngredient.item("thaumcraft:primordial_pearl"), List.of(
                TcIngredient.item("thaumcraft:mundane_amulet"),
                TcIngredient.item("minecraft:map"),
                TcIngredient.item("thaumcraft:magic_mirror"))));
    }

    private static void advancedGolem() {
        TcReforgedPort.registerRecipeUnique(new InfusionRecipe("AdvancedGolemTH", "focusAnimation",
            TcStack.withData("thaumichorizons:golemplacer", 1, Map.of("advanced", "1")), 3,
            new AspectList().add(Aspect.MIND, 8).add(Aspect.SENSES, 8).add(Aspect.LIFE, 8),
            TcIngredient.item("thaumichorizons:golemplacer"), List.of(
                TcIngredient.item("minecraft:redstone"), TcIngredient.item("minecraft:glowstone_dust"),
                TcIngredient.item("minecraft:gunpowder"), TcIngredient.item("thaumcraft:warded_jar"),
                TcIngredient.item("thaumcraft:zombie_brain"))));
    }
}
