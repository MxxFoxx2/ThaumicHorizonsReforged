package dev.visfox.thaumichorizons.aspect;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.compat.CompatAspectList;
import dev.visfox.thaumichorizons.registry.ThEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

// 1:1 entity tags from the original addAspectsToAllTheThings (1.7.10); NightmareTH is not ported.
@EventBusSubscriber(modid = ThaumicHorizons.MODID)
public final class CreatureAspects {
    private CreatureAspects() {
    }

    @SubscribeEvent
    static void commonSetup(FMLCommonSetupEvent event) {
        if (!Compat.port().aspects().isAvailable()) {
            return;
        }
        Compat.port().aspects().registerAspectData(registrar -> {
            registrar.entity(ThEntities.CHROMATIC_SHEEP.get(), tags("bestia", 2, "terra", 2, "sensus", 2, "permutatio", 2));
            registrar.entity(ThEntities.SELF_SHEARING_SHEEP.get(), tags("bestia", 2, "terra", 2, "instrumentum", 2, "pannus", 2));
            registrar.entity(ThEntities.GUARDIAN_PANTHER.get(), tags("bestia", 4, "telum", 4, "perditio", 4));
            registrar.entity(ThEntities.FAMILIAR.get(), tags("bestia", 3, "praecantatio", 3, "auram", 3));
            registrar.entity(ThEntities.GRAVEKEEPER.get(), tags("bestia", 3, "mortuus", 3, "ordo", 3));
            registrar.entity(ThEntities.CHOCOLATE_COW.get(), tags("bestia", 3, "terra", 3, "sensus", 3));
            registrar.entity(ThEntities.SCHOLAR_CHICKEN.get(), tags("bestia", 2, "volatus", 2, "aer", 1, "tenebrae", 2, "cognitio", 2));
            registrar.entity(ThEntities.GOLD_CHICKEN.get(), tags("bestia", 2, "volatus", 2, "aer", 1, "lucrum", 3));
            registrar.entity(ThEntities.ORE_PIG.get(), tags("bestia", 2, "terra", 2, "metallum", 4));
            registrar.entity(ThEntities.TAINT_EATER_PIG.get(), tags("bestia", 2, "terra", 2, "sano", 2, "ordo", 2));
            registrar.entity(ThEntities.SEAWOLF.get(), tags("bestia", 3, "terra", 3, "aqua", 3));
            registrar.entity(ThEntities.LUNAR_WOLF.get(), tags("bestia", 3, "terra", 3, "praecantatio", 3));
            registrar.entity(ThEntities.NETHER_HOUND.get(), tags("bestia", 3, "terra", 3, "ignis", 3));
            registrar.entity(ThEntities.GOLEM_TH.get(), tags("aer", 2, "terra", 2, "praecantatio", 2));
            registrar.entity(ThEntities.ENDERSTEED.get(), tags("bestia", 3, "alienis", 4));
            registrar.entity(ThEntities.SHEEDER.get(), tags("bestia", 3, "pannus", 4));
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