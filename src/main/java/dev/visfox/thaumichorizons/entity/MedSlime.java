package dev.visfox.thaumichorizons.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.cubemob.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MedSlime extends Slime {
    public MedSlime(EntityType<? extends MedSlime> type, Level level) {
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
        return ParticleTypes.SNOWFLAKE;
    }

    @Override
    protected void dropFromLootTable(ServerLevel level, DamageSource source, boolean hitByPlayer) {
    }

    @Override
    public void playerTouch(Player player) {
        if (player.getHealth() >= player.getMaxHealth()) {
            return;
        }
        int size = this.getSize();
        if (this.hasLineOfSight(player) && this.distanceToSqr(player) < 0.6 * size * 0.6 * size) {
            player.heal(this.getAttackDamage() + 1.0F);
            this.playSound(SoundEvents.PLAYER_ATTACK_CRIT, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.explode(null, this.getX(), this.getY() + this.getBbHeight() / 2.0F, this.getZ(), 0.0F, Level.ExplosionInteraction.NONE);
            }
            this.discard();
        }
    }
}
