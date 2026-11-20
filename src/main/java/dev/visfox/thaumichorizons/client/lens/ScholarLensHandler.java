package dev.visfox.thaumichorizons.client.lens;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.client.LensEffectsHandler;
import dev.visfox.thaumichorizons.client.platform.ClientNetwork;
import dev.visfox.thaumichorizons.client.screen.ThGui;
import dev.visfox.thaumichorizons.lib.THSounds;
import dev.visfox.thaumichorizons.network.LensScanPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.node.Node;
import thaumcraft.research.ScanKind;
import thaumcraft.research.ScanManager;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class ScholarLensHandler {
    private static final int SCAN_TICKS = 20;
    private static @Nullable ScanTarget startScan;
    private static int scanTicks;
    private static boolean attempted;

    private ScholarLensHandler() {
    }

    private record ScanTarget(int kind, BlockPos pos, int entityId, ScanManager.Target target) {
        boolean sameAs(@Nullable ScanTarget other) {
            return other != null && kind == other.kind && pos.equals(other.pos) && entityId == other.entityId
                && target.kind() == other.target.kind() && target.key().equals(other.target.key());
        }
    }

    @SubscribeEvent
    static void tick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        Level level = minecraft.level;
        if (player == null || level == null || !LensEffectsHandler.hasLens(player, "LensOrderEntropy")) {
            reset();
            return;
        }
        ScanTarget scan = lookAt(minecraft);
        if (scan == null || !scan.sameAs(startScan)) {
            startScan = scan;
            scanTicks = 0;
            attempted = false;
        }
        if (scan == null || attempted || ScanManager.hasBeenScanned(player, scan.target())) {
            return;
        }
        scanTicks++;
        if (scanTicks % 2 == 0) {
            SoundEvent sound = THSounds.sound("cameraticks");
            if (sound != null) {
                level.playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS,
                    0.2F, 0.45F + level.getRandom().nextFloat() * 0.1F, false);
            }
        }
        if (scanTicks >= SCAN_TICKS) {
            attempted = true;
            if (ScanManager.validScan(scan.target().aspects(), player)) {
                ClientNetwork.sendToServer(new LensScanPayload(scan.kind(), scan.pos(), scan.entityId()));
            }
        }
    }

    @SubscribeEvent
    static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null || !LensEffectsHandler.hasLens(player, "LensOrderEntropy")) {
            return;
        }
        ScanTarget scan = lookAt(minecraft);
        if (scan == null) {
            return;
        }
        Component name = displayName(minecraft, scan);
        AspectList aspects = ScanManager.hasBeenScanned(player, scan.target()) ? scan.target().aspects() : null;
        if (aspects != null && scan.kind() == ScanManager.KIND_NODE
            && minecraft.level.getBlockEntity(scan.pos()) instanceof Node node) {
            aspects = node.getAspects();
        }
        GuiGraphicsExtractor graphics = event.getGuiGraphics();
        int centerX = graphics.guiWidth() / 2;
        graphics.centeredText(minecraft.font, name, centerX, 5, 0xFFFFFFFF);
        if (aspects != null) {
            int count = aspects.size();
            int rowWidth = Math.min(5, count) * 18;
            int index = 0;
            for (Aspect aspect : aspects.getAspectsSorted()) {
                int column = index % 5;
                int row = index / 5;
                ThGui.drawTag(graphics, centerX - rowWidth / 2 + column * 18, 18 + row * 18, aspect,
                    aspects.getAmount(aspect), false);
                index++;
            }
        }
    }

    private static Component displayName(Minecraft minecraft, ScanTarget scan) {
        if (scan.kind() == ScanManager.KIND_ENTITY) {
            Entity entity = minecraft.level.getEntity(scan.entityId());
            return entity == null ? Component.literal("?") : entity.getName();
        }
        if (!scan.target().clueStack().isEmpty()) {
            return scan.target().clueStack().getHoverName();
        }
        if (scan.kind() == ScanManager.KIND_NODE && minecraft.level.getBlockEntity(scan.pos()) instanceof Node node) {
            Component type = Component.translatable(node.getNodeType().getTranslationKey());
            if (node.getNodeModifier() != null) {
                type = Component.empty().append(type).append(", ")
                    .append(Component.translatable(node.getNodeModifier().getTranslationKey()));
            }
            return minecraft.level.getBlockState(scan.pos()).getBlock().getName().copy().append(" (").append(type).append(")");
        }
        if (scan.kind() == ScanManager.KIND_BLOCK) {
            return minecraft.level.getBlockState(scan.pos()).getBlock().getName();
        }
        return Component.literal("?");
    }

    private static @Nullable ScanTarget lookAt(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.player == null || minecraft.hitResult == null) {
            return null;
        }
        HitResult hit = minecraft.hitResult;
        if (hit instanceof EntityHitResult entityHit) {
            Entity entity = entityHit.getEntity();
            if (entity.distanceTo(minecraft.player) <= ScanManager.ENTITY_RANGE) {
                ScanManager.Target target = ScanManager.resolveEntity(entity);
                if (target != null) {
                    return new ScanTarget(ScanManager.KIND_ENTITY, BlockPos.ZERO, entity.getId(), target);
                }
            }
            return null;
        }
        if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = blockHit.getBlockPos();
            ScanManager.Target target = ScanManager.resolveBlock(minecraft.level, pos);
            if (target != null) {
                int kind = target.kind() == ScanKind.PHENOMENON ? ScanManager.KIND_NODE : ScanManager.KIND_BLOCK;
                return new ScanTarget(kind, pos, 0, target);
            }
        }
        return null;
    }

    private static void reset() {
        startScan = null;
        scanTicks = 0;
        attempted = false;
    }
}
