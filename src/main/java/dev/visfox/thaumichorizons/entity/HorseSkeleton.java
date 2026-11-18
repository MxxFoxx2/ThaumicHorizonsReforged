package dev.visfox.thaumichorizons.entity;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

/**
 * Thaumic Horizons' skeleton horse: vanilla skeleton horse with the legacy
 * flat +0.1 movement-speed bonus.
 */
public final class HorseSkeleton extends SkeletonHorse {
    private static final Identifier SPEED_BONUS_ID =
        Identifier.fromNamespaceAndPath("thaumichorizons", "horse_skeleton_speed");

    public HorseSkeleton(EntityType<? extends HorseSkeleton> type, Level level) {
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
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        // Keep old saves compatible without stacking the speed modifier.
        applyLegacySpeedBonus();
    }
}
