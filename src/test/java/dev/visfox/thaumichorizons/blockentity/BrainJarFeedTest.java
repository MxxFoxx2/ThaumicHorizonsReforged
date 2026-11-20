package dev.visfox.thaumichorizons.blockentity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import java.util.function.DoubleSupplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.junit.jupiter.api.Test;

class BrainJarFeedTest {
    @Test
    void onlyRollsAboveThresholdAddXp() {
        double[] rolls = {0.99, 0.9901, 0.5, 1.0};
        int[] index = {0};
        DoubleSupplier random = () -> rolls[index[0]++];

        assertEquals(2, BrainJarFeed.xpAfter(0, 2000, 4, random));
    }

    @Test
    void xpIsClampedToMax() {
        assertEquals(2000, BrainJarFeed.xpAfter(1999, 2000, 5, () -> 1.0));
    }

    @Test
    void fullJarDoesNotConsumeRolls() {
        int[] index = {0};
        DoubleSupplier random = () -> {
            index[0]++;
            return 1.0;
        };

        assertEquals(2000, BrainJarFeed.xpAfter(2000, 2000, 3, random));
        assertEquals(0, index[0]);
    }

    @Test
    void feedWritesXpThroughCompatForBrainJar() {
        FakeBrainJars jars = new FakeBrainJars(0, 2000);
        jars.isJar = true;

        assertTrue(BrainJarFeed.feed(null, jars, 4, () -> 1.0));
        assertEquals(4, jars.xp);
        assertEquals(1, jars.setCalls);
    }

    @Test
    void failedRollDoesNotWriteThroughCompat() {
        FakeBrainJars jars = new FakeBrainJars(0, 2000);
        jars.isJar = true;

        assertTrue(BrainJarFeed.feed(null, jars, 4, () -> 0.5));
        assertEquals(0, jars.xp);
        assertEquals(0, jars.setCalls);
    }

    @Test
    void zeroBitsDoNotWriteThroughCompat() {
        FakeBrainJars jars = new FakeBrainJars(0, 2000);
        jars.isJar = true;
        int[] rolls = {0};
        DoubleSupplier random = () -> {
            rolls[0]++;
            return 1.0;
        };

        assertTrue(BrainJarFeed.feed(null, jars, 0, random));
        assertEquals(0, jars.xp);
        assertEquals(0, jars.setCalls);
        assertEquals(0, rolls[0]);
    }

    @Test
    void feedIgnoresNonPositiveMax() {
        FakeBrainJars jars = new FakeBrainJars(0, 0);
        jars.isJar = true;

        assertFalse(BrainJarFeed.feed(null, jars, 4, () -> 1.0));
        assertEquals(0, jars.xp);
        assertEquals(0, jars.setCalls);
    }

    @Test
    void feedIgnoresOtherEntities() {
        FakeBrainJars jars = new FakeBrainJars(0, 2000);
        int[] calls = {0};
        DoubleSupplier random = () -> {
            calls[0]++;
            return 1.0;
        };

        assertFalse(BrainJarFeed.feed(null, jars, 4, random));
        assertEquals(0, jars.xp);
        assertEquals(0, calls[0]);
    }

    @Test
    void feedIgnoresFullBrainJar() {
        FakeBrainJars jars = new FakeBrainJars(2000, 2000);
        jars.isJar = true;
        int[] calls = {0};
        DoubleSupplier random = () -> {
            calls[0]++;
            return 1.0;
        };

        assertFalse(BrainJarFeed.feed(null, jars, 4, random));
        assertEquals(2000, jars.xp);
        assertEquals(0, calls[0]);
    }

    private static final class FakeBrainJars extends TcReforgedPort.TcBrainJars {
        private boolean isJar;
        private int xp;
        private int setCalls;
        private final int max;

        private FakeBrainJars(int xp, int max) {
            this.xp = xp;
            this.max = max;
        }

        @Override
        public boolean isBrainJar(BlockEntity entity) {
            return isJar;
        }

        @Override
        public int xp(BlockEntity entity) {
            return xp;
        }

        @Override
        public int maxXp() {
            return max;
        }

        @Override
        public void setXp(BlockEntity entity, int value) {
            xp = value;
            setCalls++;
        }
    }
}
