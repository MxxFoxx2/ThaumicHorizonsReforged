package dev.visfox.thaumichorizons.lib;

/** Legacy soul.png animation timing: 16 frames, one every 40 ms. */
public final class SoulJarFrames {
    public static final int FRAME_COUNT = 16;
    public static final long NANOS_PER_FRAME = 40_000_000L;

    public static int frame(long nanoTime) {
        return (int) Math.floorMod(nanoTime / NANOS_PER_FRAME, (long) FRAME_COUNT);
    }

    private SoulJarFrames() {
    }
}
