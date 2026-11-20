package dev.visfox.thaumichorizons.client.lens;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.LensEffectsHandler;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class EarthLensHandler {
    private static final Map<BlockPos, BlockState> REPLACED = new HashMap<>();

    private EarthLensHandler() {
    }

    @SubscribeEvent
    static void beforeClientTick(ClientTickEvent.Pre event) {
        restore(Minecraft.getInstance().level);
    }

    @SubscribeEvent
    static void afterClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null || !LensEffectsHandler.hasLens(minecraft.player, "LensEarth")
            || !(minecraft.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }
        BlockPos center = hit.getBlockPos();
        Direction face = hit.getDirection();
        for (int first = -1; first <= 1; first++) {
            for (int second = -1; second <= 1; second++) {
                BlockPos pos = offset(center, face, first, second);
                BlockState original = level.getBlockState(pos);
                if (original.isAir() || original.hasBlockEntity() || original.getLightEmission() != 0
                    || original.getCollisionShape(level, pos).isEmpty() || original.is(ThBlocks.EVANESCENT.get())) {
                    continue;
                }
                REPLACED.put(pos.immutable(), original);
                level.setBlock(pos, ThBlocks.EVANESCENT.get().defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static BlockPos offset(BlockPos center, Direction face, int first, int second) {
        return switch (face.getAxis()) {
            case Y -> center.offset(first, 0, second);
            case Z -> center.offset(first, second, 0);
            case X -> center.offset(0, second, first);
        };
    }

    private static void restore(ClientLevel level) {
        if (level == null || REPLACED.isEmpty()) {
            return;
        }
        for (Map.Entry<BlockPos, BlockState> entry : REPLACED.entrySet()) {
            if (level.getBlockState(entry.getKey()).is(ThBlocks.EVANESCENT.get())) {
                level.setBlock(entry.getKey(), entry.getValue(), Block.UPDATE_CLIENTS);
            }
        }
        REPLACED.clear();
    }
}
