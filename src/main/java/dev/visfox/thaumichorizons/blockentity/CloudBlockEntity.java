package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import dev.visfox.thaumichorizons.entity.InvulnerableItemEntity;
import dev.visfox.thaumichorizons.item.focus.FocusAnimationItem;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import thaumcraft.aspect.Aspect;

/**
 * Server-side weather and periodic effects for the ten legacy cloud variants.
 * The cloud still uses Overworld rain state in every dimension, as TileCloud did.
 */
public final class CloudBlockEntity extends BlockEntity {
    private static final int[] DROP_TIMERS = {-1, -1, 120, -1, 480, 480, 80, 360, 280, 550};

    private int dropTimer = -1;
    private int weatherTicks;
    private boolean raining;
    private int columnDepth = 256;

    public CloudBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.CLOUD.get(), pos, state);
        dropTimer = DROP_TIMERS[state.getValue(dev.visfox.thaumichorizons.block.CloudBlock.TYPE)];
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        loadAdditional(ValueInput.of(tag, registries));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        saveAdditional(ValueOutput.of(tag, registries));
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CloudBlockEntity cloud) {
        int type = state.getValue(dev.visfox.thaumichorizons.block.CloudBlock.TYPE);
        if (level.isClientSide()) {
            cloud.renderFiniteLightning(level, pos, type);
            return;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        boolean overworldRaining = serverLevel.getServer().overworld().isRaining();
        if (++cloud.weatherTicks % 100 == 0 && cloud.raining != overworldRaining) {
            cloud.raining = overworldRaining;
            cloud.syncRenderState(serverLevel);
        }
        if (!overworldRaining) {
            return;
        }

        if (cloud.weatherTicks % 10 == 0) {
            cloud.refreshColumnDepth(serverLevel);
            cloud.applyPrecipitation(serverLevel, type);
        }
        cloud.tickDropTimer(serverLevel, type);
        cloud.applyEntityEffects(serverLevel, type);
    }

    public boolean isRaining() {
        return raining;
    }

    public int getColumnDepth() {
        return columnDepth;
    }

    private void refreshColumnDepth(ServerLevel level) {
        BlockPos pos = worldPosition;
        BlockHitResult hit = level.clip(new ClipContext(
            new Vec3(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5),
            new Vec3(pos.getX() + 0.5, pos.getY() - 256.0, pos.getZ() + 0.5),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
            net.minecraft.world.phys.shapes.CollisionContext.empty()
        ));
        int nextDepth = hit.getType() == HitResult.Type.BLOCK
            ? Math.max(1, Math.min(256, pos.getY() - hit.getBlockPos().getY()))
            : 256;
        if (nextDepth != columnDepth) {
            columnDepth = nextDepth;
            syncRenderState(level);
        }
    }

    private void syncRenderState(ServerLevel level) {
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    /**
     * TileCloud's finite bolt draws an arc from the cloud down to the impact
     * block on every client tick while its vanilla lightning entity is alive.
     */
    private void renderFiniteLightning(Level level, BlockPos pos, int type) {
        if ((type != 2 && type != 9) || !TcReforgedPort.fx.isAvailable()) {
            return;
        }

        AABB column = new AABB(pos.getX(), level.getMinBuildHeight(), pos.getZ(),
            pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
        for (LightningBolt bolt : level.getEntitiesOfClass(LightningBolt.class, column)) {
            BlockPos strike = bolt.blockPosition();
            if (strike.getX() != pos.getX() || strike.getZ() != pos.getZ()) {
                continue;
            }
            var random = level.getRandom();
            Vec3 from = new Vec3(
                pos.getX() + random.nextDouble(),
                pos.getY() + 0.5,
                pos.getZ() + random.nextDouble()
            );
            Vec3 to = new Vec3(
                pos.getX() + random.nextDouble(),
                strike.getY() + 1.0,
                pos.getZ() + random.nextDouble()
            );
            TcReforgedPort.fx.arcLightning(level, from, to, 0xFFFFFF);
        }
    }

    /**
     * The old timer advanced only while the cloud's shared rain state was true.
     * Each variant re-arms with a random delay in the upper half of its base interval.
     */
    private void tickDropTimer(ServerLevel level, int type) {
        if (dropTimer < 0 || type < 0 || type >= DROP_TIMERS.length) {
            return;
        }
        if (--dropTimer > 0) {
            return;
        }

        int base = DROP_TIMERS[type];
        dropTimer = base / 2 + level.getRandom().nextInt(base / 2);
        BlockPos ground = findGroundBelow(level);

        switch (type) {
            case 2 -> spawnLightning(level, ground);
            case 4 -> dropAlloyNugget(level);
            case 5 -> dropMeat(level);
            case 6 -> spawnAspectDrop(level);
            case 7 -> spawnExperienceDrop(level);
            case 8 -> applyGrowthDrop(level, ground);
            case 9 -> {
                animateGroundBlock(level, ground);
                spawnLightning(level, ground);
            }
            default -> {
                // Variants 0, 1 and 3 have no timed item/entity drop.
            }
        }
        setChanged();
    }

    private void spawnLightning(ServerLevel level, BlockPos ground) {
        EntityType<?> lightningType = BuiltInRegistries.ENTITY_TYPE.get(
            ResourceLocation.fromNamespaceAndPath("minecraft", "lightning_bolt"));
        Entity created = lightningType.create(level);
        if (!(created instanceof LightningBolt bolt)) {
            return;
        }
        bolt.setPos(ground.getX() + 0.5, ground.getY(), ground.getZ() + 0.5);
        level.addFreshEntity(bolt);
    }

    /**
     * Animus cloud uses the same whitelist and temporary berserk-golem setup as
     * FocusAnimationItem, but with the legacy 1200-tick lifetime and no upgrades.
     */
    private void animateGroundBlock(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.hasBlockEntity() || state.getDestroySpeed(level, pos) < 0.0F
                || (!state.isSolidRender(level, pos) && !FocusAnimationItem.isWhitelisted(state))) {
            return;
        }

        EntityGolemTH golem = new EntityGolemTH(ThEntities.GOLEM_TH.get(), level);
        golem.loadGolem(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
            state, 0, 1200, false, true, false);
        golem.setHome(golem.blockPosition(), Direction.UP);
        golem.setRemainingFireTicks(0);
        golem.setHealth(golem.getMaxHealth());

        level.removeBlock(pos, false);
        if (level.addFreshEntity(golem)) {
            level.broadcastEntityEvent(golem, (byte) 7);
        }
    }

    private void dropAlloyNugget(ServerLevel level) {
        int roll = level.getRandom().nextInt(75);
        String id = roll < 6 ? null
            : roll < 12 ? "silver_nugget"
            : roll < 20 ? "copper_nugget"
            : roll < 30 ? "tin_nugget"
            : roll < 40 ? "lead_nugget"
            : roll < 50 ? "quicksilver_drop"
            : null;

        var itemCompat = TcReforgedPort.items;
        var item = id == null || !itemCompat.isAvailable()
            ? null
            : itemCompat.lookup(ResourceLocation.fromNamespaceAndPath("thaumcraft", id));
        spawnCloudDrop(level, new ItemStack(item != null ? item : (id == null ? Items.GOLD_NUGGET : Items.IRON_NUGGET)));
    }

    private void dropMeat(ServerLevel level) {
        var random = level.getRandom();
        ItemStack stack = switch (random.nextInt(10)) {
            case 0 -> new ItemStack(Items.BEEF);
            case 1 -> new ItemStack(Items.PORKCHOP);
            case 2 -> new ItemStack(Items.CHICKEN);
            case 3 -> new ItemStack(switch (random.nextInt(3)) {
                case 0 -> Items.COD;
                case 1 -> Items.SALMON;
                default -> Items.TROPICAL_FISH;
            });
            default -> new ItemStack(ThItems.MEAT.get());
        };
        spawnCloudDrop(level, stack);
    }

    private void spawnAspectDrop(ServerLevel level) {
        Aspect[] aspects = {
            Aspect.WATER, Aspect.FIRE, Aspect.ORDER, Aspect.ENTROPY, Aspect.AIR, Aspect.EARTH
        };
        Aspect aspect = aspects[level.getRandom().nextInt(aspects.length)];
        var items = TcReforgedPort.items;
        if (items.isAvailable()) {
            items.spawnAspectOrb(level,
                worldPosition.getX() + level.getRandom().nextDouble(),
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + level.getRandom().nextDouble(),
                aspect, 1);
        }
    }

    private void spawnExperienceDrop(ServerLevel level) {
        ExperienceOrb.award(level, new Vec3(
            worldPosition.getX() + level.getRandom().nextDouble(),
            worldPosition.getY() + 0.5,
            worldPosition.getZ() + level.getRandom().nextDouble()
        ), level.getRandom().nextInt(4));
    }

    private void spawnCloudDrop(ServerLevel level, ItemStack stack) {
        InvulnerableItemEntity item = new InvulnerableItemEntity(level,
            worldPosition.getX() + level.getRandom().nextDouble(),
            worldPosition.getY() + 0.5,
            worldPosition.getZ() + level.getRandom().nextDouble(),
            stack);
        level.addFreshEntity(item);
    }

    /**
     * Entity effects cover the full vertical column below the cloud. The upper
     * AABB bound is the cloud Y, not the ground Y (the old port had zero height).
     */
    private void applyEntityEffects(ServerLevel level, int type) {
        BlockPos ground = findGroundBelow(level);
        AABB column = new AABB(
            worldPosition.getX(), ground.getY(), worldPosition.getZ(),
            worldPosition.getX() + 1.0, worldPosition.getY() + 1.0, worldPosition.getZ() + 1.0
        );
        boolean warm = level.getBiome(worldPosition).value().getBaseTemperature() >= 0.15F;

        for (Entity entity : level.getEntitiesOfClass(Entity.class, column)) {
            if (type == 1 || type == 4) {
                entity.setRemainingFireTicks(6 * 20);
                continue;
            }
            if (type == 3) {
                var items = TcReforgedPort.items;
                if (items.isAvailable()) {
                    items.hurtWithDissolve(level, entity, 1.0F);
                } else {
                    entity.hurt(level.damageSources().magic(), 1.0F);
                }
                continue;
            }
            if (!warm) {
                continue;
            }

            String entityId = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getPath();
            if (entityId.equals("enderman") || entityId.equals("blaze") || entityId.equals("firebat")) {
                entity.hurt(level.damageSources().drown(), 1.0F);
            }
            if (entity.isOnFire()) {
                entity.setRemainingFireTicks(0);
            }
            if (type == 8 && (entityId.equals("cow") || entityId.equals("chocolatecow"))
                    && entity instanceof LivingEntity cow) {
                convertCowToMooshroom(level, cow);
            }
        }
    }

    private void convertCowToMooshroom(ServerLevel level, LivingEntity cow) {
        EntityType<?> mooshroomType = BuiltInRegistries.ENTITY_TYPE.get(
            ResourceLocation.fromNamespaceAndPath("minecraft", "mooshroom"));
        Entity converted = mooshroomType.create(level);
        if (!(converted instanceof LivingEntity mooshroom)) {
            return;
        }
        double x = cow.getX();
        double y = cow.getY();
        double z = cow.getZ();
        mooshroom.setPos(x, y, z);
        mooshroom.setYRot(cow.getYRot());
        mooshroom.setXRot(cow.getXRot());
        mooshroom.setHealth(cow.getHealth());
        cow.discard();
        level.addFreshEntity(mooshroom);
        level.sendParticles(ParticleTypes.EXPLOSION, x, y + cow.getBbHeight() / 2.0, z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    private void applyGrowthDrop(ServerLevel level, BlockPos ground) {
        BlockPos above = ground.above();
        if (!level.isEmptyBlock(above)) {
            return;
        }

        BlockState soil = level.getBlockState(ground);
        var random = level.getRandom();
        if (soil.is(Blocks.FARMLAND)) {
            BlockState crop = switch (random.nextInt(8)) {
                case 1 -> Blocks.MELON_STEM.defaultBlockState();
                case 2 -> Blocks.PUMPKIN_STEM.defaultBlockState();
                case 3 -> Blocks.CARROTS.defaultBlockState();
                case 4 -> Blocks.POTATOES.defaultBlockState();
                default -> Blocks.WHEAT.defaultBlockState();
            };
            level.setBlock(above, crop, 3);
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
            BlockState plant = random.nextInt(10) == 4
                ? thaumcraftPlant("cinderpearl", Blocks.CACTUS.defaultBlockState())
                : Blocks.CACTUS.defaultBlockState();
            level.setBlock(above, plant, 3);
            return;
        }
        if (soil.is(Blocks.SOUL_SAND)) {
            level.setBlock(above, Blocks.NETHER_WART.defaultBlockState(), 3);
            return;
        }
        if (soil.is(Blocks.GRASS_BLOCK)) {
            int plant = random.nextInt(1000);
            if (plant == 666) {
                level.setBlock(above, thaumcraftPlant("silverwood_sapling", Blocks.OAK_SAPLING.defaultBlockState()), 3);
            } else if (plant < 750) {
                BlockState[] plants = {
                    Blocks.SHORT_GRASS.defaultBlockState(),
                    Blocks.FERN.defaultBlockState(),
                    Blocks.DANDELION.defaultBlockState(),
                    Blocks.POPPY.defaultBlockState(),
                    Blocks.POPPY.defaultBlockState(),
                    Blocks.BLUE_ORCHID.defaultBlockState(),
                    Blocks.ALLIUM.defaultBlockState(),
                    Blocks.AZURE_BLUET.defaultBlockState(),
                    Blocks.RED_TULIP.defaultBlockState(),
                    Blocks.ORANGE_TULIP.defaultBlockState(),
                    Blocks.WHITE_TULIP.defaultBlockState(),
                    Blocks.PINK_TULIP.defaultBlockState(),
                    Blocks.OXEYE_DAISY.defaultBlockState(),
                    Blocks.SUGAR_CANE.defaultBlockState()
                };
                level.setBlock(above, plants[random.nextInt(plants.length)], 3);
            } else if (plant < 950) {
                int sapling = random.nextInt(100);
                if (sapling < 10) {
                    level.setBlock(above,
                        thaumcraftPlant("greatwood_sapling", Blocks.OAK_SAPLING.defaultBlockState()), 3);
                } else {
                    level.setBlock(above, switch (random.nextInt(6)) {
                        case 0 -> Blocks.OAK_SAPLING.defaultBlockState();
                        case 1 -> Blocks.SPRUCE_SAPLING.defaultBlockState();
                        case 2 -> Blocks.BIRCH_SAPLING.defaultBlockState();
                        case 3 -> Blocks.JUNGLE_SAPLING.defaultBlockState();
                        case 4 -> Blocks.ACACIA_SAPLING.defaultBlockState();
                        default -> Blocks.DARK_OAK_SAPLING.defaultBlockState();
                    }, 3);
                }
            } else if (plant < 975) {
                level.setBlock(above, Blocks.LILAC.defaultBlockState(), 3);
            } else {
                level.setBlock(above, Blocks.ROSE_BUSH.defaultBlockState(), 3);
            }
        }
    }

    private BlockState thaumcraftPlant(String path, BlockState fallback) {
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("thaumcraft", path));
        return block == Blocks.AIR ? fallback : block.defaultBlockState();
    }

    private BlockPos findGroundBelow(ServerLevel level) {
        BlockPos.MutableBlockPos cursor = worldPosition.mutable();
        while (cursor.getY() > level.getMinBuildHeight()
                && level.getBlockState(cursor).getCollisionShape(level, cursor).isEmpty()) {
            cursor.move(0, -1, 0);
        }
        return cursor.immutable();
    }

    private void applyPrecipitation(ServerLevel level, int type) {
        BlockHitResult hit = level.clip(new ClipContext(
            new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() - 0.5, worldPosition.getZ() + 0.5),
            new Vec3(worldPosition.getX() + 0.5, level.getMinBuildHeight(), worldPosition.getZ() + 0.5),
            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, net.minecraft.world.phys.shapes.CollisionContext.empty()
        ));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos targetPos = hit.getBlockPos();
        BlockState target = level.getBlockState(targetPos);
        int meltable = ThItems.FOCUS_LIQUEFACTION.get().isMeltableBlock(target);
        boolean flammable = target.isFlammable(level, targetPos, Direction.UP);

        if (type == 1 || type == 4) {
            switch (meltable) {
                case 1 -> level.setBlock(targetPos, Blocks.FIRE.defaultBlockState(), 3);
                case 2 -> {
                    if (level.dimension() != Level.NETHER) {
                        level.setBlock(targetPos, Blocks.WATER.defaultBlockState(), 3);
                    } else {
                        level.removeBlock(targetPos, false);
                    }
                }
                case 3 -> level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                case 4 -> TntBlock.explode(level, targetPos);
                case 6 -> level.setBlock(targetPos, Blocks.LAVA.defaultBlockState(), 3);
                default -> {
                    if (flammable) {
                        level.setBlock(targetPos, Blocks.FIRE.defaultBlockState(), 3);
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
        if (level.getBiome(targetPos).value().coldEnoughToSnow(targetPos)) {
            if (target.isSolidRender(level, targetPos) && level.isEmptyBlock(above)) {
                level.setBlock(above, Blocks.SNOW.defaultBlockState(), 3);
            }
        } else if (target.is(Blocks.FARMLAND)
                && target.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.MOISTURE)
                && target.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.MOISTURE) != 7) {
            level.setBlock(targetPos,
                target.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.MOISTURE, 7), 3);
        } else if (level.getBlockState(above).is(Blocks.FIRE)) {
            level.removeBlock(above, false);
        } else if (target.is(Blocks.CAULDRON)) {
            level.setBlock(targetPos,
                Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), 3);
        }
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input.tag(), input.lookup());
        dropTimer = input.getIntOr("dropTimer", -1);
        weatherTicks = input.getIntOr("weatherTicks", 0);
        raining = input.getBooleanOr("raining", false);
        columnDepth = Math.max(1, Math.min(256, input.getIntOr("columnDepth", 256)));
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output.tag(), output.lookup());
        output.putInt("dropTimer", dropTimer);
        output.putInt("weatherTicks", weatherTicks);
        output.putBoolean("raining", raining);
        output.putInt("columnDepth", columnDepth);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
