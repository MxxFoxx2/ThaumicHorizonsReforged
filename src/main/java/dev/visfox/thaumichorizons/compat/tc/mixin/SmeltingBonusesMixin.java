package dev.visfox.thaumichorizons.compat.tc.mixin;

import dev.visfox.thaumichorizons.registry.ThItems;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.device.SmeltingBonuses;

@Mixin(value = SmeltingBonuses.class, remap = false)
public abstract class SmeltingBonusesMixin {
    @Unique
    private static boolean thaumichorizons$meatBonusAdded;

    @Shadow
    private static void item(Supplier<Item> input, Supplier<Item> bonus) {
        throw new AssertionError();
    }

    @Inject(method = "get", at = @At("HEAD"))
    private static void thaumichorizons$addMeatBonus(ItemStack stack, CallbackInfoReturnable<Item> cir) {
        if (!thaumichorizons$meatBonusAdded) {
            item(() -> ThItems.MEAT.get(), () -> ThItems.MEAT_NUGGET.get());
            thaumichorizons$meatBonusAdded = true;
        }
    }
}
