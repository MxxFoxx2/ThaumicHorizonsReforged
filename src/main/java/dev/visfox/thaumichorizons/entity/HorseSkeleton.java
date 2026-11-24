package dev.visfox.thaumichorizons.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;

/**
 * Thaumic Horizons' skeleton horse: vanilla skeleton horse with the legacy
 * flat +0.1 movement-speed bonus.
 */
public final class HorseSkeleton extends SkeletonHorse {
    private static final ResourceLocation SPEED_BONUS_ID =
        ResourceLocation.fromNamespaceAndPath("thaumichorizons", "horse_skeleton_speed");

    public HorseSkeleton(EntityType<HorseSkeleton> type, Level level) {
        super(type, level);
        applyLegacySpeedBonus();
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return SkeletonHorse.createAttributes();
    }

    private void applyLegacySpeedBonus() {
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null && !speed.hasModifier(SPEED_BONUS_ID)) {
            speed.addPermanentModifier(
                new AttributeModifier(SPEED_BONUS_ID, 0.1D, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Keep old saves compatible without stacking the speed modifier.
        applyLegacySpeedBonus();
    }
}
