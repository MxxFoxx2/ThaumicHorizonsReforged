package dev.visfox.thaumichorizons.container;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record InjectorMenuProvider() implements MenuProvider {
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new InjectorMenu(containerId, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.thaumichorizons.injector");
    }
}
