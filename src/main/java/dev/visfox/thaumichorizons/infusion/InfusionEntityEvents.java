package dev.visfox.thaumichorizons.infusion;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.compat.WarpKind;
import dev.visfox.thaumichorizons.blockentity.SoulBeaconBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulBeaconKeys;
import dev.visfox.thaumichorizons.blockentity.VatBlockEntity;
import dev.visfox.thaumichorizons.entity.ai.AttackOnCollideGoalTH;
import dev.visfox.thaumichorizons.entity.ai.FollowOwnerGoalTH;
import dev.visfox.thaumichorizons.entity.ai.HurtByTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.OwnerHurtByTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.OwnerHurtTargetGoalTH;
import dev.visfox.thaumichorizons.entity.ai.SitGoalTH;
import dev.visfox.thaumichorizons.entity.ai.WanderGoalTH;
import dev.visfox.thaumichorizons.item.AmuletMirrorItem;
import dev.visfox.thaumichorizons.item.SoulJarKeys;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = ThaumicHorizons.MODID)
public final class InfusionEntityEvents {
    private static final ResourceKey<DamageType> TAINT_DAMAGE =
        ResourceKey.create(Registries.DAMAGE_TYPE, thaumcraft.Thaumcraft.id("taint"));

    public static final int QUICK_LIMBS = 1;
    public static final int AWAKE_BLOOD = 3;
    public static final int DIAMOND_SKIN = 4;
    public static final int ENDER_HEART = 5;
    public static final int SHOCK_SKIN = 6;
    public static final int INSTILLED_LOYALTY = 7;
    public static final int RUNIC_CREATURE = 8;
    public static final int ELDRITCH_FANGS = 9;
    public static final int PORTABILITY = 10;

    private InfusionEntityEvents() {
    }

    @SubscribeEvent
    static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity living) {
            applyInfusions(living);
        }
    }

    @SubscribeEvent
    static void onPlayerRespawn(net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        applyInfusions(event.getEntity());
    }

    @SubscribeEvent
    static void onLivingTick(EntityTickEvent.Post event) {
        LivingEntity entity = event.getEntity() instanceof LivingEntity living ? living : null;
        if (entity == null) {
            return;
        }
        if (entity instanceof Player player) {
            playerTick(player);
        }
        if (entity.level().isClientSide() || !(entity.level() instanceof ServerLevel level)) {
            return;
        }
        if (entity.tickCount % 30 == 0 && CreatureInfusions.hasInfusion(entity, SHOCK_SKIN)) {
            LivingEntity target = entity instanceof Mob mob && mob.getTarget() != null
                ? mob.getTarget()
                : entity.getLastHurtByMob();
            if (target != null && target.isAlive()) {
                target.hurtServer(level, entity.damageSources().magic(), 1.0F);
                if (TcReforgedPort.fx.isAvailable()) {
                    TcReforgedPort.fx.arcLightning(level,
                        new Vec3(entity.getX(), entity.getY() + entity.getBbHeight() / 2.0F, entity.getZ()),
                        new Vec3(target.getX(), target.getY() + target.getBbHeight() / 2.0F, target.getZ()),
                        0xFFFFFF);
                }
                level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.0F,
                    1.0F + (entity.getRandom().nextFloat() - entity.getRandom().nextFloat()) * 0.2F);
            }
        }
        if (entity.tickCount % 100 == 0 && CreatureInfusions.hasInfusion(entity, RUNIC_CREATURE)) {
            int charge = entity.getPersistentData().getIntOr("runicCharge", 0) + 1;
            if (charge > 6) {
                charge = 6;
            }
            entity.getPersistentData().putInt("runicCharge", charge);
        }
    }

    private static void playerTick(Player player) {
        CreatureInfusionData data = CreatureInfusion.get(player);
        if (data.infusion(CreatureInfusion.SILVER_HEART) != 0) {
            clearSilverHeartEffects(player);
        }
        if (data.infusion(CreatureInfusion.SYNTH_SKIN) != 0 && player.tickCount % 200 == 0
            && player.getFoodData().needsFood() && player.level().isBrightOutside()
            && player.level().canSeeSky(player.blockPosition())) {
            player.getFoodData().eat(1, 0.0F);
        }
        if (data.infusion(CreatureInfusion.AMPHIBIOUS) != 0 && player.isInWater()) {
            player.setAirSupply(300);
        }
        if (player.tickCount % 30 == 0) {
            if (data.infusion(CreatureInfusion.WARP_TUMOR) != 0 && player instanceof ServerPlayer serverPlayer) {
                CreatureInfusion.warpTumor(serverPlayer);
            }
            applyPlayerPotionInfusions(player, data);
        }
        if (data.infusion(CreatureInfusion.SPIDER_CLIMB) != 0 && !data.climb()) {
            if (player.horizontalCollision) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0.2, player.getDeltaMovement().z);
                if (player.isShiftKeyDown()) {
                    player.setDeltaMovement(player.getDeltaMovement().x, 0.0, player.getDeltaMovement().z);
                }
                player.fallDistance = 0.0F;
            } else {
                double w = player.getBbWidth();
                net.minecraft.world.phys.AABB box = new net.minecraft.world.phys.AABB(
                    player.getX() - w / 1.5, player.getY(), player.getZ() - w / 1.5,
                    player.getX() + w / 1.5, player.getY() + player.getBbHeight() * 0.9, player.getZ() + w / 1.5);
                if (!player.level().noCollision(box)) {
                    if (player.isShiftKeyDown()) {
                        player.setDeltaMovement(player.getDeltaMovement().x, 0.0, player.getDeltaMovement().z);
                    } else {
                        player.setDeltaMovement(player.getDeltaMovement().x, -0.15, player.getDeltaMovement().z);
                    }
                    player.fallDistance = 0.0F;
                }
            }
        }
    }

    private static void clearSilverHeartEffects(Player player) {
        Set<Holder<MobEffect>> cleared = silverHeartClearedEffects();
        List<MobEffectInstance> toKeep = new ArrayList<>();
        boolean changed = false;
        for (MobEffectInstance effect : player.getActiveEffects()) {
            if (cleared.contains(effect.getEffect())) {
                toKeep.add(new MobEffectInstance(effect.getEffect(), effect.getDuration() - 1, 0, false, false));
                changed = true;
            } else {
                toKeep.add(effect);
            }
        }
        if (changed) {
            player.removeAllEffects();
            for (MobEffectInstance effect : toKeep) {
                player.addEffect(effect);
            }
        }
    }

    private static Set<Holder<MobEffect>> silverHeartClearedEffects() {
        Set<Holder<MobEffect>> effects = new HashSet<>();
        effects.add(MobEffects.POISON);
        effects.add(MobEffects.WITHER);
        if (TcReforgedPort.potions.isAvailable()) {
            effects.add(TcReforgedPort.potions.visExhaust());
            effects.add(TcReforgedPort.potions.thaumarhia());
            effects.add(TcReforgedPort.potions.taintPoison());
            effects.add(TcReforgedPort.potions.infectiousVisExhaust());
        }
        return effects;
    }

    private static void applyPlayerPotionInfusions(Player player, CreatureInfusionData data) {
        if (data.infusion(CreatureInfusion.SWIFTNESS) != 0) {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, MobEffectInstance.INFINITE_DURATION, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, MobEffectInstance.INFINITE_DURATION, 0, true, false));
        }
        if (data.infusion(CreatureInfusion.REGENERATION) != 0) {
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, true, false));
        }
        if (data.infusion(CreatureInfusion.RESISTANCE) != 0) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, true, false));
        }
    }

    public static void applyInfusions(LivingEntity entity) {
        if (entity instanceof Player player) {
            CreatureInfusionData data = CreatureInfusion.get(player);
            if (!entity.level().isClientSide() && data.infusion(CreatureInfusion.WARP_TUMOR) != 0
                && player instanceof ServerPlayer serverPlayer) {
                CreatureInfusion.warpTumor(serverPlayer);
            }
            applyPlayerPotionInfusions(player, data);
            return;
        }
        for (int slot = 0; slot < CreatureInfusionData.NUM_INFUSIONS; ++slot) {
            int infusion = CreatureInfusions.get(entity).infusions()[slot];
            if (infusion == 0) {
                continue;
            }
            if (infusion == QUICK_LIMBS) {
                entity.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, MobEffectInstance.INFINITE_DURATION, 0, true, false));
            } else if (infusion == AWAKE_BLOOD) {
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, MobEffectInstance.INFINITE_DURATION, 0, true, false));
            } else if (infusion == DIAMOND_SKIN) {
                entity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, true, false));
            } else if (infusion == RUNIC_CREATURE && !entity.getPersistentData().contains("runicCharge")) {
                entity.getPersistentData().putInt("runicCharge", 6);
            } else if (infusion == INSTILLED_LOYALTY && entity instanceof Mob mob) {
                applyNewAI(mob);
            }
        }
    }

    public static void applyNewAI(Mob entity) {
        entity.goalSelector.removeAllGoals(goal -> true);
        entity.targetSelector.removeAllGoals(goal -> true);
        entity.goalSelector.addGoal(1, new FloatGoal(entity));
        entity.goalSelector.addGoal(2, new SitGoalTH(entity));
        entity.goalSelector.addGoal(3, new LeapAtTargetGoal(entity, 0.4F));
        entity.goalSelector.addGoal(4, new AttackOnCollideGoalTH(entity, 1.0, true));
        entity.goalSelector.addGoal(5, new FollowOwnerGoalTH(entity, 1.0, 10.0F, 2.0F));
        if (entity instanceof Animal animal) {
            entity.goalSelector.addGoal(6, new BreedGoal(animal, 1.0));
        }
        entity.goalSelector.addGoal(7, new WanderGoalTH(entity, 1.0));
        entity.goalSelector.addGoal(9, new LookAtPlayerGoal(entity, Player.class, 8.0F));
        entity.goalSelector.addGoal(9, new RandomLookAroundGoal(entity));
        entity.targetSelector.addGoal(1, new OwnerHurtByTargetGoalTH(entity));
        entity.targetSelector.addGoal(2, new OwnerHurtTargetGoalTH(entity));
        entity.targetSelector.addGoal(3, new HurtByTargetGoalTH(entity, true));
    }

    @SubscribeEvent
    static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        if (entity instanceof Slime slime && slime.getSize() == 1) {
            if (slime instanceof dev.visfox.thaumichorizons.entity.MeatSlime) {
                ItemStack drop = switch (entity.getRandom().nextInt(5)) {
                    case 0 -> new ItemStack(Items.BEEF);
                    case 1 -> new ItemStack(Items.PORKCHOP);
                    case 2 -> new ItemStack(Items.CHICKEN);
                    case 3 -> new ItemStack(Items.COD);
                    default -> new ItemStack(Items.ROTTEN_FLESH);
                };
                entity.spawnAtLocation(level, drop);
            } else if (slime instanceof dev.visfox.thaumichorizons.entity.MercurialSlime) {
                ItemStack quicksilver = tcStack("quicksilver");
                if (!quicksilver.isEmpty()) {
                    entity.spawnAtLocation(level, quicksilver);
                }
            }
        }
        if (entity instanceof Player player) {
            CreatureInfusionData data = CreatureInfusion.get(player);
            if (data.tumorWarpPermanent() > 0 || data.tumorWarp() > 0 || data.tumorWarpTemp() > 0) {
                TcReforgedPort.warp.add(player, data.tumorWarpPermanent(), WarpKind.PERMANENT);
                TcReforgedPort.warp.add(player, data.tumorWarp(), WarpKind.STICKY);
                TcReforgedPort.warp.add(player, data.tumorWarpTemp(), WarpKind.TEMPORARY);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                CreatureInfusion.setInfusion(serverPlayer, 0, 0);
            }
            CreatureInfusion.DATA.set(player, CreatureInfusionData.EMPTY);
            returnDeathItems(player, level);
        }
    }

    private static void returnDeathItems(Player player, ServerLevel level) {
        if (!TcReforgedPort.baubles.isAvailable() || !TcReforgedPort.mirrors.isAvailable()) {
            return;
        }
        var baubles = TcReforgedPort.baubles;
        int amuletSlot = -1;
        for (int slot = 0; slot < baubles.slotCount(); slot++) {
            if (baubles.get(player, slot).getItem() instanceof AmuletMirrorItem) {
                amuletSlot = slot;
                break;
            }
        }
        if (amuletSlot < 0) {
            return;
        }
        ItemStack amulet = baubles.get(player, amuletSlot);
        boolean transported = false;
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && AmuletMirrorItem.transport(amulet, stack, player, level)) {
                player.getInventory().setItem(slot, ItemStack.EMPTY);
                transported = true;
            }
        }
        for (int slot = 0; slot < baubles.slotCount(); slot++) {
            if (slot == amuletSlot) {
                continue;
            }
            ItemStack stack = baubles.get(player, slot);
            if (!stack.isEmpty() && AmuletMirrorItem.transport(amulet, stack, player, level)) {
                baubles.set(player, slot, ItemStack.EMPTY);
                transported = true;
            }
        }
        if (!transported) {
            return;
        }
        baubles.set(player, amuletSlot, ItemStack.EMPTY);
        player.getInventory().setChanged();
        Vec3 eye = player.position().add(0.0, player.getEyeHeight(), 0.0);
        TcReforgedPort.fx.containmentCapture(level, eye, 29631);
        THSounds.play(level, eye, "craftfail", SoundSource.PLAYERS, 1.0F, 1.0F);
        Item pearl = TcReforgedPort.items.lookup(Identifier.parse("thaumcraft:primordial_pearl"));
        if (pearl != null) {
            ItemEntity drop = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), new ItemStack(pearl));
            drop.setUnlimitedLifetime();
            level.addFreshEntity(drop);
        }
    }

    @SubscribeEvent
    static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        if (entity instanceof ServerPlayer player) {
            if (CreatureInfusion.get(player).infusion(CreatureInfusion.SILVER_HEART) != 0
                && source.is(TAINT_DAMAGE)) {
                event.setCanceled(true);
                return;
            }
            if (event.getAmount() >= player.getHealth() && trySoulBeaconReturn(player, event)) {
                return;
            }
        }
        if (!entity.level().isClientSide()
            && source.getEntity() instanceof LivingEntity attacker
            && CreatureInfusions.hasInfusion(entity, ELDRITCH_FANGS)) {
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0, false, true));
        }
        if (!entity.level().isClientSide() && source.is(DamageTypeTags.IS_PROJECTILE)
            && CreatureInfusions.hasInfusion(entity, ENDER_HEART)) {
            event.setCanceled(true);
            teleport(entity);
        }
        if (!entity.level().isClientSide() && CreatureInfusions.hasInfusion(entity, RUNIC_CREATURE)) {
            int charge = entity.getPersistentData().getIntOr("runicCharge", 0);
            if (charge <= 0 || source.is(DamageTypes.DROWN) || source.is(DamageTypes.WITHER)
                || source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.STARVE)) {
                return;
            }
            float amount = event.getAmount();
            if (charge > amount) {
                entity.getPersistentData().putInt("runicCharge", (int) (charge - amount));
                event.setAmount(0.0F);
            } else {
                event.setAmount(amount - charge);
                entity.getPersistentData().putInt("runicCharge", 0);
            }
        }
    }

    private static boolean trySoulBeaconReturn(ServerPlayer player, LivingIncomingDamageEvent event) {
        SoulBeaconKeys.SoulBeaconData data = SoulBeaconKeys.DATA.get(player);
        if (!data.set()) {
            return false;
        }
        Identifier dimension = Identifier.tryParse(data.dimension());
        if (dimension == null) {
            return false;
        }
        ServerLevel current = player.level();
        ServerLevel beaconLevel = current.getServer().getLevel(ResourceKey.create(Registries.DIMENSION, dimension));
        if (beaconLevel == null) {
            return false;
        }
        BlockPos beaconPos = data.pos();
        if (!(beaconLevel.getBlockEntity(beaconPos) instanceof SoulBeaconBlockEntity)
            || !(beaconLevel.getBlockEntity(beaconPos.below()) instanceof VatBlockEntity vat) || vat.mode != 4) {
            return false;
        }

        event.setCanceled(true);
        event.setAmount(0.0F);
        current.explode(null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(),
            0.0F, false, Level.ExplosionInteraction.NONE);
        if (TcReforgedPort.flux.isAvailable()) {
            for (int index = 0; index < 25; index++) {
                int x = (int) player.getX() + current.getRandom().nextInt(8) - current.getRandom().nextInt(8);
                int y = (int) player.getY() + current.getRandom().nextInt(8) - current.getRandom().nextInt(8);
                int z = (int) player.getZ() + current.getRandom().nextInt(8) - current.getRandom().nextInt(8);
                BlockPos pos = new BlockPos(x, y, z);
                if (current.getBlockState(pos).isAir()) {
                    TcReforgedPort.flux.spill(current, pos, y > (int) player.getY() + 1);
                }
            }
        }
        player.getInventory().dropAll();
        if (TcReforgedPort.baubles.isAvailable()) {
            var baubles = TcReforgedPort.baubles;
            for (int slot = 0; slot < baubles.slotCount(); slot++) {
                ItemStack stack = baubles.get(player, slot);
                if (!stack.isEmpty()) {
                    player.spawnAtLocation(current, stack);
                    baubles.set(player, slot, ItemStack.EMPTY);
                }
            }
        }
        player.removeAllEffects();
        player.setHealth(player.getMaxHealth());
        Vec3 destination = new Vec3(beaconPos.getX() + 0.5, beaconPos.getY() - 2.5, beaconPos.getZ() + 0.5);
        if (beaconLevel == current) {
            player.teleportTo(destination.x, destination.y, destination.z);
        } else {
            thaumcraft.platform.Teleport.to(player, beaconLevel, destination, player.getYRot(), player.getXRot());
        }
        TcReforgedPort.fx.blockSparkle(beaconLevel, beaconPos.below(), 0xFFFFFF, 20);
        TcReforgedPort.fx.blockSparkle(beaconLevel, beaconPos.below(2), 0xFFFFFF, 20);
        THSounds.play(beaconLevel, beaconPos, "whispers", SoundSource.BLOCKS, 1.0F,
            beaconLevel.getRandom().nextFloat());
        for (int slot = 0; slot < vat.selfInfusions.length; slot++) {
            if (vat.selfInfusions[slot] != 0) {
                CreatureInfusion.setInfusion(player, slot, vat.selfInfusions[slot]);
            }
        }
        applyInfusions(player);
        vat.selfInfusions = new int[VatBlockEntity.SELF_INFUSION_SLOTS];
        vat.mode = 0;
        vat.setEntityContained(player);
        vat.sync();
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
        return true;
    }

    public static void teleport(LivingEntity entity) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }
        double targetX = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 64.0;
        double targetY = entity.getY() + (entity.getRandom().nextInt(64) - 32);
        double targetZ = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 64.0;
        double oldX = entity.getX();
        double oldY = entity.getY();
        double oldZ = entity.getZ();
        int blockX = Mth.floor(targetX);
        int blockZ = Mth.floor(targetZ);
        boolean teleported = false;
        int blockY = Mth.floor(targetY);
        if (level.hasChunkAt(new net.minecraft.core.BlockPos(blockX, blockY, blockZ))) {
            while (blockY > 0
                && !level.getBlockState(new net.minecraft.core.BlockPos(blockX, blockY - 1, blockZ)).isSolid()) {
                --blockY;
            }
            entity.snapTo(targetX, blockY, targetZ, entity.getYRot(), entity.getXRot());
            if (level.noCollision(entity, entity.getBoundingBox()) && !level.containsAnyLiquid(entity.getBoundingBox())) {
                teleported = true;
            }
        }
        if (!teleported) {
            entity.snapTo(oldX, oldY, oldZ, entity.getYRot(), entity.getXRot());
            return;
        }
        if (TcReforgedPort.fx.isAvailable()) {
            TcReforgedPort.fx.arcLightning(level,
                new Vec3(oldX, oldY, oldZ), new Vec3(entity.getX(), entity.getY(), entity.getZ()), 0xFFFFFF);
        }
        level.playSound(null, oldX, oldY, oldZ, SoundEvents.ENDERMAN_TELEPORT, SoundSource.NEUTRAL, 1.0F, 1.0F);
        entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
    }

    @SubscribeEvent
    static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity target = event.getTarget();
        if (!(target instanceof LivingEntity living) || event.getLevel().isClientSide()) {
            return;
        }
        if (CreatureInfusions.hasInfusion(living, PORTABILITY) && TcReforgedPort.items.isWand(event.getItemStack())) {
            jarIntoSoulJar(living, event.getItemStack());
        } else if (CreatureInfusions.hasInfusion(living, INSTILLED_LOYALTY)
            && event.getEntity().getName().getString().equals(CreatureInfusions.owner(living))) {
            boolean sitting = !CreatureInfusions.sitting(living);
            CreatureInfusions.setSitting(living, sitting);
            event.getEntity().sendSystemMessage(net.minecraft.network.chat.Component.literal(
                living.getName().getString() + (sitting ? " is waiting." : " will follow you.")));
        }
    }

    private static void jarIntoSoulJar(LivingEntity living, ItemStack wand) {
        Level level = living.level();
        ItemStack jar = new ItemStack(ThBlocks.SOUL_JAR.get());
        SoulJarKeys.DATA.set(jar, new SoulJarKeys.SoulJarData(
            EntityNbt.save(living), false, living.getName().getString(), ""));
        living.spawnAtLocation((ServerLevel) level, jar);
        if (TcReforgedPort.fx.isAvailable()) {
            TcReforgedPort.fx.containmentCapture(level,
                new Vec3(living.getX(), living.getY() + living.getBbHeight() / 2.0F, living.getZ()), 0xCC66FF);
        }
        living.discard();
    }

    private static ItemStack tcStack(String name) {
        var items = TcReforgedPort.items;
        net.minecraft.world.item.Item item = items.lookup(Identifier.fromNamespaceAndPath("thaumcraft", name));
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }
}
