package dev.visfox.thaumichorizons.infusion;

import com.mojang.brigadier.arguments.BoolArgumentType;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID)
public final class InfusionCommands {
    private InfusionCommands() {
    }

    @SubscribeEvent
    static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("thaumichorizons")
            .requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.literal("chameleon")
                .then(Commands.argument("active", BoolArgumentType.bool())
                    .executes(context -> setChameleon(context.getSource(), BoolArgumentType.getBool(context, "active"))))));
    }

    private static int setChameleon(CommandSourceStack source, boolean active) {
        if (source.getPlayer() instanceof ServerPlayer player) {
            CreatureInfusion.setInfusion(player, CreatureInfusion.CHAMELEON_SKIN, 1);
            CreatureInfusion.setChameleon(player, active);
            source.sendSuccess(() -> Component.literal("Chameleon skin: " + active), true);
            return 1;
        }
        return 0;
    }
}
