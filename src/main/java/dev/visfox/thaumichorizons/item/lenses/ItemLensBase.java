package dev.visfox.thaumichorizons.item.lenses;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class ItemLensBase extends Item implements ILens {
    private final String name;

    public ItemLensBase(Properties properties, String name) {
        super(properties);
        this.name = name;
    }

    @Override
    public String lensName() {
        return this.name;
    }

    @Override
    public void handleRemoval(Player player) {
    }
}
