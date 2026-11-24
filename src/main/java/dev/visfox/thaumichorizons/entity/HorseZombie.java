package dev.visfox.thaumichorizons.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.CompoundTag;

/**
 * Thaumic Horizons' zombie horse: the vanilla undead horse with the legacy
 * flat +20 max-health bonus.
 */
public final class HorseZombie extends ZombieHorse {
    private static final ResourceLocation HEALTH_BONUS_ID =
        ResourceLocation.fromNamespaceAndPath("thaumichorizons", "horse_zombie_health");

    public HorseZombie(EntityType<HorseZombie> type, Level level) {
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
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Attribute data is restored from NBT by the superclass. Re-apply only
        // when loading an older save that predates this modifier.
        applyLegacyHealthBonus();
    }
}
