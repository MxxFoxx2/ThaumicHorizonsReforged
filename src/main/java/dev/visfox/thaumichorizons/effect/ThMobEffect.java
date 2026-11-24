package dev.visfox.thaumichorizons.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public final class ThMobEffect extends MobEffect {
    private final Kind kind;

    public ThMobEffect(Kind kind, int color) {
        super(MobEffectCategory.BENEFICIAL, color);
        this.kind = kind;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return false;
    }

    public enum Kind {
        VIS_BOOST,
        VIS_REGEN,
        VACUUM,
        SHOCK,
        SYNTHESIS
    }
}
