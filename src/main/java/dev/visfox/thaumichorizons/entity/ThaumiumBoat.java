package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidType;

/**
 * The thaumium boat uses vanilla boat steering and passenger behavior, but can
 * float on lava and protects its passengers from fire while immersed.
 */
public final class ThaumiumBoat extends Boat {
    public ThaumiumBoat(EntityType<? extends ThaumiumBoat> type, Level level) {
        super(type, level, ThItems.BOAT_THAUMIUM::get);
    }

    @Override
    public boolean canBoatInFluid(FluidState state) {
        return state.is(FluidTags.WATER) || state.is(FluidTags.LAVA);
    }

    @Override
    public boolean canBoatInFluid(FluidType type) {
        return type == Fluids.WATER.getFluidType() || type == Fluids.LAVA.getFluidType();
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.isInLava()) {
            for (Entity passenger : this.getPassengers()) {
                passenger.clearFire();
                if (passenger instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 10));
                }
            }
        }
    }
}
