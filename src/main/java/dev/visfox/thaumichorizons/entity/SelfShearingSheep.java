package dev.visfox.thaumichorizons.entity;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.level.Level;

public class SelfShearingSheep extends Sheep {
    public SelfShearingSheep(EntityType<SelfShearingSheep> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes();
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide() && !this.isSheared() && this.tickCount % 100 == 0) {
            this.shear(SoundSource.NEUTRAL);
        }
        super.aiStep();
    }
}
