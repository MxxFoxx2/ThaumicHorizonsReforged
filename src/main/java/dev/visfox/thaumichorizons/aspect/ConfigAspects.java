package dev.visfox.thaumichorizons.aspect;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.compat.CompatAspectList;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThItems;

public final class ConfigAspects {
    private ConfigAspects() {
    }

    public static void init() {
        if (!Compat.port().aspects().isAvailable()) {
            return;
        }
        Compat.port().aspects().registerAspectData(registrar -> {
            registrar.item(ThItems.BAR_CHOCOLATE, tags("fames", 2, "victus", 1));
            registrar.item(ThItems.ICE_CREAM, tags("fames", 3, "victus", 1, "gelum", 1));
            registrar.item(ThItems.BUCKET_CHOCOLATE, tags("fames", 2, "victus", 2));
            registrar.item(ThItems.INK_EGG, tags("limus", 2, "tenebrae", 2));
            registrar.item(ThItems.GOLD_EGG, tags("limus", 2, "lucrum", 1));
            registrar.item(ThItems.VOID_PUTTY, tags("fabrico", 4, "alienis", 4));
            registrar.item(ThItems.MEAT, tags("corpus", 4));
            registrar.item(ThItems.MEAT_COOKED, tags("corpus", 3, "ignis", 1));
            registrar.item(ThItems.MEAT_NUGGET, tags("corpus", 1));
            registrar.item(ThBlocks.CHOCOLATE, tags("fames", 5, "victus", 5));
            registrar.item(ThBlocks.BRAIN, tags("cognitio", 4, "corpus", 4));
            registrar.item(ThBlocks.CLOUD, tags("tempestas", 2));
            registrar.item(ThBlocks.VOID, tags("vacuos", 4, "vinculum", 4));
            registrar.item(ThBlocks.DUST, tags("perditio", 2));
            registrar.item(ThBlocks.EYES, tags("sensus", 2, "bestia", 1));
            registrar.item(ThBlocks.BONE, tags("mortuus", 2));
            registrar.item(ThBlocks.FLESH, tags("corpus", 2));
            registrar.item(ThBlocks.CRYSTAL, tags("vitreus", 2));
            registrar.item(ThBlocks.CRYSTAL_DEEP, tags("vitreus", 2, "lux", 2, "permutatio", 1));
            registrar.item(ThBlocks.LEVIATHAN, tags("alienis", 8, "mortuus", 4));
        });
    }

    private static CompatAspectList tags(Object... pairs) {
        CompatAspectList list = new CompatAspectList();
        for (int index = 0; index < pairs.length; index += 2) {
            list.add((String) pairs[index], (Integer) pairs[index + 1]);
        }
        return list;
    }
}
