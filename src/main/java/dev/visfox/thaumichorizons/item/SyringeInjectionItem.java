package dev.visfox.thaumichorizons.item;

import java.util.List;
import dev.visfox.thaumichorizons.entity.BlastPhialProjectile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.level.Level;

public class SyringeInjectionItem extends Item {
    public SyringeInjectionItem(Properties properties) {
        super(properties);
    }

    public boolean isPhial(ItemStack stack) {
        SyringeKeys.InjectionData data = SyringeKeys.INJECTION.get(stack);
        return data != null && data.phial();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 8;
    }

    @Override
    public net.minecraft.world.item.UseAnim getUseAnimation(ItemStack stack) {
        return net.minecraft.world.item.UseAnim.BOW;
    }

    @Override
    public Component getName(ItemStack stack) {
        if (isPhial(stack)) {
            return Component.translatable("item.thaumichorizons.injection.1.name");
        }
        return Component.translatable("item.thaumichorizons.injection.0.name");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isPhial(stack)) {
            ItemStack projectileStack = stack.copy();
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            world.playSound(null, player.blockPosition(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 0.5F,
                0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide()) {
                world.addFreshEntity(new BlastPhialProjectile(world, player, projectileStack, 0.5F));
            }
            return InteractionResultHolder.success(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (!world.isClientSide()) {
            applyEffects(stack, entity);
        }
        if (!(entity instanceof Player player) || !player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return stack;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!target.level().isClientSide()) {
            applyEffects(stack, target);
        }
        stack.shrink(1);
        return true;
    }

    public static void applyEffects(ItemStack stack, LivingEntity target) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            for (MobEffectInstance effect : contents.getAllEffects()) {
                target.addEffect(new MobEffectInstance(effect));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines,
        TooltipFlag flag) {
        Consumer<Component> tooltip = lines::add;
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            for (MobEffectInstance effect : contents.getAllEffects()) {
                Component message = Component.translatable(effect.getEffect().value().getDescriptionId());
                if (effect.getAmplifier() > 0) {
                    message = message.copy().append(" "
                        + Component.translatable("potion.potency." + effect.getAmplifier()).getString());
                }
                tooltip.accept(effect.getEffect().value().getCategory() == net.minecraft.world.effect.MobEffectCategory.HARMFUL
                    ? message.copy().withStyle(net.minecraft.ChatFormatting.RED)
                    : message.copy().withStyle(net.minecraft.ChatFormatting.GRAY));
            }
        } else {
            tooltip.accept(Component.translatable("tooltip.thaumichorizons.syringe.empty").withStyle(net.minecraft.ChatFormatting.GRAY));
        }
    }
}
