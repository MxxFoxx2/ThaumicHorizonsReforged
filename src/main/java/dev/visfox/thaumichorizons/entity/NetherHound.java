package dev.visfox.thaumichorizons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;

public class NetherHound extends Wolf {
    public NetherHound(EntityType<? extends NetherHound> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes();
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}
