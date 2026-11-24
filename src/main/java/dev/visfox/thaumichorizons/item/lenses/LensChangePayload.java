package dev.visfox.thaumichorizons.item.lenses;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.platform.ModNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record LensChangePayload(String lens) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<LensChangePayload> TYPE =
        new CustomPacketPayload.Type<>(ThaumicHorizons.id("lens_change"));
    public static final StreamCodec<ByteBuf, LensChangePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        LensChangePayload::lens,
        LensChangePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LensChangePayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack head = serverPlayer.getItemBySlot(EquipmentSlot.HEAD);
        Item goggles = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("thaumcraft", "goggles_of_revealing"));
        if (goggles != null && head.is(goggles)) {
            LensManager.changeLens(head, serverPlayer, payload.lens());
        }
    }

    public static void register() {
        ModNetwork.toServer(TYPE, STREAM_CODEC, LensChangePayload::handle);
    }
}
