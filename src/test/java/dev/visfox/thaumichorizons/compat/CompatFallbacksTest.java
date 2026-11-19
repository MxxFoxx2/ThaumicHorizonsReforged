package dev.visfox.thaumichorizons.compat;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.junit.jupiter.api.Test;

class CompatFallbacksTest {
    @Test
    void unavailablePotionCompatThrowsForAllEffects() {
        PotionCompat potions = new PotionCompat() {
        };

        assertFalse(potions.isAvailable());
        assertThrows(UnsupportedOperationException.class, potions::visExhaust);
        assertThrows(UnsupportedOperationException.class, potions::thaumarhia);
        assertThrows(UnsupportedOperationException.class, potions::taintPoison);
        assertThrows(UnsupportedOperationException.class, potions::blurredVision);
        assertThrows(UnsupportedOperationException.class, potions::infectiousVisExhaust);
    }

    @Test
    void unavailablePortUsesUnavailableCompat() {
        ThaumcraftPort port = new ThaumcraftPort() {
        };

        assertFalse(port.potions().isAvailable());
        assertFalse(port.infusion().isAvailable());
        assertFalse(port.brainJars().isAvailable());
    }

    @Test
    void unavailableBrainJarCompatIsInert() {
        BrainJarCompat brainJars = new BrainJarCompat() {
        };

        assertFalse(brainJars.isAvailable());
        assertFalse(brainJars.isBrainJar(null));
        assertEquals(0, brainJars.xp(null));
        assertEquals(0, brainJars.maxXp());
        assertDoesNotThrow(() -> brainJars.setXp(null, 1));
    }

    @Test
    void tcPortExposesBrainJarCompat() {
        ThaumcraftPort port = new TcReforgedPort();

        assertTrue(port.brainJars().isAvailable());
        assertEquals(2000, port.brainJars().maxXp());
        assertFalse(port.brainJars().isBrainJar(null));
    }

    @Test
    void threeArgStabiliserDelegatesWithBlockPosition() {
        BlockPos blockPos = new BlockPos(3, 4, 5);
        BlockPos stabiliserPos = new BlockPos(6, 7, 8);
        BlockPos[] observed = new BlockPos[1];
        InfusionCompat infusion = new InfusionCompat() {
            @Override
            public boolean isStabiliser(Level level, BlockPos pos) {
                observed[0] = pos;
                return true;
            }
        };

        assertTrue(infusion.isStabiliser(null, blockPos, stabiliserPos));
        assertEquals(blockPos, observed[0]);
    }
}
