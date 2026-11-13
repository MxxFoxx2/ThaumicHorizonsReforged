package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.EssentiaDynamoBlockEntity;
import dev.visfox.thaumichorizons.blockentity.NodeMonitorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SyntheticNodeBlockEntity;
import dev.visfox.thaumichorizons.blockentity.TransductionAmplifierBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VisDynamoBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VortexStabilizerBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ThaumicHorizons.MODID);

    public static final Supplier<BlockEntityType<VisDynamoBlockEntity>> VIS_DYNAMO = BLOCK_ENTITIES.register(
        "visdynamo", () -> new BlockEntityType<>(VisDynamoBlockEntity::new, ThBlocks.VIS_DYNAMO.get()));
    public static final Supplier<BlockEntityType<EssentiaDynamoBlockEntity>> ESSENTIA_DYNAMO = BLOCK_ENTITIES.register(
        "essentiadynamo", () -> new BlockEntityType<>(EssentiaDynamoBlockEntity::new, ThBlocks.ESSENTIA_DYNAMO.get()));
    public static final Supplier<BlockEntityType<NodeMonitorBlockEntity>> NODE_MONITOR = BLOCK_ENTITIES.register(
        "nodemonitor", () -> new BlockEntityType<>(NodeMonitorBlockEntity::new, ThBlocks.NODE_MONITOR.get()));
    public static final Supplier<BlockEntityType<SyntheticNodeBlockEntity>> SYNTH_NODE = BLOCK_ENTITIES.register(
        "synthnode", () -> new BlockEntityType<>(SyntheticNodeBlockEntity::new, ThBlocks.SYNTH_NODE.get()));
    public static final Supplier<BlockEntityType<TransductionAmplifierBlockEntity>> TRANSDUCTION_AMPLIFIER =
        BLOCK_ENTITIES.register("transductionamplifier",
            () -> new BlockEntityType<>(TransductionAmplifierBlockEntity::new, ThBlocks.TRANSDUCTION_AMPLIFIER.get()));
    public static final Supplier<BlockEntityType<VortexBlockEntity>> VORTEX = BLOCK_ENTITIES.register(
        "vortexth", () -> new BlockEntityType<>(VortexBlockEntity::new, ThBlocks.VORTEX.get()));
    public static final Supplier<BlockEntityType<VortexStabilizerBlockEntity>> VORTEX_STABILIZER = BLOCK_ENTITIES.register(
        "vortexstabilizer", () -> new BlockEntityType<>(VortexStabilizerBlockEntity::new, ThBlocks.VORTEX_STABILIZER.get()));

    private ThBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
