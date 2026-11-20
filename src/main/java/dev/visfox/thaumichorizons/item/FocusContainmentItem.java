package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.entity.LostSoul;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.lib.EntityRay;
import dev.visfox.thaumichorizons.lib.TcBlocks;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.registry.ThItems;
import java.util.HashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.entity.CombatUtils;
import thaumcraft.entity.golem.GolemBase;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.WandManager;
import thaumcraft.wand.Wands;
import thaumcraft.wand.focus.FocusItem;
import thaumcraft.wand.focus.FocusUpgradeType;

public class FocusContainmentItem extends FocusItem {
    public static final FocusUpgradeType SLOW = new FocusUpgradeType(36, "slow",
        net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumichorizons", "textures/foci/slow.png"),
        new AspectList().add(Aspect.TRAP, 8));
    public static final HashMap<String, Entity> hitCritters = new HashMap<>();
    public static final HashMap<String, Float> contain = new HashMap<>();
    public static final HashMap<String, Long> soundDelay = new HashMap<>();
    private static final AspectList COST = new AspectList().add(Aspect.AIR, 10).add(Aspect.ENTROPY, 10);

    public FocusContainmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public String getSortingHelper(ItemStack focus) {
        return "CN" + super.getSortingHelper(focus);
    }

    @Override
    public int getFocusColor(ItemStack focus) {
        return 29631;
    }

    @Override
    public AspectList getVisCost(ItemStack focus) {
        return COST.copy();
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack focus, int rank) {
        switch (rank) {
            case 1, 3, 5 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY };
            }
            case 2, 4 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.FRUGAL, FocusUpgradeType.POTENCY, SLOW };
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public InteractionResult onFocusRightClick(ItemStack wand, Level level, Player player, @Nullable HitResult hit) {
        CombatUtils.startUsingWand(player, wand);
        WandManager.setCooldown(player, -1);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUsingFocusTick(ItemStack wand, Player p, int count) {
        WandItem item = (WandItem) wand.getItem();
        if (!canJarEntity(p)) {
            if (p.tickCount % 5 == 0) {
                p.sendSystemMessage(Component.translatable("thaumichorizons.noJar")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            }
            p.stopUsingItem();
            return;
        }
        if (!item.consumeAllVis(wand, p, getVisCost(wand), false, false)) {
            p.stopUsingItem();
            return;
        }
        String pp = "R" + p.getName().getString();
        Level world = p.level();
        Entity ent = EntityRay.getPointedEntity(world, p, 10.0);
        if (ent instanceof Mob) {
            long delay = soundDelay.getOrDefault(pp, 0L);
            if (!world.isClientSide() && delay < System.currentTimeMillis()) {
                THSounds.play(world, ent.blockPosition(), "jacobs", SoundSource.PLAYERS, 0.3F, 1.0F);
                soundDelay.put(pp, System.currentTimeMillis() + 1200L);
            }
        }
        if (ent != null && ent instanceof Mob mob && !(ent instanceof Player) && !ent.isRemoved() && !isBoss(ent)
            && !(ent instanceof GolemBase) && item.consumeAllVis(wand, p, getVisCost(wand), true, false)) {
            ItemStack focusItem = Wands.getFocusItem(wand);
            int slowLevel = focusItem == null ? 0 : getUpgradeLevel(focusItem, SLOW);
            if (slowLevel > 0) {
                mob.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, slowLevel - 1, false, true));
            }
            if (hitCritters.get(pp) == null || ent.getId() != hitCritters.get(pp).getId()) {
                hitCritters.put(pp, ent);
                contain.put(pp, 2.0F + Wands.getFocusPotency(wand) / 2.0F);
            } else {
                contain.put(pp, contain.get(pp) + 2.0F + Wands.getFocusPotency(wand) / 3.0F);
            }
            if (!world.isClientSide() && contain.get(pp) > mob.getHealth() * 20.0F) {
                boolean captured = ent instanceof LostSoul
                    ? jarSoul(world, p, ent)
                    : jarEntity(world, p, ent);
                if (captured) {
                    THSounds.play(world, ent.blockPosition(), "craftfail", SoundSource.PLAYERS, 1.0F, 1.0F);
                    ent.discard();
                }
                contain.remove(pp);
                hitCritters.remove(pp);
            }
        }
    }

    private static boolean isBoss(Entity ent) {
        return ent instanceof net.minecraft.world.entity.boss.enderdragon.EnderDragon
            || ent instanceof net.minecraft.world.entity.boss.wither.WitherBoss
            || ent instanceof net.minecraft.world.entity.monster.warden.Warden;
    }

    private boolean jarEntity(Level world, Player p, Entity ent) {
        CompoundTag entityData = EntityNbt.save(ent);
        if (!consumeEmptyJar(p)) {
            return false;
        }
        ItemStack jar = new ItemStack(dev.visfox.thaumichorizons.registry.ThBlocks.SOUL_JAR.get());
        SoulJarKeys.SoulJarData data = new SoulJarKeys.SoulJarData(entityData, false, ent.getName().getString(), "");
        SoulJarKeys.DATA.set(jar, data);
        if (!p.getInventory().add(jar)) {
            p.drop(jar, false, net.minecraft.util.Prediction.PREDICTED);
        }
        TcReforgedPort.fx.containmentCapture(world, ent.position(), 29631);
        return true;
    }

    private boolean jarSoul(Level world, Player p, Entity ent) {
        if (!consumeEmptyJar(p)) {
            return false;
        }
        ItemStack jar = new ItemStack(dev.visfox.thaumichorizons.registry.ThBlocks.SOUL_JAR.get());
        SoulJarKeys.DATA.set(jar, new SoulJarKeys.SoulJarData(new CompoundTag(), true, "", ""));
        if (!p.getInventory().add(jar)) {
            p.drop(jar, false, net.minecraft.util.Prediction.PREDICTED);
        }
        TcReforgedPort.fx.containmentCapture(world, ent.position(), 29631);
        return true;
    }

    private static boolean consumeEmptyJar(Player p) {
        ItemStack jarItem = TcBlocks.emptyJar();
        if (jarItem.isEmpty()) {
            return false;
        }
        Inventory inventory = p.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty() && ItemStack.isSameItemSameComponents(stack, jarItem)) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    boolean canJarEntity(Player p) {
        ItemStack jarItem = TcBlocks.emptyJar();
        if (jarItem.isEmpty()) {
            return false;
        }
        Inventory inventory = p.getInventory();
        boolean hasJar = inventory.hasAnyMatching(stack -> !stack.isEmpty()
            && ItemStack.isSameItemSameComponents(stack, jarItem));
        if (!hasJar) {
            return false;
        }

        // Consuming the final empty jar frees its slot for the filled Soul Jar.
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty()
                || ItemStack.isSameItemSameComponents(stack, jarItem) && stack.getCount() == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPlayerStoppedUsingFocus(ItemStack wand, Level level, Player player, int count) {
        String key = "R" + player.getName().getString();
        hitCritters.remove(key);
        contain.remove(key);
        soundDelay.remove(key);
    }

    @Override
    public boolean isVisCostPerTick(ItemStack focus) {
        return true;
    }
}
