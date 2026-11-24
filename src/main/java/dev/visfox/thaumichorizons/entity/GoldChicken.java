package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;

import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class GoldChicken extends Chicken {
    public GoldChicken(EntityType<GoldChicken> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Chicken.createAttributes();
    }

    @Override
    public void aiStep() {
        int savedEggTime = this.eggTime;
        this.eggTime = Integer.MAX_VALUE;
        super.aiStep();
        this.eggTime = savedEggTime;
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.isAlive() && !this.isBaby() && !this.isChickenJockey() && --this.eggTime <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(new ItemStack(ThItems.GOLD_EGG.get()), 1.0F);
            this.eggTime = this.random.nextInt(8000) + 8000;
        }
    }

    @Override
    public @Nullable GoldChicken getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.GOLD_CHICKEN.get().create(level);
    }
}
