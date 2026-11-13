package dev.visfox.thaumichorizons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.infusion.CreatureInfusion;
import dev.visfox.thaumichorizons.platform.ModNetwork;

public record ToggleInfusionPayload(int infusionId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ToggleInfusionPayload> TYPE = new CustomPacketPayload.Type<>(ThaumicHorizons.id("toggle_infusion"));
    public static final StreamCodec<ByteBuf, ToggleInfusionPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT,
        ToggleInfusionPayload::infusionId,
        ToggleInfusionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ToggleInfusionPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        switch (payload.infusionId()) {
            case CreatureInfusion.CHAMELEON_SKIN -> {
                if (CreatureInfusion.hasChameleon(serverPlayer)) {
                    CreatureInfusion.setChameleon(serverPlayer, !CreatureInfusion.isChameleonActive(serverPlayer));
                }
            }
            case CreatureInfusion.SPIDER_CLIMB -> {
                if (CreatureInfusion.hasClimb(serverPlayer)) {
                    CreatureInfusion.toggleClimb(serverPlayer);
                }
            }
            default -> {
            }
        }
    }

    public static void register() {
        ModNetwork.toServer(TYPE, STREAM_CODEC, ToggleInfusionPayload::handle);
    }
}
