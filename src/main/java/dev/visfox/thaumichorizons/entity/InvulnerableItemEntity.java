package dev.visfox.thaumichorizons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InvulnerableItemEntity extends ItemEntity {
    public InvulnerableItemEntity(EntityType<? extends ItemEntity> type, Level level) {
        super(type, level);
        setInvulnerable(true);
    }

    public InvulnerableItemEntity(Level level, double x, double y, double z, ItemStack stack) {
        super(level, x, y, z, stack);
        setInvulnerable(true);
    }
}
