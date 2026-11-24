package dev.visfox.thaumichorizons.entity;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

/**
 * Own golem materials of Thaumic Horizons. Independent from the stock TC4R
 * {@link thaumcraft.entity.golem.GolemType}: characteristics follow the original
 * 1.7.10 {@code EnumGolemTHType} values, identifiers are stable strings and are
 * the only persistent form of the material.
 */
public enum GolemMaterialTH {
    GRASS("grass", 10, 0, 0.38F, false, 2, 1, 75, 0, 750),
    DIRT("dirt", 10, 0, 0.38F, false, 1, 1, 75, 0, 500),
    WOOD("wood", 20, 6, 0.35F, false, 2, 4, 75, 1, 750),
    PLANT("plant", 10, 0, 0.40F, false, 2, 1, 75, 0, 750),
    ROCK("rock", 30, 12, 0.32F, true, 2, 16, 100, 3, 1000),
    METAL("metal", 35, 15, 0.31F, true, 2, 32, 125, 4, 1250),
    CLOTH("cloth", 10, 0, 0.40F, false, 2, 1, 75, 0, 750),
    SAND("sand", 10, 0, 0.40F, false, 1, 1, 75, 0, 500),
    REDSTONE("redstone", 20, 9, 0.35F, true, 2, 8, 45, 2, 1000),
    TNT("tnt", 10, 0, 0.38F, false, 1, 1, 75, 0, 750),
    ICE("ice", 20, 6, 0.35F, false, 2, 4, 75, 1, 750),
    CACTUS("cactus", 20, 6, 0.35F, false, 2, 4, 75, 1, 1000),
    CLAY("clay", 25, 9, 0.33F, true, 1, 8, 100, 2, 750),
    CAKE("cake", 20, 6, 0.35F, false, 2, 4, 25, 1, 750),
    WEB("web", 15, 6, 0.35F, false, 2, 4, 40, 1, 1000),
    VOID("void", 25, 15, 0.40F, true, 2, 8, 20, 3, 0);

    public record MaterialStats(int health, int armor, float speed, boolean fireResist, int upgrades,
        int carry, int regenDelay, int strength, int visCost, boolean heavy) {
    }

    public static final GolemMaterialTH DEFAULT = VOID;

    private static final Map<String, GolemMaterialTH> BY_ID = new HashMap<>();
    private static final Map<Integer, GolemMaterialTH> LEGACY = Map.ofEntries(
        Map.entry(1, WOOD),
        Map.entry(3, CLAY),
        Map.entry(5, ROCK),
        Map.entry(6, METAL),
        Map.entry(8, GRASS),
        Map.entry(9, DIRT),
        Map.entry(10, PLANT),
        Map.entry(11, ROCK),
        Map.entry(12, METAL),
        Map.entry(13, CLOTH),
        Map.entry(14, SAND),
        Map.entry(15, REDSTONE),
        Map.entry(16, TNT),
        Map.entry(17, ICE),
        Map.entry(18, CACTUS),
        Map.entry(19, CAKE),
        Map.entry(20, WEB),
        Map.entry(21, VOID));

    static {
        for (GolemMaterialTH material : values()) {
            BY_ID.put(material.id, material);
        }
    }

    public final String id;
    public final int health;
    public final int armor;
    public final float speed;
    public final boolean fireResist;
    public final int upgrades;
    public final int carry;
    public final int regenDelay;
    public final int strength;
    public final int visCost;

    GolemMaterialTH(String id, int health, int armor, float speed, boolean fireResist, int upgrades, int carry,
        int regenDelay, int strength, int visCost) {
        this.id = id;
        this.health = health;
        this.armor = armor;
        this.speed = speed;
        this.fireResist = fireResist;
        this.upgrades = upgrades;
        this.carry = carry;
        this.regenDelay = regenDelay;
        this.strength = strength;
        this.visCost = visCost;
    }

    public String id() {
        return id;
    }

    public MaterialStats stats() {
        boolean heavy = this == ROCK || this == METAL || this == REDSTONE;
        return new MaterialStats(health, armor, speed, fireResist, upgrades, carry, regenDelay, strength, visCost, heavy);
    }

    public static GolemMaterialTH byId(@Nullable String id) {
        if (id == null || id.isBlank()) {
            return DEFAULT;
        }
        return BY_ID.getOrDefault(id, DEFAULT);
    }

    /**
     * Legacy mapping from the old {@code GolemType} ordinal saved by previous
     * versions (8..21 = TH materials, 1/3/5/6 = overlapping stock materials).
     * Returns {@code null} when the ordinal cannot be mapped.
     */
    public static @Nullable GolemMaterialTH legacy(int oldOrdinal) {
        return LEGACY.get(oldOrdinal);
    }

    public static GolemMaterialTH fromBlock(@Nullable Block block) {
        if (block == null) {
            return VOID;
        }
        BlockState state = block.defaultBlockState();
        if (isCopperFamily(block)) {
            return METAL;
        }
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM) || state.is(Blocks.PODZOL)
            || state.is(Blocks.MOSS_BLOCK)) {
            return GRASS;
        }
        if (state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND) || state.is(Blocks.DIRT_PATH)
            || state.is(Blocks.MUD) || state.is(Blocks.MUDDY_MANGROVE_ROOTS) || state.is(Blocks.GRAVEL)
            || state.is(Blocks.SUSPICIOUS_GRAVEL) || state.is(Blocks.CRIMSON_NYLIUM)
            || state.is(Blocks.WARPED_NYLIUM)) {
            return DIRT;
        }
        if (state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS) || state.is(BlockTags.WOODEN_SLABS)
            || state.is(BlockTags.WOODEN_STAIRS) || state.is(BlockTags.WOODEN_FENCES)
            || state.is(BlockTags.WOODEN_DOORS) || state.is(BlockTags.WOODEN_TRAPDOORS)
            || state.is(Blocks.BOOKSHELF) || state.is(Blocks.CHISELED_BOOKSHELF) || state.is(Blocks.CRAFTING_TABLE)
            || state.is(Blocks.CHEST) || state.is(Blocks.TRAPPED_CHEST) || state.is(Blocks.BARREL)
            || state.is(Blocks.PUMPKIN) || state.is(Blocks.CARVED_PUMPKIN) || state.is(Blocks.JACK_O_LANTERN)
            || state.is(Blocks.MELON) || state.is(Blocks.NOTE_BLOCK) || state.is(Blocks.JUKEBOX)
            || state.is(Blocks.BAMBOO_BLOCK) || state.is(Blocks.BEEHIVE) || state.is(Blocks.BEE_NEST)) {
            return WOOD;
        }
        if (state.is(BlockTags.LEAVES) || state.is(BlockTags.SAPLINGS) || state.is(BlockTags.FLOWERS)
            || state.is(BlockTags.CROPS) || state.is(Blocks.VINE) || state.is(Blocks.SUGAR_CANE)
            || state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT) || state.is(Blocks.SEAGRASS)
            || state.is(Blocks.TALL_SEAGRASS) || state.is(Blocks.SHORT_GRASS) || state.is(Blocks.FERN)
            || state.is(Blocks.LARGE_FERN) || state.is(Blocks.DEAD_BUSH) || state.is(Blocks.BAMBOO)
            || state.is(Blocks.LILY_PAD) || state.is(Blocks.MOSS_CARPET) || state.is(Blocks.AZALEA)
            || state.is(Blocks.FLOWERING_AZALEA) || state.is(Blocks.BIG_DRIPLEAF) || state.is(Blocks.SMALL_DRIPLEAF)
            || state.is(Blocks.SPORE_BLOSSOM) || state.is(Blocks.GLOW_LICHEN) || state.is(Blocks.HANGING_ROOTS)
            || state.is(Blocks.WEEPING_VINES) || state.is(Blocks.TWISTING_VINES)
            || state.is(Blocks.MANGROVE_PROPAGULE)) {
            return PLANT;
        }
        if (state.is(Blocks.IRON_BLOCK) || state.is(Blocks.GOLD_BLOCK) || isCopperFamily(block)
            || state.is(Blocks.RAW_IRON_BLOCK) || state.is(Blocks.RAW_GOLD_BLOCK) || state.is(Blocks.ANVIL)
            || state.is(Blocks.CHIPPED_ANVIL) || state.is(Blocks.DAMAGED_ANVIL) || state.is(Blocks.IRON_BARS)
            || state.is(Blocks.CHAIN) || state.is(Blocks.CAULDRON)
            || state.is(Blocks.WATER_CAULDRON) || state.is(Blocks.LAVA_CAULDRON) || state.is(Blocks.HOPPER)
            || state.is(Blocks.RAIL) || state.is(Blocks.POWERED_RAIL) || state.is(Blocks.DETECTOR_RAIL)
            || state.is(Blocks.ACTIVATOR_RAIL) || state.is(Blocks.IRON_DOOR) || state.is(Blocks.IRON_TRAPDOOR)
            || state.is(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
            return METAL;
        }
        if (state.is(BlockTags.WOOL) || state.is(BlockTags.WOOL_CARPETS) || state.is(BlockTags.BEDS)
            || state.is(Blocks.SPONGE) || state.is(Blocks.WET_SPONGE) || state.is(Blocks.SHULKER_BOX)) {
            return CLOTH;
        }
        if (state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SOUL_SAND)
            || state.is(Blocks.SOUL_SOIL) || state.is(Blocks.SUSPICIOUS_SAND) || state.is(Blocks.SANDSTONE)
            || state.is(Blocks.RED_SANDSTONE)) {
            return SAND;
        }
        if (state.is(Blocks.REDSTONE_LAMP) || state.is(Blocks.REDSTONE_BLOCK) || state.is(Blocks.REDSTONE_WIRE)
            || state.is(Blocks.REPEATER) || state.is(Blocks.COMPARATOR) || state.is(Blocks.DAYLIGHT_DETECTOR)
            || state.is(Blocks.OBSERVER) || state.is(Blocks.PISTON) || state.is(Blocks.STICKY_PISTON)
            || state.is(Blocks.DISPENSER) || state.is(Blocks.DROPPER) || state.is(Blocks.TARGET)
            || state.is(Blocks.LEVER) || state.is(Blocks.REDSTONE_TORCH) || state.is(Blocks.TRIPWIRE)
            || state.is(Blocks.TRIPWIRE_HOOK) || state.is(Blocks.COMMAND_BLOCK)
            || state.is(Blocks.CHAIN_COMMAND_BLOCK) || state.is(Blocks.REPEATING_COMMAND_BLOCK)) {
            return REDSTONE;
        }
        if (state.is(Blocks.TNT)) {
            return TNT;
        }
        if (state.is(Blocks.ICE) || state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE)
            || state.is(Blocks.FROSTED_ICE) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.SNOW)
            || state.is(Blocks.POWDER_SNOW)) {
            return ICE;
        }
        if (state.is(Blocks.CACTUS)) {
            return CACTUS;
        }
        if (state.is(Blocks.CAKE)) {
            return CAKE;
        }
        if (state.is(Blocks.COBWEB)) {
            return WEB;
        }
        return ROCK;
    }

    private static boolean isCopperFamily(@Nullable Block block) {
        if (block == null) {
            return false;
        }
        return block instanceof net.minecraft.world.level.block.WeatheringCopper
            || block == Blocks.WAXED_COPPER_BLOCK
            || block == Blocks.WAXED_EXPOSED_COPPER
            || block == Blocks.WAXED_WEATHERED_COPPER
            || block == Blocks.WAXED_OXIDIZED_COPPER
            || block == Blocks.LIGHTNING_ROD;
    }
}
