package dev.visfox.thaumichorizons.infusion;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mojang.serialization.JsonOps;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

class ChameleonInfusionDataTest {
    private static CreatureInfusionData chameleonState() {
        int[] infusions = new int[CreatureInfusionData.NUM_INFUSIONS];
        infusions[CreatureInfusion.CHAMELEON_SKIN] = 1;
        infusions[CreatureInfusion.SPIDER_CLIMB] = 2;
        infusions[CreatureInfusion.WARP_TUMOR] = 8;
        return new CreatureInfusionData(infusions, true, true, 3, 5, 7);
    }

    @Test
    void streamCodecRoundTripCarriesFullState() {
        CreatureInfusionData state = chameleonState();
        ByteBuf buffer = Unpooled.buffer();
        try {
            var lookup = RegistryAccess.EMPTY;
            CreatureInfusionData.STREAM_CODEC.encode(new RegistryFriendlyByteBuf(buffer, lookup), state);
            CreatureInfusionData decoded = CreatureInfusionData.STREAM_CODEC.decode(new RegistryFriendlyByteBuf(buffer, lookup));
            assertEquals(state, decoded);
            assertTrue(decoded.chameleon());
            assertTrue(decoded.climb());
            assertEquals(1, decoded.infusion(CreatureInfusion.CHAMELEON_SKIN));
            assertEquals(3, decoded.tumorWarp());
            assertEquals(5, decoded.tumorWarpPermanent());
            assertEquals(7, decoded.tumorWarpTemp());
        } finally {
            buffer.release();
        }
    }

    @Test
    void codecRoundTripSurvivesSaveReload() {
        CreatureInfusionData state = chameleonState();
        var encoded = CreatureInfusionData.CODEC.encodeStart(JsonOps.INSTANCE, state).result().orElseThrow();
        var decoded = CreatureInfusionData.CODEC.parse(JsonOps.INSTANCE, encoded).result().orElseThrow();
        assertEquals(state, decoded);
        assertTrue(decoded.chameleon());
        assertEquals(2, decoded.infusion(CreatureInfusion.SPIDER_CLIMB));
    }

    @Test
    void emptyStateHasNoActiveInfusions() {
        CreatureInfusionData empty = CreatureInfusionData.EMPTY;
        assertFalse(empty.chameleon());
        assertFalse(empty.climb());
        assertEquals(0, empty.infusion(CreatureInfusion.CHAMELEON_SKIN));
        assertEquals(0, empty.tumorWarpPermanent());
    }

    @Test
    void infusionSlotWritesAreIsolated() {
        CreatureInfusionData base = CreatureInfusionData.EMPTY;
        CreatureInfusionData granted = base.withInfusion(CreatureInfusion.CHAMELEON_SKIN, 1);
        assertEquals(0, base.infusion(CreatureInfusion.CHAMELEON_SKIN));
        assertEquals(1, granted.infusion(CreatureInfusion.CHAMELEON_SKIN));
        CreatureInfusionData revoked = granted.withInfusion(CreatureInfusion.CHAMELEON_SKIN, 0);
        assertEquals(0, revoked.infusion(CreatureInfusion.CHAMELEON_SKIN));
        assertEquals(base, revoked);
    }

    @Test
    void infusionsArrayIsNormalizedToTwelveSlots() {
        CreatureInfusionData shortState = new CreatureInfusionData(new int[] { 4, 5, 6 }, false, false, 0, 0, 0);
        assertEquals(12, shortState.infusions().length);
        assertArrayEquals(new int[] { 4, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, shortState.infusions());
        int[] oversized = new int[16];
        oversized[15] = 9;
        CreatureInfusionData truncated = new CreatureInfusionData(oversized, false, false, 0, 0, 0);
        assertEquals(0, truncated.infusion(15));
    }

    @Test
    void chameleonToggleProducesDistinctStates() {
        CreatureInfusionData visible = chameleonState();
        CreatureInfusionData hidden = new CreatureInfusionData(visible.infusions(), !visible.chameleon(),
            visible.climb(), visible.tumorWarp(), visible.tumorWarpPermanent(), visible.tumorWarpTemp());
        assertFalse(visible.chameleon() == hidden.chameleon());
        assertArrayEquals(visible.infusions(), hidden.infusions());
    }
}
