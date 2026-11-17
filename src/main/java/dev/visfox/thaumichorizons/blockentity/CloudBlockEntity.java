package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.block.CloudBlock;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;

/**
 * Server-side weather processing for legacy cloud variants.
 *
 * The original cloud used the Overworld weather state for every dimension and
 * periodically applied its precipitation effect to the first block below it.
 */
public final class CloudBlockEntity extends BlockEntity {
    private int dropTimer = -1;
    private int weatherTicks;

    private static final int[] DROP_TIMERS = {-1, -1, 120, -1, 480, 480, 80, 360, 280, 550};

    public CloudBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.CLOUD.get(), pos, state);
        int type = state.getValue(CloudBlock.TYPE);
        dropTimer = DROP_TIMERS[type];
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CloudBlockEntity cloud) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean raining = serverLevel.getServer().overworld().isRaining();
        if (raining) {
            if (++cloud.weatherTicks % 10 == 0) {
                cloud.applyPrecipitation(serverLevel, state.getValue(CloudBlock.TYPE));
            }
            cloud.tickDropTimer(serverLevel, state.getValue(CloudBlock.TYPE));
            cloud.applyEntityEffects(serverLevel, state.getValue(CloudBlock.TYPE));
        }
    }


    /**
     * Timed effects from the original TileCloud. The timer advances only while
     * the Overworld reports rain, matching the legacy rain-state gate.
     */
    private void tickDropTimer(ServerLevel level, int type) {
        if (dropTimer < 0 || type < 0 || type >= DROP_TIMERS.length) {
            return;
        }
        if (--dropTimer > 0) {
            return;
        }

        int base = DROP_TIMERS[type];
        if (base > 0) {
            dropTimer = base / 2 + level.getRandom().nextInt(base / 2);
        }

        BlockPos target = findGroundBelow(level);
        switch (type) {
            // Type 2 uses the legacy finite-length lightning entity. It is
            // intentionally deferred until that custom entity is ported.
            case 5 -> {
                ItemStack drop = switch (level.getRandom().nextInt(10)) {
                    case 0 -> new ItemStack(Items.BEEF);
                    case 1 -> new ItemStack(Items.PORKCHOP);
                    case 2 -> new ItemStack(Items.CHICKEN);
                    case 3 -> new ItemStack(switch (level.getRandom().nextInt(3)) {
                        case 0 -> Items.COD;
                        case 1 -> Items.SALMON;
                        default -> Items.TROPICAL_FISH;
                    });
                    default -> new ItemStack(ThItems.MEAT.get());
                };
                ItemEntity item = new ItemEntity(level, worldPosition.getX() + level.getRandom().nextDouble(),
                    worldPosition.getY() + 0.5, worldPosition.getZ() + level.getRandom().nextDouble(), drop);
                level.addFreshEntity(item);
            }
            case 8 -> applyGrowthDrop(level, target);
        }
        setChanged();
    }


    /**
     * Applies the cloud's continuous entity effects over the vertical column
     * between the cloud and the first solid block below it.
     */
    private void applyEntityEffects(ServerLevel level, int type) {
        BlockPos ground = findGroundBelow(level);
        AABB column = new AABB(worldPosition.getX(), ground.getY(), worldPosition.getZ(),
            worldPosition.getX() + 1.0, worldPosition.getY(), worldPosition.getZ() + 1.0);
        boolean warm = level.getBiome(worldPosition).value().getBaseTemperature() >= 0.15F;

        for (Entity entity : level.getEntitiesOfClass(Entity.class, column)) {
            if (type == 1 || type == 4) {
                entity.setRemainingFireTicks(6 * 20);
                continue;
            }
            if (type == 3) {
                entity.hurt(level.damageSources().magic(), 1.0F);
                continue;
            }
            if (!warm) {
                continue;
            }
            String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
            if (entityId.equals("enderman") || entityId.equals("blaze")) {
                entity.hurt(level.damageSources().drown(), 1.0F);
            }
            if (entity.isOnFire()) {
                entity.setRemainingFireTicks(0);
            }
            // Type 8's cow-to-mooshroom conversion is kept separate from
            // generic entity effects until the target entity factory is verified.
        }
    }

    private void applyGrowthDrop(ServerLevel level, BlockPos ground) {
        BlockPos above = ground.above();
        if (!level.isEmptyBlock(above)) {
            return;
        }

        BlockState soil = level.getBlockState(ground);
        var random = level.getRandom();
        if (soil.is(Blocks.FARMLAND)) {
            switch (random.nextInt(8)) {
                case 1 -> level.setBlock(above, Blocks.MELON_STEM.defaultBlockState(), 3);
                case 2 -> level.setBlock(above, Blocks.PUMPKIN_STEM.defaultBlockState(), 3);
                case 3 -> level.setBlock(above, Blocks.CARROTS.defaultBlockState(), 3);
                case 4 -> level.setBlock(above, Blocks.POTATOES.defaultBlockState(), 3);
                default -> level.setBlock(above, Blocks.WHEAT.defaultBlockState(), 3);
            }
            return;
        }
        if (soil.is(Blocks.DIRT)) {
            level.setBlock(ground, random.nextInt(10) == 4
                ? Blocks.MYCELIUM.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            return;
        }
        if (soil.is(Blocks.STONE) || soil.is(Blocks.MYCELIUM)) {
            level.setBlock(above, (random.nextInt(3) == 1
                ? Blocks.BROWN_MUSHROOM : Blocks.RED_MUSHROOM).defaultBlockState(), 3);
            return;
        }
        if (soil.is(Blocks.SAND)) {
            level.setBlock(above, random.nextInt(10) == 4
                ? Blocks.DEAD_BUSH.defaultBlockState() : Blocks.CACTUS.defaultBlockState(), 3);
            return;
        }
        if (soil.is(Blocks.SOUL_SAND)) {
            level.setBlock(above, Blocks.NETHER_WART.defaultBlockState(), 3);
            return;
        }
        if (soil.is(Blocks.GRASS_BLOCK)) {
            int roll = random.nextInt(1000);
            if (roll < 750) {
                level.setBlock(above, switch (random.nextInt(6)) {
                    case 0 -> Blocks.SHORT_GRASS.defaultBlockState();
                    case 1 -> Blocks.FERN.defaultBlockState();
                    case 2 -> Blocks.DANDELION.defaultBlockState();
                    case 3 -> Blocks.POPPY.defaultBlockState();
                    case 4 -> Blocks.AZURE_BLUET.defaultBlockState();
                    default -> Blocks.OXEYE_DAISY.defaultBlockState();
                }, 3);
            } else if (roll < 950) {
                level.setBlock(above, switch (random.nextInt(6)) {
                    case 0 -> Blocks.OAK_SAPLING.defaultBlockState();
                    case 1 -> Blocks.SPRUCE_SAPLING.defaultBlockState();
                    case 2 -> Blocks.BIRCH_SAPLING.defaultBlockState();
                    case 3 -> Blocks.JUNGLE_SAPLING.defaultBlockState();
                    case 4 -> Blocks.ACACIA_SAPLING.defaultBlockState();
                    default -> Blocks.DARK_OAK_SAPLING.defaultBlockState();
                }, 3);
            } else if (roll < 975) {
                level.setBlock(above, Blocks.LILAC.defaultBlockState(), 3);
            } else {
                level.setBlock(above, Blocks.ROSE_BUSH.defaultBlockState(), 3);
            }
        }
    }

    private BlockPos findGroundBelow(ServerLevel level) {
        BlockPos.MutableBlockPos cursor = worldPosition.mutable();
        while (cursor.getY() > level.getMinY() && level.getBlockState(cursor).getCollisionShape(level, cursor).isEmpty()) {
            cursor.move(0, -1, 0);
        }
        return cursor.immutable();
    }

    private void applyPrecipitation(ServerLevel level, int type) {
        BlockHitResult hit = level.clip(new ClipContext(
            new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() - 0.5, worldPosition.getZ() + 0.5),
            new Vec3(worldPosition.getX() + 0.5, level.getMinY(), worldPosition.getZ() + 0.5),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, net.minecraft.world.phys.shapes.CollisionContext.empty()));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos targetPos = hit.getBlockPos();
        BlockState target = level.getBlockState(targetPos);
        int meltable = ThItems.FOCUS_LIQUEFACTION.get().isMeltableBlock(target);
        boolean flammable = target.isFlammable(level, targetPos, Direction.UP);

        if (type == 1 || type == 4) {
            switch (meltable) {
                case 1 -> level.removeBlock(targetPos, false);
                case 2 -> {
                    if (level.dimension() != Level.NETHER) {
                        level.setBlock(targetPos, Blocks.WATER.defaultBlockState(), 3);
                    } else {
                        level.removeBlock(targetPos, false);
                    }
                }
                case 3 -> level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                case 4 -> TntBlock.prime(level, targetPos);
                case 6 -> level.setBlock(targetPos, Blocks.LAVA.defaultBlockState(), 3);
                default -> {
                    if (flammable) {
                        level.setBlock(targetPos.above(), Blocks.FIRE.defaultBlockState(), 3);
                    }
                }
            }
            return;
        }

        if (type == 3) {
            if (meltable == 1 || (meltable == 0 && flammable)) {
                level.removeBlock(targetPos, false);
            } else if (meltable == 3) {
                level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
            }
            return;
        }

        BlockPos above = targetPos.above();
        if (level.getBiome(targetPos).value().coldEnoughToSnow(targetPos, level.getSeaLevel())) {
            if (target.isSolidRender() && level.isEmptyBlock(above)) {
                level.setBlock(above, Blocks.SNOW.defaultBlockState(), 3);
            }
        } else {
            if (target.is(Blocks.FARMLAND) && target.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.MOISTURE)
                    && target.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.MOISTURE) != 7) {
                level.setBlock(targetPos, target.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.MOISTURE, 7), 3);
            } else if (level.getBlockState(above).is(Blocks.FIRE)) {
                level.removeBlock(above, false);
            }
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        dropTimer = input.getIntOr("dropTimer", -1);
        weatherTicks = input.getIntOr("weatherTicks", 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("dropTimer", dropTimer);
        output.putInt("weatherTicks", weatherTicks);
    }
}
