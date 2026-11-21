package dev.visfox.thaumichorizons.lib;

import java.util.LinkedHashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import thaumcraft.aspect.Aspect;
import thaumcraft.network.NodeZapPayload;
import thaumcraft.node.NodeBlockEntity;
import thaumcraft.platform.ModNetwork;
import thaumcraft.registry.ModBlocks;

/** Aspect recombination performed by the Recombinator multiblock. */
public final class NodeRecombination {
    private NodeRecombination() {
    }

    public static boolean tryRecombine(ServerLevel level, BlockPos machinePos, BlockPos targetPos) {
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

    private static List<Aspect> findCombinations(NodeBlockEntity source, NodeBlockEntity target, int priority) {
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
}
