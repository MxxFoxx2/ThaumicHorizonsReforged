package thaumichorizons.compat;

import java.util.ServiceLoader;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;
import thaumichorizons.ThaumicHorizons;

public final class Compat {
    private static @Nullable ThaumcraftPort port;

    private Compat() {
    }

    public static ThaumcraftPort port() {
        if (port == null) {
            Stream<ThaumcraftPort> found = ServiceLoader.load(ThaumcraftPort.class).stream().map(ServiceLoader.Provider::get);
            port = found.findFirst().orElseGet(UnavailablePort::new);
        }
        return port;
    }

    private static final class UnavailablePort implements ThaumcraftPort, AspectCompat, ResearchCompat, WarpCompat, EssentiaCompat, NodeCompat, VisNetCompat, GolemCompat, InfusionCompat, FxCompat, ScanCompat, PotionCompat, ItemCompat {
        @Override
        public String portId() {
            return "unavailable";
        }

        @Override
        public boolean isAvailable() {
            return false;
        }

        @Override
        public AspectCompat aspects() {
            return this;
        }

        @Override
        public ResearchCompat research() {
            return this;
        }

        @Override
        public WarpCompat warp() {
            return this;
        }

        @Override
        public EssentiaCompat essentia() {
            return this;
        }

        @Override
        public NodeCompat nodes() {
            return this;
        }

        @Override
        public VisNetCompat visNet() {
            return this;
        }

        @Override
        public GolemCompat golems() {
            return this;
        }

        @Override
        public InfusionCompat infusion() {
            return this;
        }

        @Override
        public FxCompat fx() {
            return this;
        }

        @Override
        public ScanCompat scan() {
            return this;
        }

        @Override
        public PotionCompat potions() {
            return this;
        }

        @Override
        public ItemCompat items() {
            return this;
        }
    }
}
