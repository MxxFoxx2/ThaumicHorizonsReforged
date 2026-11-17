package dev.visfox.thaumichorizons.compat.tc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.AddonAspects;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.item.AspectTooltips;
import thaumcraft.aspect.AspectRegistry;
import thaumcraft.aspect.AspectSync;
import thaumcraft.device.HandMirrorItem;
import thaumcraft.device.MirrorBlock;
import thaumcraft.device.MirrorBlockEntity;
import thaumcraft.device.MirrorKeys;
import thaumcraft.device.MirrorLinks;
import thaumcraft.entity.TaintedMob;
import thaumcraft.essentia.EssentiaHandler;
import thaumcraft.platform.Baubles;
import thaumcraft.registry.ModSounds;
import thaumcraft.research.Knowledge;
import thaumcraft.research.ResearchCategories;
import thaumcraft.research.ResearchGate;
import thaumcraft.research.ResearchItem;
import thaumcraft.research.ResearchManager;
import thaumcraft.research.ResearchPage;
import thaumcraft.research.PlayerKnowledge;
import thaumcraft.research.Warp;
import thaumcraft.research.WarpType;
import dev.visfox.thaumichorizons.compat.AspectCompat;
import dev.visfox.thaumichorizons.compat.AspectCompat.AspectDataRegistrar;
import dev.visfox.thaumichorizons.compat.BaubleCompat;
import dev.visfox.thaumichorizons.compat.CompatAspectList;
import dev.visfox.thaumichorizons.compat.EssentiaCompat;
import dev.visfox.thaumichorizons.compat.FxCompat;
import dev.visfox.thaumichorizons.compat.ItemCompat;
import dev.visfox.thaumichorizons.compat.MirrorCompat;
import dev.visfox.thaumichorizons.compat.PotionCompat;
import dev.visfox.thaumichorizons.compat.ResearchCompat;
import dev.visfox.thaumichorizons.compat.ThaumcraftPort;
import dev.visfox.thaumichorizons.compat.WarpCompat;
import dev.visfox.thaumichorizons.compat.WarpKind;

public final class TcReforgedPort implements ThaumcraftPort {
    private final AspectCompat aspects = new TcAspects();
    private final ResearchCompat research = new TcResearch();
    private final WarpCompat warp = new TcWarp();
    private final EssentiaCompat essentia = new TcEssentia();
    private final FxCompat fx = new TcFx();
    private final PotionCompat potions = new UnavailablePotions();
    private final ItemCompat items = new TcItems();
    private final MirrorCompat mirrors = new TcMirrors();
    private final BaubleCompat baubles = new TcBaubles();

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
    public FxCompat fx() {
        return fx;
    }

    @Override
    public PotionCompat potions() {
        return potions;
    }

    @Override
    public ItemCompat items() {
        return items;
    }

    @Override
    public MirrorCompat mirrors() {
        return mirrors;
    }

    @Override
    public BaubleCompat baubles() {
        return baubles;
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
                case ARCANE -> ResearchPage.arcane(page.recipeKeys().toArray(String[]::new));
                case CRUCIBLE -> ResearchPage.crucible(page.recipeKeys().toArray(String[]::new));
                case INFUSION -> ResearchPage.infusion(page.recipeKeys().toArray(String[]::new));
                case CREATURE_INFUSION -> ResearchPage.creatureInfusion(page.payload());
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

        @Override
        public AspectList getAspects(ItemStack stack) {
            return AspectTooltips.getAspects(stack);
        }

        @Override
        public void setSingleAspect(ItemStack stack, Aspect aspect, int amount) {
            AspectTooltips.setSingle(stack, aspect, amount);
        }
    }

    static final class TcMirrors implements MirrorCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public boolean isMirror(Level level, BlockPos pos) {
            return level.getBlockState(pos).getBlock() instanceof MirrorBlock mirror && !mirror.isEssentia();
        }

        @Override
        public boolean isLinked(ItemStack stack) {
            return MirrorKeys.LINK.has(stack);
        }

        @Override
        public void link(ItemStack stack, Player player, Level level, BlockPos pos) {
            if (!(level.getBlockEntity(pos) instanceof MirrorBlockEntity)) {
                return;
            }
            MirrorKeys.LINK.set(stack, GlobalPos.of(level.dimension(), pos));
            level.playSound(null, pos, ModSounds.JAR.get(), SoundSource.BLOCKS, 1.0F, 2.0F);
            player.sendSystemMessage(Component.translatable("tc.handmirrorlinked")
                .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC));
            player.inventoryMenu.broadcastChanges();
        }

        @Override
        public void appendLink(ItemStack stack, Consumer<Component> builder) {
            GlobalPos link = MirrorKeys.LINK.get(stack);
            if (link != null) {
                MirrorLinks.appendLink(link, builder);
            }
        }

        @Override
        public boolean transport(ItemStack mirror, ItemStack items, Player player, Level level) {
            return HandMirrorItem.transport(mirror, items, player, level);
        }
    }

    static final class TcBaubles implements BaubleCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int slotCount() {
            return Baubles.slotCount();
        }

        @Override
        public ItemStack get(Player player, int slot) {
            return Baubles.get(player, slot);
        }

        @Override
        public void set(Player player, int slot, ItemStack stack) {
            Baubles.set(player, slot, stack);
        }
    }

    static final class TcEssentia implements EssentiaCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public int drainEssentia(Level level, BlockPos from, String aspectTag, int amount, Direction side, int range) {
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (aspect == null || amount <= 0) {
                return 0;
            }
            if (!(level.getBlockEntity(from) instanceof BlockEntity tile)) {
                return 0;
            }
            return EssentiaHandler.drainEssentia(tile, aspect, side, range) ? amount : 0;
        }

        @Override
        public boolean isTaintedMob(LivingEntity entity) {
            return entity instanceof TaintedMob;
        }
    }

    static final class TcFx implements FxCompat {
        private static thaumcraft.fx.FxProxy fx() {
            return thaumcraft.fx.Fx.get();
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public void sparkle(Level level, float x, float y, float z, float size, int color, float gravity) {
            fx().sparkle(level, x, y, z, size, color, gravity);
        }

        @Override
        public void wisp(Level level, net.minecraft.world.phys.Vec3 from, net.minecraft.world.phys.Vec3 to, int color, boolean longer) {
            fx().essentiaTrail(level, BlockPos.containing(from), BlockPos.containing(to), longer ? 16 : 8, color, 1.0F);
        }

        @Override
        public void burst(Level level, net.minecraft.world.phys.Vec3 pos, int color, int count) {
            fx().shockBurst(level, pos.x, pos.y, pos.z, 1.0F + count * 0.1F);
        }

        @Override
        public void arcLightning(Level level, net.minecraft.world.phys.Vec3 from, net.minecraft.world.phys.Vec3 to, int color) {
            fx().nodeBolt(level, (float) from.x, (float) from.y, (float) from.z, (float) to.x, (float) to.y, (float) to.z);
        }

        @Override
        public void blockSparkle(Level level, BlockPos pos, int color, int count) {
            fx().blockSparkle(level, pos, color, count);
        }

        @Override
        public void essentiaTrail(Level level, BlockPos from, net.minecraft.world.phys.Vec3 to, int color) {
            fx().essentiaTrail(level, from, BlockPos.containing(to), 8, color, 1.0F);
        }

        @Override
        public void blocksplosion(Level level, BlockPos center, int color) {
            fx().blocksplosion(level, center, color);
        }

        @Override
        public void containmentCapture(Level level, net.minecraft.world.phys.Vec3 pos, int color) {
            fx().containmentCapture(level, pos.x, pos.y, pos.z, color);
        }

        @Override
        public void essentiaBubble(Level level, BlockPos pos, int color) {
            fx().blockBubble(level, pos, color);
        }

        @Override
        public void deadCreatureFx(Level level, net.minecraft.world.phys.Vec3 pos, int color) {
            fx().deadCreature(level, pos.x, pos.y, pos.z, color);
        }
    }

    static final class UnavailablePotions implements PotionCompat {
    }
}
