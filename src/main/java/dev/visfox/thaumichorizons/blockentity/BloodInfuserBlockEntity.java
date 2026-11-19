package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThEffects;
import dev.visfox.thaumichorizons.registry.ThItems;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.essentia.Essentia;
import thaumcraft.essentia.EssentiaTransport;

public class BloodInfuserBlockEntity extends ThBlockEntity implements EssentiaTransport, Container, net.minecraft.world.MenuProvider {
    public AspectList aspectsSelected = new AspectList();
    public AspectList aspectsAcquired = new AspectList();
    public @Nullable Aspect currentlySucking;
    public int mode;
    @Nullable
    private ItemStack syringe;
    public final ItemStack[] output = new ItemStack[9];
    public int color = 16777215;
    private final HashMap<Aspect, HashMap<Holder<MobEffect>, Integer>> effectWeights = new HashMap<>();
    private final HashMap<Holder<MobEffect>, Float> duration = new HashMap<>();

    public BloodInfuserBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.BLOOD_INFUSER.get(), pos, state);
        buildWeights();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BloodInfuserBlockEntity entity) {
        entity.tick(level);
    }

    private void tick(Level tickLevel) {
        if (tickLevel.isClientSide()) {
            return;
        }
        currentlySucking = null;
        if (mode > 0 && syringe != null && !syringe.isEmpty() && emptyOutputSlot()) {
            for (Aspect asp : aspectsSelected.getAspects()) {
                if (aspectsAcquired.getAmount(asp) < aspectsSelected.getAmount(asp)) {
                    currentlySucking = asp;
                    break;
                }
            }
            if (currentlySucking == null && !aspectsSelected.isEmpty() && !aspectsAcquired.getAspects().isEmpty()) {
                ItemStack theInjection = new ItemStack(ThItems.SYRINGE_INJECTION.get());
                List<MobEffectInstance> effects = getCurrentEffects();
                PotionContents contents = new PotionContents(java.util.Optional.empty(), java.util.Optional.<Integer>empty(), effects, java.util.Optional.<String>empty());
                theInjection.set(DataComponents.POTION_CONTENTS, contents);
                removeItem(0, 1);
                for (int i = 0; i < 9; i++) {
                    if (output[i] == null || output[i].isEmpty()) {
                        output[i] = theInjection;
                        break;
                    }
                }
                aspectsAcquired = new AspectList();
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                }
                if (mode == 1) {
                    mode = 0;
                }
            } else {
                tryDrawEssentia();
            }
        }
    }

    public List<MobEffectInstance> getCurrentEffects() {
        List<MobEffectInstance> effectList = new ArrayList<>();
        HashMap<Holder<MobEffect>, Integer> effects = new HashMap<>();
        int totalEssentia = 0;
        int green = 0;
        int red = 0;
        int blue = 0;
        for (Aspect asp : aspectsSelected.getAspects()) {
            if (effectWeights.get(asp) != null) {
                for (Map.Entry<Holder<MobEffect>, Integer> entry : effectWeights.get(asp).entrySet()) {
                    effects.merge(entry.getKey(), aspectsSelected.getAmount(asp) * entry.getValue(), Integer::sum);
                }
                totalEssentia += aspectsSelected.getAmount(asp);
                Color col = new Color(asp.getColor());
                red += col.getRed() * aspectsSelected.getAmount(asp);
                blue += col.getBlue() * aspectsSelected.getAmount(asp);
                green += col.getGreen() * aspectsSelected.getAmount(asp);
            }
        }
        if (totalEssentia > 0) {
            red /= totalEssentia;
            green /= totalEssentia;
            blue /= totalEssentia;
        }
        for (int i = 0; i < (totalEssentia + 1) / 2; i++) {
            int largestWeight = 0;
            Holder<MobEffect> largestEffect = null;
            for (Map.Entry<Holder<MobEffect>, Integer> entry : effects.entrySet()) {
                if (entry.getValue() > largestWeight) {
                    largestWeight = entry.getValue();
                    largestEffect = entry.getKey();
                }
            }
            if (largestEffect == null) {
                break;
            }
            int amplifier = largestWeight / 30;
            int effectDuration = 100 * largestWeight;
            if (largestEffect.value() == MobEffects.INSTANT_DAMAGE.value()
                || largestEffect.value() == MobEffects.INSTANT_HEALTH.value()) {
                effectDuration = 1;
            }
            effectList.add(new MobEffectInstance(largestEffect, effectDuration, amplifier, false, true));
            effects.remove(largestEffect);
        }
        color = new Color(clamp(red), clamp(green), clamp(blue)).getRGB();
        return effectList;
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public HashMap<Holder<MobEffect>, Integer> getEffects(Aspect asp) {
        return effectWeights.get(asp);
    }

    public void setEssentiaSelected(AspectList as) {
        aspectsSelected = as.copy();
        sync();
    }

    public boolean emptyOutputSlot() {
        for (int i = 0; i < 9; i++) {
            if (output[i] == null || output[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    void tryDrawEssentia() {
        if (level == null || currentlySucking == null) {
            return;
        }
        for (Direction dir : Direction.values()) {
            if (dir == Direction.UP) {
                continue;
            }
            EssentiaTransport ic = Essentia.getConnectable(level, worldPosition, dir);
            if (ic != null) {
                Direction opposite = dir.getOpposite();
                if (ic.getEssentiaAmount(opposite) > 0 && ic.getSuctionAmount(opposite) < getSuctionAmount(null)
                    && getSuctionAmount(null) >= ic.getMinimumSuction()) {
                    int ess = ic.takeEssentia(currentlySucking, 1, opposite);
                    if (ess > 0) {
                        addToContainer(currentlySucking, ess);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public boolean isConnectable(@Nullable Direction face) {
        return face != Direction.UP;
    }

    @Override
    public boolean canInputFrom(@Nullable Direction face) {
        return face != Direction.UP;
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
        if (face == Direction.UP) {
            return null;
        }
        return currentlySucking;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        if (face == Direction.UP) {
            return 0;
        }
        return currentlySucking != null ? 128 : 0;
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
        aspectsAcquired.add(tag, amount);
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return 0;
    }

    public int containerContains(Aspect tag) {
        return aspectsAcquired.getAmount(tag);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory,
        Player player) {
        return new dev.visfox.thaumichorizons.container.BloodInfuserMenu(containerId, inventory, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumichorizons.bloodInfuser");
    }

    @Override
    public int getContainerSize() {
        return 10;
    }

    @Override
    public boolean isEmpty() {
        if (syringe != null && !syringe.isEmpty()) {
            return false;
        }
        for (ItemStack stack : output) {
            if (stack != null && !stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot == 0) {
            return syringe == null ? ItemStack.EMPTY : syringe;
        }
        if (slot <= 9) {
            ItemStack stack = output[slot - 1];
            return stack == null ? ItemStack.EMPTY : stack;
        }
        return ItemStack.EMPTY;
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
                syringe = null;
            } else {
                output[slot - 1] = null;
            }
            setChanged();
            return outStack;
        }
        ItemStack outStack = theStack.split(amount);
        if (theStack.getCount() == 0) {
            if (slot == 0) {
                syringe = null;
            } else {
                output[slot - 1] = null;
            }
        }
        setChanged();
        return outStack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        if (slot == 0) {
            syringe = null;
        } else if (slot <= 9) {
            output[slot - 1] = null;
        }
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot == 0) {
            syringe = stack;
        } else if (slot < 10) {
            output[slot - 1] = stack;
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
            && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        syringe = null;
        for (int i = 0; i < 9; i++) {
            output[i] = null;
        }
    }

    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return slot == 0 && stack.is(ThItems.SYRINGE_BLOOD.get());
    }

    public boolean hasBlood() {
        if (syringe != null && !syringe.isEmpty() && syringe.getCount() > 0) {
            return true;
        }
        for (int i = 0; i < 9; i++) {
            if (output[i] != null && !output[i].isEmpty() && output[i].getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    public void dropContents(Level dropLevel, BlockPos pos) {
        if (syringe != null && !syringe.isEmpty()) {
            dropLevel.addFreshEntity(new ItemEntity(dropLevel, pos.getX(), pos.getY(), pos.getZ(), syringe));
        }
        syringe = null;
        for (int i = 0; i < 9; i++) {
            if (output[i] != null && !output[i].isEmpty()) {
                dropLevel.addFreshEntity(new ItemEntity(dropLevel, pos.getX(), pos.getY(), pos.getZ(), output[i]));
            }
            output[i] = null;
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        mode = input.getIntOr("mode", 0);
        String sucking = input.getStringOr("sucking", "");
        currentlySucking = sucking.isEmpty() ? null : Aspect.getAspect(sucking);
        aspectsSelected = input.read("aspectsSelected", AspectList.CODEC).orElseGet(AspectList::new);
        aspectsAcquired = input.read("aspectsAcquired", AspectList.CODEC).orElseGet(AspectList::new);
        syringe = input.read("syringe", ItemStack.OPTIONAL_CODEC).orElse(null);
        List<ItemStack> items = input.read("items", ItemStack.OPTIONAL_CODEC.listOf()).orElseGet(ArrayList::new);
        for (int i = 0; i < 9; i++) {
            ItemStack stack = i < items.size() ? items.get(i) : ItemStack.EMPTY;
            this.output[i] = stack.isEmpty() ? null : stack;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput out) {
        super.saveAdditional(out);
        out.putInt("mode", mode);
        out.putString("sucking", currentlySucking == null ? "" : currentlySucking.getTag());
        out.store("aspectsSelected", AspectList.CODEC, aspectsSelected);
        out.store("aspectsAcquired", AspectList.CODEC, aspectsAcquired);
        if (syringe != null) {
            out.store("syringe", ItemStack.OPTIONAL_CODEC, syringe);
        }
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            items.add(this.output[i] == null ? ItemStack.EMPTY : this.output[i]);
        }
        out.store("items", ItemStack.OPTIONAL_CODEC.listOf(), items);
    }

    private void buildWeights() {
        Holder<MobEffect> speed = MobEffects.SPEED;
        Holder<MobEffect> slow = MobEffects.SLOWNESS;
        Holder<MobEffect> haste = MobEffects.HASTE;
        Holder<MobEffect> fatigue = MobEffects.MINING_FATIGUE;
        Holder<MobEffect> strength = MobEffects.STRENGTH;
        Holder<MobEffect> health = MobEffects.INSTANT_HEALTH;
        Holder<MobEffect> harm = MobEffects.INSTANT_DAMAGE;
        Holder<MobEffect> jump = MobEffects.JUMP_BOOST;
        Holder<MobEffect> nausea = MobEffects.NAUSEA;
        Holder<MobEffect> regen = MobEffects.REGENERATION;
        Holder<MobEffect> resist = MobEffects.RESISTANCE;
        Holder<MobEffect> fireres = MobEffects.FIRE_RESISTANCE;
        Holder<MobEffect> water = MobEffects.WATER_BREATHING;
        Holder<MobEffect> invis = MobEffects.INVISIBILITY;
        Holder<MobEffect> blind = MobEffects.BLINDNESS;
        Holder<MobEffect> night = MobEffects.NIGHT_VISION;
        Holder<MobEffect> hunger = MobEffects.HUNGER;
        Holder<MobEffect> weak = MobEffects.WEAKNESS;
        Holder<MobEffect> poison = MobEffects.POISON;
        Holder<MobEffect> wither = MobEffects.WITHER;
        Holder<MobEffect> hboost = MobEffects.HEALTH_BOOST;
        Holder<MobEffect> satur = MobEffects.SATURATION;
        Holder<MobEffect> visBoost = Compat.port().potions().isAvailable() ? Compat.port().potions().visExhaust() : null;
        Holder<MobEffect> visRegen = ThEffects.VIS_REGEN;
        Holder<MobEffect> vacuum = ThEffects.VACUUM;
        Holder<MobEffect> shock = ThEffects.SHOCK;
        Holder<MobEffect> synth = ThEffects.SYNTHESIS;
        duration.put(speed, 1.2F);
        duration.put(slow, 0.8F);
        duration.put(haste, 1.2F);
        duration.put(fatigue, 0.8F);
        duration.put(strength, 1.2F);
        duration.put(jump, 1.2F);
        duration.put(nausea, 0.2F);
        duration.put(regen, 0.6F);
        duration.put(resist, 1.2F);
        duration.put(fireres, 1.2F);
        duration.put(water, 1.2F);
        duration.put(invis, 1.2F);
        duration.put(blind, 0.2F);
        duration.put(night, 1.2F);
        duration.put(hunger, 0.4F);
        duration.put(weak, 0.8F);
        duration.put(poison, 0.6F);
        duration.put(wither, 0.4F);
        duration.put(hboost, 1.2F);
        duration.put(satur, 0.4F);
        if (visBoost != null) {
            duration.put(visBoost, 1.2F);
        }
        duration.put(visRegen, 0.6F);
        duration.put(vacuum, 1.2F);
        duration.put(shock, 0.8F);
        duration.put(synth, 1.5F);
        HashMap<Holder<MobEffect>, Integer> map = new HashMap<>();
        map.put(speed, 3);
        map.put(jump, 4);
        map.put(water, 3);
        effectWeights.put(Aspect.AIR, map);
        map = new HashMap<>();
        map.put(slow, 3);
        map.put(haste, 4);
        map.put(strength, 1);
        map.put(resist, 2);
        effectWeights.put(Aspect.EARTH, map);
        map = new HashMap<>();
        map.put(speed, 1);
        map.put(strength, 2);
        map.put(fireres, 6);
        map.put(night, 1);
        effectWeights.put(Aspect.FIRE, map);
        map = new HashMap<>();
        map.put(speed, 2);
        map.put(haste, 2);
        map.put(regen, 1);
        map.put(fireres, 2);
        map.put(water, 3);
        effectWeights.put(Aspect.WATER, map);
        map = new HashMap<>();
        map.put(slow, 2);
        map.put(fatigue, 3);
        map.put(resist, 3);
        map.put(hboost, 2);
        effectWeights.put(Aspect.ORDER, map);
        map = new HashMap<>();
        map.put(harm, 1);
        map.put(nausea, 1);
        map.put(hunger, 1);
        map.put(weak, 2);
        map.put(poison, 2);
        map.put(wither, 3);
        effectWeights.put(Aspect.ENTROPY, map);
        map = new HashMap<>();
        map.put(invis, 4);
        map.put(hunger, 2);
        map.put(vacuum, 4);
        effectWeights.put(Aspect.VOID, map);
        map = new HashMap<>();
        map.put(blind, 2);
        map.put(night, 8);
        effectWeights.put(Aspect.LIGHT, map);
        map = new HashMap<>();
        map.put(speed, 1);
        map.put(jump, 2);
        map.put(blind, 1);
        map.put(shock, 8);
        effectWeights.put(Aspect.WEATHER, map);
        map = new HashMap<>();
        map.put(speed, 5);
        map.put(jump, 5);
        effectWeights.put(Aspect.MOTION, map);
        map = new HashMap<>();
        map.put(slow, 2);
        map.put(fatigue, 2);
        map.put(resist, 2);
        map.put(invis, 2);
        map.put(weak, 2);
        effectWeights.put(Aspect.COLD, map);
        map = new HashMap<>();
        map.put(fatigue, 2);
        map.put(strength, 2);
        map.put(invis, 6);
        effectWeights.put(Aspect.CRYSTAL, map);
        map = new HashMap<>();
        map.put(health, 2);
        map.put(regen, 6);
        map.put(hboost, 2);
        effectWeights.put(Aspect.LIFE, map);
        map = new HashMap<>();
        map.put(fatigue, 1);
        map.put(nausea, 1);
        map.put(hunger, 1);
        map.put(poison, 6);
        map.put(weak, 1);
        effectWeights.put(Aspect.POISON, map);
        map = new HashMap<>();
        map.put(speed, 2);
        map.put(haste, 2);
        map.put(strength, 1);
        map.put(jump, 1);
        map.put(visRegen, 1);
        map.put(shock, 3);
        effectWeights.put(Aspect.ENERGY, map);
        map = new HashMap<>();
        map.put(speed, 2);
        map.put(haste, 2);
        map.put(jump, 1);
        map.put(weak, 2);
        map.put(vacuum, 3);
        effectWeights.put(Aspect.EXCHANGE, map);
        map = new HashMap<>();
        map.put(slow, 2);
        map.put(haste, 2);
        map.put(strength, 2);
        map.put(resist, 4);
        effectWeights.put(Aspect.METAL, map);
        map = new HashMap<>();
        map.put(harm, 6);
        map.put(nausea, 1);
        map.put(hunger, 1);
        map.put(weak, 1);
        map.put(wither, 1);
        effectWeights.put(Aspect.DEATH, map);
        map = new HashMap<>();
        map.put(speed, 2);
        map.put(jump, 8);
        effectWeights.put(Aspect.FLIGHT, map);
        map = new HashMap<>();
        map.put(invis, 4);
        map.put(blind, 5);
        map.put(night, 1);
        effectWeights.put(Aspect.DARKNESS, map);
        map = new HashMap<>();
        map.put(health, 2);
        map.put(regen, 1);
        map.put(invis, 3);
        map.put(night, 2);
        map.put(hboost, 1);
        map.put(visRegen, 1);
        effectWeights.put(Aspect.SOUL, map);
        map = new HashMap<>();
        map.put(health, 6);
        map.put(regen, 2);
        map.put(hboost, 2);
        effectWeights.put(Aspect.HEAL, map);
        map = new HashMap<>();
        map.put(speed, 8);
        map.put(jump, 2);
        effectWeights.put(Aspect.TRAVEL, map);
        map = new HashMap<>();
        map.put(invis, 2);
        map.put(blind, 2);
        map.put(night, 2);
        if (visBoost != null) {
            map.put(visBoost, 2);
        }
        map.put(visRegen, 2);
        effectWeights.put(Aspect.ELDRITCH, map);
        map = new HashMap<>();
        if (visBoost != null) {
            map.put(visBoost, 5);
        }
        map.put(visRegen, 5);
        effectWeights.put(Aspect.MAGIC, map);
        map = new HashMap<>();
        map.put(visRegen, 10);
        effectWeights.put(Aspect.AURA, map);
        map = new HashMap<>();
        if (visBoost != null) {
            map.put(visBoost, 2);
        }
        map.put(wither, 2);
        effectWeights.put(Aspect.TAINT, map);
        map = new HashMap<>();
        map.put(slow, 4);
        map.put(fatigue, 2);
        map.put(nausea, 2);
        map.put(poison, 2);
        effectWeights.put(Aspect.SLIME, map);
        map = new HashMap<>();
        map.put(satur, 2);
        map.put(synth, 8);
        effectWeights.put(Aspect.PLANT, map);
        map = new HashMap<>();
        map.put(slow, 2);
        map.put(resist, 2);
        map.put(synth, 6);
        effectWeights.put(Aspect.TREE, map);
        map = new HashMap<>();
        map.put(speed, 1);
        map.put(strength, 5);
        map.put(regen, 1);
        map.put(water, 1);
        map.put(night, 1);
        map.put(hboost, 1);
        effectWeights.put(Aspect.BEAST, map);
        map = new HashMap<>();
        map.put(regen, 2);
        map.put(hboost, 2);
        map.put(satur, 6);
        effectWeights.put(Aspect.FLESH, map);
        map = new HashMap<>();
        map.put(hunger, 2);
        map.put(wither, 2);
        map.put(harm, 6);
        effectWeights.put(Aspect.UNDEAD, map);
        map = new HashMap<>();
        map.put(night, 2);
        if (visBoost != null) {
            map.put(visBoost, 3);
        }
        map.put(visRegen, 3);
        map.put(vacuum, 2);
        effectWeights.put(Aspect.MIND, map);
        map = new HashMap<>();
        map.put(invis, 4);
        map.put(night, 6);
        effectWeights.put(Aspect.SENSES, map);
        map = new HashMap<>();
        map.put(health, 3);
        map.put(regen, 3);
        map.put(hboost, 4);
        effectWeights.put(Aspect.MAN, map);
        map = new HashMap<>();
        map.put(satur, 6);
        map.put(synth, 4);
        effectWeights.put(Aspect.CROP, map);
        map = new HashMap<>();
        map.put(haste, 10);
        effectWeights.put(Aspect.MINE, map);
        map = new HashMap<>();
        map.put(speed, 1);
        map.put(haste, 8);
        map.put(resist, 1);
        effectWeights.put(Aspect.TOOL, map);
        map = new HashMap<>();
        map.put(haste, 2);
        map.put(health, 1);
        map.put(satur, 5);
        map.put(synth, 2);
        effectWeights.put(Aspect.HARVEST, map);
        map = new HashMap<>();
        map.put(haste, 2);
        map.put(strength, 8);
        effectWeights.put(Aspect.WEAPON, map);
        map = new HashMap<>();
        map.put(resist, 8);
        map.put(fireres, 2);
        effectWeights.put(Aspect.ARMOR, map);
        map = new HashMap<>();
        map.put(weak, 1);
        map.put(satur, 8);
        map.put(vacuum, 1);
        effectWeights.put(Aspect.HUNGER, map);
        map = new HashMap<>();
        map.put(satur, 2);
        map.put(vacuum, 8);
        effectWeights.put(Aspect.GREED, map);
        map = new HashMap<>();
        map.put(fatigue, 2);
        map.put(health, 2);
        map.put(resist, 2);
        map.put(hboost, 4);
        effectWeights.put(Aspect.CRAFT, map);
        map = new HashMap<>();
        map.put(fatigue, 2);
        map.put(resist, 4);
        map.put(blind, 2);
        map.put(weak, 2);
        effectWeights.put(Aspect.CLOTH, map);
        map = new HashMap<>();
        map.put(speed, 2);
        map.put(haste, 4);
        map.put(strength, 4);
        effectWeights.put(Aspect.MECHANISM, map);
        map = new HashMap<>();
        map.put(slow, 8);
        map.put(fatigue, 2);
        effectWeights.put(Aspect.TRAP, map);
    }
}
