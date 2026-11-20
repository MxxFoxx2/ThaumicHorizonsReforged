package dev.visfox.thaumichorizons.world.pocket;

import dev.visfox.thaumichorizons.block.pocket.PortalTHBlock;
import dev.visfox.thaumichorizons.block.pocket.PortalTHBlockEntity;
import dev.visfox.thaumichorizons.lib.TcBlocks;
import dev.visfox.thaumichorizons.registry.PocketRegistry;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThEntities;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.taint.TaintBiomes;

public final class PocketPlaneGenerator {
    private static final short MAX_CREATURES = 100;
    private static short creatures = 0;
    private static RandomSource rand = RandomSource.create();

    private PocketPlaneGenerator() {
    }

    /**
     * Starts a new plane generation pass. The legacy generator used the
     * dimension world's RNG and reset its creature budget after each plane.
     */
    static void beginGeneration(ServerLevel world) {
        creatures = 0;
        rand = world.getRandom();
    }

    static int fastFloor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }

    private static Block tc(String name, Block fallback) {
        Block block = TcBlocks.block(name);
        return block == null ? fallback : block;
    }

    private static Block infusedOre(int md) {
        return switch (md) {
            case 0 -> tc("cinnabar_ore", Blocks.REDSTONE_ORE);
            case 1 -> tc("air_infused_stone", Blocks.STONE);
            case 2 -> tc("fire_infused_stone", Blocks.STONE);
            case 3 -> tc("water_infused_stone", Blocks.STONE);
            case 4 -> tc("earth_infused_stone", Blocks.STONE);
            case 5 -> tc("order_infused_stone", Blocks.STONE);
            case 6 -> tc("entropy_infused_stone", Blocks.STONE);
            case 7 -> tc("amber_ore", Blocks.STONE);
            default -> Blocks.STONE;
        };
    }

    private static Block cosmeticSolid(int md) {
        // Mapping confirmed against TC4R tools/data/id_map.json:
        // blockCosmeticSolid metadata 0, 1, 6 and 11 are not interchangeable.
        return switch (md) {
            case 0 -> tc("obsidian_totem", Blocks.OBSIDIAN);
            case 1 -> tc("obsidian_tile", Blocks.OBSIDIAN);
            case 2 -> tc("travel_paving_stone", Blocks.STONE_BRICKS);
            case 3 -> tc("warding_paving_stone", Blocks.STONE_BRICKS);
            case 4 -> tc("thaumium_block", Blocks.IRON_BLOCK);
            case 5 -> tc("tallow_block", Blocks.HONEY_BLOCK);
            case 6 -> tc("arcane_stone", Blocks.STONE_BRICKS);
            case 7 -> tc("arcane_stone_bricks", Blocks.STONE_BRICKS);
            case 8 -> tc("charged_obsidian_totem", Blocks.OBSIDIAN);
            case 9, 10 -> tc("golem_fetter", Blocks.IRON_BLOCK);
            case 11 -> tc("ancient_stone", Blocks.STONE_BRICKS);
            case 12 -> tc("ancient_rock", Blocks.STONE);
            case 13 -> tc("ancient_stone_no_spawn", Blocks.STONE);
            case 14 -> tc("crusted_stone", Blocks.STONE);
            case 15 -> tc("ancient_stone_pedestal", Blocks.STONE);
            default -> Blocks.STONE;
        };
    }

    private static Block customPlant(int md) {
        // blockCustomPlant metadata mapping from the TC4R migration table.
        return switch (md) {
            case 0 -> tc("greatwood_sapling", Blocks.OAK_SAPLING);
            case 1 -> tc("silverwood_sapling", Blocks.BIRCH_SAPLING);
            case 2 -> tc("shimmerleaf", Blocks.OXEYE_DAISY);
            case 3 -> tc("cinderpearl", Blocks.DEAD_BUSH);
            case 4 -> tc("ethereal_bloom", Blocks.OXEYE_DAISY);
            case 5 -> tc("vishroom", Blocks.RED_MUSHROOM);
            default -> Blocks.OXEYE_DAISY;
        };
    }

    private static Block taint(int md) {
        // blockTaint metadata: 0 crusted taint, 1 tainted soil, 2 flesh.
        return switch (md) {
            case 0 -> tc("crusted_taint", Blocks.DYED_TERRACOTTA.pick(DyeColor.PURPLE));
            case 1 -> tc("tainted_soil", Blocks.DIRT);
            case 2 -> tc("flesh_block", Blocks.WOOL.pick(DyeColor.RED));
            default -> Blocks.DYED_TERRACOTTA.pick(DyeColor.PURPLE);
        };
    }

    private static Block taintFiber(int md) {
        // blockTaintFibres is a separate legacy registry from blockTaint.
        return switch (md) {
            case 0 -> tc("fibrous_taint", Blocks.WOOL.pick(DyeColor.PURPLE));
            case 1 -> tc("tainted_grass", Blocks.GRASS_BLOCK);
            case 2 -> tc("tainted_plant", Blocks.TALL_GRASS);
            case 3 -> tc("spore_stalk", Blocks.WARPED_STEM);
            case 4 -> tc("mature_spore_stalk", Blocks.WARPED_STEM);
            default -> Blocks.WOOL.pick(DyeColor.PURPLE);
        };
    }

    private static Block flower(int md) {
        // 1.7.10 red_flower accepts metadata 0–8; invalid values fall back to Poppy.
        return switch (md) {
            case 1 -> Blocks.BLUE_ORCHID;
            case 2 -> Blocks.ALLIUM;
            case 3 -> Blocks.AZURE_BLUET;
            case 4 -> Blocks.RED_TULIP;
            case 5 -> Blocks.ORANGE_TULIP;
            case 6 -> Blocks.WHITE_TULIP;
            case 7 -> Blocks.PINK_TULIP;
            case 8 -> Blocks.OXEYE_DAISY;
            default -> Blocks.POPPY;
        };
    }

    private static Block sapling(int md) {
        return switch (md) {
            case 1 -> Blocks.SPRUCE_SAPLING;
            case 2 -> Blocks.BIRCH_SAPLING;
            case 3 -> Blocks.JUNGLE_SAPLING;
            case 4 -> Blocks.ACACIA_SAPLING;
            case 5 -> Blocks.DARK_OAK_SAPLING;
            default -> Blocks.OAK_SAPLING;
        };
    }

    static void setBlock(ServerLevel world, int x, int y, int z, Block block, int md) {
        world.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
        BlockState state = block.defaultBlockState();
        // Legacy snow-layer metadata stores (layers - 1); other metadata
        // variants have already been converted to distinct modern block IDs.
        if (block == Blocks.SNOW && state.hasProperty(SnowLayerBlock.LAYERS)) {
            state = state.setValue(SnowLayerBlock.LAYERS, Math.max(1, Math.min(8, md + 1)));
        }
        world.setBlock(new BlockPos(x, y, z), state, 2);
    }

    public static void setBlockPublic(ServerLevel world, int x, int y, int z, Block block) {
        setBlock(world, x, y, z, block, 0);
    }

    public static void setPortalBlock(ServerLevel world, int x, int y, int z, int which,
        BlockPos sourceSlot, int plane, int portal, String dimension, boolean xAligned) {
        world.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
        BlockPos pos = new BlockPos(x, y, z);
        world.setBlock(pos, PocketRegistry.PORTAL_TH.get().defaultBlockState().setValue(PortalTHBlock.MD, which), 2);
        if (world.getBlockEntity(pos) instanceof PortalTHBlockEntity entity) {
            entity.configure(sourceSlot, plane, portal, dimension, xAligned);
        }
    }

    public static void drawPlatform(ServerLevel world, int x, int y, int z, Block block) {
        setBlock(world, x, y, z, block, 0);
        setBlockToAir(world, x, y + 1, z);
        setBlockToAir(world, x, y + 2, z);
    }

    private static void setBlockToAir(ServerLevel world, int x, int y, int z) {
        world.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
        world.removeBlock(new BlockPos(x, y, z), false);
    }

    private static boolean isAir(ServerLevel world, int x, int y, int z) {
        world.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
        return world.getBlockState(new BlockPos(x, y, z)).isAir();
    }

    private static Block getBlock(ServerLevel world, int x, int y, int z) {
        world.getChunk(x >> 4, z >> 4, ChunkStatus.FULL, true);
        return world.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    static int getColor(AspectList aspects) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (Aspect asp : aspects.getAspects()) {
            if (asp != null) {
                Color aspColor = new Color(asp.getColor());
                r += (int) (aspColor.getRed() * aspectFraction(asp, aspects));
                g += (int) (aspColor.getGreen() * aspectFraction(asp, aspects));
                b += (int) (aspColor.getBlue() * aspectFraction(asp, aspects));
            }
        }
        return r * 256 * 256 + g * 256 + b;
    }

    public static void addEffects(PocketPlaneData data, AspectList aspects) {
        int pointer = 0;
        data.potionEffects = new int[8];
        if (aspects.getAmount(Aspect.MOTION) > 0) {
            data.potionEffects[pointer++] = id(MobEffects.SPEED);
        }
        if (aspects.getAmount(Aspect.FLIGHT) > 0) {
            data.potionEffects[pointer++] = id(MobEffects.JUMP_BOOST);
        }
        if (aspects.getAmount(Aspect.HEAL) > 0) {
            data.potionEffects[pointer++] = id(MobEffects.REGENERATION);
        }
        if (aspects.getAmount(Aspect.TRAVEL) > 0) {
            data.potionEffects[pointer++] = id(MobEffects.SPEED);
        }
        if (aspects.getAmount(Aspect.TOOL) > 0) {
            data.potionEffects[pointer++] = id(MobEffects.HASTE);
        }
        if (aspects.getAmount(Aspect.WEAPON) > 0) {
            data.potionEffects[pointer++] = id(MobEffects.STRENGTH);
        }
        if (aspects.getAmount(Aspect.ARMOR) > 0) {
            data.potionEffects[pointer++] = id(MobEffects.RESISTANCE);
        }
    }

    private static int id(net.minecraft.core.Holder<MobEffect> effect) {
        return BuiltInRegistries.MOB_EFFECT.getId(effect.value());
    }

    static boolean drawRings(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects) {
        boolean drewAnything = false;
        int numRings = 0;
        if (aspects.getAmount(Aspect.ELDRITCH) > 0) {
            drawSphere(xCenter, yCenter, zCenter, data.radius - ++numRings, cosmeticSolid(11), 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.DARKNESS) > 0) {
            drawSphere(xCenter, yCenter, zCenter, data.radius - ++numRings, Blocks.OBSIDIAN, 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.ELDRITCH) > 0) {
            drawSphere(xCenter, yCenter, zCenter, data.radius - ++numRings, cosmeticSolid(11), 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.CRYSTAL) > 0) {
            drawSphere(xCenter, yCenter, zCenter, data.radius - ++numRings, Blocks.GLASS, 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.COLD) > 0) {
            drawSphere(xCenter, yCenter, zCenter, data.radius - ++numRings, Blocks.PACKED_ICE, 0, world);
            drewAnything = true;
        }
        return drewAnything;
    }

    public static double calcNoise(AspectList aspects) {
        double noise = 50.0D;
        noise -= aspectFraction(Aspect.ORDER, aspects) * 100.0D;
        noise += aspectFraction(Aspect.ENTROPY, aspects) * 100.0D;
        noise += aspectFraction(Aspect.MOTION, aspects) * 50.0D;
        noise += aspectFraction(Aspect.EXCHANGE, aspects) * 100.0D;
        noise += aspectFraction(Aspect.FLIGHT, aspects) * 50.0D;
        noise -= aspectFraction(Aspect.TRAVEL, aspects) * 100.0D;
        if (noise > 100.0D) {
            return 100.0D;
        }
        return Math.max(noise, 0.0D);
    }

    public static double calcLife(AspectList aspects) {
        double life = 0;
        life += aspectFraction(Aspect.LIGHT, aspects) * 25.0D;
        life += aspectFraction(Aspect.WEATHER, aspects) * 25.0D;
        life += aspectFraction(Aspect.LIFE, aspects) * 150.0D;
        life -= aspectFraction(Aspect.POISON, aspects) * 75.0D;
        life -= aspectFraction(Aspect.DEATH, aspects) * 100.0D;
        life -= aspectFraction(Aspect.DARKNESS, aspects) * 25.0D;
        life += aspectFraction(Aspect.SOUL, aspects) * 50.0D;
        life += aspectFraction(Aspect.HEAL, aspects) * 150.0D;
        life += aspectFraction(Aspect.AURA, aspects) * 50.0D;
        life += aspectFraction(Aspect.SLIME, aspects) * 50.0D;
        life += aspectFraction(Aspect.PLANT, aspects) * 100.0D;
        life += aspectFraction(Aspect.TREE, aspects) * 100.0D;
        life += aspectFraction(Aspect.BEAST, aspects) * 100.0D;
        life += aspectFraction(Aspect.FLESH, aspects) * 50.0D;
        life -= aspectFraction(Aspect.UNDEAD, aspects) * 50.0D;
        life += aspectFraction(Aspect.MIND, aspects) * 25.0D;
        life += aspectFraction(Aspect.SENSES, aspects) * 25.0D;
        life += aspectFraction(Aspect.MAN, aspects) * 25.0D;
        life += aspectFraction(Aspect.CROP, aspects) * 50.0D;
        life += aspectFraction(Aspect.HARVEST, aspects) * 25.0D;
        life -= aspectFraction(Aspect.WEAPON, aspects) * 25.0D;
        life -= aspectFraction(Aspect.HUNGER, aspects) * 25.0D;
        life += aspectFraction(Aspect.CLOTH, aspects) * 25.0D;
        if (life < 0.0D) {
            life = 0.0D;
        } else if (life > 100.0D) {
            life = 100.0D;
        }
        return life;
    }

    public static double aspectFraction(Aspect asp, AspectList aspects) {
        return (double) aspects.getAmount(asp) / (double) aspects.visSize();
    }

    static void drawLayers(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects, int noise, ResourceKey<net.minecraft.world.level.biome.Biome> bio, int life) {
        int level = yCenter;
        boolean drewAnything = false;
        boolean cold = bio == Biomes.SNOWY_PLAINS;
        boolean desert = bio == Biomes.DESERT;

        if (aspects.getAmount(Aspect.COLD) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.PACKED_ICE, 0, level, 0, cold, desert, life,
                aspects);
            level -= fastFloor(aspectFraction(Aspect.COLD, aspects) * data.radius);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.WATER, 0, level, 0, cold, desert, life, aspects);
            level -= fastFloor(aspectFraction(Aspect.WATER, aspects) * data.radius / 1.5F);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, taint(1), 1, level, noise, cold, desert, life, aspects);
            level -= fastFloor(aspectFraction(Aspect.TAINT, aspects) * data.radius);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.CLOTH) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.WOOL.pick(DyeColor.WHITE), 0, level, noise, cold, desert, life,
                aspects);
            level -= fastFloor(aspectFraction(Aspect.CLOTH, aspects) * data.radius);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.FLESH) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, taint(2), 2, level, noise, cold, desert, life, aspects);
            level -= fastFloor(aspectFraction(Aspect.CLOTH, aspects) * data.radius);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.EARTH) > 0) {
            if (desert) {
                drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.SAND, 0, level, noise, cold, desert, life,
                    aspects);
            } else {
                drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.DIRT, 0, level, noise, cold, desert, life,
                    aspects);
            }
            if (aspects.getAmount(Aspect.ORDER) >= aspects.getAmount(Aspect.ENTROPY)) {
                drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.STONE, 0, level - 5, noise, cold, desert,
                    life, aspects);
            } else {
                drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.COBBLESTONE, 0, level - 5, noise, cold,
                    desert, life, aspects);
            }
            level -= fastFloor(aspectFraction(Aspect.EARTH, aspects) * data.radius);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.NETHERRACK, 0, level, noise, cold, desert, life,
                aspects);
            level -= fastFloor(aspectFraction(Aspect.FIRE, aspects) * data.radius);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.ARMOR) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.OBSIDIAN, 0, level, noise, cold, desert, life,
                aspects);
            level -= fastFloor(aspectFraction(Aspect.ARMOR, aspects) * data.radius);
            drewAnything = true;
        }
        if (!drewAnything) {
            drawLayer(xCenter, yCenter, zCenter, data, world, ThBlocks.DUST.get(), 0, level, noise, cold, desert,
                life, aspects);
        }
    }

    static void drawLayer(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world, Block block,
            int md, int level, int noise, boolean cold, boolean desert, int life, AspectList aspects) {
        ResourceKey<net.minecraft.world.level.biome.Biome> bio = data.biome;
        double[] noiseData = null;
        if (noise != 0) {
            noiseData = PocketNoise.generateOctaves(rand, xCenter - data.radius, yCenter, zCenter - data.radius,
                2 * data.radius, 1, 2 * data.radius, noise / 25.0F, noise / 50.0F, noise / 25.0F);
        }
        for (int x = -data.radius; x <= data.radius; ++x) {
            for (int z = -data.radius; z <= data.radius; ++z) {
                int top = 0;
                if (noise != 0 && x + data.radius + (z * 2 * data.radius + data.radius * data.radius * 2) < noiseData.length) {
                    top = (int) (noiseData[x + data.radius + (z * 2 * data.radius + data.radius * data.radius * 2)] / 8.0D);
                }
                int offsettop = -level + yCenter;
                int ctop = x * x + z * z + offsettop * offsettop - data.radius * data.radius;
                int tmax = offsettop + (int) Math.sqrt(offsettop * offsettop - ctop);
                int offsetbottom = level - yCenter;
                int cbottom = x * x + z * z + offsetbottom * offsetbottom - data.radius * data.radius;
                int bmax = offsetbottom + (int) Math.sqrt(offsetbottom * offsetbottom - cbottom);
                if (top > tmax) {
                    top = tmax;
                }
                int y;
                for (int bottom = y = -bmax; y <= top; ++y) {
                    if (top != bottom && level + y > 0) {
                        if (y == top) {
                            if (block != null) {
                                setBlock(world, x + xCenter, y + level, z + zCenter, block, md);
                            }
                            if (block == Blocks.WATER) {
                                if (cold) {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, Blocks.ICE, 0);
                                } else {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, block, 0);
                                    if ((life > 40 || aspects.getAmount(Aspect.BEAST) > 0)
                                            && rand.nextInt(100) > 98 && creatures < MAX_CREATURES) {
                                        spawn(world, EntityTypes.SQUID, x + xCenter, y + level, z + zCenter, false);
                                    }
                                    if (life > 0 && rand.nextInt(100) > 98) {
                                        setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.LILY_PAD, 0);
                                    }
                                }
                            } else if (block == Blocks.DIRT) {
                                if (life > 0 && isAir(world, x + xCenter, y + level + 1, z + zCenter)) {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, Blocks.GRASS_BLOCK, 0);
                                    if (life >= 10) {
                                        if ((life >= 20 || aspects.getAmount(Aspect.CROP) > 0
                                                || aspects.getAmount(Aspect.HARVEST) > 0)
                                                && rand.nextInt(100) > 97) {
                                            switch (rand.nextInt(10)) {
                                                case 0 -> {
                                                    setBlock(world, x + xCenter, y + level, z + zCenter, Blocks.FARMLAND, 0);
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.CARROTS, 0);
                                                }
                                                case 1 -> {
                                                    setBlock(world, x + xCenter, y + level, z + zCenter, Blocks.FARMLAND, 0);
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.POTATOES, 0);
                                                }
                                                case 2 -> {
                                                    setBlock(world, x + xCenter, y + level, z + zCenter, Blocks.FARMLAND, 0);
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.WHEAT, 0);
                                                }
                                                case 3 -> {
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.MELON, 0);
                                                }
                                                case 4 -> {
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.PUMPKIN, 0);
                                                }
                                                case 5 -> {
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.SUGAR_CANE, 0);
                                                }
                                                case 6 -> {
                                                    setBlock(world, x + xCenter, y + level, z + zCenter, Blocks.MYCELIUM, 0);
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.RED_MUSHROOM, 0);
                                                }
                                                case 7 -> {
                                                    setBlock(world, x + xCenter, y + level, z + zCenter, Blocks.MYCELIUM, 0);
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.BROWN_MUSHROOM, 0);
                                                }
                                                case 8 -> {
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, customPlant(2), 0);
                                                }
                                                case 9 -> {
                                                    setBlock(world, x + xCenter, y + level + 1, z + zCenter, customPlant(5), 0);
                                                }
                                            }
                                        } else if ((life >= 15 || aspects.getAmount(Aspect.TREE) > 0)
                                                && rand.nextInt(100) > 97) {
                                            switch (rand.nextInt(10)) {
                                                case 0 -> setBlock(world, x + xCenter, y + level + 1, z + zCenter, sapling(0), 0);
                                                case 1 -> setBlock(world, x + xCenter, y + level + 1, z + zCenter, sapling(1), 0);
                                                case 2 -> setBlock(world, x + xCenter, y + level + 1, z + zCenter, sapling(2), 0);
                                                case 3 -> setBlock(world, x + xCenter, y + level + 1, z + zCenter, sapling(3), 0);
                                                case 4 -> setBlock(world, x + xCenter, y + level + 1, z + zCenter, sapling(4), 0);
                                                case 5 -> setBlock(world, x + xCenter, y + level + 1, z + zCenter, sapling(5), 0);
                                                case 6 -> {
                                                    if ((bio != null && bio == TaintBiomes.MAGICAL_FOREST)
                                                            || aspects.getAmount(Aspect.MAGIC) > 10
                                                            || aspects.getAmount(Aspect.AURA) > 5) {
                                                        if (rand.nextInt(4) == 0) {
                                                            setBlock(world, x + xCenter, y + level + 1, z + zCenter, customPlant(1), 0);
                                                        }
                                                    }
                                                }
                                                case 7 -> {
                                                    if (rand.nextInt(3) == 0) {
                                                        setBlock(world, x + xCenter, y + level + 1, z + zCenter, customPlant(0), 0);
                                                    }
                                                }
                                            }
                                        } else if ((life >= 10 || aspects.getAmount(Aspect.PLANT) > 0)
                                                && rand.nextInt(100) > 94) {
                                            int random = rand.nextInt(18);
                                            if (random <= 15) {
                                                setBlock(world, x + xCenter, y + level + 1, z + zCenter, flower(random), 0);
                                            } else if (random == 16) {
                                                // Legacy tallgrass metadata 0 = dead bush.
                                                setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.DEAD_BUSH, 0);
                                            } else {
                                                // Legacy tallgrass metadata 1 = ordinary grass.
                                                setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.SHORT_GRASS, 0);
                                            }
                                        }
                                    }
                                } else {
                                    setBlock(world, x + xCenter, y + level, z + zCenter, block, 0);
                                }
                            } else if (block == Blocks.SAND) {
                                setBlock(world, x + xCenter, y + level, z + zCenter, block, 0);
                                if (life > 10) {
                                    if (life >= 20 && rand.nextInt(100) > 98) {
                                        setBlock(world, x + xCenter, y + level + 1, z + zCenter, customPlant(3), 0);
                                    } else if (rand.nextInt(100) > 97) {
                                        setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.CACTUS, 0);
                                    }
                                }
                            } else if (block == taint(2) && rand.nextInt(300) > 298) {
                                if (rand.nextBoolean()) {
                                    for (int ecks = 0; ecks < 3; ++ecks) {
                                        for (int zee = 0; zee < 2; ++zee) {
                                            for (int why = 0; why < 2; ++why) {
                                                setBlock(world, x + xCenter + ecks, y + level + why, z + zCenter + zee,
                                                    ThBlocks.BRAIN.get(), 0);
                                            }
                                        }
                                    }
                                } else {
                                    for (int ecks = 0; ecks < 2; ++ecks) {
                                        for (int zee = 0; zee < 3; ++zee) {
                                            for (int why = 0; why < 2; ++why) {
                                                setBlock(world, x + xCenter + ecks, y + level + why, z + zCenter + zee,
                                                    ThBlocks.BRAIN.get(), 0);
                                            }
                                        }
                                    }
                                }
                            }
                            if (cold && block != null) {
                                setBlock(world, x + xCenter, y + level + 1, z + zCenter, Blocks.SNOW, 1);
                            }
                            if ((life >= 50 || aspects.getAmount(Aspect.BEAST) > 0) && rand.nextInt(100) > 98) {
                                Mob critter = null;
                                switch (rand.nextInt(7)) {
                                    case 0 -> critter = EntityTypes.COW.create(world, EntitySpawnReason.NATURAL);
                                    case 1 -> critter = EntityTypes.PIG.create(world, EntitySpawnReason.NATURAL);
                                    case 2 -> critter = EntityTypes.CHICKEN.create(world, EntitySpawnReason.NATURAL);
                                    case 3 -> critter = EntityTypes.SHEEP.create(world, EntitySpawnReason.NATURAL);
                                    case 4 -> critter = EntityTypes.HORSE.create(world, EntitySpawnReason.NATURAL);
                                    case 5 -> critter = EntityTypes.OCELOT.create(world, EntitySpawnReason.NATURAL);
                                    case 6 -> critter = EntityTypes.WOLF.create(world, EntitySpawnReason.NATURAL);
                                }
                                if (critter != null && creatures < MAX_CREATURES) {
                                    critter.setPos(x + xCenter + 0.5D, y + level + 1, z + zCenter + 0.5D);
                                    world.addFreshEntity(critter);
                                    ++creatures;
                                }
                            }
                            if ((aspects.getAmount(Aspect.POISON) > 0 || aspects.getAmount(Aspect.EXCHANGE) > 0
                                    || aspects.getAmount(Aspect.METAL) > 0
                                    || aspects.getAmount(Aspect.MECHANISM) > 0) && rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                spawn(world, ThEntities.MERCURIAL_SLIME.get(), x + xCenter, y + level + 1,
                                    z + zCenter, true);
                            }
                            if ((aspects.getAmount(Aspect.SLIME) > 0 || aspects.getAmount(Aspect.FLESH) > 0
                                    || aspects.getAmount(Aspect.HUNGER) > 0) && rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                spawn(world, ThEntities.MEAT_SLIME.get(), x + xCenter, y + level + 1, z + zCenter, true);
                            }
                            if (aspects.getAmount(Aspect.SLIME) > 0 && rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                spawn(world, EntityTypes.SLIME, x + xCenter, y + level + 1, z + zCenter, true);
                            }
                            if (aspects.getAmount(Aspect.ELDRITCH) > 0 && rand.nextInt(200) > 198) {
                                setBlock(world, x + xCenter, y + level, z + zCenter, cosmeticSolid(1), 0);
                                setBlock(world, x + xCenter, y + level + 1, z + zCenter, cosmeticSolid(0), 0);
                                setBlock(world, x + xCenter, y + level + 2, z + zCenter, cosmeticSolid(0), 0);
                                setBlock(world, x + xCenter, y + level + 3, z + zCenter, cosmeticSolid(0), 0);
                            }
                            if (aspects.getAmount(Aspect.CRAFT) > 0 && rand.nextInt(200) > 198) {
                                // Legacy blockStoneDevice metadata 1 maps to TC4R arcane_pedestal.
                                setBlock(world, x + xCenter, y + level + 1, z + zCenter,
                                    tc("arcane_pedestal", Blocks.CRAFTING_TABLE), 0);
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && rand.nextInt(100) > 98) {
                                setBlock(world, x + xCenter, y + level, z + zCenter, taintFiber(0), 0);
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && rand.nextInt(200) > 198 && creatures < MAX_CREATURES) {
                                spawnTc4Entity(world, "taintacle", x + xCenter, y + level + 1, z + zCenter);
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && rand.nextInt(200) > 198 && creatures < MAX_CREATURES) {
                                spawnTc4Entity(world, "taint_spore_swarmer", x + xCenter, y + level + 1, z + zCenter);
                            }
                        } else if (block != null) {
                            setBlock(world, x + xCenter, y + level, z + zCenter, block, md);
                        }
                    }
                }
            }
        }
    }

    private static void spawn(ServerLevel world, EntityType<? extends Mob> type, int x, int y, int z,
            boolean persistence) {
        Mob mob = type.create(world, EntitySpawnReason.NATURAL);
        if (mob != null) {
            mob.setPos(x + 0.5D, y, z + 0.5D);
            if (persistence) {
                mob.setPersistenceRequired();
            }
            world.addFreshEntity(mob);
            ++creatures;
        }
    }

    private static void spawnTc4Entity(ServerLevel world, String name, int x, int y, int z) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.fromNamespaceAndPath("thaumcraft", name));
        if (type != null) {
            Entity entity = type.create(world, EntitySpawnReason.NATURAL);
            if (entity instanceof Mob mob) {
                mob.setPos(x + 0.5D, y, z + 0.5D);
                mob.setPersistenceRequired();
                world.addFreshEntity(mob);
                ++creatures;
            }
        }
    }

    static void drawCaves(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects, int noise) {
        float solidity = 0.5F;
        solidity -= aspectFraction(Aspect.AIR, aspects) * 0.5F;
        solidity += aspectFraction(Aspect.ORDER, aspects) * 0.25F;
        solidity -= aspectFraction(Aspect.VOID, aspects);
        solidity -= aspectFraction(Aspect.FLIGHT, aspects) * 0.5F;
        solidity += aspectFraction(Aspect.CRAFT, aspects) * 0.5F;
        solidity -= aspectFraction(Aspect.TRAP, aspects) * 0.5F;
        if (solidity <= 0.0F) {
            solidity = 0.0F;
        } else if (solidity > 1.0F) {
            solidity = 1.0F;
        }
        int numCaves = (int) (data.radius * data.radius * data.radius / 10000 * (solidity + 0.1F));
        HashMap<Aspect, Number> dressing = new HashMap<>();
        if (aspects.getAmount(Aspect.EARTH) > 0) {
            dressing.put(Aspect.EARTH, aspectFraction(Aspect.EARTH, aspects));
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            dressing.put(Aspect.FIRE, aspectFraction(Aspect.FIRE, aspects));
        }
        if (aspects.getAmount(Aspect.ORDER) > 0) {
            dressing.put(Aspect.ORDER, aspectFraction(Aspect.ORDER, aspects));
        }
        if (aspects.getAmount(Aspect.AIR) > 0) {
            dressing.put(Aspect.AIR, aspectFraction(Aspect.AIR, aspects));
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            dressing.put(Aspect.WATER, aspectFraction(Aspect.WATER, aspects));
        }
        if (aspects.getAmount(Aspect.ENTROPY) > 0) {
            dressing.put(Aspect.ENTROPY, aspectFraction(Aspect.ENTROPY, aspects));
        }
        if (aspects.getAmount(Aspect.METAL) > 0) {
            dressing.put(Aspect.METAL, aspectFraction(Aspect.METAL, aspects));
        }
        if (aspects.getAmount(Aspect.DEATH) > 0) {
            dressing.put(Aspect.DEATH, aspectFraction(Aspect.DEATH, aspects));
        }
        if (aspects.getAmount(Aspect.UNDEAD) > 0) {
            dressing.put(Aspect.UNDEAD, aspectFraction(Aspect.UNDEAD, aspects));
        }
        if (aspects.getAmount(Aspect.SENSES) > 0) {
            dressing.put(Aspect.SENSES, aspectFraction(Aspect.SENSES, aspects));
        }
        if (aspects.getAmount(Aspect.GREED) > 0) {
            dressing.put(Aspect.GREED, aspectFraction(Aspect.GREED, aspects));
        }
        for (int i = 0; i < numCaves; ++i) {
            drawACave(xCenter, yCenter, zCenter, data, world, aspects, noise, Blocks.AIR,
                rand.nextInt(10) + 10, rand.nextInt(10) + 10,
                rand.nextInt((int) (data.radius * 1.5F)) - (int) (data.radius * 0.75F),
                -rand.nextInt((int) (data.radius * 0.5F)),
                rand.nextInt((int) (data.radius * 1.5F)) - (int) (data.radius * 0.75F), dressing);
        }
    }

    public static void drawACave(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects, int noise, Block block, int xSize, int zSize, int xOffset, int yOffset, int zOffset,
            HashMap<Aspect, Number> dressing) {
        double[] noiseData = PocketNoise.generateOctaves(rand, xCenter - xSize + xOffset, yCenter + yOffset,
            zCenter - zSize + zOffset, xSize, 16, zSize, noise / 10.0F, noise / 25.0F, noise / 10.0F);
        int avg = getAvg(noiseData);
        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                for (int y = 0; y < 16; ++y) {
                    int px = xCenter - xSize + xOffset + x;
                    int py = yCenter + yOffset + y;
                    int pz = zCenter - zSize + zOffset + z;
                    if (noiseData[x + xSize * z + y * xSize * zSize] > avg && getBlock(world, px, py, pz) != Blocks.WATER) {
                        if (!isAir(world, px, py - 1, pz) && rand.nextInt(50) == 49) {
                            float which = rand.nextFloat();
                            Aspect whichAspect = null;
                            Iterator<Aspect> it = dressing.keySet().iterator();
                            boolean doDressing = false;
                            while (it.hasNext()) {
                                whichAspect = it.next();
                                if (which <= dressing.get(whichAspect).floatValue()) {
                                    doDressing = true;
                                    break;
                                }
                                which -= dressing.get(whichAspect).floatValue();
                            }
                            if (doDressing && whichAspect != null && !isAir(world, px, py, pz)
                                    && getBlock(world, px, py, pz) != Blocks.WATER) {
                                Block dressingBlock;
                                if (whichAspect == Aspect.EARTH) {
                                    dressingBlock = tc("earth_infused_stone", Blocks.STONE);
                                } else if (whichAspect == Aspect.AIR) {
                                    dressingBlock = tc("air_infused_stone", Blocks.STONE);
                                } else if (whichAspect == Aspect.WATER) {
                                    dressingBlock = tc("water_infused_stone", Blocks.STONE);
                                } else if (whichAspect == Aspect.FIRE) {
                                    dressingBlock = tc("fire_infused_stone", Blocks.STONE);
                                } else if (whichAspect == Aspect.ORDER) {
                                    dressingBlock = tc("order_infused_stone", Blocks.STONE);
                                } else if (whichAspect == Aspect.ENTROPY) {
                                    dressingBlock = tc("entropy_infused_stone", Blocks.STONE);
                                } else if (whichAspect == Aspect.DEATH || whichAspect == Aspect.UNDEAD) {
                                    dressingBlock = Blocks.SKELETON_SKULL;
                                } else if (whichAspect == Aspect.SENSES) {
                                    dressingBlock = Blocks.LAPIS_BLOCK;
                                } else if (whichAspect == Aspect.GREED) {
                                    dressingBlock = rand.nextBoolean() ? Blocks.GOLD_BLOCK : Blocks.EMERALD_BLOCK;
                                } else if (whichAspect == Aspect.METAL) {
                                    dressingBlock = rand.nextBoolean() ? Blocks.GOLD_BLOCK : Blocks.IRON_BLOCK;
                                } else {
                                    dressingBlock = null;
                                }
                                if (dressingBlock != null) {
                                    setBlock(world, px, py, pz, dressingBlock, 0);
                                }
                            }
                        } else {
                            setBlock(world, px, py, pz, Blocks.AIR, 0);
                        }
                    }
                }
            }
        }
    }

    public static void drawRavines(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects, int noise) {
    }

    public static void drawClouds(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects, int noise) {
    }

    public static void drawSurfaceFeatures(int xCenter, int yCenter, int zCenter, PocketPlaneData data,
            ServerLevel world, AspectList aspects, int noise, int life) {
    }

    public static void drawUndergroundFeatures(int xCenter, int yCenter, int zCenter, PocketPlaneData data,
            ServerLevel world, AspectList aspects, int noise, int life) {
    }

    public static void drawLeviathanBones(int xCenter, int yCenter, int zCenter, PocketPlaneData data,
            ServerLevel world, AspectList aspects, int noise) {
    }

    public static int getAvg(double[] array) {
        long x = 0L;
        for (double v : array) {
            x += (long) v;
        }
        x /= array.length;
        return (int) x;
    }

    static void drawPockets(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects, int noise) {
        int numPockets = data.radius * data.radius * data.radius / 200;
        HashMap<Aspect, Number> probabilities = new HashMap<>();
        for (Aspect aspect : new Aspect[] { Aspect.EARTH, Aspect.FIRE, Aspect.ORDER, Aspect.AIR, Aspect.WATER,
            Aspect.ENTROPY, Aspect.METAL, Aspect.CRYSTAL, Aspect.SOUL, Aspect.MAGIC, Aspect.AURA, Aspect.ENERGY,
            Aspect.TREE, Aspect.FLESH, Aspect.SENSES, Aspect.MINE, Aspect.TOOL, Aspect.GREED, Aspect.TAINT,
            Aspect.POISON, Aspect.EXCHANGE, Aspect.MIND, Aspect.MAN, Aspect.HUNGER, Aspect.CRAFT }) {
            if (aspects.getAmount(aspect) > 0) {
                probabilities.put(aspect, aspectFraction(aspect, aspects));
            }
        }
        Block block = Blocks.AIR;
        for (int i = 0; i < numPockets; ++i) {
            float which = rand.nextFloat();
            Aspect whichAspect = null;
            Iterator<Aspect> it = probabilities.keySet().iterator();
            boolean doDressing = false;
            while (it.hasNext()) {
                whichAspect = it.next();
                if (which <= probabilities.get(whichAspect).floatValue()) {
                    doDressing = true;
                    break;
                }
                which -= probabilities.get(whichAspect).floatValue();
            }
            if (doDressing && whichAspect != null) {
                if (whichAspect == Aspect.EARTH) {
                    block = infusedOre(4);
                } else if (whichAspect == Aspect.AIR) {
                    block = infusedOre(1);
                } else if (whichAspect == Aspect.WATER) {
                    block = infusedOre(3);
                } else if (whichAspect == Aspect.FIRE) {
                    block = rand.nextBoolean() ? infusedOre(2) : Blocks.LAVA;
                } else if (whichAspect == Aspect.ORDER) {
                    block = infusedOre(5);
                } else if (whichAspect == Aspect.ENTROPY) {
                    block = infusedOre(6);
                } else if (whichAspect == Aspect.METAL) {
                    if (rand.nextFloat() <= 0.5D) {
                        block = Blocks.IRON_ORE;
                    } else if (rand.nextFloat() <= 0.8D) {
                        block = Blocks.GOLD_ORE;
                    } else {
                        block = infusedOre(0);
                    }
                } else if (whichAspect == Aspect.MAGIC || whichAspect == Aspect.AURA) {
                    block = infusedOre(rand.nextInt(6) + 1);
                } else if (whichAspect == Aspect.CRYSTAL) {
                    block = Blocks.QUARTZ_BLOCK;
                } else if (whichAspect == Aspect.SOUL) {
                    block = Blocks.SOUL_SAND;
                } else if (whichAspect == Aspect.ENERGY) {
                    block = Blocks.REDSTONE_BLOCK;
                } else if (whichAspect == Aspect.TREE) {
                    block = rand.nextBoolean() ? Blocks.OAK_LOG : infusedOre(7);
                } else if (whichAspect == Aspect.FLESH) {
                    block = taint(2);
                } else if (whichAspect == Aspect.SENSES) {
                    block = Blocks.LAPIS_ORE;
                } else if (whichAspect == Aspect.MINE || whichAspect == Aspect.TOOL) {
                    float f = rand.nextFloat();
                    if (f < 0.4F) {
                        block = Blocks.IRON_ORE;
                    } else if (f < 0.5F) {
                        block = Blocks.GOLD_ORE;
                    } else if (f < 0.6F) {
                        block = Blocks.DIAMOND_ORE;
                    } else if (f < 0.7F) {
                        block = Blocks.EMERALD_ORE;
                    } else if (f < 0.8F) {
                        block = Blocks.LAPIS_ORE;
                    } else {
                        block = infusedOre(rand.nextInt(8));
                    }
                } else if (whichAspect == Aspect.GREED) {
                    float f = rand.nextFloat();
                    if (f < 0.4F) {
                        block = Blocks.GOLD_ORE;
                    } else if (f < 0.6F) {
                        block = Blocks.DIAMOND_ORE;
                    } else if (f < 0.8F) {
                        block = Blocks.EMERALD_ORE;
                    } else {
                        block = Blocks.LAPIS_ORE;
                    }
                } else if (whichAspect == Aspect.TAINT) {
                    block = taint(0);
                } else if (whichAspect == Aspect.POISON) {
                    block = tc("liquid_death", Blocks.LAVA);
                } else if (whichAspect == Aspect.EXCHANGE) {
                    block = infusedOre(0);
                } else if (whichAspect == Aspect.MIND) {
                    block = rand.nextBoolean() ? Blocks.BOOKSHELF : ThBlocks.BRAIN.get();
                } else if (whichAspect == Aspect.MAN) {
                    int man = rand.nextInt(3);
                    if (man == 0) {
                        block = Blocks.BOOKSHELF;
                    } else if (man == 1) {
                        block = ThBlocks.BRAIN.get();
                    } else {
                        block = taint(2);
                    }
                } else if (whichAspect == Aspect.HUNGER) {
                    block = taint(2);
                } else if (whichAspect == Aspect.CRAFT) {
                    block = Blocks.CRAFTING_TABLE;
                }
            }
            drawAPocket(xCenter, yCenter, zCenter, data, world, aspects, noise, block,
                rand.nextInt(3) + 1, rand.nextInt(3) + 1,
                rand.nextInt((int) (data.radius * 2.0F)) - (int) (data.radius * 1.0F),
                -rand.nextInt((int) (data.radius * 1.0F)),
                rand.nextInt((int) (data.radius * 2.0F)) - (int) (data.radius * 1.0F));
        }
    }

    public static void drawAPocket(int xCenter, int yCenter, int zCenter, PocketPlaneData data, ServerLevel world,
            AspectList aspects, int noise, Block block, int xSize, int zSize, int xOffset, int yOffset, int zOffset) {
        double[] noiseDataTop = PocketNoise.generateOctaves(rand, xCenter - xSize + xOffset, yCenter + yOffset,
            zCenter - zSize + zOffset, xSize, 1, zSize, noise / 50.0D, noise / 25.0D, noise / 50.0D);
        double[] noiseDataBottom = PocketNoise.generateOctaves(rand, xCenter - xSize + xOffset, yCenter + yOffset,
            zCenter - zSize + zOffset, xSize, 1, zSize, noise / 50.0D, noise / 25.0D, noise / 50.0D);
        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                int y;
                int top = (int) (noiseDataTop[x + z * xSize] / 16.0D) + 1;
                int bottom = y = (int) (noiseDataBottom[x + z * xSize] / 16.0D) - 1;
                for (; y < top; ++y) {
                    int px = xCenter - xSize + xOffset + x;
                    int py = yCenter + yOffset + y;
                    int pz = zCenter - zSize + zOffset + z;
                    if (!isAir(world, px, py, pz) && getBlock(world, px, py, pz) != Blocks.WATER) {
                        setBlock(world, px, py, pz, block, 0);
                    }
                }
            }
        }
    }

    static ResourceKey<net.minecraft.world.level.biome.Biome> setBiome(int xCenter, int yCenter, int zCenter,
            PocketPlaneData data, ServerLevel world, AspectList aspects) {
        ResourceKey<net.minecraft.world.level.biome.Biome> bio = Biomes.PLAINS;
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            bio = TaintBiomes.TAINTED_LAND;
        } else if (aspects.getAmount(Aspect.ELDRITCH) > 0 || aspects.getAmount(Aspect.UNDEAD) > 0) {
            bio = TaintBiomes.EERIE;
        } else if (aspects.getAmount(Aspect.MAGIC) > 0 || aspects.getAmount(Aspect.AURA) > 0) {
            bio = TaintBiomes.MAGICAL_FOREST;
        } else {
            float temp = 0.5F;
            float moist = 0.5F;
            temp += aspectFraction(Aspect.FIRE, aspects);
            temp -= aspectFraction(Aspect.WATER, aspects) * 0.5F;
            temp -= aspectFraction(Aspect.ORDER, aspects) * 0.5F;
            temp -= aspectFraction(Aspect.VOID, aspects) * 0.5F;
            temp += aspectFraction(Aspect.LIGHT, aspects) * 0.5F;
            temp -= aspectFraction(Aspect.WEATHER, aspects) * 0.25F;
            temp += aspectFraction(Aspect.MOTION, aspects) * 0.5F;
            temp -= aspectFraction(Aspect.COLD, aspects);
            temp += aspectFraction(Aspect.ENERGY, aspects) * 0.5F;
            temp -= aspectFraction(Aspect.DARKNESS, aspects) * 0.25F;
            if (temp < 0.0F) {
                temp = 0.0F;
            } else if (temp > 1.0F) {
                temp = 1.0F;
            }
            moist += aspectFraction(Aspect.WATER, aspects);
            moist -= aspectFraction(Aspect.FIRE, aspects) * 0.5F;
            moist -= aspectFraction(Aspect.ENTROPY, aspects) * 0.25F;
            moist -= aspectFraction(Aspect.VOID, aspects) * 0.25F;
            moist += aspectFraction(Aspect.WEATHER, aspects) * 0.5F;
            moist += aspectFraction(Aspect.SLIME, aspects) * 0.5F;
            if (moist < 0.0F) {
                moist = 0.0F;
            } else if (moist > 1.0F) {
                moist = 1.0F;
            }
            if (temp > 0.8D) {
                bio = Biomes.DESERT;
            } else if (temp > 0.5D) {
                bio = moist < 0.4D ? Biomes.SAVANNA : Biomes.JUNGLE;
            } else if (temp > 0.2D) {
                bio = moist < 0.4D ? Biomes.PLAINS : Biomes.FOREST;
            } else {
                bio = Biomes.SNOWY_PLAINS;
            }
        }
        for (int x = -data.radius; x <= data.radius; ++x) {
            for (int z = -data.radius; z <= data.radius; ++z) {
                world.getChunk((x + xCenter) >> 4, (z + zCenter) >> 4, ChunkStatus.FULL, true);
                TaintBiomes.set(world, new BlockPos(x + xCenter, yCenter, z + zCenter), bio);
            }
        }
        return bio;
    }

    static void drawCircle(int x0, int y0, int z0, int y1, int radius, int error0, Block block, ServerLevel world) {
        int x = radius;
        int z = 0;
        int radiusError = error0;
        while (x >= z) {
            setBlock(world, x0 + x, y0 + y1, z0 + z, block, 0);
            setBlock(world, x0 + z, y0 + y1, z0 + x, block, 0);
            setBlock(world, x0 - x, y0 + y1, z0 + z, block, 0);
            setBlock(world, x0 - z, y0 + y1, z0 + x, block, 0);
            setBlock(world, x0 - x, y0 + y1, z0 - z, block, 0);
            setBlock(world, x0 - z, y0 + y1, z0 - x, block, 0);
            setBlock(world, x0 + x, y0 + y1, z0 - z, block, 0);
            setBlock(world, x0 + z, y0 + y1, z0 - x, block, 0);
            setBlock(world, x0 + x, y0 - y1, z0 + z, block, 0);
            setBlock(world, x0 + z, y0 - y1, z0 + x, block, 0);
            setBlock(world, x0 - x, y0 - y1, z0 + z, block, 0);
            setBlock(world, x0 - z, y0 - y1, z0 + x, block, 0);
            setBlock(world, x0 - x, y0 - y1, z0 - z, block, 0);
            setBlock(world, x0 - z, y0 - y1, z0 - x, block, 0);
            setBlock(world, x0 + x, y0 - y1, z0 - z, block, 0);
            setBlock(world, x0 + z, y0 - y1, z0 - x, block, 0);
            setBlock(world, x0 + y1, y0 + x, z0 + z, block, 0);
            setBlock(world, x0 + z, y0 + x, z0 + y1, block, 0);
            setBlock(world, x0 - y1, y0 + x, z0 + z, block, 0);
            setBlock(world, x0 - z, y0 + x, z0 + y1, block, 0);
            setBlock(world, x0 + y1, y0 + x, z0 - z, block, 0);
            setBlock(world, x0 + z, y0 + x, z0 - y1, block, 0);
            setBlock(world, x0 - y1, y0 + x, z0 - z, block, 0);
            setBlock(world, x0 - z, y0 + x, z0 - y1, block, 0);
            setBlock(world, x0 + y1, y0 - x, z0 + z, block, 0);
            setBlock(world, x0 + z, y0 - x, z0 + y1, block, 0);
            setBlock(world, x0 - y1, y0 - x, z0 + z, block, 0);
            setBlock(world, x0 - z, y0 - x, z0 + y1, block, 0);
            setBlock(world, x0 + y1, y0 - x, z0 - z, block, 0);
            setBlock(world, x0 + z, y0 - x, z0 - y1, block, 0);
            setBlock(world, x0 - y1, y0 - x, z0 - z, block, 0);
            setBlock(world, x0 - z, y0 - x, z0 - y1, block, 0);
            ++z;
            if (radiusError < 0) {
                radiusError += 2 * z + 1;
            } else {
                --x;
                radiusError += 2 * (z - x + 1);
            }
        }
    }

    public static void drawSphere(int x0, int y0, int z0, int radius, Block block, ServerLevel world) {
        int x = radius;
        int y = 0;
        int radiusError = 1 - x;
        while (x >= y) {
            drawCircle(x0, y0, z0, y, x, radiusError, block, world);
            ++y;
            if (radiusError < 0) {
                radiusError += 2 * y + 1;
            } else {
                --x;
                radiusError += 2 * (y - x + 1);
            }
        }
    }

    static void drawSphere(int x0, int y0, int z0, int radius, Block block, int md, ServerLevel world) {
        drawSphere(x0, y0, z0, radius, block, world);
    }
}
