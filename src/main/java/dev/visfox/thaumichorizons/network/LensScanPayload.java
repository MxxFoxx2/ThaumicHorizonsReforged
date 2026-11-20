package dev.visfox.thaumichorizons.network;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.item.lenses.LensManager;
import dev.visfox.thaumichorizons.platform.ModNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import thaumcraft.research.ScanManager;

public record LensScanPayload(int kind, BlockPos pos, int entityId) implements CustomPacketPayload {
    public static final Type<LensScanPayload> TYPE = new Type<>(ThaumicHorizons.id("lens_scan"));
    public static final StreamCodec<RegistryFriendlyByteBuf, LensScanPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, LensScanPayload::kind,
        BlockPos.STREAM_CODEC, LensScanPayload::pos,
        ByteBufCodecs.VAR_INT, LensScanPayload::entityId,
        LensScanPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LensScanPayload payload, Player player) {
        if (!(player instanceof ServerPlayer serverPlayer) || !hasScholarLens(serverPlayer)) {
            return;
        }
        ScanManager.Target target = resolveChecked(serverPlayer, payload);
        if (target != null && !ScanManager.hasBeenScanned(serverPlayer, target)) {
            ScanManager.completeScan(serverPlayer, target);
        }
    }

    private static boolean hasScholarLens(ServerPlayer player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        Item goggles = BuiltInRegistries.ITEM.getValue(Identifier.fromNamespaceAndPath("thaumcraft", "goggles_of_revealing"));
        return goggles != null && head.is(goggles) && LensManager.getLensId(head).equals("LensOrderEntropy");
    }

    private static ScanManager.Target resolveChecked(ServerPlayer player, LensScanPayload payload) {
        if (payload.kind() == ScanManager.KIND_ENTITY) {
            Entity entity = player.level().getEntity(payload.entityId());
            if (entity == null || entity == player || entity.isRemoved()
                || player.distanceTo(entity) > ScanManager.ENTITY_RANGE + 3.0) {
                return null;
            }
            return ScanManager.resolveEntity(entity);
        }
        BlockPos pos = payload.pos();
        double range = player.blockInteractionRange() + 3.0;
        if (!player.level().isLoaded(pos)
            || player.getEyePosition().distanceToSqr(Vec3.atCenterOf(pos)) > range * range) {
            return null;
        }
        if (payload.kind() == ScanManager.KIND_NODE) {
            return ScanManager.resolveNode(player.level(), pos);
        }
        if (payload.kind() == ScanManager.KIND_BLOCK) {
            return ScanManager.resolveBlock(player.level(), pos);
        }
        return null;
    }

    public static void register() {
        ModNetwork.toServer(TYPE, STREAM_CODEC, LensScanPayload::handle);
    }
}
