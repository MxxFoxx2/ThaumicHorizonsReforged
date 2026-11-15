package dev.visfox.thaumichorizons.infusion;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.entity.Endersteed;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.feline.Ocelot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jspecify.annotations.Nullable;

public final class ThInfusionRecipes {
    private static final ArrayList<CreatureInfusionRecipe> critterRecipes = new ArrayList<>();
    private static final ArrayList<SelfInfusionRecipe> selfRecipes = new ArrayList<>();
    private static final ArrayList<Predicate> classBanList = new ArrayList<>();

    private ThInfusionRecipes() {
    }

    public static class CreatureInfusionRecipe {
        private final String research;
        private final Object recipeOutput;
        private final int instability;
        private final thaumcraft.aspect.AspectList aspects;
        private final @Nullable Class<? extends LivingEntity> recipeInput;
        private final ItemStack[] components;
        private final int id;

        public CreatureInfusionRecipe(String research, Object output, int instability, thaumcraft.aspect.AspectList aspects,
            @Nullable Class<? extends LivingEntity> input, ItemStack[] recipe, int id) {
            this.research = research;
            this.recipeOutput = output;
            this.recipeInput = input;
            this.aspects = aspects;
            this.components = recipe;
            this.instability = instability;
            this.id = id;
        }

        public boolean matches(ArrayList<ItemStack> input, @Nullable Class<? extends LivingEntity> central, Level level, Player player) {
            if (!this.research.isEmpty() && !Compat.port().research().isComplete(player, this.research)) {
                return false;
            }
            if (this.recipeInput != null && (central == null || !central.isAssignableFrom(this.recipeInput))) {
                return false;
            }
            ArrayList<ItemStack> remaining = new ArrayList<>();
            for (ItemStack stack : input) {
                remaining.add(stack.copy());
            }
            for (ItemStack component : this.components) {
                boolean matched = false;
                for (int index = 0; index < remaining.size(); ++index) {
                    if (areItemStacksEqual(remaining.get(index).copy(), component)) {
                        remaining.remove(index);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
            return remaining.isEmpty();
        }

        public String getResearch() {
            return this.research;
        }

        public @Nullable Class<? extends LivingEntity> getRecipeInput() {
            return this.recipeInput;
        }

        public ItemStack[] getComponents() {
            return this.components;
        }

        public Object getRecipeOutput() {
            return this.recipeOutput;
        }

        public thaumcraft.aspect.AspectList getAspects() {
            return this.aspects;
        }

        public int getInstability() {
            return this.instability;
        }

        public int getID() {
            return this.id;
        }
    }

    public static class SelfInfusionRecipe {
        private final String research;
        private final int instability;
        private final thaumcraft.aspect.AspectList aspects;
        private final ItemStack[] components;
        private final int id;

        public SelfInfusionRecipe(String research, int instability, thaumcraft.aspect.AspectList aspects, ItemStack[] recipe, int id) {
            this.research = research;
            this.instability = instability;
            this.aspects = aspects;
            this.components = recipe;
            this.id = id;
        }

        public boolean matches(ArrayList<ItemStack> input, Level level, Player player) {
            if (!this.research.isEmpty() && !Compat.port().research().isComplete(player, this.research)) {
                return false;
            }
            ArrayList<ItemStack> remaining = new ArrayList<>();
            for (ItemStack stack : input) {
                remaining.add(stack.copy());
            }
            for (ItemStack component : this.components) {
                boolean matched = false;
                for (int index = 0; index < remaining.size(); ++index) {
                    if (areItemStacksEqual(remaining.get(index).copy(), component)) {
                        remaining.remove(index);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    return false;
                }
            }
            return remaining.isEmpty();
        }

        public thaumcraft.aspect.AspectList getAspects() {
            return this.aspects;
        }

        public int getInstability() {
            return this.instability;
        }

        public String getResearch() {
            return this.research;
        }

        public ItemStack[] getComponents() {
            return this.components;
        }

        public int getID() {
            return this.id;
        }
    }

    public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1) {
        if (stack0.isEmpty() && !stack1.isEmpty()) {
            return false;
        }
        if (!stack0.isEmpty() && stack1.isEmpty()) {
            return false;
        }
        if (stack0.isEmpty() && stack1.isEmpty()) {
            return true;
        }
        return ItemStack.isSameItemSameComponents(stack0, stack1);
    }

    @Nullable
    public static CreatureInfusionRecipe getCreatureInfusion(LivingEntity entityContained, ArrayList<ItemStack> components, Player player) {
        ensureRecipes();
        for (CreatureInfusionRecipe recipe : critterRecipes) {
            if (recipe.matches(components, entityContained.getClass(), player.level(), player)) {
                return recipe;
            }
        }
        return null;
    }

    @Nullable
    public static SelfInfusionRecipe getSelfInfusion(ArrayList<ItemStack> components, Player player) {
        ensureRecipes();
        for (SelfInfusionRecipe recipe : selfRecipes) {
            if (recipe.matches(components, player.level(), player)) {
                return recipe;
            }
        }
        return null;
    }

    public static boolean isBanned(EntityType<?> type, LivingEntity entity) {
        for (Predicate predicate : classBanList) {
            if (predicate.test(type, entity)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<CreatureInfusionRecipe> getCritterRecipes() {
        ensureRecipes();
        return critterRecipes;
    }

    public static ArrayList<SelfInfusionRecipe> getSelfRecipes() {
        ensureRecipes();
        return selfRecipes;
    }

    public static void init() {
        classBanList.add((type, entity) -> type == net.minecraft.world.entity.EntityTypes.GHAST);
        classBanList.add((type, entity) -> type == net.minecraft.world.entity.EntityTypes.ENDERMAN);
        classBanList.add((type, entity) -> type == net.minecraft.world.entity.EntityTypes.WITCH);
        classBanList.add((type, entity) -> type == net.minecraft.world.entity.EntityTypes.BLAZE);
        classBanList.add((type, entity) -> type == net.minecraft.world.entity.EntityTypes.MAGMA_CUBE);
        classBanList.add((type, entity) -> tcEntityType("pech") == type);
        classBanList.add((type, entity) -> tcEntityType("wisp") == type);
        classBanList.add((type, entity) -> entity instanceof thaumcraft.entity.TaintedMob);
    }

    private static boolean recipesInitialized;

    private static synchronized void ensureRecipes() {
        if (!recipesInitialized) {
            registerRecipes();
            recipesInitialized = true;
        }
    }

    private static @Nullable EntityType<?> tcEntityType(String name) {
        return net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE
            .getOptional(Identifier.fromNamespaceAndPath("thaumcraft", name))
            .orElse(null);
    }

    private static @Nullable Item tcItem(String name) {
        return Compat.port().items().lookup(Identifier.fromNamespaceAndPath("thaumcraft", name));
    }

    private static ItemStack tc(String name) {
        Item item = tcItem(name);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    private static ItemStack tc(String name, int count) {
        Item item = tcItem(name);
        return item == null ? ItemStack.EMPTY : new ItemStack(item, count);
    }

    private static ItemStack block(String name) {
        var block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath("thaumcraft", name));
        return block.map(b -> new ItemStack(b)).orElse(ItemStack.EMPTY);
    }

    private static ItemStack block(String name, int count) {
        var block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getOptional(Identifier.fromNamespaceAndPath("thaumcraft", name));
        return block.map(b -> new ItemStack(b, count)).orElse(ItemStack.EMPTY);
    }

    private static void registerRecipes() {
        critterRecipes.add(new CreatureInfusionRecipe("pigInfusion", 11, 4,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.EARTH, 8).add(thaumcraft.aspect.Aspect.HUNGER, 8)
                .add(thaumcraft.aspect.Aspect.METAL, 16).add(thaumcraft.aspect.Aspect.FIRE, 8),
            Pig.class,
            new ItemStack[] { new ItemStack(Blocks.FURNACE), tc("fire_shard"), tc("entropy_shard"), tc("air_shard") },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("pigInfusion", 12, 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.TAINT, 6).add(thaumcraft.aspect.Aspect.HEAL, 8)
                .add(thaumcraft.aspect.Aspect.PLANT, 8).add(thaumcraft.aspect.Aspect.HUNGER, 16),
            Pig.class,
            new ItemStack[] { block("tainted_plant"), block("silverwood_log"), block("void_jar"), block("silverwood_log") },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("chickenInfusion", 9, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.DARKNESS, 4).add(thaumcraft.aspect.Aspect.SLIME, 4)
                .add(thaumcraft.aspect.Aspect.EXCHANGE, 2),
            Chicken.class,
            new ItemStack[] { new ItemStack(Items.DYE.black()), new ItemStack(Items.SHEARS) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("chickenInfusion", 10, 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.GREED, 8).add(thaumcraft.aspect.Aspect.METAL, 8)
                .add(thaumcraft.aspect.Aspect.CRAFT, 16),
            Chicken.class,
            new ItemStack[] { new ItemStack(Items.GOLDEN_APPLE), block("alchemical_construct"),
                new ItemStack(Items.GOLDEN_APPLE), block("crucible") },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("cowInfusion", -96, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.PLANT, 8).add(thaumcraft.aspect.Aspect.BEAST, 4),
            Cow.class,
            new ItemStack[] { new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Blocks.RED_MUSHROOM),
                new ItemStack(Blocks.BROWN_MUSHROOM), new ItemStack(Blocks.RED_MUSHROOM) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("cowInfusion", 7, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.HUNGER, 4).add(thaumcraft.aspect.Aspect.EXCHANGE, 2),
            Cow.class,
            new ItemStack[] { new ItemStack(Items.COCOA_BEANS), new ItemStack(Items.SUGAR),
                new ItemStack(Items.COCOA_BEANS), new ItemStack(Items.SUGAR) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("sheepInfusion", 2, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.SENSES, 8).add(thaumcraft.aspect.Aspect.EXCHANGE, 4),
            Sheep.class,
            new ItemStack[] { new ItemStack(Items.DYE.red()), new ItemStack(Items.LAPIS_LAZULI),
                new ItemStack(Items.DYE.yellow()) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("sheepInfusion", 3, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.TOOL, 8).add(thaumcraft.aspect.Aspect.CLOTH, 4),
            Sheep.class,
            new ItemStack[] { new ItemStack(Items.SHEARS), new ItemStack(Items.COMPARATOR) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("sheederInfusion", 29, 5,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.CLOTH, 32).add(thaumcraft.aspect.Aspect.EXCHANGE, 8)
                .add(thaumcraft.aspect.Aspect.POISON, 8),
            Sheep.class,
            new ItemStack[] { new ItemStack(Blocks.COBWEB), new ItemStack(Items.SPIDER_EYE),
                new ItemStack(Blocks.COBWEB), new ItemStack(Items.SPIDER_EYE) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("sheederInfusion", 29, 5,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.CLOTH, 32).add(thaumcraft.aspect.Aspect.EXCHANGE, 8)
                .add(thaumcraft.aspect.Aspect.MIND, 16),
            Spider.class,
            new ItemStack[] { tc("zombie_brain"), new ItemStack(Items.WOOL.white()), new ItemStack(Items.WOOL.white()),
                new ItemStack(Items.WOOL.white()) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("catInfusion", 4, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.BEAST, 8).add(thaumcraft.aspect.Aspect.WEAPON, 8),
            Ocelot.class,
            new ItemStack[] { new ItemStack(Items.SPLASH_POTION), new ItemStack(Items.SADDLE) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("catInfusion", 5, 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ORDER, 16).add(thaumcraft.aspect.Aspect.DEATH, 8)
                .add(thaumcraft.aspect.Aspect.LIGHT, 24),
            Ocelot.class,
            new ItemStack[] { new ItemStack(Items.BONE), new ItemStack(Blocks.GOLD_BLOCK), tc("crystallized_essence"),
                block("silverwood_log") },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("catInfusion", 6, 4,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.TRAP, 8).add(thaumcraft.aspect.Aspect.AURA, 4)
                .add(thaumcraft.aspect.Aspect.ENERGY, 16),
            Ocelot.class,
            new ItemStack[] { tc("balanced_shard"), new ItemStack(Items.WOOL.black()), tc("balanced_shard"),
                new ItemStack(Items.GOLD_NUGGET) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("dogInfusion", 14, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.WATER, 8).add(thaumcraft.aspect.Aspect.AIR, 8)
                .add(thaumcraft.aspect.Aspect.EXCHANGE, 4).add(thaumcraft.aspect.Aspect.MOTION, 8),
            Wolf.class,
            new ItemStack[] { new ItemStack(Items.COD), tc("earth_shard"), new ItemStack(Items.COD),
                new ItemStack(Items.SPLASH_POTION) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("dogInfusion", 16, 4,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.LIGHT, 8).add(thaumcraft.aspect.Aspect.VOID, 8)
                .add(thaumcraft.aspect.Aspect.MAGIC, 16).add(thaumcraft.aspect.Aspect.EXCHANGE, 4),
            Wolf.class,
            new ItemStack[] { tc("wisp_essence"), tc("quicksilver_drop"), tc("wisp_essence") },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("dogInfusion", 15, 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.FIRE, 24).add(thaumcraft.aspect.Aspect.WEAPON, 8)
                .add(thaumcraft.aspect.Aspect.BEAST, 8),
            Wolf.class,
            new ItemStack[] { new ItemStack(Items.LAVA_BUCKET), new ItemStack(Items.BLAZE_ROD),
                tc("fire_shard"), new ItemStack(Items.BLAZE_ROD) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("horseInfusion", 13, 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ELDRITCH, 16).add(thaumcraft.aspect.Aspect.TRAVEL, 16)
                .add(thaumcraft.aspect.Aspect.VOID, 24),
            Horse.class,
            new ItemStack[] { new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.EGG),
                new ItemStack(Items.ENDER_PEARL), tc("wisp_essence") },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("horseInfusion", 20, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.DEATH, 8).add(thaumcraft.aspect.Aspect.UNDEAD, 8),
            Horse.class,
            new ItemStack[] { new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.ROTTEN_FLESH),
                new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.ROTTEN_FLESH) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("horseInfusion", 21, 2,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.DEATH, 8).add(thaumcraft.aspect.Aspect.UNDEAD, 8),
            Horse.class,
            new ItemStack[] { new ItemStack(Items.BONE), new ItemStack(Items.BONE),
                new ItemStack(Items.BONE), new ItemStack(Items.BONE) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("nightmareInfusion", 22, 8,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.FIRE, 32).add(thaumcraft.aspect.Aspect.TRAVEL, 16)
                .add(thaumcraft.aspect.Aspect.VOID, 16),
            Endersteed.class,
            new ItemStack[] { new ItemStack(Items.NETHER_STAR), new ItemStack(Blocks.OBSIDIAN),
                tc("fire_shard"), block("arcane_bellows"), tc("fire_shard"), new ItemStack(Blocks.OBSIDIAN) },
            0));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("quickLimbs", 1, "generic.movementSpeed", 3), 3,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.MOTION, 16).add(thaumcraft.aspect.Aspect.MECHANISM, 8)
                .add(thaumcraft.aspect.Aspect.FLIGHT, 4),
            null,
            new ItemStack[] { new ItemStack(Items.SPLASH_POTION), tc("quicksilver"), tc("quicksilver") },
            1));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("generic.attackDamage", 2, "thaumClaws", 2), 3,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.WEAPON, 12).add(thaumcraft.aspect.Aspect.METAL, 8)
                .add(thaumcraft.aspect.Aspect.MAGIC, 8),
            null,
            new ItemStack[] { tc("thaumium_sword"), tc("thaumium_sword") },
            2));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("awakeBlood", 3), 4,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.HEAL, 8).add(thaumcraft.aspect.Aspect.LIFE, 16)
                .add(thaumcraft.aspect.Aspect.MIND, 4),
            null,
            new ItemStack[] { new ItemStack(Items.GLISTERING_MELON_SLICE), new ItemStack(Items.SPLASH_POTION),
                new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.SPLASH_POTION) },
            3));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("diamondSkin", 4), 4,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ARMOR, 16).add(thaumcraft.aspect.Aspect.CRYSTAL, 16)
                .add(thaumcraft.aspect.Aspect.BEAST, 8),
            null,
            new ItemStack[] { new ItemStack(Items.LEATHER), new ItemStack(Items.DIAMOND), new ItemStack(Items.DIAMOND) },
            4));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("enderHeart", 5), 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ELDRITCH, 16).add(thaumcraft.aspect.Aspect.TRAVEL, 16)
                .add(thaumcraft.aspect.Aspect.FLESH, 16),
            null,
            new ItemStack[] { new ItemStack(Items.ENDER_PEARL), new ItemStack(Items.EGG), new ItemStack(Items.EGG) },
            5));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("shockSkin", 6), 5,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ENERGY, 16).add(thaumcraft.aspect.Aspect.WEAPON, 12)
                .add(thaumcraft.aspect.Aspect.WEATHER, 4),
            null,
            new ItemStack[] { new ItemStack(Items.QUARTZ), tc("order_shard"), new ItemStack(Items.QUARTZ),
                new ItemStack(Items.REDSTONE) },
            6));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("instilledLoyalty", 7), 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.MIND, 4).add(thaumcraft.aspect.Aspect.BEAST, 3)
                .add(thaumcraft.aspect.Aspect.EXCHANGE, 3),
            null,
            new ItemStack[] { new ItemStack(Items.SADDLE), tc("zombie_brain"), new ItemStack(Items.GOLDEN_APPLE) },
            7));
        critterRecipes.add(new CreatureInfusionRecipe("runicHide", tag("runicCreature", 8), 7,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ARMOR, 16).add(thaumcraft.aspect.Aspect.MAGIC, 32)
                .add(thaumcraft.aspect.Aspect.ENERGY, 16).add(thaumcraft.aspect.Aspect.FLESH, 8),
            null,
            new ItemStack[] { new ItemStack(Items.DIAMOND), tc("crystallized_essence"), tc("enchanted_fabric"),
                tc("thaumium_ingot"), tc("wisp_essence") },
            8));
        critterRecipes.add(new CreatureInfusionRecipe("eldritchFangs", tag("eldritchFangs", 9, "generic.attackDamage", 1), 8,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ELDRITCH, 8).add(thaumcraft.aspect.Aspect.WEAPON, 16)
                .add(thaumcraft.aspect.Aspect.METAL, 16).add(thaumcraft.aspect.Aspect.POISON, 32),
            null,
            new ItemStack[] { tc("void_seed"), block("ethereal_bloom"), tc("void_seed"), block("ethereal_bloom") },
            9));
        critterRecipes.add(new CreatureInfusionRecipe("infusionVat", tag("portability", 10), 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.VOID, 16).add(thaumcraft.aspect.Aspect.TRAP, 16),
            null,
            new ItemStack[] { block("warded_jar"), new ItemStack(Items.ENDER_PEARL) },
            10));

        selfRecipes.add(new SelfInfusionRecipe("selfInfusion", 4,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.MOTION, 64).add(thaumcraft.aspect.Aspect.MECHANISM, 32)
                .add(thaumcraft.aspect.Aspect.FLIGHT, 32),
            new ItemStack[] { new ItemStack(Items.SPLASH_POTION), tc("quicksilver"), tc("void_seed"),
                new ItemStack(Items.SPLASH_POTION), tc("quicksilver"), tc("void_seed") },
            1));
        selfRecipes.add(new SelfInfusionRecipe("selfInfusion", 5,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.HEAL, 32).add(thaumcraft.aspect.Aspect.LIFE, 64)
                .add(thaumcraft.aspect.Aspect.MIND, 32),
            new ItemStack[] { new ItemStack(Items.GOLDEN_APPLE), new ItemStack(Items.GLISTERING_MELON_SLICE),
                new ItemStack(Items.GLISTERING_MELON_SLICE), tc("zombie_brain"),
                new ItemStack(Items.GLISTERING_MELON_SLICE), new ItemStack(Items.GLISTERING_MELON_SLICE) },
            3));
        selfRecipes.add(new SelfInfusionRecipe("selfInfusion", 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ARMOR, 32).add(thaumcraft.aspect.Aspect.CRYSTAL, 32)
                .add(thaumcraft.aspect.Aspect.MAN, 16),
            new ItemStack[] { new ItemStack(Items.LEATHER_HELMET), new ItemStack(Items.DIAMOND),
                new ItemStack(Items.LEATHER_CHESTPLATE), new ItemStack(Blocks.DIAMOND_BLOCK),
                new ItemStack(Items.LEATHER_LEGGINGS), new ItemStack(Items.DIAMOND),
                new ItemStack(Items.LEATHER_BOOTS), new ItemStack(Blocks.DIAMOND_BLOCK) },
            4));
        selfRecipes.add(new SelfInfusionRecipe("morphicFingers", 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.TOOL, 32).add(thaumcraft.aspect.Aspect.CRAFT, 32)
                .add(thaumcraft.aspect.Aspect.MAN, 32).add(thaumcraft.aspect.Aspect.EXCHANGE, 16),
            new ItemStack[] { block("arcane_worktable"), tc("void_seed"), tc("balanced_shard"), tc("void_seed") },
            2));
        selfRecipes.add(new SelfInfusionRecipe("silverHeart", 7,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.ORDER, 32).add(thaumcraft.aspect.Aspect.HEAL, 32)
                .add(thaumcraft.aspect.Aspect.EXCHANGE, 16),
            new ItemStack[] { block("shimmerleaf"), block("silverwood_log"), block("vishroom"),
                block("silverwood_log") },
            5));
        selfRecipes.add(new SelfInfusionRecipe("synthSkin", 6,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.PLANT, 32).add(thaumcraft.aspect.Aspect.HUNGER, 32)
                .add(thaumcraft.aspect.Aspect.LIGHT, 32).add(thaumcraft.aspect.Aspect.MAN, 16),
            new ItemStack[] { new ItemStack(Blocks.OAK_LEAVES), new ItemStack(Blocks.OAK_SAPLING),
                new ItemStack(Blocks.POPPY), new ItemStack(Blocks.VINE) },
            6));
        selfRecipes.add(new SelfInfusionRecipe("amphibious", 7,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.WATER, 64).add(thaumcraft.aspect.Aspect.AIR, 64)
                .add(thaumcraft.aspect.Aspect.LIFE, 32).add(thaumcraft.aspect.Aspect.EXCHANGE, 16),
            new ItemStack[] { new ItemStack(Items.COD), new ItemStack(Items.SPLASH_POTION),
                new ItemStack(Items.COD), new ItemStack(Items.SPLASH_POTION) },
            7));
        selfRecipes.add(new SelfInfusionRecipe("warpedTumor", 12,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.TAINT, 64).add(thaumcraft.aspect.Aspect.ELDRITCH, 48)
                .add(thaumcraft.aspect.Aspect.TRAP, 32).add(thaumcraft.aspect.Aspect.FLESH, 24).add(thaumcraft.aspect.Aspect.EXCHANGE, 16),
            new ItemStack[] { block("tainted_soil"), tc("bath_salts"), new ItemStack(Items.NETHER_STAR),
                block("arcane_stone"), new ItemStack(Items.NETHER_STAR), tc("sanitizing_soap") },
            8));
        selfRecipes.add(new SelfInfusionRecipe("spiderClimb", 8,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.BEAST, 32).add(thaumcraft.aspect.Aspect.MOTION, 48)
                .add(thaumcraft.aspect.Aspect.SLIME, 32),
            new ItemStack[] { new ItemStack(Blocks.COBWEB), tc("enchanted_fabric"), new ItemStack(Blocks.COBWEB),
                new ItemStack(Blocks.LADDER), new ItemStack(Blocks.COBWEB), tc("enchanted_fabric"),
                new ItemStack(Blocks.COBWEB), new ItemStack(Blocks.LADDER) },
            9));
        selfRecipes.add(new SelfInfusionRecipe("chameleonSkin", 7,
            new thaumcraft.aspect.AspectList().add(thaumcraft.aspect.Aspect.SENSES, 48).add(thaumcraft.aspect.Aspect.EXCHANGE, 32)
                .add(thaumcraft.aspect.Aspect.VOID, 32),
            new ItemStack[] { new ItemStack(Items.SPIDER_EYE), new ItemStack(Items.DYE.red()),
                new ItemStack(Items.LAPIS_LAZULI), new ItemStack(Items.DYE.yellow()),
                new ItemStack(Items.DYE.black()), new ItemStack(Items.BONE_MEAL) },
            10));
    }

    private static Object tag(Object... pairs) {
        java.util.LinkedHashMap<String, Integer> map = new java.util.LinkedHashMap<>();
        for (int index = 0; index + 1 < pairs.length; index += 2) {
            map.put((String) pairs[index], (Integer) pairs[index + 1]);
        }
        return map;
    }

    public interface Predicate {
        boolean test(EntityType<?> type, LivingEntity entity);
    }
}
