package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.lib.EntityPlacement;

import dev.visfox.thaumichorizons.block.SoulBeaconBlock;
import dev.visfox.thaumichorizons.entity.GuardianPanther;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.compat.WarpKind;
import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import dev.visfox.thaumichorizons.infusion.ThInfusionRecipes;
import dev.visfox.thaumichorizons.item.SoulJarKeys;
import dev.visfox.thaumichorizons.item.SyringeKeys;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.lib.TcBlocks;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.essentia.Essentia;
import thaumcraft.essentia.EssentiaTransport;
import thaumcraft.vis.VisNet;

public class VatBlockEntity extends ThBlockEntity implements EssentiaTransport, Container, net.minecraft.world.MenuProvider {
    public static final int CLONE_PROGRESS = 800;
    public static final int COUNT_DELAY = 10;
    public static final int HEAL_ASPECT_COOLDOWN = 40;
    public static final int LIFE_ASPECT_COOLDOWN = 50;
    public static final int EFFIGY_PROGRESS = 80;
    public static final int BLOOD_SAMPLE_INITIAL_PROGRESS = 40;
    public static final int CRAFTCOUNT_WIND_DOWN_MAX = 50;
    public static final int INSTABILITY_EFFECT_CHANCE = 200;
    public static final int INSTABILITY_CRAFT_CHANCE = 500;
    public static final int INSTABILITY_INCREASE_CHANCE_BASE = 100;
    public static final int MAX_INSTABILITY = 25;
    public static final float STRONG_HEAL_AMOUNT = 8.0F;
    public static final float WEAK_HEAL_AMOUNT = 4.0F;
    public static final int EFFIGY_ESSENTIA_AMOUNT = 8;
    public static final int BLOOD_SAMPLE_LIFE_COST = 4;
    public static final int VIS_DRAIN_AMOUNT = 100;
    public static final float INSTABILITY_DAMAGE_DIVISOR = 10.0F;
    public static final int PEDESTAL_SEARCH_HORIZONTAL = 12;
    public static final int PEDESTAL_SEARCH_Y_DOWN = 5;
    public static final int PEDESTAL_SEARCH_Y_UP = 10;
    public static final int PEDESTAL_SYMMETRY_RANGE = 8;
    public static final int SELF_INFUSION_SLOTS = 12;
    public static final float SELF_INFUSION_INITIAL_HEALTH = 20.0F;
    public static final int INFUSER_PARTICLE_COUNT = 25;

    public int mode;
    public AspectList myEssentia = new AspectList();
    public AspectList essentiaDemanded = new AspectList();
    public @Nullable Aspect currentlySucking;
    public @Nullable ItemStack sample;
    public @Nullable ItemStack nutrients;
    public int progress;
    private @Nullable LivingEntity entityContained;
    public int[] selfInfusions = new int[SELF_INFUSION_SLOTS];
    public float selfInfusionHealth = SELF_INFUSION_INITIAL_HEALTH;
    private final ArrayList<BlockPos> pedestals = new ArrayList<>();
    public boolean checkSurroundings = true;
    public int symmetry;
    public int instability;
    private ArrayList<ItemStack> recipeIngredients = new ArrayList<>();
    private @Nullable Object recipeOutput;
    private @Nullable String recipePlayer;
    private int recipeInstability;
    public int recipeType;
    int itemCount;
    public int count;
    public int craftCount;
    public float startUp;
    private boolean firstTick = true;
    private @Nullable CompoundTag entityNbtObj;

    public VatBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.VAT.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VatBlockEntity entity) {
        entity.tick(level);
    }

    private void tick(Level tickLevel) {
        if (firstTick && entityNbtObj != null && tickLevel instanceof ServerLevel serverLevel) {
            loadContainedEntity(entityNbtObj, serverLevel);
            firstTick = false;
        }
        if (getEntityContained() != null && getEntityContained().isOnFire()) {
            getEntityContained().extinguishFire();
        }
        if (tickLevel.isClientSide()) {
            if (mode == 2) {
                doEffects(tickLevel);
            } else if (startUp > 0.0F) {
                startUp -= startUp / 10.0F;
                if (startUp < 0.001F) {
                    startUp = 0.0F;
                }
            }
            if (mode == 1) {
                TcReforgedPort.fx.blockSparkle(tickLevel, worldPosition.below(), 14184241, 1);
                TcReforgedPort.fx.blockSparkle(tickLevel, worldPosition.below(2), 14184241, 1);
            }
            return;
        }
        RandomSource random = tickLevel.getRandom();
        if (mode == 0) {
            essentiaDemanded = new AspectList();
            if (getEntityContained() != null) {
                if (getEntityContained().getHealth() < getEntityContained().getMaxHealth()) {
                    boolean undead = isUndead(getEntityContained());
                    Aspect strong = undead ? Aspect.UNDEAD : Aspect.HEAL;
                    Aspect weakAspect = undead ? Aspect.DEATH : Aspect.LIFE;
                    if (myEssentia.getAmount(strong) > 0 && progress <= 0) {
                        getEntityContained().heal(STRONG_HEAL_AMOUNT);
                        myEssentia.remove(strong, 1);
                        markAndSync();
                        progress += HEAL_ASPECT_COOLDOWN;
                    }
                    if (getEntityContained().getHealth() < getEntityContained().getMaxHealth()
                        && essentiaDemanded.getAmount(strong) < 1) {
                        essentiaDemanded.add(strong, 1);
                    }
                    if (myEssentia.getAmount(weakAspect) > 0 && progress <= 0) {
                        getEntityContained().heal(WEAK_HEAL_AMOUNT);
                        myEssentia.remove(weakAspect, 1);
                        markAndSync();
                        progress += LIFE_ASPECT_COOLDOWN;
                    }
                    if (getEntityContained().getHealth() < getEntityContained().getMaxHealth()
                        && essentiaDemanded.getAmount(weakAspect) < 1) {
                        essentiaDemanded.add(weakAspect, 1);
                    }
                }
                if (hasNegativeEffect(getEntityContained())) {
                    boolean undead = isUndead(getEntityContained());
                    Aspect strong = undead ? Aspect.UNDEAD : Aspect.HEAL;
                    if (myEssentia.getAmount(strong) > 0 && progress <= 0) {
                        removeNegativeEffects(getEntityContained());
                        myEssentia.remove(strong, 1);
                        markAndSync();
                        progress += 50;
                    } else if (essentiaDemanded.getAmount(strong) < 1) {
                        essentiaDemanded.add(strong, 1);
                    }
                }
                if (getEntityContained() instanceof Player player && player.getFoodData().needsFood()) {
                    if (myEssentia.getAmount(Aspect.HUNGER) > 0 && progress <= 0) {
                        player.getFoodData().eat(4, 2.0F);
                        markAndSync();
                        progress += 50;
                    }
                    if (player.getFoodData().needsFood() && essentiaDemanded.getAmount(Aspect.HUNGER) < 1) {
                        essentiaDemanded.add(Aspect.HUNGER, 1);
                    }
                }
            } else if (sample != null && sample.is(ThItems.CORPSE_EFFIGY.get())) {
                mode = 3;
                essentiaDemanded = new AspectList().add(Aspect.LIFE, EFFIGY_ESSENTIA_AMOUNT)
                    .add(Aspect.HEAL, EFFIGY_ESSENTIA_AMOUNT);
                progress = EFFIGY_PROGRESS;
                markAndSync();
            } else if (sample != null && nutrients != null) {
                mode = 1;
                essentiaDemanded = new AspectList().add(Aspect.LIFE, BLOOD_SAMPLE_LIFE_COST);
                SyringeKeys.SampleData sampleData = SyringeKeys.SAMPLE.get(sample);
                if (sampleData != null) {
                    for (int infusion : sampleData.infusions()) {
                        for (ThInfusionRecipes.CreatureInfusionRecipe recipe : ThInfusionRecipes.getCritterRecipes()) {
                            if (recipe.getID() == infusion) {
                                essentiaDemanded.add(recipe.getAspects());
                            }
                        }
                    }
                }
                progress = BLOOD_SAMPLE_INITIAL_PROGRESS;
                markAndSync();
            }
        } else if (mode == 1) {
            if (sample == null && getEntityContained() == null) {
                progress = 0;
                mode = 0;
                essentiaDemanded = new AspectList();
                myEssentia = new AspectList();
                return;
            }
            if (getEntityContained() == null && myEssentia.getAmount(Aspect.LIFE) >= 4) {
                if (sample != null && sample.is(ThItems.SYRINGE_BLOOD_SAMPLE.get())
                    && tickLevel instanceof ServerLevel serverLevel) {
                    SyringeKeys.SampleData sampleData = SyringeKeys.SAMPLE.get(sample);
                    CompoundTag critter = sampleData == null ? null : sampleData.entity();
                    if (critter != null && !critter.isEmpty()) {
                        Entity created = EntityNbt.load(critter, serverLevel);
                        if (created instanceof LivingEntity living) {
                            if (living instanceof TamableAnimal tameable) {
                                tameable.setTame(false, false);
                            }
                            setEntityContained(living);
                        }
                    }
                } else if (sample != null && tickLevel instanceof ServerLevel serverLevel) {
                    EntityType<?> type = incarnationType(sample.getItem());
                    if (type != null) {
                        Entity created = type.create(tickLevel);
                        if (created instanceof LivingEntity living) {
                            setEntityContained(living);
                        }
                    }
                }
                if (sample != null) {
                    sample.shrink(1);
                    if (sample.getCount() <= 0) {
                        sample = null;
                    }
                }
                if (nutrients != null) {
                    nutrients.shrink(1);
                    if (nutrients.getCount() <= 0) {
                        nutrients = null;
                    }
                }
                progress = CLONE_PROGRESS;
                essentiaDemanded = new AspectList();
                myEssentia = new AspectList();
                if (getEntityContained() == null) {
                    progress = 0;
                    mode = 0;
                    markAndSync();
                    return;
                }
                markAndSync();
            } else if (progress <= 0) {
                mode = 0;
                markAndSync();
            }
        } else if (mode == 2) {
            count++;
            if (checkSurroundings) {
                checkSurroundings = false;
                getSurroundings();
            } else if (count % COUNT_DELAY == 0) {
                craftCycle(tickLevel);
                setChanged();
            }
        } else if (mode == 3) {
            if (sample == null || !sample.is(ThItems.CORPSE_EFFIGY.get())) {
                progress = 0;
                mode = 0;
                essentiaDemanded = new AspectList();
                myEssentia = new AspectList();
                markAndSync();
                return;
            }
            if (progress <= 0 && myEssentia.getAmount(Aspect.LIFE) >= 8 && myEssentia.getAmount(Aspect.HEAL) >= 8) {
                THSounds.play(tickLevel, worldPosition, "wand", SoundSource.BLOCKS, 1.0F, random.nextFloat());
                TcReforgedPort.fx.blockSparkle(tickLevel, worldPosition.below(2), 16720418, 20);
                TcReforgedPort.fx.blockSparkle(tickLevel, worldPosition.below(), 16720418, 20);
                mode = 4;
                selfInfusionHealth = SELF_INFUSION_INITIAL_HEALTH;
                sample = null;
                essentiaDemanded = new AspectList();
                myEssentia = new AspectList();
                markAndSync();
            }
        }
        if (mode != 2 && needsEssentia()) {
            tryDrawAllEssentia(tickLevel);
        }
        if (progress > 0) {
            progress--;
            setChanged();
            tickLevel.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private void markAndSync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static boolean isUndead(LivingEntity entity) {
        return entity.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD);
    }

    private static @Nullable EntityType<?> vanillaEntityType(String name) {
        return BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.withDefaultNamespace(name));
    }

    private static @Nullable EntityType<?> incarnationType(Item item) {
        if (item == Items.BEEF) {
            return vanillaEntityType("cow");
        }
        if (item == Items.PORKCHOP) {
            return vanillaEntityType("pig");
        }
        if (item == Items.CHICKEN) {
            return vanillaEntityType("chicken");
        }
        return null;
    }

    boolean needsEssentia() {
        currentlySucking = null;
        if (progress > 0) {
            return false;
        }
        for (Aspect asp : essentiaDemanded.getAspects()) {
            if (myEssentia.getAmount(asp) < essentiaDemanded.getAmount(asp)) {
                currentlySucking = asp;
                break;
            }
        }
        return currentlySucking != null;
    }

    boolean tryDrawAllEssentia(Level drawLevel) {
        boolean drew = false;
        drew |= tryDrawEssentia(drawLevel, worldPosition.offset(-1, -3, 0));
        drew |= tryDrawEssentia(drawLevel, worldPosition.offset(1, -3, 0));
        drew |= tryDrawEssentia(drawLevel, worldPosition.offset(0, -3, -1));
        drew |= tryDrawEssentia(drawLevel, worldPosition.offset(0, -3, 1));
        return drew;
    }

    boolean tryDrawEssentia(Level drawLevel, BlockPos connPos) {
        if (!(drawLevel.getBlockEntity(connPos) instanceof VatConnectorBlockEntity)) {
            return false;
        }
        for (Direction dir : Direction.values()) {
            EssentiaTransport ic = Essentia.getConnectable(drawLevel, connPos, dir);
            if (ic != null) {
                Direction opposite = dir.getOpposite();
                if (ic.getEssentiaAmount(opposite) > 0 && ic.getSuctionAmount(opposite) < getSuctionAmount(null)
                    && getSuctionAmount(null) >= ic.getMinimumSuction()) {
                    for (Aspect asp : essentiaDemanded.getAspects()) {
                        if (mode == 2 || myEssentia.getAmount(asp) < essentiaDemanded.getAmount(asp)) {
                            int ess = ic.takeEssentia(asp, 1, opposite);
                            if (ess > 0) {
                                addToContainer(asp, ess);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasNegativeEffect(LivingEntity ent) {
        for (MobEffectInstance effect : ent.getActiveEffects()) {
            Holder<MobEffect> type = effect.getEffect();
            if (type.value() == MobEffects.POISON.value() || type.value() == MobEffects.BLINDNESS.value()
                || type.value() == MobEffects.HUNGER.value() || type.value() == MobEffects.WEAKNESS.value()
                || type.value() == MobEffects.WITHER.value() || type.value() == MobEffects.CONFUSION.value()
                || type.value() == MobEffects.DIG_SLOWDOWN.value() || type.value() == MobEffects.MOVEMENT_SLOWDOWN.value()) {
                return true;
            }
        }
        if (TcReforgedPort.potions.isAvailable()) {
            return ent.hasEffect(TcReforgedPort.potions.visExhaust())
                || ent.hasEffect(TcReforgedPort.potions.thaumarhia())
                || ent.hasEffect(TcReforgedPort.potions.taintPoison())
                || ent.hasEffect(TcReforgedPort.potions.blurredVision())
                || ent.hasEffect(TcReforgedPort.potions.infectiousVisExhaust());
        }
        return false;
    }

    private static void removeNegativeEffects(LivingEntity ent) {
        ent.removeEffect(MobEffects.BLINDNESS);
        ent.removeEffect(MobEffects.CONFUSION);
        ent.removeEffect(MobEffects.DIG_SLOWDOWN);
        ent.removeEffect(MobEffects.HUNGER);
        ent.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        ent.removeEffect(MobEffects.POISON);
        ent.removeEffect(MobEffects.WEAKNESS);
        ent.removeEffect(MobEffects.WITHER);
        if (TcReforgedPort.potions.isAvailable()) {
            ent.removeEffect(TcReforgedPort.potions.visExhaust());
            ent.removeEffect(TcReforgedPort.potions.thaumarhia());
            ent.removeEffect(TcReforgedPort.potions.taintPoison());
            ent.removeEffect(TcReforgedPort.potions.blurredVision());
            ent.removeEffect(TcReforgedPort.potions.infectiousVisExhaust());
        }
    }

    public boolean jarCritter(ItemStack possibleJar, Player player) {
        LivingEntity contained = getEntityContained();
        if (contained == null || !(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        ItemStack jar = new ItemStack(dev.visfox.thaumichorizons.registry.ThBlocks.SOUL_JAR.get());
        CompoundTag entityData = EntityNbt.save(contained);
        SoulJarKeys.SoulJarData data = new SoulJarKeys.SoulJarData(entityData, false, contained.getName().getString(), "");
        SoulJarKeys.DATA.set(jar, data);
        if (player.getInventory().add(jar)) {
            possibleJar.shrink(1);
            setEntityContained(null);
            markAndSync();
            return true;
        }
        return false;
    }

    public @Nullable LivingEntity getEntity() {
        return getEntityContained();
    }

    public boolean activate(Player player, boolean direct) {
        if (level == null) {
            return false;
        }
        ItemStack possibleJar = player.getMainHandItem();
        SoulJarKeys.SoulJarData jarData = possibleJar.isEmpty() ? null : SoulJarKeys.DATA.get(possibleJar);
        if (jarData != null && !jarData.soul()) {
            if (mode == 0 && getEntityContained() == null && giveEmptyJar(player)
                && level instanceof ServerLevel serverLevel) {
                Entity loaded = EntityNbt.load(jarData.entity(), serverLevel);
                if (loaded instanceof LivingEntity living) {
                    setEntityContained(living);
                    possibleJar.shrink(1);
                    markAndSync();
                    return true;
                }
            }
        } else if (!possibleJar.isEmpty() && TcReforgedPort.items.isEssentiaJar(possibleJar)
            && getEntityContained() != null && !(getEntityContained() instanceof Player)) {
            if (mode == 0) {
                return jarCritter(possibleJar, player);
            }
        } else if (mode == 4 && jarData != null && jarData.soul()) {
            if (selfInfusions[1] == 0 && giveEmptyJar(player)) {
                THSounds.play(level, worldPosition, "whispers", SoundSource.BLOCKS, 1.0F,
                    level.getRandom().nextFloat());
                TcReforgedPort.fx.blockSparkle(level, worldPosition.below(), 16777215, 20);
                TcReforgedPort.fx.blockSparkle(level, worldPosition.below(2), 16777215, 20);
                Villager villager = new Villager((EntityType<Villager>) vanillaEntityType("villager"), level);
                ResourceLocation professionId = ResourceLocation.tryParse(jarData.villagerType());
                VillagerProfession profession = professionId == null ? null
                    : BuiltInRegistries.VILLAGER_PROFESSION.get(professionId);
                if (profession != null) {
                    villager.setVillagerData(villager.getVillagerData().setProfession(profession));
                }
                setEntityContained(villager);
                mode = 0;
                selfInfusions = new int[SELF_INFUSION_SLOTS];
                possibleJar.shrink(1);
                markAndSync();
            }
        } else {
            if (mode == 0 && direct && getEntityContained() == null) {
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    serverPlayer.teleportTo(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5);
                }
                setEntityContained(player);
                markAndSync();
                return true;
            }
            if (mode == 0 && getEntityContained() == player) {
                boolean beaconAbove = level.getBlockState(worldPosition.above()).getBlock() instanceof SoulBeaconBlock;
                double yOffset = beaconAbove ? 2.0 : 1.0;
                if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                    serverPlayer.teleportTo(worldPosition.getX() + 0.5, worldPosition.getY() + yOffset,
                        worldPosition.getZ() + 0.5);
                }
                setEntityContained(null);
                markAndSync();
                return true;
            }
            player.openMenu(this, worldPosition);
            return true;
        }
        return false;
    }

    private boolean giveEmptyJar(Player player) {
        ItemStack jar = TcBlocks.emptyJar();
        return !jar.isEmpty() && player.getInventory().add(jar);
    }

    public void startInfusion(Player player) {
        if (level == null) {
            return;
        }
        getSurroundings();
        ArrayList<ItemStack> components = new ArrayList<>();
        for (BlockPos pedestalPos : pedestals) {
            BlockEntity te = level.getBlockEntity(pedestalPos);
            if (te instanceof Container pedestal) {
                ItemStack pedestalStack = pedestal.getItem(0);
                if (pedestalStack.isEmpty()) {
                    continue;
                }
                components.add(pedestalStack.copy());
            }
        }
        if (components.isEmpty()) {
            return;
        }
        if (mode != 4) {
            LivingEntity contained = getEntityContained();
            if (contained == null) {
                return;
            }
            ThInfusionRecipes.CreatureInfusionRecipe recipe =
                ThInfusionRecipes.getCreatureInfusion(contained, components, player);
            if (recipe == null || (recipe.getID() != 0 && CreatureInfusions.hasInfusion(contained, recipe.getID()))) {
                return;
            }
            if (recipe.getRecipeOutput() instanceof LinkedHashMap<?, ?> mods
                && ((LinkedHashMap<String, Integer>) mods).getOrDefault("instilledLoyalty", 0) != 0
                && contained instanceof Mob mob
                && mob.goalSelector.getAvailableGoals().stream().noneMatch(g -> g.isRunning())) {
                return;
            }
            recipeType = 0;
            recipeIngredients = new ArrayList<>();
            for (ItemStack ing : recipe.getComponents()) {
                recipeIngredients.add(ing.copy());
            }
            recipeOutput = recipe.getRecipeOutput();
            recipeInstability = recipe.getInstability();
            essentiaDemanded = recipe.getAspects().copy();
            myEssentia = recipe.getAspects().copy();
        } else {
            ThInfusionRecipes.SelfInfusionRecipe recipe2 = ThInfusionRecipes.getSelfInfusion(components, player);
            if (recipe2 == null) {
                return;
            }
            for (int selfInfusion : selfInfusions) {
                if (selfInfusion == recipe2.getID()) {
                    return;
                }
            }
            recipeType = 1;
            recipeIngredients = new ArrayList<>();
            for (ItemStack ing : recipe2.getComponents()) {
                recipeIngredients.add(ing.copy());
            }
            recipeOutput = recipe2.getID();
            recipeInstability = recipe2.getInstability();
            myEssentia = recipe2.getAspects().copy();
            essentiaDemanded = recipe2.getAspects().copy();
        }
        recipePlayer = player.getName().getString();
        instability = symmetry + recipeInstability;
        mode = 2;
        THSounds.play(level, worldPosition, "craftstart", SoundSource.BLOCKS, 0.5F, 1.0F);
        markAndSync();
    }

    public boolean isValidInfusionTarget() {
        LivingEntity contained = getEntityContained();
        if (contained == null || isUndead(contained) || contained instanceof Player
            || contained instanceof Villager
            || contained instanceof net.minecraft.world.entity.animal.AbstractGolem) {
            return false;
        }
        return !ThInfusionRecipes.isBanned(contained.getType(), contained);
    }

    private void getSurroundings() {
        if (level == null) {
            return;
        }
        ArrayList<BlockPos> stuff = new ArrayList<>();
        pedestals.clear();
        for (int xx = -PEDESTAL_SEARCH_HORIZONTAL; xx <= PEDESTAL_SEARCH_HORIZONTAL; xx++) {
            for (int zz = -PEDESTAL_SEARCH_HORIZONTAL; zz <= PEDESTAL_SEARCH_HORIZONTAL; zz++) {
                boolean skip = false;
                for (int yy = -PEDESTAL_SEARCH_Y_DOWN; yy <= PEDESTAL_SEARCH_Y_UP; yy++) {
                    if (xx != 0 || zz != 0) {
                        BlockPos pos = worldPosition.offset(xx, -yy, zz);
                        BlockEntity te = level.getBlockEntity(pos);
                        if (!skip && yy > 0 && Math.abs(xx) <= PEDESTAL_SYMMETRY_RANGE
                            && Math.abs(zz) <= PEDESTAL_SYMMETRY_RANGE && te instanceof Container pedestal
                            && pedestal.getContainerSize() > 0) {
                            pedestals.add(pos);
                            skip = true;
                        } else {
                            if (TcReforgedPort.infusion.isStabiliser(level, pos)) {
                                stuff.add(pos);
                            }
                        }
                    }
                }
            }
        }
        symmetry = 0;
        for (BlockPos pedestalPos : pedestals) {
            boolean items = false;
            if (level.getBlockEntity(pedestalPos) instanceof Container pedestal && pedestal.getContainerSize() > 0) {
                symmetry += 2;
                if (!pedestal.getItem(0).isEmpty()) {
                    symmetry++;
                    items = true;
                }
            }
            BlockPos mirrored = new BlockPos(worldPosition.getX() * 2 - pedestalPos.getX(), pedestalPos.getY(),
                worldPosition.getZ() * 2 - pedestalPos.getZ());
            if (level.getBlockEntity(mirrored) instanceof Container pedestal2 && pedestal2.getContainerSize() > 0) {
                symmetry -= 2;
                if (!pedestal2.getItem(0).isEmpty() && items) {
                    symmetry--;
                }
            }
        }
        float sym = 0.0F;
        for (BlockPos pos : stuff) {
            if (TcReforgedPort.infusion.isStabiliser(level, pos)) {
                sym += 0.1F;
            }
            BlockPos mirrored = new BlockPos(worldPosition.getX() * 2 - pos.getX(), pos.getY(),
                worldPosition.getZ() * 2 - pos.getZ());
            if (TcReforgedPort.infusion.isStabiliser(level, mirrored, pos)) {
                sym -= 0.2F;
            }
        }
        symmetry += (int) sym;
    }

    private void doEffects(Level fxLevel) {
        RandomSource random = fxLevel.getRandom();
        if (mode == 2) {
            if (craftCount == 0) {
                fxLevel.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                    clientSound("infuserstart"), SoundSource.BLOCKS, 0.5F, 1.0F, false);
            } else if (craftCount % 65 == 0) {
                fxLevel.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                    clientSound("infuser"), SoundSource.BLOCKS, 0.5F, 1.0F, false);
            }
            craftCount++;
        } else if (craftCount > 0) {
            craftCount -= 2;
            if (craftCount < 0) {
                craftCount = 0;
            }
            if (craftCount > CRAFTCOUNT_WIND_DOWN_MAX) {
                craftCount = CRAFTCOUNT_WIND_DOWN_MAX;
            }
        }
        if (mode == 2 && startUp != 1.0F) {
            if (startUp < 1.0F) {
                startUp += Math.max(startUp / 10.0F, 0.001F);
            }
            if (startUp > 0.999F) {
                startUp = 1.0F;
            }
        }
        if (mode == 2 && instability > 0 && random.nextInt(INSTABILITY_EFFECT_CHANCE) <= instability) {
            double tx = worldPosition.getX() + 0.5 + (random.nextFloat() - random.nextFloat()) * 2.0;
            double ty = worldPosition.getY() + 0.5 + (random.nextFloat() - random.nextFloat()) * 2.0;
            double tz = worldPosition.getZ() + 0.5 + (random.nextFloat() - random.nextFloat()) * 2.0;
            TcReforgedPort.fx.arcLightning(fxLevel,
                new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5),
                new Vec3(tx, ty, tz), 0xFFFFFF);
        }
    }

    private static net.minecraft.sounds.SoundEvent clientSound(String path) {
        net.minecraft.sounds.SoundEvent event = THSounds.sound(path);
        return event != null ? event : net.minecraft.sounds.SoundEvents.BEACON_ACTIVATE;
    }

    public void craftCycle(Level cycleLevel) {
        RandomSource random = cycleLevel.getRandom();
        if (instability > 0 && random.nextInt(INSTABILITY_CRAFT_CHANCE) <= instability) {
            switch (random.nextInt(21)) {
                case 0, 2, 10, 13 -> inEvEjectItem(cycleLevel, 0);
                case 6, 17 -> inEvEjectItem(cycleLevel, 1);
                case 1, 11 -> inEvEjectItem(cycleLevel, 2);
                case 3, 8, 14 -> inEvZap(cycleLevel, false);
                case 5, 16 -> inEvHarm(cycleLevel, false);
                case 12 -> inEvZap(cycleLevel, true);
                case 19 -> inEvEjectItem(cycleLevel, 3);
                case 7 -> inEvEjectItem(cycleLevel, 4);
                case 4, 15 -> inEvEjectItem(cycleLevel, 5);
                case 18 -> inEvHarm(cycleLevel, true);
                case 9 -> cycleLevel.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5, 1.5F + random.nextFloat(), false, Level.ExplosionInteraction.NONE);
                case 20 -> inEvWarp(cycleLevel);
                default -> {
                }
            }
        }
        if (instability > 0 && entityContained != null) {
            float visDrawn = 999.0F;
            float temp = VisNet.drainVis(cycleLevel, worldPosition.above(), Aspect.EARTH, VIS_DRAIN_AMOUNT);
            if (temp < visDrawn) {
                visDrawn = temp;
            }
            temp = VisNet.drainVis(cycleLevel, worldPosition.above(), Aspect.WATER, VIS_DRAIN_AMOUNT);
            if (temp < visDrawn) {
                visDrawn = temp;
            }
            temp = VisNet.drainVis(cycleLevel, worldPosition.above(), Aspect.ORDER, VIS_DRAIN_AMOUNT);
            if (temp < visDrawn) {
                visDrawn = temp;
            }
            entityContained.setHealth(entityContained.getHealth() - instability / INSTABILITY_DAMAGE_DIVISOR / (5.0F + visDrawn));
            markAndSync();
            if (entityContained.getHealth() <= 0.0F) {
                killSubject(cycleLevel);
                return;
            }
        } else if (instability > 0) {
            float visDrawn = 999.0F;
            float temp = VisNet.drainVis(cycleLevel, worldPosition.above(), Aspect.EARTH, VIS_DRAIN_AMOUNT);
            if (temp < visDrawn) {
                visDrawn = temp;
            }
            temp = VisNet.drainVis(cycleLevel, worldPosition.above(), Aspect.WATER, VIS_DRAIN_AMOUNT);
            if (temp < visDrawn) {
                visDrawn = temp;
            }
            temp = VisNet.drainVis(cycleLevel, worldPosition.above(), Aspect.ORDER, VIS_DRAIN_AMOUNT);
            if (temp < visDrawn) {
                visDrawn = temp;
            }
            selfInfusionHealth -= instability / INSTABILITY_DAMAGE_DIVISOR / (5.0F + visDrawn);
            markAndSync();
            if (selfInfusionHealth <= 0.0F) {
                killSubject(cycleLevel);
                return;
            }
        }
        if (essentiaDemanded.visSize() > 0) {
            for (Aspect aspect : essentiaDemanded.getAspects()) {
                if (essentiaDemanded.getAmount(aspect) > 0) {
                    currentlySucking = aspect;
                    if (tryDrawAllEssentia(cycleLevel)) {
                        markAndSync();
                        return;
                    }
                    if (random.nextInt(INSTABILITY_INCREASE_CHANCE_BASE - recipeInstability * 3) == 0) {
                        instability++;
                    }
                    if (instability > MAX_INSTABILITY) {
                        instability = MAX_INSTABILITY;
                    }
                    markAndSync();
                    break;
                }
            }
            checkSurroundings = true;
            markAndSync();
            return;
        }
        if (!recipeIngredients.isEmpty()) {
            for (int a = 0; a < recipeIngredients.size(); a++) {
                for (BlockPos pedestalPos : pedestals) {
                    BlockEntity te = cycleLevel.getBlockEntity(pedestalPos);
                    if (te instanceof Container pedestal && !pedestal.getItem(0).isEmpty()
                        && ItemStack.isSameItemSameComponents(pedestal.getItem(0), recipeIngredients.get(a))) {
                        if (itemCount == 0) {
                            itemCount = 5;
                            TcReforgedPort.fx.essentiaTrail(cycleLevel, pedestalPos,
                                new Vec3(worldPosition.getX(), worldPosition.getY() - 2, worldPosition.getZ()),
                                0xFFFFFF);
                        } else if (itemCount-- <= 1) {
                            net.minecraft.world.item.Item remainderItem =
                                pedestal.getItem(0).getItem().getCraftingRemainingItem();
                            ItemStack leftover = remainderItem == null ? ItemStack.EMPTY : new ItemStack(remainderItem);
                            pedestal.setItem(0, leftover.isEmpty() ? ItemStack.EMPTY : leftover.copy());
                            recipeIngredients.remove(a);
                        }
                        markAndSync();
                        return;
                    }
                }
            }
            return;
        }
        instability = 0;
        craftingFinish(cycleLevel, recipeOutput);
        recipeOutput = null;
        markAndSync();
    }

    private void inEvZap(Level zapLevel, boolean all) {
        List<LivingEntity> targets = zapLevel.getEntitiesOfClass(LivingEntity.class,
            new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1).inflate(10.0));
        for (Entity target : targets) {
            TcReforgedPort.fx.arcLightning(zapLevel,
                new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5),
                target.position().add(0.0, target.getBbHeight() / 2.0, 0.0), 0xFFFFFF);
            target.hurt(zapLevel.damageSources().magic(), 4 + zapLevel.getRandom().nextInt(4));
            if (!all) {
                break;
            }
        }
    }

    private void inEvHarm(Level harmLevel, boolean all) {
        List<LivingEntity> targets = harmLevel.getEntitiesOfClass(LivingEntity.class,
            new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1).inflate(10.0));
        for (LivingEntity target : targets) {
            if (TcReforgedPort.potions.isAvailable() && harmLevel.getRandom().nextBoolean()) {
                target.addEffect(new MobEffectInstance(TcReforgedPort.potions.visExhaust(), 2400, 0, false, true, false));
            } else {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0, false, true));
            }
            if (!all) {
                break;
            }
        }
    }

    private void inEvWarp(Level warpLevel) {
        List<Player> targets = warpLevel.getEntitiesOfClass(Player.class,
            new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1).inflate(10.0));
        if (!targets.isEmpty()) {
            Player target = targets.get(warpLevel.getRandom().nextInt(targets.size()));
            if (warpLevel.getRandom().nextFloat() < 0.25F) {
                TcReforgedPort.warp.add(target, 1, WarpKind.STICKY);
            } else {
                TcReforgedPort.warp.add(target, 1 + warpLevel.getRandom().nextInt(5), WarpKind.PERMANENT);
            }
        }
    }

    private void inEvEjectItem(Level ejectLevel, int type) {
        RandomSource random = ejectLevel.getRandom();
        for (int q = 0; q < 50 && !pedestals.isEmpty(); q++) {
            BlockPos pedestalPos = pedestals.get(random.nextInt(pedestals.size()));
            BlockEntity te = ejectLevel.getBlockEntity(pedestalPos);
            if (te instanceof Container pedestal && !pedestal.getItem(0).isEmpty()) {
                if (type < 3 || type == 5) {
                    for (int slot = 0; slot < pedestal.getContainerSize(); slot++) {
                        ItemStack stack = pedestal.removeItem(slot, pedestal.getItem(slot).getCount());
                        if (!stack.isEmpty()) {
                            ItemEntity item = new ItemEntity(ejectLevel, pedestalPos.getX() + 0.5,
                                pedestalPos.getY() + 0.5, pedestalPos.getZ() + 0.5, stack);
                            ejectLevel.addFreshEntity(item);
                        }
                    }
                } else {
                    pedestal.setItem(0, ItemStack.EMPTY);
                }
                if (type == 1 || type == 3) {
                    Block fluxGoo = TcBlocks.block("flux_goo");
                    if (fluxGoo != null) {
                        ejectLevel.setBlock(pedestalPos.above(), fluxGoo.defaultBlockState(), 3);
                    }
                    ejectLevel.playSound(null, pedestalPos, net.minecraft.sounds.SoundEvents.HONEY_BLOCK_STEP,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.3F, 1.0F);
                } else if (type == 2 || type == 4) {
                    Block fluxGas = TcBlocks.block("flux_gas");
                    if (fluxGas != null) {
                        ejectLevel.setBlock(pedestalPos.above(), fluxGas.defaultBlockState(), 3);
                    }
                    ejectLevel.playSound(null, pedestalPos, net.minecraft.sounds.SoundEvents.FIRE_EXTINGUISH,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.3F, 1.0F);
                } else if (type == 5) {
                    ejectLevel.explode(null, pedestalPos.getX() + 0.5, pedestalPos.getY() + 0.5,
                        pedestalPos.getZ() + 0.5, 1.0F, false, Level.ExplosionInteraction.NONE);
                }
                TcReforgedPort.fx.arcLightning(ejectLevel,
                    new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5),
                    new Vec3(pedestalPos.getX() + 0.5, pedestalPos.getY() + 1.5, pedestalPos.getZ() + 0.5),
                    0xFFFFFF);
                return;
            }
        }
    }

    public void craftingFinish(Level finishLevel, @Nullable Object out) {
        if (recipeType == 0) {
            LivingEntity contained = getEntityContained();
            if (contained == null) {
                mode = 0;
                return;
            }
            if (out instanceof Integer id && finishLevel instanceof ServerLevel serverLevel) {
                LivingEntity created = null;
                EntityType<?> type = ThInfusionRecipes.outputEntityType(id);
                if (type != null) {
                    created = createFromType(type, serverLevel, contained);
                }
                if (created != null) {
                    EntityPlacement.snapTo(created, contained.getX(), contained.getY(), contained.getZ(), contained.getYRot(), contained.getXRot());
                    setEntityContained(created);
                }
            } else if (out instanceof LinkedHashMap<?, ?> tagMods) {
                applyAttributeModifier(contained, Attributes.MOVEMENT_SPEED,
                    ((LinkedHashMap<String, Integer>) tagMods).getOrDefault("generic.movementSpeed", 0), 0.1);
                applyAttributeModifier(contained, Attributes.MAX_HEALTH,
                    ((LinkedHashMap<String, Integer>) tagMods).getOrDefault("generic.maxHealth", 0), 1.0);
                applyAttributeModifier(contained, Attributes.ATTACK_DAMAGE,
                    ((LinkedHashMap<String, Integer>) tagMods).getOrDefault("generic.attackDamage", 0), 1.0);
                for (Map.Entry<String, Integer> entry : ((LinkedHashMap<String, Integer>) tagMods).entrySet()) {
                    if (!entry.getKey().startsWith("generic.")) {
                        CreatureInfusions.addInfusion(contained, entry.getValue());
                        if (entry.getValue() == 7) {
                            CreatureInfusions.setOwner(contained, recipePlayer);
                        }
                    }
                }
            }
            if (entityContained instanceof Mob mob) {
                mob.setPersistenceRequired();
            }
            mode = 0;
        } else {
            for (int i = 0; i < selfInfusions.length; i++) {
                if (selfInfusions[i] == 0) {
                    selfInfusions[i] = (Integer) recipeOutput;
                    break;
                }
            }
            mode = 4;
        }
        TcReforgedPort.fx.burst(finishLevel,
            new Vec3(worldPosition.getX(), worldPosition.getY() - 1, worldPosition.getZ()), 0xFFFFFF, 16);
        essentiaDemanded = new AspectList();
        myEssentia = new AspectList();
        markAndSync();
    }

    private static @Nullable LivingEntity createFromType(EntityType<?> type, ServerLevel serverLevel,
        LivingEntity contained) {
        Entity created = type.create(serverLevel);
        if (!(created instanceof LivingEntity living)) {
            return null;
        }
        CompoundTag oldData = EntityNbt.save(contained);
        living.load(oldData);
        if (living instanceof GuardianPanther panther) {
            panther.resetStats();
        }
        return living;
    }

    private static void applyAttributeModifier(LivingEntity entity,
        Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, int value, double divisor) {
        if (value <= 0) {
            return;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            AttributeModifier modifier = new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("thaumichorizons", "vat_" + attribute.getRegisteredName().replace(":", "_")),
                value / divisor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            instance.addTransientModifier(modifier);
        }
    }

    public void killMe() {
        if (level == null) {
            return;
        }
        Level killLevel = level;
        if (entityContained != null) {
            killSubject(killLevel);
        }
        Block wooden = TcBlocks.block("arcane_pressure_plate");
        Block metal = TcBlocks.block("arcane_levitator");
        for (int y = 0; y < 4; y++) {
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    if (x != 0 || z != 0) {
                        if (y == 0 || y == 3) {
                            if (wooden != null) {
                                killLevel.setBlock(worldPosition.offset(x, -y, z), wooden.defaultBlockState(), 3);
                            }
                        } else {
                            killLevel.setBlock(worldPosition.offset(x, -y, z), Blocks.GLASS.defaultBlockState(), 3);
                        }
                    } else if (y == 0 || y == 3) {
                        if (metal != null) {
                            killLevel.setBlock(worldPosition.offset(x, -y, z), metal.defaultBlockState(), 3);
                        }
                    } else {
                        killLevel.setBlock(worldPosition.offset(x, -y, z), Blocks.WATER.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    public void killSubject(Level killLevel) {
        RandomSource random = killLevel.getRandom();
        if (!killLevel.isClientSide() && ((entityContained != null && !(entityContained instanceof Player)) || recipeType == 1)) {
            killLevel.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                0.5F, false, Level.ExplosionInteraction.NONE);
            for (int a = 0; a < 25; a++) {
                int xx = worldPosition.getX() + random.nextInt(8) - random.nextInt(8);
                int yy = worldPosition.getY() + random.nextInt(8) - random.nextInt(8);
                int zz = worldPosition.getZ() + random.nextInt(8) - random.nextInt(8);
                if (killLevel.getBlockState(new BlockPos(xx, yy, zz)).isAir()) {
                    Block flux = yy < worldPosition.getY() ? TcBlocks.block("flux_goo") : TcBlocks.block("flux_gas");
                    if (flux != null) {
                        killLevel.setBlock(new BlockPos(xx, yy, zz), flux.defaultBlockState(), 3);
                    }
                }
            }
        }
        selfInfusions = new int[SELF_INFUSION_SLOTS];
        setEntityContained(null);
        mode = 0;
        currentlySucking = null;
        myEssentia = new AspectList();
        essentiaDemanded = new AspectList();
        progress = 0;
        symmetry = 0;
        instability = 0;
        craftCount = 0;
        count = 0;
        markAndSync();
    }

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if (slot == 0) {
            return stack.is(ThItems.SYRINGE_BLOOD_SAMPLE.get()) || stack.is(Items.CHICKEN) || stack.is(Items.BEEF)
                || stack.is(Items.PORKCHOP);
        }
        return stack.is(ThItems.NUTRIENTS.get());
    }

    public @Nullable AspectList getAspects() {
        if (mode != 2) {
            if (!myEssentia.getAspects().isEmpty() && myEssentia.getAspects().get(0) != null) {
                return myEssentia;
            }
        } else {
            if (!essentiaDemanded.getAspects().isEmpty() && essentiaDemanded.getAspects().get(0) != null) {
                return essentiaDemanded;
            }
        }
        return null;
    }

    @Override
    public boolean isConnectable(@Nullable Direction face) {
        return mode == 1 && face == Direction.UP;
    }

    @Override
    public boolean canInputFrom(@Nullable Direction face) {
        return mode == 1 && face == Direction.UP;
    }

    @Override
    public boolean canOutputTo(@Nullable Direction face) {
        return false;
    }

    @Override
    public void setSuction(@Nullable Aspect aspect, int amount) {
    }

    @Override
    public @Nullable Aspect getSuctionType(@Nullable Direction face) {
        if (mode != 2) {
            return null;
        }
        return currentlySucking;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        return essentiaDemanded.size() > 0 ? 128 : 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return canInputFrom(face) ? amount - addToContainer(aspect, amount) : 0;
    }

    @Override
    public @Nullable Aspect getEssentiaType(@Nullable Direction face) {
        return null;
    }

    @Override
    public int getEssentiaAmount(@Nullable Direction face) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    public int addToContainer(Aspect tag, int amount) {
        if (mode != 2) {
            myEssentia.add(tag, amount);
        } else {
            essentiaDemanded.reduce(tag, amount);
        }
        if (level != null && !level.isClientSide()) {
            TcReforgedPort.fx.essentiaBubble(level,
                new BlockPos(worldPosition.getX(), worldPosition.getY() - 2, worldPosition.getZ()), tag.getColor());
        }
        markAndSync();
        return 0;
    }

    public int containerContains(Aspect tag) {
        return myEssentia.getAmount(tag);
    }

    public @Nullable LivingEntity getEntityContained() {
        return entityContained;
    }

    /**
     * Client-side serialized subject used for rendering the creature inside the Vat.
     * Treat the returned tag as read-only; the renderer caches a copy when it changes.
     */
    public @Nullable CompoundTag getEntityPreviewData() {
        if (entityNbtObj == null || "PLAYER".equals(entityNbtObj.getString("id"))) {
            return null;
        }
        return entityNbtObj;
    }

    public void setEntityContained(@Nullable LivingEntity newEntity) {
        entityContained = newEntity;
        if (entityContained != null) {
            EntityPlacement.snapTo(entityContained, worldPosition.getX() + 0.5, worldPosition.getY() - 1.75, worldPosition.getZ() + 0.5, 0.0F, 0.0F);
        }
    }

    private void loadContainedEntity(CompoundTag entityNbt, ServerLevel serverLevel) {
        String id = entityNbt.getString("id");
        if (id.equals("PLAYER")) {
            String name = entityNbt.getString("playerName");
            entityContained = serverLevel.getServer().getPlayerList().getPlayerByName(name);
        } else if (!id.isEmpty()) {
            Entity loaded = EntityNbt.load(entityNbt, serverLevel);
            if (loaded instanceof LivingEntity living) {
                entityContained = living;
            }
        }
    }

    private LinkedHashMap<String, Integer> tagToMods(CompoundTag tag) {
        LinkedHashMap<String, Integer> mods = new LinkedHashMap<>();
        for (String key : tag.getAllKeys()) {
            mods.put(key, (int) tag.getDouble(key));
        }
        return mods;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mode = input.getIntOr("mode", 0);
        progress = input.getIntOr("progress", 0);
        instability = input.getShortOr("instability", (short) 0);
        String sucking = input.getStringOr("sucking", "");
        currentlySucking = sucking.isEmpty() ? null : Aspect.getAspect(sucking);
        myEssentia = input.read("myEssentia", AspectList.CODEC).orElseGet(AspectList::new);
        essentiaDemanded = input.read("essentiaDemanded", AspectList.CODEC).orElseGet(AspectList::new);
        if (firstTick) {
            entityNbtObj = input.read("entity", CompoundTag.CODEC).orElse(null);
        } else if (level instanceof ServerLevel serverLevel) {
            input.read("entity", CompoundTag.CODEC).ifPresent(tag -> loadContainedEntity(tag, serverLevel));
        }
        sample = input.read("sample", ItemStack.OPTIONAL_CODEC).orElse(null);
        nutrients = input.read("nutrients", ItemStack.OPTIONAL_CODEC).orElse(null);
        int[] stored = input.getIntArray("selfInfusions").orElse(new int[0]);
        selfInfusions = stored.length == 0 ? new int[SELF_INFUSION_SLOTS] : stored;
        selfInfusionHealth = input.getFloatOr("selfInfusionHealth", SELF_INFUSION_INITIAL_HEALTH);
        recipeIngredients = new ArrayList<>();
        ValueInput.ValueInputList recipeIn = input.childrenList("recipein").orElse(null);
        if (recipeIn != null) {
            for (ValueInput child : recipeIn) {
                child.read("item", ItemStack.OPTIONAL_CODEC).ifPresent(recipeIngredients::add);
            }
        }
        String rot = input.getStringOr("rotype", "");
        if (rot.equals("@")) {
            recipeOutput = input.getIntOr("recipeout", 0);
        } else if (!rot.isEmpty()) {
            recipeOutput = input.read("recipeout", CompoundTag.CODEC).map(this::tagToMods).orElse(null);
        }
        recipeInstability = input.getIntOr("recipeinst", 0);
        recipeType = input.getIntOr("recipetype", 0);
        recipePlayer = input.getStringOr("recipeplayer", "");
        if (recipePlayer.isEmpty()) {
            recipePlayer = null;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("mode", mode);
        out.putInt("progress", progress);
        out.putShort("instability", (short) instability);
        out.putString("sucking", currentlySucking == null ? "" : currentlySucking.getTag());
        out.store("myEssentia", AspectList.CODEC, myEssentia);
        out.store("essentiaDemanded", AspectList.CODEC, essentiaDemanded);
        CompoundTag entityData = new CompoundTag();
        if (getEntityContained() != null && !(getEntityContained() instanceof Player)) {
            entityData = EntityNbt.save(getEntityContained());
        } else if (getEntityContained() != null) {
            entityData.putString("id", "PLAYER");
            entityData.putString("playerName", getEntityContained().getName().getString());
        }
        out.store("entity", CompoundTag.CODEC, entityData);
        if (sample != null) {
            out.store("sample", ItemStack.OPTIONAL_CODEC, sample);
        }
        if (nutrients != null) {
            out.store("nutrients", ItemStack.OPTIONAL_CODEC, nutrients);
        }
        out.putIntArray("selfInfusions", selfInfusions);
        out.putFloat("selfInfusionHealth", selfInfusionHealth);
        if (!recipeIngredients.isEmpty()) {
            ValueOutput.ValueOutputList recipeIn = out.childrenList("recipein");
            for (ItemStack stack : recipeIngredients) {
                ValueOutput child = recipeIn.addChild();
                child.store("item", ItemStack.OPTIONAL_CODEC, stack);
            }
        }
        if (recipeOutput instanceof Integer id) {
            out.putString("rotype", "@");
            out.putInt("recipeout", id);
        } else if (recipeOutput instanceof LinkedHashMap<?, ?> mods) {
            out.putString("rotype", "");
            CompoundTag tag = new CompoundTag();
            for (Map.Entry<String, Integer> entry : ((LinkedHashMap<String, Integer>) mods).entrySet()) {
                tag.putInt(entry.getKey(), entry.getValue());
            }
            out.store("recipeout", CompoundTag.CODEC, tag);
        }
        out.putInt("recipeinst", recipeInstability);
        out.putInt("recipetype", recipeType);
        out.putString("recipeplayer", recipePlayer == null ? "" : recipePlayer);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory,
        Player player) {
        return new dev.visfox.thaumichorizons.container.VatMenu(containerId, inventory, this);
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.thaumichorizons.vat");
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return (sample == null || sample.isEmpty()) && (nutrients == null || nutrients.isEmpty());
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot == 0) {
            return sample == null ? ItemStack.EMPTY : sample;
        }
        return nutrients == null ? ItemStack.EMPTY : nutrients;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack theStack = getItem(slot);
        if (theStack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (theStack.getCount() <= amount) {
            ItemStack outStack = theStack.copy();
            if (slot == 0) {
                sample = null;
            } else {
                nutrients = null;
            }
            setChanged();
            return outStack;
        }
        ItemStack outStack = theStack.split(amount);
        if (theStack.getCount() == 0) {
            if (slot == 0) {
                sample = null;
            } else {
                nutrients = null;
            }
        }
        setChanged();
        return outStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) {
            sample = null;
        } else {
            nutrients = null;
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            sample = stack;
        } else {
            nutrients = stack;
        }
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
            && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        sample = null;
        nutrients = null;
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null && !level.isClientSide()) {
            if (entityContained != null) {
                killSubject(level);
            }
            if (sample != null) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() - 0.5, sample));
                sample = null;
            }
            if (nutrients != null) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() - 0.5, nutrients));
                nutrients = null;
            }
        }
        super.preRemoveSideEffects(pos, state);
    }
}
