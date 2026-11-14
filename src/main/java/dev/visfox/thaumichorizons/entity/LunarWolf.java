package dev.visfox.thaumichorizons.entity;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.Wands;

public class LunarWolf extends Wolf {
    public LunarWolf(EntityType<? extends LunarWolf> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(this.level() instanceof ServerLevel level) || level.isBrightOutside()) {
            return;
        }
        float moonFactor = moonPhaseFactor(level.environmentAttributes()
            .getValue(net.minecraft.world.attribute.EnvironmentAttributes.MOON_PHASE, this.blockPosition()).ordinal());
        int interval = (int) (7.0F - moonFactor * 4.0F);
        if (interval <= 0 || this.tickCount % interval != 0) {
            return;
        }
        List<Player> players = level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(5.0));
        for (Player player : players) {
            ItemStack held = player.getMainHandItem();
            if (Wands.isWand(held)) {
                AspectList room = Wands.getAspectsWithRoom(held);
                for (Aspect aspect : room.getAspects()) {
                    Wands.storeVis(held, aspect, Wands.getVis(held, aspect) + 1);
                }
            }
        }
    }

    private static float moonPhaseFactor(int phase) {
        return switch (phase) {
            case 0 -> 1.0F;
            case 1, 7 -> 0.75F;
            case 2, 6 -> 0.5F;
            case 3, 5 -> 0.25F;
            default -> 0.083F;
        };
    }
}
