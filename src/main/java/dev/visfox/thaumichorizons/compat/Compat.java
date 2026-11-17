package dev.visfox.thaumichorizons.compat;

import java.util.ServiceLoader;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public final class Compat {
    private static final ThaumcraftPort UNAVAILABLE = new ThaumcraftPort() {
    };
    private static @Nullable ThaumcraftPort port;

    private Compat() {
    }

    public static ThaumcraftPort port() {
        if (port == null) {
            Stream<ThaumcraftPort> found = ServiceLoader.load(ThaumcraftPort.class).stream().map(ServiceLoader.Provider::get);
            port = found.findFirst().orElse(UNAVAILABLE);
        }
        return port;
    }
}
