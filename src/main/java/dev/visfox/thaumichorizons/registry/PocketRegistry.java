package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.pocket.GatewayPortalBlock;
import dev.visfox.thaumichorizons.block.pocket.PortalTHBlock;
import dev.visfox.thaumichorizons.block.pocket.SlotBlock;
import dev.visfox.thaumichorizons.item.pocket.KeystoneItem;
import dev.visfox.thaumichorizons.item.pocket.PlanarConduitItem;
import dev.visfox.thaumichorizons.world.pocket.PocketPlaneChunkGenerator;
import dev.visfox.thaumichorizons.world.pocket.SlotBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class PocketRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ThaumicHorizons.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ThaumicHorizons.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThaumicHorizons.MODID);
    public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = DeferredRegister.create(
        Registries.CHUNK_GENERATOR, ThaumicHorizons.MODID);

    public static final net.neoforged.neoforge.registries.DeferredBlock<GatewayPortalBlock> GATEWAY_TH = BLOCKS.registerBlock("gatewayth",
        GatewayPortalBlock::new, properties -> properties.mapColor(net.minecraft.world.level.material.MapColor.COLOR_PURPLE).strength(-1.0F, 3600000.0F).noOcclusion().lightLevel(s -> 7));
    public static final net.neoforged.neoforge.registries.DeferredBlock<PortalTHBlock> PORTAL_TH = BLOCKS.registerBlock("portalth",
        PortalTHBlock::new, properties -> properties.mapColor(net.minecraft.world.level.material.MapColor.COLOR_PURPLE).strength(-1.0F, 3600000.0F).noOcclusion().lightLevel(s -> 11));
    public static final net.neoforged.neoforge.registries.DeferredBlock<SlotBlock> SLOT = BLOCKS.registerBlock("slotth",
        SlotBlock::new, properties -> properties.mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(3.0F, 15.0F).noOcclusion());
    public static final net.neoforged.neoforge.registries.DeferredItem<BlockItem> SLOT_ITEM = ITEMS.registerItem("slotth",
        properties -> new BlockItem(SLOT.get(), properties.stacksTo(1)));
    public static final net.neoforged.neoforge.registries.DeferredItem<KeystoneItem> KEYSTONE_TH = ITEMS.registerItem("keystoneth",
        KeystoneItem::new, properties -> properties.stacksTo(1));
    public static final net.neoforged.neoforge.registries.DeferredItem<PlanarConduitItem> PLANAR_CONDUIT = ITEMS.registerItem("planarconduit",
        PlanarConduitItem::new, properties -> properties.stacksTo(1));
    public static final Supplier<BlockEntityType<SlotBlockEntity>> SLOT_BE = BLOCK_ENTITIES.register("slotth",
        () -> new BlockEntityType<>(SlotBlockEntity::new, SLOT.get()));
    public static final Supplier<com.mojang.serialization.MapCodec<? extends ChunkGenerator>> POCKET_GENERATOR = CHUNK_GENERATORS.register(
        "pocket_plane", () -> PocketPlaneChunkGenerator.CODEC);

    private PocketRegistry() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CHUNK_GENERATORS.register(bus);
    }
}