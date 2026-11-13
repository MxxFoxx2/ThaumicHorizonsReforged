package dev.visfox.thaumichorizons.infusion;

import dev.visfox.thaumichorizons.ThaumicHorizonsConfig;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.compat.WarpKind;
import dev.visfox.thaumichorizons.platform.DataKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class CreatureInfusion {
    public static final int SWIFTNESS = 1;
    public static final int MORPHIC_FINGERS = 2;
    public static final int REGENERATION = 3;
    public static final int RESISTANCE = 4;
    public static final int SILVER_HEART = 5;
    public static final int SYNTH_SKIN = 6;
    public static final int AMPHIBIOUS = 7;
    public static final int WARP_TUMOR = 8;
    public static final int SPIDER_CLIMB = 9;
    public static final int CHAMELEON_SKIN = 10;

    public static final DataKey<CreatureInfusionData> DATA = DataKey.builder("creature_infusion", () -> CreatureInfusionData.EMPTY)
        .codec(CreatureInfusionData.CODEC)
        .copyOnDeath()
        .syncToAll(CreatureInfusionData.STREAM_CODEC)
        .build();

    private CreatureInfusion() {
    }

    public static void init() {
    }

    public static CreatureInfusionData get(Player player) {
        return DATA.get(player);
    }

    public static boolean hasChameleon(Player player) {
        return get(player).infusion(CHAMELEON_SKIN) != 0;
    }

    public static boolean hasClimb(Player player) {
        return get(player).infusion(SPIDER_CLIMB) != 0;
    }

    public static boolean isChameleonActive(Player player) {
        return get(player).chameleon();
    }

    public static void setInfusion(ServerPlayer player, int slot, int recipeId) {
        DATA.set(player, get(player).withInfusion(slot, recipeId));
        DATA.sync(player);
    }

    public static void setChameleon(ServerPlayer player, boolean active) {
        CreatureInfusionData data = get(player);
        if (data.chameleon() == active) {
            return;
        }
        DATA.set(player, new CreatureInfusionData(data.infusions(), active, data.climb(), data.tumorWarp(), data.tumorWarpPermanent(), data.tumorWarpTemp()));
        DATA.sync(player);
        player.setInvisible(active);
    }

    public static void toggleClimb(ServerPlayer player) {
        CreatureInfusionData data = get(player);
        DATA.set(player, new CreatureInfusionData(data.infusions(), data.chameleon(), !data.climb(), data.tumorWarp(), data.tumorWarpPermanent(), data.tumorWarpTemp()));
        DATA.sync(player);
    }

    public static void applyVisualState(Player player) {
        if (player.level().isClientSide()) {
            return;
        }
        boolean active = isChameleonActive(player) && hasChameleon(player);
        if (player.isInvisible() != active) {
            player.setInvisible(active);
        }
    }

    public static void warpTumor(ServerPlayer player) {
        CreatureInfusionData data = get(player);
        if (data.infusion(WARP_TUMOR) == 0) {
            return;
        }
        int capacity = ThaumicHorizonsConfig.warpedTumorValue() - data.tumorWarpPermanent() - data.tumorWarp() - data.tumorWarpTemp();
        if (capacity <= 0) {
            return;
        }
        int permanent = Compat.port().warp().get(player, WarpKind.PERMANENT);
        int sticky = Compat.port().warp().get(player, WarpKind.STICKY);
        int temporary = Compat.port().warp().get(player, WarpKind.TEMPORARY);
        int drainedPermanent = Math.min(permanent, capacity);
        int drainedSticky = Math.min(sticky, capacity - drainedPermanent);
        int drainedTemp = Math.min(temporary, capacity - drainedPermanent - drainedSticky);
        if (drainedPermanent == 0 && drainedSticky == 0 && drainedTemp == 0) {
            return;
        }
        if (drainedPermanent > 0) {
            Compat.port().warp().removePermanent(player, drainedPermanent);
        }
        if (drainedSticky > 0) {
            Compat.port().warp().add(player, -drainedSticky, WarpKind.STICKY);
        }
        if (drainedTemp > 0) {
            Compat.port().warp().add(player, -drainedTemp, WarpKind.TEMPORARY);
        }
        DATA.set(player, new CreatureInfusionData(
            data.infusions(), data.chameleon(), data.climb(),
            data.tumorWarp() + drainedSticky, data.tumorWarpPermanent() + drainedPermanent, data.tumorWarpTemp() + drainedTemp));
        DATA.sync(player);
    }
}
