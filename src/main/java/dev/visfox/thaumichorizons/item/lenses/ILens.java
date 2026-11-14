package dev.visfox.thaumichorizons.item.lenses;

import net.minecraft.world.entity.player.Player;

public interface ILens {
    String lensName();

    void handleRemoval(Player player);
}
