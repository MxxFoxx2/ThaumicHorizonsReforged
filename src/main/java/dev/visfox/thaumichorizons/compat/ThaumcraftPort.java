package dev.visfox.thaumichorizons.compat;

public interface ThaumcraftPort {
    String portId();

    AspectCompat aspects();

    ResearchCompat research();

    WarpCompat warp();

    EssentiaCompat essentia();

    NodeCompat nodes();

    VisNetCompat visNet();

    GolemCompat golems();

    InfusionCompat infusion();

    FxCompat fx();

    ScanCompat scan();

    PotionCompat potions();

    ItemCompat items();
}
