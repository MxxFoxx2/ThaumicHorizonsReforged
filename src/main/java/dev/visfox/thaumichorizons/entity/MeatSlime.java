package dev.visfox.thaumichorizons.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;

public class MeatSlime extends Slime {
    public MeatSlime(EntityType<MeatSlime> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 1.0)
            .add(Attributes.MOVEMENT_SPEED, 0.2)
            .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected ParticleOptions getParticleType() {
        return new net.minecraft.core.particles.DustParticleOptions(new org.joml.Vector3f(1.0F, 0.2F, 0.2F), 1.0F);
    }

    @Override
    protected void dropFromLootTable(DamageSource source, boolean hitByPlayer) {
    }
}
