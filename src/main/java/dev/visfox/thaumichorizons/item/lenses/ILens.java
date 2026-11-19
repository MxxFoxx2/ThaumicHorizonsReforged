package dev.visfox.thaumichorizons.item.lenses;

import net.minecraft.world.entity.player.Player;

public interface ILens {
    String lensName();

    default void handleRender(Player player, float partialTicks) {
    }

    void handleRemoval(Player player);
}
