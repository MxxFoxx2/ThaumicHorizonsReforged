package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.entity.BlastPhialProjectile;
import dev.visfox.thaumichorizons.entity.SyringeProjectile;
import dev.visfox.thaumichorizons.registry.ThItems;
import dev.visfox.thaumichorizons.item.SyringeKeys.InjectorVisualData;
import net.minecraft.world.entity.LivingEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class InjectorItem extends Item {
    public static final int MAX_DAMAGE = 1000;

    public InjectorItem(Properties properties) {
        super(properties.durability(MAX_DAMAGE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            if (!world.isClientSide() && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                serverPlayer.openMenu(new dev.visfox.thaumichorizons.container.InjectorMenuProvider());
            }
        } else if (getAmmo(stack, 0) == null) {
            rotateAmmo(stack);
            world.playSound(null, player.getX(), player.getY() + player.getEyeHeight(), player.getZ(),
                net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
        } else {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
        return 72000;
    }

    @Override
    public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int ticksRemaining) {
        int usedTicks = Math.max(0, getUseDuration(stack, livingEntity) - ticksRemaining);
        SyringeKeys.INJECTOR_VISUAL.update(stack, InjectorVisualData.EMPTY,
            visual -> visual.withUseTicks(usedTicks));
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        SyringeKeys.INJECTOR_VISUAL.update(stack, InjectorVisualData.EMPTY,
            visual -> visual.withUseTicks(0));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
        ItemStack ammo = getAmmo(stack, 0);
        if (!(entity instanceof Player player) || ammo == null) {
            SyringeKeys.INJECTOR_VISUAL.update(stack, InjectorVisualData.EMPTY,
                visual -> visual.withUseTicks(0));
            return;
        }

        int charge = getUseDuration(stack, entity) - timeLeft;
        float draw = charge / 30.0F;
        draw = (draw * draw + draw * 2.0F) / 3.0F;
        if (draw < 0.1F) {
            SyringeKeys.INJECTOR_VISUAL.update(stack, InjectorVisualData.EMPTY,
                visual -> visual.withUseTicks(0));
            return;
        }
        draw = Math.min(draw, 1.0F);

        int gameTick = (int) (world.getGameTime() & 0x7FFFFFFFL);
        InjectorVisualData previousVisual =
            SyringeKeys.INJECTOR_VISUAL.getOrDefault(stack, InjectorVisualData.EMPTY);
        int currentRotation = Math.round(previousVisual.rotationAt(gameTick));
        int nextRotationTarget = Math.floorMod(previousVisual.rotationTarget() + 90, 360);
        SyringeKeys.INJECTOR_VISUAL.set(stack,
            new InjectorVisualData(0, currentRotation, nextRotationTarget, gameTick));

        if (!world.isClientSide()) {
            float projectilePower = draw * 2.0F;
            if (ThItems.SYRINGE_INJECTION.get().isPhial(ammo)) {
                world.addFreshEntity(new BlastPhialProjectile(world, player, ammo, projectilePower));
            } else {
                SyringeProjectile projectile = new SyringeProjectile(world, player, ammo, projectilePower, stack);
                HolderGetter<Enchantment> enchantments = player.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                int power = stack.getEnchantmentLevel(enchantments.getOrThrow(Enchantments.POWER));
                projectile.setBaseDamage(1.5D + (power > 0 ? power * 0.5D + 0.5D : 0.0D));
                if (draw >= 1.0F) {
                    projectile.setCritArrow(true);
                }
                if (stack.getEnchantmentLevel(enchantments.getOrThrow(Enchantments.FLAME)) > 0) {
                    projectile.igniteForTicks(100);
                }
                world.addFreshEntity(projectile);
            }

            stack.hurtAndBreak(1, player,
                player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            world.playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.ARROW_SHOOT,
                net.minecraft.sounds.SoundSource.PLAYERS, 1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F) + draw * 0.5F);
            rotateAmmo(stack);
        }
    }

    public @Nullable ItemStack getAmmo(ItemStack stack, int slot) {
        SyringeKeys.AmmoData data = SyringeKeys.AMMO.get(stack);
        if (data == null || slot < 0 || slot >= data.ammo().size()) {
            return null;
        }
        ItemStack ammo = data.ammo().get(slot);
        return ammo == null || ammo.isEmpty() ? null : ammo;
    }

    void rotateAmmo(ItemStack stack) {
        SyringeKeys.AmmoData data = SyringeKeys.AMMO.get(stack);
        List<ItemStack> ammo = data == null ? new ArrayList<>() : new ArrayList<>(data.ammo());
        while (ammo.size() < 7) {
            ammo.add(ItemStack.EMPTY);
        }
        List<ItemStack> rotated = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            rotated.add(ammo.get(i));
        }
        rotated.add(ItemStack.EMPTY);
        SyringeKeys.AMMO.set(stack, new SyringeKeys.AmmoData(rotated));
    }
}
