package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;

import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class ScholarChicken extends Chicken {
    public int timeUntilNextFeather;

    public ScholarChicken(EntityType<? extends ScholarChicken> type, Level level) {
        super(type, level);
        this.timeUntilNextFeather = this.random.nextInt(4000) + 4000;
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
            this.spawnAtLocation(level, new ItemStack(ThItems.INK_EGG.get()), 1.0F);
            this.eggTime = this.random.nextInt(6000) + 6000;
        }
        if (this.isAlive() && !this.isBaby() && !this.isChickenJockey() && --this.timeUntilNextFeather <= 0) {
            this.playSound(SoundEvents.CHICKEN_EGG, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(level, new ItemStack(Items.FEATHER), 1.0F);
            this.timeUntilNextFeather = this.random.nextInt(4000) + 4000;
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.timeUntilNextFeather = input.getIntOr("feather", this.random.nextInt(4000) + 4000);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("feather", this.timeUntilNextFeather);
    }

    @Override
    public @Nullable ScholarChicken getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.SCHOLAR_CHICKEN.get().create(level, EntitySpawnReason.MOB_SUMMONED);
    }
}
