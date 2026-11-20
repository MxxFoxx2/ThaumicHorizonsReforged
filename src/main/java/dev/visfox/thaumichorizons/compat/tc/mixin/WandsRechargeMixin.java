package dev.visfox.thaumichorizons.compat.tc.mixin;

import dev.visfox.thaumichorizons.item.golem.DisposableWandItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.Wands;

@Mixin(value = Wands.class, remap = false)
public abstract class WandsRechargeMixin {
    @Inject(method = "getAspectsWithRoom", at = @At("HEAD"), cancellable = true)
    private static void thaumichorizons$noRoom(ItemStack stack, CallbackInfoReturnable<AspectList> cir) {
        if (stack.getItem() instanceof DisposableWandItem) {
            cir.setReturnValue(new AspectList());
        }
    }

    @Inject(method = "addRealVis", at = @At("HEAD"), cancellable = true)
    private static void thaumichorizons$rejectRecharge(ItemStack stack, Aspect aspect, int amount, boolean doit,
        CallbackInfoReturnable<Integer> cir) {
        if (amount > 0 && stack.getItem() instanceof DisposableWandItem) {
            cir.setReturnValue(amount);
        }
    }
}
