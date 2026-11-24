package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.entity.ai.EatTaintGoal;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class TaintEaterPig extends Pig {
    public TaintEaterPig(EntityType<TaintEaterPig> type, Level level) {
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
        if (TcReforgedPort.potions.isAvailable()) {
            this.removeEffect(TcReforgedPort.potions.visExhaust());
            this.removeEffect(TcReforgedPort.potions.taintPoison());
            this.removeEffect(TcReforgedPort.potions.infectiousVisExhaust());
        }
    }

    @Override
    public @Nullable TaintEaterPig getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.TAINT_EATER_PIG.get().create(level);
    }
}
