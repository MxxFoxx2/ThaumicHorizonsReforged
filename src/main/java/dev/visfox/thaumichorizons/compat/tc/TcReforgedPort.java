package dev.visfox.thaumichorizons.compat.tc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractSkullBlock;
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
import thaumcraft.device.BrainJarBlockEntity;
import thaumcraft.device.HandMirrorItem;
import thaumcraft.device.MirrorBlock;
import thaumcraft.device.MirrorBlockEntity;
import thaumcraft.device.MirrorKeys;
import thaumcraft.device.MirrorLinks;
import thaumcraft.device.SmeltingBonuses;
import thaumcraft.entity.TaintedMob;
import thaumcraft.node.NodeHooks;
import thaumcraft.node.NodeTransducerBlockEntity;
import thaumcraft.essentia.EssentiaHandler;
import thaumcraft.platform.Baubles;
import thaumcraft.registry.ModSounds;
import thaumcraft.registry.ModEntities;
import thaumcraft.research.Knowledge;
import thaumcraft.research.ResearchCategories;
import thaumcraft.research.ResearchGate;
import thaumcraft.research.ResearchItem;
import thaumcraft.research.ResearchManager;
import thaumcraft.research.ResearchPage;
import thaumcraft.research.PlayerKnowledge;
import thaumcraft.research.Warp;
import thaumcraft.research.WarpType;
import dev.visfox.thaumichorizons.compat.CompatAspectList;
import dev.visfox.thaumichorizons.compat.ResearchCompat;
import dev.visfox.thaumichorizons.compat.ResearchCompat.Flag;
import dev.visfox.thaumichorizons.compat.ResearchCompat.PageKind;
import dev.visfox.thaumichorizons.compat.ResearchCompat.PageSpec;
import dev.visfox.thaumichorizons.compat.ResearchCompat.ResearchSpec;
import dev.visfox.thaumichorizons.compat.WarpKind;

public final class TcReforgedPort {
    public static final TcAspects aspects = new TcAspects();
    public static final TcResearch research = new TcResearch();
    public static final TcWarp warp = new TcWarp();
    public static final TcFx fx = new TcFx();
    public static final TcFlux flux = new TcFlux();
    public static final TcNodes nodes = new TcNodes();
    public static final TcPotions potions = new TcPotions();
    public static final TcInfusion infusion = new TcInfusion();
    public static final TcItems items = new TcItems();
    public static final TcMirrors mirrors = new TcMirrors();
    public static final TcBaubles baubles = new TcBaubles();
    public static final TcBrainJars brainJars = new TcBrainJars();

    public static final class TcAspects {
        public void registerAspect(String tag, int color, List<String> componentTags, int blend, Identifier texture) {
            Aspect[] components = TcConversions.resolveAspects(componentTags).toArray(new Aspect[0]);
            AddonAspects.addRegistrar(api -> api.register(tag, color, components, blend, texture));
        }

        public void registerAspectData(Consumer<DataRegistrar> registrar) {
            AspectSync.addRegistrar(registry -> registrar.accept(new DataRegistrar(registry)));
        }
    }

    public record DataRegistrar(AspectRegistry registry) {
        public void item(ItemLike item, CompatAspectList aspects) {
            registry.register(item.asItem(), TcConversions.toAspectList(aspects));
        }

        public void entity(EntityType<?> type, CompatAspectList aspects, Object... nbtConditions) {
            registry.mergeEntity(type, TcConversions.toAspectList(aspects), nbtConditions);
        }
    }

    public static final class TcResearch {
        public void registerCategory(String key, Identifier icon, Identifier background) {
            ResearchCategories.registerCategory(key, icon, background);
        }

        public void registerResearch(ResearchSpec spec) {
            ResearchItem item = new ResearchItem(spec.key(), spec.category(), TcConversions.toAspectList(spec.tags()), spec.column(), spec.row(), spec.complexity(), spec.icon());
            for (ResearchCompat.Flag flag : spec.flags()) {
                item = applyFlag(item, flag);
            }
            if (!spec.parents().isEmpty()) {
                item.setParents(spec.parents().toArray(String[]::new));
            }
            List<String> parentsHidden = new ArrayList<>(spec.parentsHidden());
            if (!parentsHidden.contains("INFUSION")
                    && spec.pages().stream().anyMatch(page -> page.kind() == PageKind.INFUSION)) {
                parentsHidden.add("INFUSION");
            }
            if (!parentsHidden.isEmpty()) {
                item.setParentsHidden(parentsHidden.toArray(String[]::new));
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
            };
        }

        public boolean isComplete(Player player, String researchKey) {
            return ResearchGate.isComplete(player, researchKey);
        }

        public void complete(Player player, String researchKey) {
            if (player instanceof ServerPlayer serverPlayer) {
                ResearchManager.complete(serverPlayer, researchKey);
            }
        }
    }

    public static final class TcWarp {
        public int get(Player player, WarpKind kind) {
            return Warp.get(player, WarpType.valueOf(kind.name()));
        }

        public int total(Player player) {
            return Warp.total(player);
        }

        public void add(Player player, int amount, WarpKind kind) {
            if (player instanceof ServerPlayer serverPlayer) {
                Warp.add(serverPlayer, amount, WarpType.valueOf(kind.name()));
            }
        }

        public void removePermanent(Player player, int amount) {
            if (!(player instanceof ServerPlayer)) {
                return;
            }
            PlayerKnowledge knowledge = Knowledge.get(player);
            knowledge.setWarp(WarpType.PERMANENT, Math.max(0, knowledge.getWarp(WarpType.PERMANENT) - amount));
            Knowledge.sync(player);
        }

        public void registerItemWarp(Identifier item, int warp) {
            Warp.registerItem(item, warp);
        }
    }

    public static final class TcItems {
        public @Nullable Item getSmeltingBonus(ItemStack input) {
            return SmeltingBonuses.get(input);
        }

        public @Nullable Item lookup(Identifier id) {
            Item item = BuiltInRegistries.ITEM.getValue(id);
            return item == Items.AIR ? null : item;
        }

        public boolean isWand(ItemStack stack) {
            return thaumcraft.wand.Wands.isWand(stack);
        }

        public boolean consumeVis(ItemStack stack, Player player, Aspect aspect, int amount, boolean crafting) {
            return stack.getItem() instanceof thaumcraft.wand.WandItem wand
                && wand.consumeVis(stack, player, aspect, amount, crafting);
        }

        public void hurtWithDissolve(ServerLevel level, Entity entity, float amount) {
            entity.hurt(level.damageSources().source(ModDamageTypes.DISSOLVE), amount);
        }

        public boolean spawnAspectOrb(ServerLevel level, double x, double y, double z, Aspect aspect, int amount) {
            AspectOrb orb = AspectOrb.create(level, x, y, z, aspect, amount);
            return orb != null && level.addFreshEntity(orb);
        }

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

        public boolean isEssentiaJar(ItemStack stack) {
            return false;
        }

        public AspectList getAspects(ItemStack stack) {
            return AspectTooltips.getAspects(stack);
        }

        public @Nullable AspectList getObjectAspects(ItemStack stack) {
            AspectList objectAspects = AspectHelper.getObjectTags(stack);
            return AspectHelper.getBonusTags(stack, objectAspects);
        }

        public void setSingleAspect(ItemStack stack, Aspect aspect, int amount) {
            AspectTooltips.setSingle(stack, aspect, amount);
        }
    }

    public static final class TcMirrors {
        public boolean isMirror(Level level, BlockPos pos) {
            return level.getBlockState(pos).getBlock() instanceof MirrorBlock mirror && !mirror.isEssentia();
        }

        public boolean isLinked(ItemStack stack) {
            return MirrorKeys.LINK.has(stack);
        }

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

        public void appendLink(ItemStack stack, Consumer<Component> builder) {
            GlobalPos link = MirrorKeys.LINK.get(stack);
            if (link != null) {
                MirrorLinks.appendLink(link, builder);
            }
        }

        public boolean transport(ItemStack mirror, ItemStack items, Player player, Level level) {
            return HandMirrorItem.transport(mirror, items, player, level);
        }
    }

    public static final class TcBaubles {
        public int slotCount() {
            return Baubles.slotCount();
        }

        public ItemStack get(Player player, int slot) {
            return Baubles.get(player, slot);
        }

        public void set(Player player, int slot, ItemStack stack) {
            Baubles.set(player, slot, stack);
        }
    }

    public static class TcBrainJars {
        public boolean isBrainJar(BlockEntity entity) {
            return entity instanceof BrainJarBlockEntity;
        }

        public int xp(BlockEntity entity) {
            return entity instanceof BrainJarBlockEntity jar ? jar.getXp() : 0;
        }

        public int maxXp() {
            return BrainJarBlockEntity.XP_MAX;
        }

        public void setXp(BlockEntity entity, int xp) {
            if (entity instanceof BrainJarBlockEntity jar) {
                jar.setXp(Math.max(0, Math.min(xp, BrainJarBlockEntity.XP_MAX)));
                jar.sync();
            }
        }
    }

    public static final class TcFlux {
        public boolean spill(ServerLevel level, BlockPos pos, boolean gas) {
            if (!level.hasChunkAt(pos) || !level.isEmptyBlock(pos)) {
                return false;
            }
            NodeHooks.spillFlux(level, pos, gas);
            return !level.isEmptyBlock(pos);
        }
    }

    public static final class TcNodes {
        public void stopTransducerForVortexConversion(ServerLevel level, BlockPos transducerPos) {
            if (level.getBlockEntity(transducerPos) instanceof NodeTransducerBlockEntity transducer) {
                transducer.stopForVortexConversion();
            }
        }
    }

    public static final class TcFx {
        private static thaumcraft.fx.FxProxy fx() {
            return thaumcraft.fx.Fx.get();
        }

        public void sparkle(Level level, float x, float y, float z, float size, int color, float gravity) {
            fx().sparkle(level, x, y, z, size, color, gravity);
        }

        public void wisp(Level level, net.minecraft.world.phys.Vec3 from, net.minecraft.world.phys.Vec3 to, int color, boolean longer) {
            fx().essentiaTrail(level, BlockPos.containing(from), BlockPos.containing(to), longer ? 16 : 8, color, 1.0F);
        }

        public void burst(Level level, net.minecraft.world.phys.Vec3 pos, int color, int count) {
            fx().shockBurst(level, pos.x, pos.y, pos.z, 1.0F + count * 0.1F);
        }

        public void arcLightning(Level level, net.minecraft.world.phys.Vec3 from, net.minecraft.world.phys.Vec3 to, int color) {
            fx().nodeBolt(level, (float) from.x, (float) from.y, (float) from.z, (float) to.x, (float) to.y, (float) to.z);
        }

        public void beam(Level level, BlockPos key, net.minecraft.world.phys.Vec3 from,
            net.minecraft.world.phys.Vec3 to, int color, float width, int type) {
            fx().machineBeam(level, key, from, to, color, width, type);
        }

        public void blockSparkle(Level level, BlockPos pos, int color, int count) {
            fx().blockSparkle(level, pos, color, count);
        }

        public void essentiaTrail(Level level, BlockPos from, net.minecraft.world.phys.Vec3 to, int color) {
            fx().essentiaTrail(level, from, BlockPos.containing(to), 8, color, 1.0F);
        }

        public void blocksplosion(Level level, BlockPos center, int color) {
            fx().shockBurst(level, center.getX() + 0.5D, center.getY() + 0.5D, center.getZ() + 0.5D, 1.6F);
            fx().blockSparkle(level, center, color, 12);
        }

        public void containmentCapture(Level level, net.minecraft.world.phys.Vec3 pos, int color) {
            fx().sparkle(level, (float) pos.x, (float) pos.y, (float) pos.z, 1.0F, color, -0.03F);
        }

        public void essentiaBubble(Level level, BlockPos pos, int color) {
            fx().blockBubble(level, pos, color);
        }

        public void deadCreatureFx(Level level, net.minecraft.world.phys.Vec3 pos, int color) {
            fx().sparkle(level, (float) pos.x, (float) pos.y, (float) pos.z, 0.8F, color, -0.05F);
        }
    }

    public static final class TcPotions {
        public Holder<MobEffect> visExhaust() {
            return effect("vis_exhaust");
        }

        public Holder<MobEffect> thaumarhia() {
            return effect("thaumarhia");
        }

        public Holder<MobEffect> taintPoison() {
            return effect("taint_poison");
        }

        public Holder<MobEffect> blurredVision() {
            return effect("blurred_vision");
        }

        public Holder<MobEffect> infectiousVisExhaust() {
            return effect("infectious_vis_exhaust");
        }

        private static Holder<MobEffect> effect(String path) {
            return BuiltInRegistries.MOB_EFFECT
                .get(Identifier.fromNamespaceAndPath("thaumcraft", path))
                .<Holder<MobEffect>>map(reference -> reference)
                .orElse(MobEffects.POISON);
        }
    }

    public static final class TcInfusion {
        public boolean isStabiliser(Level level, BlockPos pos) {
            return isStabiliser(level, pos, pos);
        }

        public boolean isStabiliser(Level level, BlockPos blockPos, BlockPos stabiliserPos) {
            net.minecraft.world.level.block.Block block = level.getBlockState(blockPos).getBlock();
            return block instanceof AbstractSkullBlock
                || block instanceof thaumcraft.block.InfusionStabiliser stabiliser
                    && stabiliser.canStabiliseInfusion(level, stabiliserPos);
        }
    }
}
