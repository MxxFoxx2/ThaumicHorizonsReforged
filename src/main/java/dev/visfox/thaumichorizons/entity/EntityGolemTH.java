package dev.visfox.thaumichorizons.entity;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.phys.AABB;
import javax.annotation.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.entity.golem.GolemBase;
import thaumcraft.entity.golem.GolemKeys;
import thaumcraft.entity.golem.GolemSnapshot;
import thaumcraft.entity.golem.GolemType;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.entity.ai.AttackOnCollideGoalTH;
import dev.visfox.thaumichorizons.entity.ai.FollowPlayerGoal;
import dev.visfox.thaumichorizons.lib.THSounds;

public class EntityGolemTH extends GolemBase {
    private static final EntityDataAccessor<Integer> TH_BLOCK =
        SynchedEntityData.defineId(EntityGolemTH.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TH_META =
        SynchedEntityData.defineId(EntityGolemTH.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<java.util.Optional<BlockState>> TH_STATE =
        SynchedEntityData.defineId(EntityGolemTH.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Boolean> TH_BERSERK =
        SynchedEntityData.defineId(EntityGolemTH.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TH_KABOOM =
        SynchedEntityData.defineId(EntityGolemTH.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> TH_MATERIAL =
        SynchedEntityData.defineId(EntityGolemTH.class, EntityDataSerializers.STRING);

    public int ticksAlive;
    public int voidCount;

    @SuppressWarnings("unchecked")
    public static EntityType<EntityGolemTH> thType() {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(ThaumicHorizons.id("golemth")).orElse(null);
        if (type == null) {
            type = BuiltInRegistries.ENTITY_TYPE.getOptional(Thaumcraft.id("golem")).orElse(null);
        }
        return (EntityType<EntityGolemTH>) type;
    }

    public EntityGolemTH(EntityType<? extends GolemBase> type, Level level) {
        super(type, level);
        this.ticksAlive = 0;
        this.voidCount = 0;
    }

    public void loadGolem(double x, double y, double z, @Nullable BlockState state, int md, int ticksAlive, boolean adv,
            boolean berserk, boolean kaboom) {
        setPos(x, y, z);
        this.ticksAlive = ticksAlive;
        Block block = state == null ? null : state.getBlock();
        entityData.set(TH_BLOCK, block == null ? 0 : BuiltInRegistries.BLOCK.getId(block));
        entityData.set(TH_META, md);
        entityData.set(TH_STATE, java.util.Optional.ofNullable(state));
        entityData.set(TH_BERSERK, berserk);
        entityData.set(TH_KABOOM, kaboom);
        setMaterial(GolemMaterialTH.fromBlock(block));
        configure(GolemType.WOOD, new GolemSnapshot(-1, adv, List.of(), "", List.of(), ItemContainerContents.EMPTY, List.of(), 0));
        setHealth(getMaxHealth());
        if (level().isClientSide()) {
            loadTexture();
        }
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

    public static GolemMaterialTH materialType(@Nullable Block block) {
        return GolemMaterialTH.fromBlock(block);
    }

    public @Nullable Block getBlocky() {
        int id = entityData.get(TH_BLOCK);
        if (id == 0) {
            return null;
        }
        return BuiltInRegistries.BLOCK.byId(id);
    }

    public int getMd() {
        return entityData.get(TH_META);
    }

    public GolemMaterialTH material() {
        return GolemMaterialTH.byId(entityData.get(TH_MATERIAL));
    }

    public void setMaterial(GolemMaterialTH material) {
        entityData.set(TH_MATERIAL, material.id());
    }

    @Override
    public int getGolemStrength() {
        return material().strength + getUpgradeAmount(1);
    }

    @Override
    public int getCarryLimit() {
        int base = material().carry;
        return base + Math.min(16, Math.max(4, base)) * getUpgradeAmount(1);
    }

    @Override
    public boolean fireImmune() {
        return material().fireResist;
    }

    @Override
    public void configure(GolemType type, GolemSnapshot state) {
        super.configure(type, state);
        GolemMaterialTH golemMaterial = material();
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(
            golemMaterial.health + (state.decoration().contains("H") ? 5 : 0));
        getAttribute(Attributes.ARMOR).setBaseValue(golemMaterial.armor
            + (state.decoration().contains("V") ? 1 : 0)
            + (state.decoration().contains("P") ? 4 : 0));
        getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
            2 + getGolemStrength() + getUpgradeAmount(1) + (state.decoration().contains("M") ? 2 : 0));
        setPathfindingMalus(net.minecraft.world.level.pathfinder.PathType.WATER,
            golemMaterial.stats().heavy() ? 0.0F : 8.0F);
        if (getHealth() > getMaxHealth()) {
            setHealth(getMaxHealth());
        }
    }

    @Override
    public GolemType getGolemType() {
        return GolemType.WOOD;
    }

    public ResourceLocation getGolemTexture() {
        return ThaumicHorizons.id("textures/model/golem_" + material().id() + ".png");
    }

    public double getMaterialMovementSpeed() {
        GolemMaterialTH golemMaterial = material();
        float speed = golemMaterial.speed
            * (getGolemDecoration().contains("B") ? 1.1F : 1.0F)
            * (getGolemDecoration().contains("P") ? 0.88F : 1.0F)
            * (1.0F + getUpgradeAmount(0) * 0.15F)
            * (isAdvanced() ? 1.1F : 1.0F);
        if (isInWater() && golemMaterial.stats().heavy()) {
            speed *= 2.0F;
        }
        return isWorking() ? speed : 0.0F;
    }

    public @Nullable BlockState getGolemBlockState() {
        Block block = getBlocky();
        if (block == null) {
            return null;
        }
        return entityData.get(TH_STATE).orElse(block.defaultBlockState());
    }

    public boolean isBerserk() {
        return entityData.get(TH_BERSERK);
    }

    public boolean isKaboom() {
        return entityData.get(TH_KABOOM);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TH_BLOCK, 0);
        builder.define(TH_META, 0);
        builder.define(TH_STATE, java.util.Optional.empty());
        builder.define(TH_BERSERK, false);
        builder.define(TH_KABOOM, false);
        builder.define(TH_MATERIAL, GolemMaterialTH.VOID.id());
    }

    @Override
    public boolean isWorking() {
        return getCore() >= 0 ? super.isWorking() : !inactive && isAlive();
    }

    @Override
    public boolean isWithinHome(BlockPos pos) {
        return getCore() < 0 || super.isWithinHome(pos);
    }

    @Override
    public void rebuildGoals() {
        super.rebuildGoals();
        if (getCore() < 0) {
            setupUncoredGoals();
        }
    }

    private void setupUncoredGoals() {
        goalSelector.addGoal(0, new AvoidEntityGoal<>(this, Creeper.class, 8.0F, 1.0D, 1.0D));
        goalSelector.addGoal(3, new AttackOnCollideGoalTH(this, 1.0D, true));
        goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        float speed = (float) (getAttributeValue(Attributes.MOVEMENT_SPEED) * 3.0D);
        if (!isKaboom()) {
            goalSelector.addGoal(6, new FollowPlayerGoal(this, speed, 2.0F, 12.0F));
        } else {
            goalSelector.addGoal(6, new FollowPlayerGoal(this, speed, 8.0F, 12.0F));
        }
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 0, false, false,
            target -> target instanceof Enemy));
        if (isBerserk()) {
            targetSelector.addGoal(1, new BerserkTargetGoal());
        }
        setHome(blockPosition().immutable(), Direction.UP);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Entity attacker = source.getDirectEntity();
        if (attacker != null && attacker != this && getBlocky() == Blocks.CACTUS) {
            attacker.hurt(damageSources().thorns(this), getUpgradeAmount(5) * 2 + random.nextInt(2));
        }
        return super.hurt(source, amount);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide() && getCore() == -1) {
            if (ticksAlive > 0) {
                ticksAlive--;
                Player owner = getOwnerPlayer();
                if (owner != null) {
                    setHome(owner.blockPosition(), getHomeFacing());
                }
            } else if (ticksAlive != -420) {
                die();
            } else {
                if (tickCount % 10 == 0 && random.nextInt(500) == 0) {
                    Player player = getOwnerPlayer();
                    if (player != null) {
                        switch (voidCount) {
                            case 0 -> player.sendSystemMessage(
                                Component.translatable("thaumichorizons.golemWarning1").withStyle(net.minecraft.ChatFormatting.ITALIC, net.minecraft.ChatFormatting.DARK_PURPLE));
                            case 1 -> player.sendSystemMessage(
                                Component.translatable("thaumichorizons.golemWarning2").withStyle(net.minecraft.ChatFormatting.ITALIC, net.minecraft.ChatFormatting.DARK_PURPLE));
                            case 2 -> player.sendSystemMessage(
                                Component.translatable("thaumichorizons.golemWarning3").withStyle(net.minecraft.ChatFormatting.ITALIC, net.minecraft.ChatFormatting.DARK_PURPLE));
                            case 3 -> {
                                die();
                                spawnEldritchGuardian();
                            }
                            default -> {
                            }
                        }
                        voidCount++;
                    }
                }
                Player owner = getOwnerPlayer();
                if (owner != null) {
                    setHome(owner.blockPosition(), getHomeFacing());
                }
            }
        }
    }

    private @Nullable Player getOwnerPlayer() {
        if (getOwnerUUID() == null || !(level() instanceof ServerLevel serverLevel)) {
            return null;
        }
        return serverLevel.getPlayerByUUID(getOwnerUUID());
    }

    private void spawnEldritchGuardian() {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.fromNamespaceAndPath("thaumcraft", "eldritch_guardian")).orElse(null);
        if (type != null) {
            Entity guardian = type.create(level());
            if (guardian != null) {
                guardian.setPos(getX(), getY(), getZ());
                level().addFreshEntity(guardian);
            }
        }
    }

    public void die() {
        discard();
        Block blocky = getBlocky();
        if (blocky == Blocks.TNT) {
            level().explode(this, getX(), getY(), getZ(), 4.0F, Level.ExplosionInteraction.MOB);
            return;
        }
        if (isKaboom()) {
            level().explode(this, getX(), getY(), getZ(), 3.0F, Level.ExplosionInteraction.MOB);
            return;
        }
        if (blocky == null) {
            return;
        }
        BlockPos base = BlockPos.containing(getX() - 0.5, getY(), getZ() - 0.5);
        if (tryRestoreBlock(base)) {
            return;
        }
        for (int dy : new int[] {0, -1, 1}) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx != 0 || dz != 0 || dy != 0) {
                        if (tryRestoreBlock(base.offset(dx, dy, dz))) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean tryRestoreBlock(BlockPos pos) {
        BlockState state = getGolemBlockState();
        if (level().getBlockState(pos).isAir() && state != null) {
            level().setBlock(pos, state, 3);
            return true;
        }
        return false;
    }

    @Override
    public Component getName() {
        if (hasCustomName()) {
            return getCustomName();
        }
        Block blocky = getBlocky();
        if (blocky == null) {
            return Component.translatable("entity.thaumichorizons.golemth.voidling");
        }
        return new ItemStack(blocky).getHoverName().copy().append(" ")
            .append(Component.translatable("entity.thaumichorizons.golemth.name"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("GolemBlockTH", entityData.get(TH_BLOCK));
        tag.putString("GolemMaterialTH", material().id());
        tag.putInt("GolemMetaTH", entityData.get(TH_META));
        BlockState state = getGolemBlockState();
        if (state != null) {
            tag.put("GolemStateTH", NbtUtils.writeBlockState(state));
        }
        tag.putInt("TicksAliveTH", ticksAlive);
        tag.putInt("VoidCountTH", voidCount);
        tag.putBoolean("BerserkTH", isBerserk());
        tag.putBoolean("KaboomTH", isKaboom());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        int legacyType = tag.contains("GolemType") ? tag.getInt("GolemType") : 1;
        super.readAdditionalSaveData(tag);
        entityData.set(TH_BLOCK, tag.contains("GolemBlockTH") ? tag.getInt("GolemBlockTH") : 0);
        entityData.set(TH_META, tag.contains("GolemMetaTH") ? tag.getInt("GolemMetaTH") : 0);
        entityData.set(TH_STATE, tag.contains("GolemStateTH")
            ? java.util.Optional.of(NbtUtils.readBlockState(
                BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("GolemStateTH")))
            : java.util.Optional.empty());
        ticksAlive = tag.contains("TicksAliveTH") ? tag.getInt("TicksAliveTH") : 0;
        voidCount = tag.contains("VoidCountTH") ? tag.getInt("VoidCountTH") : 0;
        entityData.set(TH_BERSERK, tag.getBoolean("BerserkTH"));
        entityData.set(TH_KABOOM, tag.getBoolean("KaboomTH"));
        GolemMaterialTH restored;
        String savedMaterial = tag.getString("GolemMaterialTH");
        if (!savedMaterial.isBlank()) {
            restored = GolemMaterialTH.byId(savedMaterial);
        } else {
            int blockId = entityData.get(TH_BLOCK);
            restored = GolemMaterialTH.legacy(legacyType);
            if (restored == null && blockId != 0) {
                restored = GolemMaterialTH.fromBlock(BuiltInRegistries.BLOCK.byId(blockId));
            }
            if (restored == null) {
                restored = GolemMaterialTH.DEFAULT;
            }
        }
        setMaterial(restored);
        configure(getGolemType(), snapshot());
    }

    @Override
    public ItemStack toItem(boolean dismantle) {
        var item = BuiltInRegistries.ITEM.get(ThaumicHorizons.id("golemplacer"));
        if (item == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item);
        GolemKeys.STATE.set(stack, dismantle ? GolemSnapshot.EMPTY : snapshot());
        if (!dismantle) {
            GolemBlockData.set(stack, getGolemBlockState(), getMd(), isBerserk(), isKaboom(), material());
            if (hasCustomName()) {
                stack.set(DataComponents.CUSTOM_NAME, getCustomName());
            }
        }
        return stack;
    }

    public static final class GolemBlockData {
        private GolemBlockData() {
        }

        public static void set(ItemStack stack, @Nullable BlockState state, int md, boolean berserk, boolean kaboom,
            GolemMaterialTH material) {
            CompoundTag tag = new CompoundTag();
            tag.putString("GolemMaterial", material.id());
            if (state != null) {
                tag.putInt("GolemBlockId", BuiltInRegistries.BLOCK.getId(state.getBlock()));
                tag.put("GolemState", NbtUtils.writeBlockState(state));
            }
            tag.putInt("GolemMd", md);
            tag.putBoolean("Berserk", berserk);
            tag.putBoolean("Kaboom", kaboom);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        public static CompoundTag get(ItemStack stack) {
            return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        }

        public static @Nullable Block blockOf(CompoundTag tag) {
            int id = tag.contains("GolemBlockId") ? tag.getInt("GolemBlockId") : 0;
            return id == 0 ? null : BuiltInRegistries.BLOCK.byId(id);
        }

        public static GolemMaterialTH materialOf(CompoundTag tag) {
        return GolemMaterialTH.byId(tag.contains("GolemMaterial") ? tag.getString("GolemMaterial") : GolemMaterialTH.DEFAULT.id());
    }

    public static @Nullable BlockState stateOf(CompoundTag tag) {
            Block block = blockOf(tag);
            if (block == null) {
                return null;
            }
            return tag.contains("GolemState") ? NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),
                tag.getCompound("GolemState")) : block.defaultBlockState();
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && getBlocky() == Blocks.COBWEB && target instanceof LivingEntity living) {
            living.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 20, 0));
        }
        return hit;
    }

    public void loadTexture() {
    }

    private class BerserkTargetGoal extends TargetGoal {
        private @Nullable LivingEntity target;

        BerserkTargetGoal() {
            super(EntityGolemTH.this, false);
            setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (tickCount % 10 != 0) {
                return false;
            }
            AABB box = getBoundingBox().inflate(getRange(), 4.0D, getRange());
            List<LivingEntity> candidates = level().getEntitiesOfClass(LivingEntity.class, box,
                candidate -> candidate != EntityGolemTH.this && candidate.isAlive()
                    && (getOwnerUUID() == null || !getOwnerUUID().equals(candidate.getUUID())));
            double best = Double.MAX_VALUE;
            LivingEntity chosen = null;
            for (LivingEntity candidate : candidates) {
                double distance = distanceToSqr(candidate);
                if (distance < best) {
                    best = distance;
                    chosen = candidate;
                }
            }
            target = chosen;
            return target != null;
        }

        @Override
        public boolean canContinueToUse() {
            return target != null && target.isAlive()
                && distanceToSqr(target) < getRange() * getRange() * 2.0D
                && (getOwnerUUID() == null || !getOwnerUUID().equals(target.getUUID()));
        }

        @Override
        public void start() {
            if (target != null) {
                setTarget(target);
            }
        }

        @Override
        public void stop() {
            if (getTarget() == target) {
                setTarget(null);
            }
            target = null;
        }
    }
}
