package dev.visfox.thaumichorizons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;

public class ChromaticSheep extends Sheep {
    public ChromaticSheep(EntityType<? extends ChromaticSheep> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Sheep.createAttributes();
    }

    @Override
    public void aiStep() {
        if (!this.level().isClientSide() && this.tickCount % 30 == 0) {
            DyeColor[] colors = DyeColor.values();
            this.setColor(colors[(this.getColor().ordinal() + 1) % colors.length]);
        }
        super.aiStep();
    }
}
