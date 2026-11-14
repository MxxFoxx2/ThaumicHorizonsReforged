package dev.visfox.thaumichorizons.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class SelfShearingSheep extends Sheep {
    public SelfShearingSheep(EntityType<? extends SelfShearingSheep> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes();
    }

    @Override
    public void aiStep() {
        if (this.level() instanceof ServerLevel serverLevel && !this.isSheared() && this.tickCount % 100 == 0) {
            this.shear(serverLevel, SoundSource.NEUTRAL, new ItemStack(Items.SHEARS));
        }
        super.aiStep();
    }
}
