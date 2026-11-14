package dev.visfox.thaumichorizons.item.focus;

import dev.visfox.thaumichorizons.entity.InvulnerableItemEntity;
import dev.visfox.thaumichorizons.lib.THSounds;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.jspecify.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectHelper;
import thaumcraft.aspect.AspectKeys;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;
import thaumcraft.wand.focus.FocusItem;
import thaumcraft.wand.focus.FocusUpgradeType;

public class FocusDisintegrationItem extends FocusItem {
    public static final FocusUpgradeType ENERVATION = new FocusUpgradeType(35, "enervation",
        Identifier.fromNamespaceAndPath("thaumichorizons", "textures/foci/enervation.png"),
        new AspectList().add(Aspect.DEATH, 8));

    private static final ResourceKey<DamageType> DISSOLVE =
        ResourceKey.create(Registries.DAMAGE_TYPE, Thaumcraft.id("dissolve"));

    private static final AspectList COST = new AspectList()
        .add(Aspect.FIRE, 30)
        .add(Aspect.ENTROPY, 30)
        .add(Aspect.EARTH, 30);
    private static final AspectList COST_CRITTER = new AspectList()
        .add(Aspect.FIRE, 12)
        .add(Aspect.ENTROPY, 12)
        .add(Aspect.EARTH, 12);
    private static final double RANGE = 10.0;

    private static final Map<UUID, State> STATES = new HashMap<>();

    private static final class State {
        private long soundDelay;
        private float breakCount;
        private @Nullable BlockPos last;
    }

    private static State state(Player player) {
        return STATES.computeIfAbsent(player.getUUID(), key -> new State());
    }

    public FocusDisintegrationItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getSortingHelper(ItemStack focus) {
        return "DS" + super.getSortingHelper(focus);
    }

    @Override
    public int getFocusColor(ItemStack focus) {
        return 7930047;
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focus) {
        return false;
    }

    @Override
    public InteractionResult onFocusRightClick(ItemStack stack, Level level, Player player, @Nullable HitResult hit) {
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUsingFocusTick(ItemStack stack, Player player, int count) {
        if (!(stack.getItem() instanceof WandItem wand)) {
            return;
        }
        if (!wand.consumeAllVis(stack, player, COST, false, true)) {
            player.stopUsingItem();
            return;
        }
        Level level = player.level();
        State state = state(player);
        BlockHitResult hit = FocusLiquefactionItem.getTargetBlock(level, player);
        Entity entity = getPointedEntity(level, player, RANGE);
        Vec3 look = player.getLookAngle();
        double tx = player.getX() + look.x * RANGE;
        double ty = player.getEyeY() + look.y * RANGE;
        double tz = player.getZ() + look.z * RANGE;
        if ((entity == null || entity instanceof ItemEntity item && !hasAspects(item)) && hit != null) {
            tx = hit.getLocation().x;
            ty = hit.getLocation().y;
            tz = hit.getLocation().z;
            if (!level.isClientSide() && state.soundDelay < System.currentTimeMillis()) {
                THSounds.play(level, new Vec3(tx, ty, tz), "zap", SoundSource.PLAYERS, 0.3F, 1.0F);
                state.soundDelay = System.currentTimeMillis() + 100L;
            }
        } else if (entity != null) {
            tx = entity.getX();
            ty = entity.getY();
            tz = entity.getZ();
            if (!level.isClientSide() && state.soundDelay < System.currentTimeMillis()) {
                THSounds.play(level, new Vec3(tx, ty, tz), "zap", SoundSource.PLAYERS, 0.3F, 1.0F);
                state.soundDelay = System.currentTimeMillis() + 100L;
            }
        }
        if ((entity == null || (entity instanceof ItemEntity item && !hasAspects(item))) && hit != null) {
            BlockPos pos = hit.getBlockPos();
            BlockState blockState = level.getBlockState(pos);
            Block block = blockState.getBlock();
            AspectList blockAspects = getAspects(block.asItem());
            if (!block.asItem().equals(Items.AIR) && blockAspects != null) {
                int potency = Wands.getFocusPotency(stack);
                float speed = 0.05F + potency * 0.015F;
                float hardness = blockState.getDestroySpeed(level, pos);
                if (hardness < 0.0F) {
                    return;
                }
                if (pos.equals(state.last)) {
                    if (level.isClientSide()) {
                        state.breakCount = state.breakCount >= hardness ? 0.0F : state.breakCount + speed;
                    } else if (state.breakCount >= hardness) {
                        processBlock(pos, wand, stack, player, state);
                    } else {
                        state.breakCount = state.breakCount + speed;
                    }
                } else {
                    state.last = pos.immutable();
                    state.breakCount = 0.0F;
                }
            }
            return;
        }
        if (entity instanceof LivingEntity living
            && wand.consumeAllVis(stack, player, COST_CRITTER, true, true)) {
            ItemStack focusStack = Wands.getFocusItem(stack);
            if (isUpgradedWith(focusStack, ENERVATION)) {
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
            }
            if (level instanceof ServerLevel serverLevel) {
                living.hurtServer(serverLevel, level.damageSources().source(DISSOLVE),
                    0.5F + 0.25F * Wands.getFocusPotency(stack)
                        + 0.5F * getUpgradeLevel(Wands.getFocusItem(stack), ENERVATION));
            }
            return;
        }
        if (entity instanceof ItemEntity itemEntity && hasAspects(itemEntity)
            && wand.consumeAllVis(stack, player, COST, true, true)) {
            int num = itemEntity.getItem().getCount();
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                AspectList aspects = getAspects(itemEntity.getItem().getItem());
                for (Aspect asp : aspects == null ? java.util.List.<Aspect>of() : aspects.getAspects()) {
                    ItemStack essence = new ItemStack(
                        BuiltInRegistries.ITEM.getValue(Thaumcraft.id("crystallized_essence")),
                        aspects == null ? 0 : aspects.getAmount(asp) * num);
                    if (!essence.isEmpty()) {
                        AspectKeys.ASPECTS.set(essence, new AspectList().add(asp, 1));
                        serverLevel.addFreshEntity(new InvulnerableItemEntity(serverLevel, entity.getX(), entity.getY(),
                            entity.getZ(), essence));
                    }
                }
                entity.discard();
            }
            return;
        }
        state.last = null;
        state.breakCount = 0.0F;
    }

    private static boolean hasAspects(ItemEntity itemEntity) {
        AspectList aspects = getAspects(itemEntity.getItem().getItem());
        return aspects != null && aspects.size() > 0;
    }

    @Override
    public void onPlayerStoppedUsingFocus(ItemStack wand, Level level, Player player, int count) {
        State state = state(player);
        state.last = null;
        state.breakCount = 0.0F;
    }

    @Override
    public WandFocusAnimation getAnimation(ItemStack focus) {
        return WandFocusAnimation.CHARGE;
    }

    @Override
    public FocusUpgradeType @Nullable [] getPossibleUpgradesByRank(ItemStack focus, int rank) {
        return switch (rank) {
            case 1, 2, 4 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY};
            case 3 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY,
                FocusUpgradeType.ENLARGE};
            case 5 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY, ENERVATION};
            default -> null;
        };
    }

    private void processBlock(BlockPos pos, WandItem wand, ItemStack stack, Player player, State state) {
        if (!(player.level() instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        BlockState blockState = serverLevel.getBlockState(pos);
        Block block = blockState.getBlock();
        if (block.asItem().equals(Items.AIR) || blockState.getDestroySpeed(serverLevel, pos) < 0.0F) {
            return;
        }
        if (CommonHooks.fireBlockBreak(serverLevel, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer, pos,
            blockState).isCanceled()) {
            return;
        }
        AspectList aspects = getAspects(block.asItem());
        if (aspects != null && aspects.size() > 0 && wand.consumeAllVis(stack, player, COST, true, true)) {
            for (Aspect asp : aspects.getAspects()) {
                ItemStack essence = new ItemStack(
                    BuiltInRegistries.ITEM.getValue(Thaumcraft.id("crystallized_essence")), aspects.getAmount(asp));
                if (!essence.isEmpty()) {
                    AspectKeys.ASPECTS.set(essence, new AspectList().add(asp, 1));
                    serverLevel.addFreshEntity(new InvulnerableItemEntity(serverLevel, pos.getX() + 0.5,
                        pos.getY() + 0.5, pos.getZ() + 0.5, essence));
                }
            }
            serverLevel.removeBlock(pos, false);
        }
        state.last = null;
        state.breakCount = 0.0F;
        serverLevel.sendBlockUpdated(pos, serverLevel.getBlockState(pos), serverLevel.getBlockState(pos), 3);
    }

    private static @Nullable AspectList getAspects(Item item) {
        ItemStack stack = new ItemStack(item);
        AspectList tags = AspectHelper.getObjectTags(stack);
        tags = AspectHelper.getBonusTags(stack, tags);
        return tags != null && tags.size() > 0 ? tags : null;
    }

    public static @Nullable Entity getPointedEntity(Level world, LivingEntity entityplayer, double range) {
        Entity pointedEntity = null;
        Vec3 vec3d = new Vec3(entityplayer.getX(), entityplayer.getEyeY(), entityplayer.getZ());
        Vec3 vec3d2 = entityplayer.getLookAngle();
        Vec3 vec3d3 = vec3d.add(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range);
        AABB search = entityplayer.getBoundingBox()
            .expandTowards(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range)
            .inflate(1.1F);
        Item essenceItem = BuiltInRegistries.ITEM.getValue(Thaumcraft.id("crystallized_essence"));
        double d2 = 0.0D;
        for (Entity entity : world.getEntities(entityplayer, search, entity -> true)) {
            if (entity instanceof ItemEntity itemEntity && essenceItem != null
                && itemEntity.getItem().is(essenceItem)) {
                continue;
            }
            if (world.clip(new ClipContext(
                new Vec3(entityplayer.getX(), entityplayer.getEyeY(), entityplayer.getZ()),
                new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entityplayer)).getType()
                == HitResult.Type.MISS) {
                float f2 = Math.max(0.8F, entity.getPickRadius());
                AABB box = entity.getBoundingBox().inflate(f2, f2, f2);
                Vec3 intercept = box.clip(vec3d, vec3d3).orElse(null);
                if (box.contains(vec3d)) {
                    pointedEntity = entity;
                    d2 = 0.0D;
                } else if (intercept != null) {
                    double d3 = vec3d.distanceTo(intercept);
                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity;
                        d2 = d3;
                    }
                }
            }
        }
        return pointedEntity;
    }

    @Override
    public net.minecraft.resources.@Nullable Identifier getFocusTexture(ItemStack focus) {
        return Identifier.fromNamespaceAndPath("thaumichorizons", "item/focus_disintegration");
    }
}
