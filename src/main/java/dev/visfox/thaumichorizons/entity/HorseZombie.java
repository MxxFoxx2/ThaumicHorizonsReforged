package dev.visfox.thaumichorizons.entity;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.ZombieHorse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;

/**
 * Thaumic Horizons' zombie horse: the vanilla undead horse with the legacy
 * flat +20 max-health bonus.
 */
public final class HorseZombie extends ZombieHorse {
    private static final Identifier HEALTH_BONUS_ID =
        Identifier.fromNamespaceAndPath("thaumichorizons", "horse_zombie_health");

    public HorseZombie(EntityType<? extends HorseZombie> type, Level level) {
        super(type, level);
        applyLegacyHealthBonus();
        this.setHealth(this.getMaxHealth());
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return ZombieHorse.createAttributes();
    }

    private void applyLegacyHealthBonus() {
        AttributeInstance maxHealth = this.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null && !maxHealth.hasModifier(HEALTH_BONUS_ID)) {
            maxHealth.addPermanentModifier(
                new AttributeModifier(HEALTH_BONUS_ID, 20.0D, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        // Attribute data is restored from NBT by the superclass. Re-apply only
        // when loading an older save that predates this modifier.
        applyLegacyHealthBonus();
    }
}
