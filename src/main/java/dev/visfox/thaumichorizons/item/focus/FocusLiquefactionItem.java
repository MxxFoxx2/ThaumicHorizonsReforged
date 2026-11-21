package dev.visfox.thaumichorizons.item.focus;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.item.tool.SpecialMining;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.Wands;
import thaumcraft.wand.focus.FocusItem;
import thaumcraft.wand.focus.FocusUpgradeType;

public class FocusLiquefactionItem extends FocusItem {
    public static final FocusUpgradeType NUGGETS = new FocusUpgradeType(33, "nuggets",
        Identifier.fromNamespaceAndPath("thaumichorizons", "textures/foci/nuggets.png"),
        new AspectList().add(Aspect.METAL, 8));
    public static final FocusUpgradeType PURITY = new FocusUpgradeType(34, "purity",
        Identifier.fromNamespaceAndPath("thaumichorizons", "textures/foci/purity.png"),
        new AspectList().add(Aspect.ORDER, 8));

    private static final AspectList COST = new AspectList().add(Aspect.FIRE, 40);
    private static final AspectList COST_CRITTER = new AspectList().add(Aspect.FIRE, 15);
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

    public FocusLiquefactionItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getSortingHelper(ItemStack focus) {
        return "BE" + super.getSortingHelper(focus);
    }

    @Override
    public int getFocusColor(ItemStack focus) {
        return 16711680;
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
        BlockHitResult hit = getTargetBlock(level, player);
        Entity entity = getPointedEntity(level, player, RANGE);
        Vec3 look = player.getLookAngle();
        double tx = player.getX() + look.x * RANGE;
        double ty = player.getEyeY() + look.y * RANGE;
        double tz = player.getZ() + look.z * RANGE;
        boolean impact = false;
        ItemStack droppedItem = ItemStack.EMPTY;
        ItemStack resultItem = ItemStack.EMPTY;
        if (entity instanceof ItemEntity itemEntity) {
            droppedItem = itemEntity.getItem();
            if (!droppedItem.isEmpty()) {
                ItemStack smeltingOutput = smeltingResult(level, droppedItem);
                if (!smeltingOutput.isEmpty()) {
                    resultItem = smeltingOutput.copy();
                }
            }
        }
        if (entity instanceof LivingEntity) {
            tx = entity.getX();
            ty = entity.getY();
            tz = entity.getZ();
            impact = true;
            if (!level.isClientSide() && state.soundDelay < System.currentTimeMillis()) {
                level.playSound(null, tx, ty, tz, SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 0.3F, 1.0F);
                state.soundDelay = System.currentTimeMillis() + 1200L;
            }
        } else if (resultItem.isEmpty() && hit != null) {
            tx = hit.getLocation().x;
            ty = hit.getLocation().y;
            tz = hit.getLocation().z;
            impact = true;
            if (!level.isClientSide() && state.soundDelay < System.currentTimeMillis()) {
                level.playSound(null, tx, ty, tz, SoundEvents.FIRE_AMBIENT, SoundSource.PLAYERS, 0.3F, 1.0F);
                state.soundDelay = System.currentTimeMillis() + 1200L;
            }
        } else {
            state.soundDelay = 0L;
        }
        if (entity instanceof LivingEntity living) {
            if (wand.consumeAllVis(stack, player, COST_CRITTER, true, true)) {
                living.hurtServer((ServerLevel) level, level.damageSources().inFire(),
                    1.0F + 0.5F * Wands.getFocusPotency(stack));
            }
            return;
        }
        if (!resultItem.isEmpty() && entity instanceof ItemEntity target) {
            int quantity = target.getItem().getCount();
            if (wand.consumeAllVis(stack, player, COST, true, true)) {
                ItemStack result = resultItem.copy();
                result.setCount(quantity);
                target.setItem(result);
            }
            return;
        }
        if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            BlockState blockState = level.getBlockState(pos);
            Block block = blockState.getBlock();
            ItemStack smeltInput = new ItemStack(block);
            ItemStack resultBlock = smeltingResult(level, smeltInput);
            int meltingBehaviour = isMeltableBlock(blockState);
            boolean flammable = blockState.isFlammable(level, pos, Direction.UP)
                || blockState.isFlammable(level, pos, Direction.DOWN);
            if (meltingBehaviour > 0 || flammable || !resultBlock.isEmpty()) {
                int potency = Wands.getFocusPotency(stack);
                float speed = 0.15F + potency * 0.05F;
                float hardness = 2.0F;
                if (meltingBehaviour == 6) {
                    hardness = 20.0F;
                } else if (meltingBehaviour > 0 || blockState.isFlammable(level, pos, Direction.UP)) {
                    hardness = 0.25F;
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
        state.last = null;
        state.breakCount = 0.0F;
    }

    @Override
    public void onPlayerStoppedUsingFocus(ItemStack wand, Level level, Player player, int count) {
        State state = state(player);
        state.last = null;
        state.breakCount = 0.0F;
    }

    public int isMeltableBlock(BlockState state) {
        if (state.is(Blocks.STONE)) {
            return 6;
        }
        if (state.is(Blocks.TNT)) {
            return 4;
        }
        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM) || state.is(Blocks.PODZOL)) {
            return 3;
        }
        if (state.is(Blocks.ICE) || state.is(Blocks.PACKED_ICE) || state.is(Blocks.BLUE_ICE)
            || state.is(Blocks.FROSTED_ICE)) {
            return 2;
        }
        if (state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.SNOW) || state.is(Blocks.POWDER_SNOW)
            || state.is(BlockTags.LEAVES) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.SAPLINGS)
            || state.is(BlockTags.CROPS) || state.is(Blocks.VINE) || state.is(Blocks.COBWEB)) {
            return 1;
        }
        return 0;
    }

    @Override
    public WandFocusAnimation getAnimation(ItemStack focus) {
        return WandFocusAnimation.CHARGE;
    }

    @Override
    public FocusUpgradeType @Nullable [] getPossibleUpgradesByRank(ItemStack focus, int rank) {
        return switch (rank) {
            case 1, 2, 4 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY, NUGGETS};
            case 3 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY,
                FocusUpgradeType.ENLARGE};
            case 5 -> new FocusUpgradeType[] {FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY, PURITY};
            default -> null;
        };
    }

    private void processBlock(BlockPos pos, WandItem wand, ItemStack stack, Player player, State state) {
        if (!(player.level() instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        BlockState blockState = serverLevel.getBlockState(pos);
        if (blockState.getDestroySpeed(serverLevel, pos) < 0.0F) {
            return;
        }
        if (CommonHooks.fireBlockBreak(serverLevel, serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer, pos,
            blockState).isCanceled()) {
            return;
        }
        int meltable = isMeltableBlock(blockState);
        boolean flammable = blockState.isFlammable(serverLevel, pos, Direction.UP);
        ItemStack result = smeltingResult(serverLevel, new ItemStack(blockState.getBlock()));
        if (meltable > 0 && wand.consumeAllVis(stack, player, COST, true, true)) {
            switch (meltable) {
                case 1 -> serverLevel.removeBlock(pos, false);
                case 2 -> {
                    if (serverLevel.dimension() != Level.NETHER) {
                        serverLevel.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
                    } else {
                        serverLevel.removeBlock(pos, false);
                    }
                }
                case 3 -> serverLevel.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                case 4 -> {
                    TntBlock.prime(serverLevel, pos);
                }
                case 6 -> serverLevel.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
                default -> {
                }
            }
        } else if (!result.isEmpty() && wand.consumeAllVis(stack, player, COST, true, true)) {
            ItemStack focusStack = Wands.getFocusItem(stack);
            if (isUpgradedWith(focusStack, PURITY)) {
                ItemStack pure = SpecialMining.find(blockState, new ItemStack(blockState.getBlock()), 1.0F,
                    serverLevel.getRandom());
                if (!pure.isEmpty()) {
                    ItemStack pureResult = smeltingResult(serverLevel, pure);
                    if (!pureResult.isEmpty()) {
                        result = pureResult;
                    }
                }
            }

            int nuggetLevel = Wands.getFocusUpgrade(stack, NUGGETS);
            if (nuggetLevel > 0) {
                Item bonusItem = TcReforgedPort.items.getSmeltingBonus(new ItemStack(blockState.getBlock()));
                if (bonusItem != null) {
                    int bonusCount = 0;
                    for (int i = 0; i < nuggetLevel; i++) {
                        if (serverLevel.getRandom().nextFloat() < 0.45F) {
                            bonusCount++;
                        }
                    }
                    if (bonusCount > 0) {
                        ItemStack bonusStack = new ItemStack(bonusItem, bonusCount);
                        ItemEntity bonusDrop = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5,
                            pos.getZ() + 0.5, bonusStack);
                        serverLevel.addFreshEntity(bonusDrop);
                    }
                }
            }

            Block resultBlock = Block.byItem(result.getItem());
            if (resultBlock != null) {
                serverLevel.setBlock(pos, resultBlock.defaultBlockState(), 3);
            } else {
                serverLevel.removeBlock(pos, false);
                ItemEntity item = new ItemEntity(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    result.copy());
                serverLevel.addFreshEntity(item);
            }
        } else if (flammable && wand.consumeAllVis(stack, player, COST, true, true)) {
            serverLevel.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
        }
        state.last = null;
        state.breakCount = 0.0F;
        serverLevel.sendBlockUpdated(pos, serverLevel.getBlockState(pos), serverLevel.getBlockState(pos), 3);
    }

    public static ItemStack smeltingResult(Level level, ItemStack input) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return ItemStack.EMPTY;
        }
        return serverLevel.recipeAccess()
            .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(input), serverLevel)
            .map(recipe -> recipe.value().assemble(new SingleRecipeInput(input))).orElse(ItemStack.EMPTY);
    }

    public static @Nullable BlockHitResult getTargetBlock(Level level, Player player) {
        Vec3 start = player.getEyePosition();
        Vec3 end = start.add(player.getViewVector(1.0F).scale(RANGE));
        BlockHitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
            ClipContext.Fluid.ANY, player));
        return hit.getType() == HitResult.Type.BLOCK ? hit : null;
    }

    public static @Nullable Entity getPointedEntity(Level world, LivingEntity entityplayer, double range) {
        Entity pointedEntity = null;
        Vec3 vec3d = new Vec3(entityplayer.getX(), entityplayer.getEyeY(), entityplayer.getZ());
        Vec3 vec3d2 = entityplayer.getLookAngle();
        Vec3 vec3d3 = vec3d.add(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range);
        AABB search = entityplayer.getBoundingBox()
            .expandTowards(vec3d2.x * range, vec3d2.y * range, vec3d2.z * range)
            .inflate(1.1F);
        List<Entity> list = world.getEntities(entityplayer, search, entity -> true);
        double d2 = 0.0D;
        for (Entity entity : list) {
            if (world.clip(new ClipContext(
                new Vec3(entityplayer.getX(), entityplayer.getEyeY(), entityplayer.getZ()),
                new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entityplayer)).getType()
                == HitResult.Type.MISS) {
                float f2 = Math.max(0.8F, entity.getPickRadius());
                AABB box = entity.getBoundingBox().inflate(f2, f2, f2);
                Vec3 intercept = box.clip(vec3d, vec3d3).orElse(null);
                if (box.contains(vec3d)) {
                    if (0.0D < d2 || d2 == 0.0D) {
                        pointedEntity = entity;
                        d2 = 0.0D;
                    }
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
        return Identifier.fromNamespaceAndPath("thaumichorizons", "item/focus_liquefaction");
    }
}
