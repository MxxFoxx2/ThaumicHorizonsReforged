package dev.visfox.thaumichorizons.lib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SoulJarFramesTest {
    @Test
    void firstFrameAtStart() {
        assertEquals(0, SoulJarFrames.frame(0L));
    }

    @Test
    void advancesAfterFortyMilliseconds() {
        assertEquals(1, SoulJarFrames.frame(40_000_000L));
    }

    @Test
    void lastFrameBeforeWrap() {
        assertEquals(15, SoulJarFrames.frame(600_000_000L));
    }

    @Test
    void wrapsAfterSixteenFrames() {
        assertEquals(0, SoulJarFrames.frame(640_000_000L));
    }

    @Test
    void negativeTimeStaysWithinSheet() {
        assertEquals(15, SoulJarFrames.frame(-40_000_000L));
    }
}
