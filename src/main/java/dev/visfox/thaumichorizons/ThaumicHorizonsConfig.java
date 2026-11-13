package dev.visfox.thaumichorizons;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ThaumicHorizonsConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue WARPED_TUMOR_VALUE;
    public static final ModConfigSpec.BooleanValue ENABLE_POCKET_PLANE;
    public static final ModConfigSpec.BooleanValue USE_ALTERNATE_BELL;

    private ThaumicHorizonsConfig() {
    }

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        WARPED_TUMOR_VALUE = builder.comment("Points of warp held by warped tumor.").defineInRange("warpedTumorValue", 50, 0, Integer.MAX_VALUE);
        ENABLE_POCKET_PLANE = builder.comment("Enable pocket plane content.").define("enablePocketPlane", true);
        USE_ALTERNATE_BELL = builder.comment("Enable alternate golemancer's bell recipe (in case of mod conflict).").define("alternateGolemBell", true);
        SPEC = builder.build();
    }

    public static int warpedTumorValue() {
        return WARPED_TUMOR_VALUE.get();
    }

    public static boolean pocketPlaneEnabled() {
        return ENABLE_POCKET_PLANE.get();
    }

    public static boolean alternateBell() {
        return USE_ALTERNATE_BELL.get();
    }
}
