package thaumichorizons.compat.tc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.AddonAspects;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.aspect.AspectRegistry;
import thaumcraft.aspect.AspectHelper;
import thaumcraft.aspect.AspectSync;
import thaumcraft.research.Knowledge;
import thaumcraft.research.ResearchCategories;
import thaumcraft.research.ResearchGate;
import thaumcraft.research.ResearchItem;
import thaumcraft.research.ResearchManager;
import thaumcraft.research.ResearchPage;
import thaumcraft.research.PlayerKnowledge;
import thaumcraft.research.Warp;
import thaumcraft.research.WarpType;
import thaumichorizons.compat.AspectCompat;
import thaumichorizons.compat.AspectCompat.AspectDataRegistrar;
import thaumichorizons.compat.CompatAspectList;
import thaumichorizons.compat.EssentiaCompat;
import thaumichorizons.compat.FxCompat;
import thaumichorizons.compat.GolemCompat;
import thaumichorizons.compat.InfusionCompat;
import thaumichorizons.compat.ItemCompat;
import thaumichorizons.compat.NodeCompat;
import thaumichorizons.compat.PotionCompat;
import thaumichorizons.compat.ResearchCompat;
import thaumichorizons.compat.ScanCompat;
import thaumichorizons.compat.ThaumcraftPort;
import thaumichorizons.compat.VisNetCompat;
import thaumichorizons.compat.WarpCompat;
import thaumichorizons.compat.WarpKind;

public final class TcReforgedPort implements ThaumcraftPort {
    private final AspectCompat aspects = new TcAspects();
    private final ResearchCompat research = new TcResearch();
    private final WarpCompat warp = new TcWarp();
    private final EssentiaCompat essentia = new UnavailableEssentia();
    private final NodeCompat nodes = new UnavailableNodes();
    private final VisNetCompat visNet = new UnavailableVisNet();
    private final GolemCompat golems = new UnavailableGolems();
    private final InfusionCompat infusion = new UnavailableInfusion();
    private final FxCompat fx = new UnavailableFx();
    private final ScanCompat scan = new UnavailableScan();
    private final PotionCompat potions = new UnavailablePotions();
    private final ItemCompat items = new TcItems();

    @Override
    public String portId() {
        return "thaumcraft-4-reforged";
    }

    @Override
    public AspectCompat aspects() {
        return aspects;
    }

    @Override
    public ResearchCompat research() {
        return research;
    }

    @Override
    public WarpCompat warp() {
        return warp;
    }

    @Override
    public EssentiaCompat essentia() {
        return essentia;
    }

    @Override
    public NodeCompat nodes() {
        return nodes;
    }

    @Override
    public VisNetCompat visNet() {
        return visNet;
    }

    @Override
    public GolemCompat golems() {
        return golems;
    }

    @Override
    public InfusionCompat infusion() {
        return infusion;
    }

    @Override
    public FxCompat fx() {
        return fx;
    }

    @Override
    public ScanCompat scan() {
        return scan;
    }

    @Override
    public PotionCompat potions() {
        return potions;
    }

    @Override
    public ItemCompat items() {
        return items;
    }

    static final class TcAspects implements AspectCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void registerAspect(String tag, int color, List<String> componentTags, int blend, Identifier texture) {
            Aspect[] components = TcConversions.resolveAspects(componentTags).toArray(new Aspect[0]);
            AddonAspects.addRegistrar(api -> api.register(tag, color, components, blend, texture));
        }

        @Override
        public void registerAspectData(Consumer<AspectDataRegistrar> registrar) {
            AspectSync.addRegistrar(registry -> registrar.accept(new DataRegistrar(registry)));
        }

        @Override
        public CompatAspectList getObjectAspects(ItemStack stack) {
            return TcConversions.toCompat(AspectHelper.getObjectTags(stack));
        }
    }

    private record DataRegistrar(AspectRegistry registry) implements AspectDataRegistrar {
        @Override
        public void item(ItemLike item, CompatAspectList aspects) {
            registry.register(item.asItem(), TcConversions.toAspectList(aspects));
        }

        @Override
        public void entity(EntityType<?> type, CompatAspectList aspects, Object... nbtConditions) {
            registry.mergeEntity(type, TcConversions.toAspectList(aspects), nbtConditions);
        }
    }

    static final class TcResearch implements ResearchCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void registerCategory(String key, Identifier icon, Identifier background) {
            ResearchCategories.registerCategory(key, icon, background);
        }

        @Override
        public void registerResearch(ResearchSpec spec) {
            ResearchItem item = new ResearchItem(spec.key(), spec.category(), TcConversions.toAspectList(spec.tags()), spec.column(), spec.row(), spec.complexity(), spec.icon());
            for (ResearchCompat.Flag flag : spec.flags()) {
                item = applyFlag(item, flag);
            }
            if (!spec.parents().isEmpty()) {
                item.setParents(spec.parents().toArray(String[]::new));
            }
            if (!spec.parentsHidden().isEmpty()) {
                item.setParentsHidden(spec.parentsHidden().toArray(String[]::new));
            }
            if (!spec.siblings().isEmpty()) {
                item.setSiblings(spec.siblings().toArray(String[]::new));
            }
            if (!spec.itemTriggers().isEmpty()) {
                item.setItemTriggers(spec.itemTriggers().toArray(String[]::new));
            }
            if (!spec.entityTriggers().isEmpty()) {
                item.setEntityTriggers(spec.entityTriggers().toArray(String[]::new));
            }
            if (!spec.aspectTriggers().isEmpty()) {
                item.setAspectTriggers(TcConversions.resolveAspects(spec.aspectTriggers()).toArray(new Aspect[0]));
            }
            if (spec.warp() != 0) {
                item.setWarp(spec.warp());
            }
            List<ResearchPage> pages = new ArrayList<>();
            for (PageSpec page : spec.pages()) {
                ResearchPage converted = convertPage(page);
                if (converted != null) {
                    pages.add(converted);
                }
            }
            item.setPages(pages.toArray(new ResearchPage[0]));
            item.registerResearchItem();
        }

        private ResearchItem applyFlag(ResearchItem item, Flag flag) {
            return switch (flag) {
                case SPECIAL -> item.setSpecial();
                case SECONDARY -> item.setSecondary();
                case ROUND -> item.setRound();
                case STUB -> item.setStub();
                case VIRTUAL -> item.setVirtual();
                case CONCEALED -> item.setConcealed();
                case HIDDEN -> item.setHidden();
                case LOST -> item.setLost();
                case AUTO_UNLOCK -> item.setAutoUnlock();
            };
        }

        private @Nullable ResearchPage convertPage(PageSpec page) {
            return switch (page.kind()) {
                case TEXT -> ResearchPage.text(page.textKey());
                case CONCEALED_TEXT -> ResearchPage.concealed(page.concealedResearch(), page.textKey());
                case IMAGE -> ResearchPage.image(page.image().toString(), page.textKey());
                case ASPECTS -> null;
                case CRAFTING -> ResearchPage.crafting(page.recipeKeys().toArray(String[]::new));
                case CRUCIBLE -> ResearchPage.crucible(page.recipeKeys().toArray(String[]::new));
                case INFUSION -> ResearchPage.infusion(page.recipeKeys().toArray(String[]::new));
                case CREATURE_INFUSION -> null;
            };
        }

        @Override
        public boolean isComplete(Player player, String researchKey) {
            return ResearchGate.isComplete(player, researchKey);
        }

        @Override
        public void complete(Player player, String researchKey) {
            if (player instanceof ServerPlayer serverPlayer) {
                ResearchManager.complete(serverPlayer, researchKey);
            }
        }
    }

    static final class TcWarp implements WarpCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int get(Player player, WarpKind kind) {
            return Warp.get(player, WarpType.valueOf(kind.name()));
        }

        @Override
        public int total(Player player) {
            return Warp.total(player);
        }

        @Override
        public void add(Player player, int amount, WarpKind kind) {
            if (player instanceof ServerPlayer serverPlayer) {
                Warp.add(serverPlayer, amount, WarpType.valueOf(kind.name()));
            }
        }

        @Override
        public void removePermanent(Player player, int amount) {
            if (!(player instanceof ServerPlayer)) {
                return;
            }
            PlayerKnowledge knowledge = Knowledge.get(player);
            knowledge.setWarp(WarpType.PERMANENT, Math.max(0, knowledge.getWarp(WarpType.PERMANENT) - amount));
            Knowledge.sync(player);
        }

        @Override
        public void registerItemWarp(Identifier item, int warp) {
            Warp.registerItem(item, warp);
        }
    }

    static final class TcItems implements ItemCompat {
        @Override
        public @Nullable Item lookup(Identifier id) {
            Item item = BuiltInRegistries.ITEM.getValue(id);
            return item == Items.AIR ? null : item;
        }

        @Override
        public boolean isWand(ItemStack stack) {
            return thaumcraft.wand.Wands.isWand(stack);
        }

        @Override
        public boolean isEssentiaJar(ItemStack stack) {
            return false;
        }
    }

    static final class UnavailableEssentia implements EssentiaCompat {
    }

    static final class UnavailableNodes implements NodeCompat {
    }

    static final class UnavailableVisNet implements VisNetCompat {
    }

    static final class UnavailableGolems implements GolemCompat {
    }

    static final class UnavailableInfusion implements InfusionCompat {
    }

    static final class UnavailableFx implements FxCompat {
    }

    static final class UnavailableScan implements ScanCompat {
    }

    static final class UnavailablePotions implements PotionCompat {
    }
}
