package dev.visfox.thaumichorizons.compat.tc;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
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
import thaumcraft.damage.ModDamageTypes;
import thaumcraft.entity.AspectOrb;
import thaumcraft.entity.monster.Wisp;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.aspect.AspectHelper;
import thaumcraft.item.AspectTooltips;
import thaumcraft.aspect.AspectRegistry;
import thaumcraft.aspect.AspectSync;
import thaumcraft.device.HandMirrorItem;
import thaumcraft.device.MirrorBlock;
import thaumcraft.device.MirrorBlockEntity;
import thaumcraft.device.MirrorKeys;
import thaumcraft.device.MirrorLinks;
import thaumcraft.device.SmeltingBonuses;
import thaumcraft.entity.TaintedMob;
import thaumcraft.network.NodeZapPayload;
import thaumcraft.node.NodeBlockEntity;
import thaumcraft.node.NodeHooks;
import thaumcraft.node.NodeTransducerBlockEntity;
import thaumcraft.essentia.EssentiaHandler;
import thaumcraft.platform.Baubles;
import thaumcraft.platform.ModNetwork;
import thaumcraft.registry.ModSounds;
import thaumcraft.registry.ModEntities;
import thaumcraft.registry.ModBlocks;
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
import dev.visfox.thaumichorizons.compat.FluxCompat;
import dev.visfox.thaumichorizons.compat.ItemCompat;
import dev.visfox.thaumichorizons.compat.MirrorCompat;
import dev.visfox.thaumichorizons.compat.NodeCompat;
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
    private final FluxCompat flux = new TcFlux();
    private final NodeCompat nodes = new TcNodes();
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
    public FluxCompat flux() {
        return flux;
    }

    @Override
    public NodeCompat nodes() {
        return nodes;
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
        public boolean isAvailable() {
            return true;
        }

        @Override
        public @Nullable Item getSmeltingBonus(ItemStack input) {
            return SmeltingBonuses.get(input);
        }

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
        public boolean consumeVis(ItemStack stack, Player player, Aspect aspect, int amount, boolean crafting) {
            return stack.getItem() instanceof thaumcraft.wand.WandItem wand
                && wand.consumeVis(stack, player, aspect, amount, crafting);
        }

        @Override
        public void hurtWithDissolve(ServerLevel level, Entity entity, float amount) {
            entity.hurt(level.damageSources().source(ModDamageTypes.DISSOLVE), amount);
        }

        @Override
        public boolean spawnAspectOrb(ServerLevel level, double x, double y, double z, Aspect aspect, int amount) {
            AspectOrb orb = AspectOrb.create(level, x, y, z, aspect, amount);
            return orb != null && level.addFreshEntity(orb);
        }

        @Override
        public void spawnWisps(ServerLevel level, BlockPos pos, AspectList aspects, int salisMundusCount) {
            List<Aspect> storedAspects = aspects.getAspects();
            for (int itemIndex = 0; itemIndex < salisMundusCount; itemIndex++) {
                int wispCount = level.getRandom().nextInt(4) + 1;
                for (int i = 0; i < wispCount; i++) {
                    Wisp wisp = ModEntities.WISP.get().create(level, EntitySpawnReason.TRIGGERED);
                    if (wisp == null) {
                        continue;
                    }
                    wisp.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                    if (!storedAspects.isEmpty()) {
                        Aspect aspect = storedAspects.get(level.getRandom().nextInt(storedAspects.size()));
                        wisp.setAspect(aspect);
                    }
                    level.addFreshEntity(wisp);
                }
            }
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
        public @Nullable AspectList getObjectAspects(ItemStack stack) {
            AspectList objectAspects = AspectHelper.getObjectTags(stack);
            return AspectHelper.getBonusTags(stack, objectAspects);
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

    static final class TcFlux implements FluxCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public boolean spill(ServerLevel level, BlockPos pos, boolean gas) {
            if (!level.hasChunkAt(pos) || !level.isEmptyBlock(pos)) {
                return false;
            }
            NodeHooks.spillFlux(level, pos, gas);
            return !level.isEmptyBlock(pos);
        }
    }

    static final class TcNodes implements NodeCompat {
        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public boolean tryRecombine(ServerLevel level, BlockPos machinePos, BlockPos targetPos) {
            if (!(level.getBlockEntity(machinePos.below()) instanceof NodeBlockEntity source)
                    || !(level.getBlockEntity(targetPos) instanceof NodeBlockEntity target)
                    || !level.getBlockState(targetPos).is(ModBlocks.AURA_NODE.get())
                    || target.getLock() > 0
                    || target.getAspects().isEmpty()
                    || source.getId().equals(target.getId())) {
                return false;
            }

            List<Aspect> combinations = List.of();
            for (int priority = 0; priority < 4 && combinations.isEmpty(); priority++) {
                combinations = findCombinations(source, target, priority);
            }
            if (combinations.isEmpty()) {
                return false;
            }

            Aspect result = combinations.get(level.getRandom().nextInt(combinations.size()));
            Aspect[] components = result.getComponents();
            if (components == null || components.length != 2) {
                return false;
            }
            Aspect sourceAspect = source.getAspectsBase().getAmount(components[0]) > 0
                ? components[0]
                : components[1];
            Aspect targetAspect = sourceAspect.equals(components[0]) ? components[1] : components[0];

            source.getAspectsBase().add(result, 1);
            source.getAspects().add(result, 1);
            source.getAspectsBase().remove(sourceAspect, 1);
            source.getAspects().remove(sourceAspect, 1);
            target.getAspects().remove(targetAspect, 1);
            if (level.getRandom().nextInt(3) == 0) {
                target.setNodeVisBase(targetAspect, target.getNodeVisBase(targetAspect) - 1);
            }

            source.sync();
            target.sync();
            ModNetwork.sendToNear(level, null, machinePos.getX(), machinePos.getY(), machinePos.getZ(), 32.0,
                new NodeZapPayload(
                    targetPos.getX() + 0.5F,
                    targetPos.getY() + 0.5F,
                    targetPos.getZ() + 0.5F,
                    machinePos.getX() + 0.5F,
                    machinePos.getY() + 0.5F,
                    machinePos.getZ() + 0.5F));
            return true;
        }

        private List<Aspect> findCombinations(NodeBlockEntity source, NodeBlockEntity target, int priority) {
            LinkedHashSet<Aspect> combinations = new LinkedHashSet<>();
            for (Aspect sourceAspect : source.getAspectsBase().getAspects()) {
                for (Aspect targetAspect : target.getAspectsBase().getAspects()) {
                    if (priority == 0 && (!sourceAspect.isPrimal() || !targetAspect.isPrimal())) {
                        continue;
                    }
                    if (priority == 1 && !sourceAspect.isPrimal()) {
                        continue;
                    }
                    if (priority == 2 && !targetAspect.isPrimal()) {
                        continue;
                    }
                    Aspect combined = Aspect.combine(sourceAspect, targetAspect);
                    if (combined != null) {
                        combinations.add(combined);
                    }
                }
            }
            return List.copyOf(combinations);
        }

        @Override
        public void stopTransducerForVortexConversion(ServerLevel level, BlockPos transducerPos) {
            if (level.getBlockEntity(transducerPos) instanceof NodeTransducerBlockEntity transducer) {
                transducer.stopForVortexConversion();
            }
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
        public void beam(Level level, BlockPos key, net.minecraft.world.phys.Vec3 from,
            net.minecraft.world.phys.Vec3 to, int color, float width, int type) {
            fx().machineBeam(level, key, from, to, color, width, type);
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
