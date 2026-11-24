package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.BloodInfuserBlockEntity;
import dev.visfox.thaumichorizons.blockentity.CloudBlockEntity;
import dev.visfox.thaumichorizons.blockentity.EssentiaDynamoBlockEntity;
import dev.visfox.thaumichorizons.blockentity.InspiratronBlockEntity;
import dev.visfox.thaumichorizons.blockentity.IlluminationLightBlockEntity;
import dev.visfox.thaumichorizons.blockentity.NodeMonitorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.RecombinatorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulBeaconBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SpikeBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulExtractorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulforgeBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulJarBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SyntheticNodeBlockEntity;
import dev.visfox.thaumichorizons.blockentity.TransductionAmplifierBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VatBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VatConnectorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VatMatrixBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VatSlaveBlockEntity;
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

    public static final Supplier<BlockEntityType<CloudBlockEntity>> CLOUD = BLOCK_ENTITIES.register(
        "cloud", () -> BlockEntityType.Builder.of(CloudBlockEntity::new, ThBlocks.CLOUD.get(), ThBlocks.CLOUD_GLOWING.get()).build(null));
    public static final Supplier<BlockEntityType<SpikeBlockEntity>> SPIKE = BLOCK_ENTITIES.register(
        "spike", () -> BlockEntityType.Builder.of(SpikeBlockEntity::new, ThBlocks.SPIKE.get(), ThBlocks.SPIKE_WOOD.get(), ThBlocks.SPIKE_TOOTH.get()).build(null));
    public static final Supplier<BlockEntityType<VisDynamoBlockEntity>> VIS_DYNAMO = BLOCK_ENTITIES.register(
        "visdynamo", () -> BlockEntityType.Builder.of(VisDynamoBlockEntity::new, ThBlocks.VIS_DYNAMO.get()).build(null));
    public static final Supplier<BlockEntityType<EssentiaDynamoBlockEntity>> ESSENTIA_DYNAMO = BLOCK_ENTITIES.register(
        "essentiadynamo", () -> BlockEntityType.Builder.of(EssentiaDynamoBlockEntity::new, ThBlocks.ESSENTIA_DYNAMO.get()).build(null));
    public static final Supplier<BlockEntityType<NodeMonitorBlockEntity>> NODE_MONITOR = BLOCK_ENTITIES.register(
        "nodemonitor", () -> BlockEntityType.Builder.of(NodeMonitorBlockEntity::new, ThBlocks.NODE_MONITOR.get()).build(null));
    public static final Supplier<BlockEntityType<IlluminationLightBlockEntity>> ILLUMINATION_LIGHT = BLOCK_ENTITIES.register(
        "illumination_light", () -> BlockEntityType.Builder.of(IlluminationLightBlockEntity::new, ThBlocks.LIGHT.get(), ThBlocks.LIGHT_SOLAR.get()).build(null));
    public static final Supplier<BlockEntityType<SyntheticNodeBlockEntity>> SYNTH_NODE = BLOCK_ENTITIES.register(
        "synthnode", () -> BlockEntityType.Builder.of(SyntheticNodeBlockEntity::new, ThBlocks.SYNTH_NODE.get()).build(null));
    public static final Supplier<BlockEntityType<TransductionAmplifierBlockEntity>> TRANSDUCTION_AMPLIFIER =
        BLOCK_ENTITIES.register("transductionamplifier",
            () -> BlockEntityType.Builder.of(TransductionAmplifierBlockEntity::new, ThBlocks.TRANSDUCTION_AMPLIFIER.get()).build(null));
    public static final Supplier<BlockEntityType<RecombinatorBlockEntity>> RECOMBINATOR = BLOCK_ENTITIES.register(
        "recombinator", () -> BlockEntityType.Builder.of(RecombinatorBlockEntity::new, ThBlocks.RECOMBINATOR.get()).build(null));
    public static final Supplier<BlockEntityType<VortexBlockEntity>> VORTEX = BLOCK_ENTITIES.register(
        "vortexth", () -> BlockEntityType.Builder.of(VortexBlockEntity::new, ThBlocks.VORTEX.get()).build(null));
    public static final Supplier<BlockEntityType<VortexStabilizerBlockEntity>> VORTEX_STABILIZER = BLOCK_ENTITIES.register(
        "vortexstabilizer", () -> BlockEntityType.Builder.of(VortexStabilizerBlockEntity::new, ThBlocks.VORTEX_STABILIZER.get()).build(null));
    public static final Supplier<BlockEntityType<SoulJarBlockEntity>> SOUL_JAR = BLOCK_ENTITIES.register(
        "souljar", () -> BlockEntityType.Builder.of(SoulJarBlockEntity::new, ThBlocks.SOUL_JAR.get()).build(null));
    public static final Supplier<BlockEntityType<SoulExtractorBlockEntity>> SOUL_EXTRACTOR = BLOCK_ENTITIES.register(
        "soulsieve", () -> BlockEntityType.Builder.of(SoulExtractorBlockEntity::new, ThBlocks.SOUL_EXTRACTOR.get()).build(null));
    public static final Supplier<BlockEntityType<InspiratronBlockEntity>> INSPIRATRON = BLOCK_ENTITIES.register(
        "inspiratron", () -> BlockEntityType.Builder.of(InspiratronBlockEntity::new, ThBlocks.INSPIRATRON.get()).build(null));
    public static final Supplier<BlockEntityType<SoulforgeBlockEntity>> SOULFORGE = BLOCK_ENTITIES.register(
        "soulforge", () -> BlockEntityType.Builder.of(SoulforgeBlockEntity::new, ThBlocks.SOULFORGE.get()).build(null));
    public static final Supplier<BlockEntityType<SoulBeaconBlockEntity>> SOUL_BEACON = BLOCK_ENTITIES.register(
        "soulbeacon", () -> BlockEntityType.Builder.of(SoulBeaconBlockEntity::new, ThBlocks.SOUL_BEACON.get()).build(null));
    public static final Supplier<BlockEntityType<BloodInfuserBlockEntity>> BLOOD_INFUSER = BLOCK_ENTITIES.register(
        "bloodinfuser", () -> BlockEntityType.Builder.of(BloodInfuserBlockEntity::new, ThBlocks.BLOOD_INFUSER.get()).build(null));
    public static final Supplier<BlockEntityType<VatBlockEntity>> VAT = BLOCK_ENTITIES.register("vat",
        () -> BlockEntityType.Builder.of(VatBlockEntity::new, ThBlocks.VAT_SOLID.get()).build(null));
    public static final Supplier<BlockEntityType<VatSlaveBlockEntity>> VAT_SLAVE = BLOCK_ENTITIES.register("vatslave",
        () -> BlockEntityType.Builder.of(VatSlaveBlockEntity::new, ThBlocks.VAT.get(), ThBlocks.VAT_SOLID.get(), ThBlocks.VAT_INTERIOR.get()).build(null));
    public static final Supplier<BlockEntityType<VatConnectorBlockEntity>> VAT_CONNECTOR = BLOCK_ENTITIES.register(
        "vatconnector", () -> BlockEntityType.Builder.of(VatConnectorBlockEntity::new, ThBlocks.VAT_SOLID.get()).build(null));
    public static final Supplier<BlockEntityType<VatMatrixBlockEntity>> VAT_MATRIX = BLOCK_ENTITIES.register(
        "vatmatrix", () -> BlockEntityType.Builder.of(VatMatrixBlockEntity::new, ThBlocks.MOD_MATRIX.get()).build(null));

    private ThBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
