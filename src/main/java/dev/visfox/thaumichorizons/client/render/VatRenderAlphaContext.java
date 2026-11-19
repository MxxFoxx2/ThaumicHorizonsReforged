package dev.visfox.thaumichorizons.client.render;

/** Scoped alpha state used only while the Vat submits its contained creature. */
public final class VatRenderAlphaContext {
    private static final ThreadLocal<Float> ALPHA = new ThreadLocal<>();

    private VatRenderAlphaContext() {
    }

    public static boolean isActive() {
        return ALPHA.get() != null;
    }

    public static float alpha() {
        Float value = ALPHA.get();
        return value == null ? 1.0F : value;
    }

    public static void run(float alpha, Runnable render) {
        Float previous = ALPHA.get();
        ALPHA.set(Math.max(0.0F, Math.min(1.0F, alpha)));
        try {
            render.run();
        } finally {
            if (previous == null) {
                ALPHA.remove();
            } else {
                ALPHA.set(previous);
            }
        }
    }
}
