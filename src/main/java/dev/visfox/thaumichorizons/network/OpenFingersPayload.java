package dev.visfox.thaumichorizons.network;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.container.FingersMenu;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import dev.visfox.thaumichorizons.platform.ModNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public record OpenFingersPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenFingersPayload> TYPE = new CustomPacketPayload.Type<>(ThaumicHorizons.id("open_fingers"));
    public static final StreamCodec<ByteBuf, OpenFingersPayload> STREAM_CODEC = StreamCodec.unit(new OpenFingersPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenFingersPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (CreatureInfusion.get(serverPlayer).infusion(CreatureInfusion.MORPHIC_FINGERS) == 0) {
            return;
        }
        serverPlayer.openMenu(new SimpleMenuProvider(
            (windowId, inventory, p) -> new FingersMenu(windowId, inventory),
            Component.translatable("container.thaumichorizons.fingers")));
    }

    public static void register() {
        ModNetwork.toServer(TYPE, STREAM_CODEC, OpenFingersPayload::handle);
    }
}
