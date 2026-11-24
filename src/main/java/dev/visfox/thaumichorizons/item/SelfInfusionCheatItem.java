package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import dev.visfox.thaumichorizons.infusion.InfusionEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class SelfInfusionCheatItem extends Item {
    private final int infusionId;

    public SelfInfusionCheatItem(Properties properties, int infusionId) {
        super(properties);
        this.infusionId = infusionId;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.success(stack);
        }
        int[] infusions = CreatureInfusion.get(serverPlayer).infusions();
        for (int infusion : infusions) {
            if (infusion == infusionId) {
                return InteractionResultHolder.pass(stack);
            }
        }
        for (int slot = 0; slot < infusions.length; slot++) {
            if (infusions[slot] == 0) {
                CreatureInfusion.setInfusion(serverPlayer, slot, infusionId);
                InfusionEntityEvents.applyInfusions(serverPlayer);
                if (TcReforgedPort.fx.isAvailable()) {
                    TcReforgedPort.fx.burst(level,
                        new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), 0xFFFFFF, 16);
                }
                return InteractionResultHolder.consume(stack);
            }
        }
        return InteractionResultHolder.pass(stack);
    }
}
