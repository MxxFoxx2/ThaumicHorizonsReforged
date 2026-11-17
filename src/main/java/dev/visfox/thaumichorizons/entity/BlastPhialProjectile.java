package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;

/**
 * Splash-phial projectile. Potion data is transferred from the TH syringe item
 * into a vanilla splash-potion stack so vanilla potion impact rules are retained.
 */
public final class BlastPhialProjectile extends ThrownSplashPotion {
    public BlastPhialProjectile(EntityType<? extends BlastPhialProjectile> type, Level level) {
        super(type, level);
    }

    public BlastPhialProjectile(Level level, LivingEntity owner, ItemStack injection, float power) {
        super(ThEntities.BLAST_PHIAL.get(), level);
        this.setOwner(owner);
        this.setItem(toSplashPotion(injection));

        float yaw = owner.getYRot() * Mth.DEG_TO_RAD;
        this.setPos(
            owner.getX() - Mth.cos(yaw) * 0.16F,
            owner.getEyeY() - 0.1D,
            owner.getZ() - Mth.sin(yaw) * 0.16F
        );
        this.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, power * 1.5F, 1.0F);
    }

    public static ItemStack toSplashPotion(ItemStack injection) {
        ItemStack potion = new ItemStack(Items.SPLASH_POTION);
        PotionContents contents = injection.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            potion.set(DataComponents.POTION_CONTENTS, contents);
        }
        return potion;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }
}
