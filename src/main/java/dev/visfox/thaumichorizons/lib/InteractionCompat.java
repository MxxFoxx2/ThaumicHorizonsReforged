package dev.visfox.thaumichorizons.lib;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;

public final class InteractionCompat {
    private InteractionCompat() {
    }

    public static EquipmentSlot slot(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
    }
}
