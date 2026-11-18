package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.item.SyringeInjectionItem;
import dev.visfox.thaumichorizons.registry.ThEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Arrow-like syringe dart. It uses vanilla arrow collision, embedding and pickup
 * while carrying the original potion contents in its recoverable syringe stack.
 */
public final class SyringeProjectile extends AbstractArrow implements ItemSupplier {
    public SyringeProjectile(EntityType<? extends SyringeProjectile> type, Level level) {
        super(type, level);
        this.setBaseDamage(1.5D);
    }

    public SyringeProjectile(Level level, LivingEntity owner, ItemStack injection, float power, ItemStack weapon) {
        super(ThEntities.SYRINGE.get(), owner, level, injection.copy(), weapon.copy());
        this.setBaseDamage(1.5D);
        this.pickup = owner instanceof net.minecraft.world.entity.player.Player ? Pickup.ALLOWED : Pickup.DISALLOWED;
        this.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, power * 1.5F, 1.0F);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ThItems.SYRINGE_INJECTION.get());
    }

    @Override
    protected void doPostHurtEffects(LivingEntity target) {
        super.doPostHurtEffects(target);
        SyringeInjectionItem.applyEffects(this.getPickupItemStackOrigin(), target);
    }

    @Override
    public ItemStack getItem() {
        ItemStack payload = this.getPickupItemStackOrigin();
        return payload.isEmpty() ? getDefaultPickupItem() : payload;
    }
}
