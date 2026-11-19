package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.entity.ai.EatTaintGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class TaintEaterPig extends Pig {
    public TaintEaterPig(EntityType<? extends TaintEaterPig> type, Level level) {
        super(type, level);
        this.goalSelector.addGoal(9, new EatTaintGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Pig.createAttributes();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }
        if (Compat.port().potions().isAvailable()) {
            this.removeEffect(Compat.port().potions().visExhaust());
            this.removeEffect(Compat.port().potions().taintPoison());
            this.removeEffect(Compat.port().potions().infectiousVisExhaust());
        }
    }

    @Override
    public @Nullable TaintEaterPig getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.TAINT_EATER_PIG.get().create(level, EntitySpawnReason.MOB_SUMMONED);
    }
}
