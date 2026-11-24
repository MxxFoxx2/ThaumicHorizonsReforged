package dev.visfox.thaumichorizons.item.golem;

import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import dev.visfox.thaumichorizons.lib.THSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.item.golem.GolemBellItem;
import thaumcraft.item.golem.GolemItems;

public class GolemBellTHItem extends GolemBellItem {
    public GolemBellTHItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof EntityGolemTH golem && golem.isAlive()) {
            if (!(golem.level() instanceof ServerLevel level)) {
                return false;
            }
            if (golem.getCore() < 0) {
                golem.ticksAlive = 0;
                return true;
            }
            if (player.isShiftKeyDown()) {
                if (golem.getCore() >= 0) {
                    golem.spawnAtLocation(GolemItems.core(golem.getCore()));
                }
                golem.die();
                return true;
            }
            ItemStack dropped = golem.toItem(false);
            golem.dropCarried();
            if (!dropped.isEmpty()) {
                golem.spawnAtLocation(dropped);
            }
            THSounds.play(level, golem.position(), "zap", SoundSource.PLAYERS, 0.5F, 1.0F);
            golem.discard();
            return true;
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
}
