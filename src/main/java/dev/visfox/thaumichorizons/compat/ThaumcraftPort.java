package dev.visfox.thaumichorizons.compat;

public interface ThaumcraftPort {
    AspectCompat UNAVAILABLE_ASPECTS = new AspectCompat() {
    };
    ResearchCompat UNAVAILABLE_RESEARCH = new ResearchCompat() {
    };
    WarpCompat UNAVAILABLE_WARP = new WarpCompat() {
    };
    EssentiaCompat UNAVAILABLE_ESSENTIA = new EssentiaCompat() {
    };
    FxCompat UNAVAILABLE_FX = new FxCompat() {
    };
    PotionCompat UNAVAILABLE_POTIONS = new PotionCompat() {
    };
    ItemCompat UNAVAILABLE_ITEMS = new ItemCompat() {
    };
    MirrorCompat UNAVAILABLE_MIRRORS = new MirrorCompat() {
    };
    BaubleCompat UNAVAILABLE_BAUBLES = new BaubleCompat() {
    };

    default String portId() {
        return "unavailable";
    }

    default AspectCompat aspects() {
        return UNAVAILABLE_ASPECTS;
    }

    default ResearchCompat research() {
        return UNAVAILABLE_RESEARCH;
    }

    default WarpCompat warp() {
        return UNAVAILABLE_WARP;
    }

    default EssentiaCompat essentia() {
        return UNAVAILABLE_ESSENTIA;
    }

    default FxCompat fx() {
        return UNAVAILABLE_FX;
    }

    default PotionCompat potions() {
        return UNAVAILABLE_POTIONS;
    }

    default ItemCompat items() {
        return UNAVAILABLE_ITEMS;
    }

    default MirrorCompat mirrors() {
        return UNAVAILABLE_MIRRORS;
    }

    default BaubleCompat baubles() {
        return UNAVAILABLE_BAUBLES;
    }
}
