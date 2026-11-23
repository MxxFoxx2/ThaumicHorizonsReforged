package dev.visfox.thaumichorizons.compat.tc.mixin;

import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.entity.golem.GolemBase;
import thaumcraft.entity.golem.GolemType;

/**
 * Adapts TH's per-material golem values to the stock TC4R GolemBase contract.
 * The upstream API exposes fixed GolemType fields rather than the add-on
 * GolemStats extension, so keep the specialization on the TH entity only.
 */
@Mixin(value = GolemBase.class, remap = false)
public abstract class GolemMaterialStatsMixin {
    @ModifyArg(
        method = "aiStep",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;setBaseValue(D)V"
        ),
        index = 0
    )
    private double thaumichorizons$useMaterialMovementSpeed(double stockSpeed) {
        if ((Object) this instanceof EntityGolemTH golem) {
            return golem.getMaterialMovementSpeed();
        }
        return stockSpeed;
    }

    @Redirect(
        method = "aiStep",
        at = @At(
            value = "FIELD",
            target = "Lthaumcraft/entity/golem/GolemType;regenDelay:I",
            opcode = Opcodes.GETFIELD
        )
    )
    private int thaumichorizons$useMaterialRegenDelay(GolemType type) {
        if ((Object) this instanceof EntityGolemTH golem) {
            return golem.material().regenDelay;
        }
        return type.regenDelay;
    }

    @Redirect(
        method = "configure",
        at = @At(
            value = "FIELD",
            target = "Lthaumcraft/entity/golem/GolemType;upgrades:I",
            opcode = Opcodes.GETFIELD
        )
    )
    private int thaumichorizons$useMaterialUpgradeSlots(GolemType type) {
        if ((Object) this instanceof EntityGolemTH golem) {
            return golem.material().upgrades;
        }
        return type.upgrades;
    }
}
