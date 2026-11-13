package dev.visfox.thaumichorizons.infusion;

import dev.visfox.thaumichorizons.platform.DataKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class CreatureInfusion {
    public static final int CHAMELEON_SKIN = 10;
    public static final int SPIDER_CLIMB = 9;

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
        DATA.set(player, new CreatureInfusionData(data.infusions(), active, data.climb(), data.tumorWarp(), data.tumorWarpSticky(), data.tumorWarpTemp()));
        DATA.sync(player);
        player.setInvisible(active);
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
}
