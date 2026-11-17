package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class EggIncubatedEntity extends ThrowableItemProjectile {
    public EggIncubatedEntity(EntityType<? extends EggIncubatedEntity> type, Level level) {
        super(type, level);
    }

    public EggIncubatedEntity(Level level, LivingEntity owner, ItemStack itemStack) {
        super(ThEntities.EGG_INCUBATED.get(), owner, level, itemStack);
    }

    public EggIncubatedEntity(Level level, double x, double y, double z, ItemStack itemStack) {
        super(ThEntities.EGG_INCUBATED.get(), x, y, z, level, itemStack);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        hitResult.getEntity().hurt(this.damageSources().thrown(this, this.getOwner()), 0.0F);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            Chicken chicken = EntityTypes.CHICKEN.create(this.level(), EntitySpawnReason.TRIGGERED);
            if (chicken != null) {
                chicken.setAge(-24000);
                chicken.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                this.level().addFreshEntity(chicken);
            }
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return ThItems.EGG_INCUBATED.get();
    }
}
