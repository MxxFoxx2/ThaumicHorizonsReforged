package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.BloodInfuserBlockEntity;
import dev.visfox.thaumichorizons.blockentity.EssentiaDynamoBlockEntity;
import dev.visfox.thaumichorizons.blockentity.InspiratronBlockEntity;
import dev.visfox.thaumichorizons.blockentity.IlluminationLightBlockEntity;
import dev.visfox.thaumichorizons.blockentity.NodeMonitorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.RecombinatorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulBeaconBlockEntity;
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

    public static final Supplier<BlockEntityType<VisDynamoBlockEntity>> VIS_DYNAMO = BLOCK_ENTITIES.register(
        "visdynamo", () -> new BlockEntityType<>(VisDynamoBlockEntity::new, ThBlocks.VIS_DYNAMO.get()));
    public static final Supplier<BlockEntityType<EssentiaDynamoBlockEntity>> ESSENTIA_DYNAMO = BLOCK_ENTITIES.register(
        "essentiadynamo", () -> new BlockEntityType<>(EssentiaDynamoBlockEntity::new, ThBlocks.ESSENTIA_DYNAMO.get()));
    public static final Supplier<BlockEntityType<NodeMonitorBlockEntity>> NODE_MONITOR = BLOCK_ENTITIES.register(
        "nodemonitor", () -> new BlockEntityType<>(NodeMonitorBlockEntity::new, ThBlocks.NODE_MONITOR.get()));
    public static final Supplier<BlockEntityType<IlluminationLightBlockEntity>> ILLUMINATION_LIGHT = BLOCK_ENTITIES.register(
        "illumination_light", () -> new BlockEntityType<>(IlluminationLightBlockEntity::new,
            java.util.Set.of(ThBlocks.LIGHT.get(), ThBlocks.LIGHT_SOLAR.get())));
    public static final Supplier<BlockEntityType<SyntheticNodeBlockEntity>> SYNTH_NODE = BLOCK_ENTITIES.register(
        "synthnode", () -> new BlockEntityType<>(SyntheticNodeBlockEntity::new, ThBlocks.SYNTH_NODE.get()));
    public static final Supplier<BlockEntityType<TransductionAmplifierBlockEntity>> TRANSDUCTION_AMPLIFIER =
        BLOCK_ENTITIES.register("transductionamplifier",
            () -> new BlockEntityType<>(TransductionAmplifierBlockEntity::new, ThBlocks.TRANSDUCTION_AMPLIFIER.get()));
    public static final Supplier<BlockEntityType<RecombinatorBlockEntity>> RECOMBINATOR = BLOCK_ENTITIES.register(
        "recombinator", () -> new BlockEntityType<>(RecombinatorBlockEntity::new, ThBlocks.RECOMBINATOR.get()));
    public static final Supplier<BlockEntityType<VortexBlockEntity>> VORTEX = BLOCK_ENTITIES.register(
        "vortexth", () -> new BlockEntityType<>(VortexBlockEntity::new, ThBlocks.VORTEX.get()));
    public static final Supplier<BlockEntityType<VortexStabilizerBlockEntity>> VORTEX_STABILIZER = BLOCK_ENTITIES.register(
        "vortexstabilizer", () -> new BlockEntityType<>(VortexStabilizerBlockEntity::new, ThBlocks.VORTEX_STABILIZER.get()));
    public static final Supplier<BlockEntityType<SoulJarBlockEntity>> SOUL_JAR = BLOCK_ENTITIES.register(
        "souljar", () -> new BlockEntityType<>(SoulJarBlockEntity::new, ThBlocks.SOUL_JAR.get()));
    public static final Supplier<BlockEntityType<SoulExtractorBlockEntity>> SOUL_EXTRACTOR = BLOCK_ENTITIES.register(
        "soulsieve", () -> new BlockEntityType<>(SoulExtractorBlockEntity::new, ThBlocks.SOUL_EXTRACTOR.get()));
    public static final Supplier<BlockEntityType<InspiratronBlockEntity>> INSPIRATRON = BLOCK_ENTITIES.register(
        "inspiratron", () -> new BlockEntityType<>(InspiratronBlockEntity::new, ThBlocks.INSPIRATRON.get()));
    public static final Supplier<BlockEntityType<SoulforgeBlockEntity>> SOULFORGE = BLOCK_ENTITIES.register(
        "soulforge", () -> new BlockEntityType<>(SoulforgeBlockEntity::new, ThBlocks.SOULFORGE.get()));
    public static final Supplier<BlockEntityType<SoulBeaconBlockEntity>> SOUL_BEACON = BLOCK_ENTITIES.register(
        "soulbeacon", () -> new BlockEntityType<>(SoulBeaconBlockEntity::new, ThBlocks.SOUL_BEACON.get()));
    public static final Supplier<BlockEntityType<BloodInfuserBlockEntity>> BLOOD_INFUSER = BLOCK_ENTITIES.register(
        "bloodinfuser", () -> new BlockEntityType<>(BloodInfuserBlockEntity::new, ThBlocks.BLOOD_INFUSER.get()));
    public static final Supplier<BlockEntityType<VatBlockEntity>> VAT = BLOCK_ENTITIES.register("vat",
        () -> new BlockEntityType<>(VatBlockEntity::new, ThBlocks.VAT_SOLID.get()));
    public static final Supplier<BlockEntityType<VatSlaveBlockEntity>> VAT_SLAVE = BLOCK_ENTITIES.register("vatslave",
        () -> new BlockEntityType<>(VatSlaveBlockEntity::new,
            java.util.Set.of(ThBlocks.VAT.get(), ThBlocks.VAT_SOLID.get(), ThBlocks.VAT_INTERIOR.get())));
    public static final Supplier<BlockEntityType<VatConnectorBlockEntity>> VAT_CONNECTOR = BLOCK_ENTITIES.register(
        "vatconnector", () -> new BlockEntityType<>(VatConnectorBlockEntity::new, ThBlocks.VAT_SOLID.get()));
    public static final Supplier<BlockEntityType<VatMatrixBlockEntity>> VAT_MATRIX = BLOCK_ENTITIES.register(
        "vatmatrix", () -> new BlockEntityType<>(VatMatrixBlockEntity::new, ThBlocks.MOD_MATRIX.get()));

    private ThBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
