package dev.visfox.thaumichorizons.world.pocket;

import net.minecraft.util.RandomSource;

public final class PocketNoise {
    private static final int OCTAVES = 10;

    private PocketNoise() {
    }

    public static double[] generateOctaves(RandomSource random, int x0, int y0, int z0, int xSize, int ySize,
            int zSize, double xScale, double yScale, double zScale) {
        long seed = random.nextLong();
        double[] result = new double[xSize * ySize * zSize];
        double amplitude = 1.0D;
        double total = 0.0D;
        for (int octave = 0; octave < OCTAVES; octave++) {
            double frequency = Math.pow(2.0D, octave);
            for (int y = 0; y < ySize; y++) {
                for (int z = 0; z < zSize; z++) {
                    for (int x = 0; x < xSize; x++) {
                        double dx = (x0 + x) / (xScale * frequency + 1.0E-5D);
                        double dy = (y0 + y) / (yScale * frequency + 1.0E-5D);
                        double dz = (z0 + z) / (zScale * frequency + 1.0E-5D);
                        result[x + xSize * z + y * xSize * zSize] += amplitude * valueNoise(seed, octave, dx, dy, dz);
                    }
                }
            }
            total += amplitude;
            amplitude *= 0.5D;
        }
        for (int i = 0; i < result.length; i++) {
            result[i] /= total;
        }
        return result;
    }

    private static double valueNoise(long seed, int octave, double x, double y, double z) {
        int x0 = floor(x);
        int y0 = floor(y);
        int z0 = floor(z);
        double fx = x - x0;
        double fy = y - y0;
        double fz = z - z0;
        double sx = fx * fx * (3.0D - 2.0D * fx);
        double sy = fy * fy * (3.0D - 2.0D * fy);
        double sz = fz * fz * (3.0D - 2.0D * fz);
        double n000 = lattice(seed, octave, x0, y0, z0);
        double n100 = lattice(seed, octave, x0 + 1, y0, z0);
        double n010 = lattice(seed, octave, x0, y0 + 1, z0);
        double n110 = lattice(seed, octave, x0 + 1, y0 + 1, z0);
        double n001 = lattice(seed, octave, x0, y0, z0 + 1);
        double n101 = lattice(seed, octave, x0 + 1, y0, z0 + 1);
        double n011 = lattice(seed, octave, x0, y0 + 1, z0 + 1);
        double n111 = lattice(seed, octave, x0 + 1, y0 + 1, z0 + 1);
        double nx00 = n000 + (n100 - n000) * sx;
        double nx10 = n010 + (n110 - n010) * sx;
        double nx01 = n001 + (n101 - n001) * sx;
        double nx11 = n011 + (n111 - n011) * sx;
        double nxy0 = nx00 + (nx10 - nx00) * sy;
        double nxy1 = nx01 + (nx11 - nx01) * sy;
        return nxy0 + (nxy1 - nxy0) * sz;
    }

    private static double lattice(long seed, int octave, int x, int y, int z) {
        long hash = seed;
        hash = hash * 31 + octave;
        hash = hash * 31 + x;
        hash = hash * 31 + y;
        hash = hash * 31 + z;
        hash ^= hash >> 16;
        hash *= 0x2545F4914F6CDD1DL;
        hash ^= hash >> 13;
        return (hash & 0xFFFFL) / 32767.5D - 1.0D;
    }

    private static int floor(double value) {
        int i = (int) value;
        return value < i ? i - 1 : i;
    }
}
