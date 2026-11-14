package dev.visfox.thaumichorizons.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Seawolf extends Wolf {
    public Seawolf(EntityType<? extends Seawolf> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isInWater()) {
            float bonus = 0.025F;
            this.moveRelative(bonus, new Vec3(0.0, 1.0, 0.0));
            this.setAirSupply(300);
        }
    }
}
