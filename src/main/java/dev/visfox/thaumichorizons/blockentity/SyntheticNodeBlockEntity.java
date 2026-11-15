package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.node.Node;
import thaumcraft.node.NodeModifier;
import thaumcraft.node.NodeType;
import thaumcraft.research.ResearchGate;
import thaumcraft.vis.VisNode;
import thaumcraft.wand.WandInteractable;
import thaumcraft.wand.WandKeys;
import thaumcraft.wand.Wands;

public class SyntheticNodeBlockEntity extends VisNode implements Node, WandInteractable {
    private AspectList aspects = new AspectList();
    private AspectList fractionalAspects = new AspectList();
    private AspectList aspectsMax = new AspectList();
    public float rotation;
    float increment = 1.0F;
    public int drainColor = 16777215;
    public int targetColor = 16777215;
    public int color = 16777215;

    public SyntheticNodeBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.SYNTH_NODE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SyntheticNodeBlockEntity entity) {
        entity.tick(level);
    }

    private void tick(Level tickLevel) {
        tickNetwork(tickLevel);
        if (!tickLevel.isClientSide()) {
            for (Aspect aspect : aspectsMax.getAspectsSortedAmount()) {
                if (aspects.getAmount(aspect) < aspectsMax.getAmount(aspect)) {
                    if (aspect.isPrimal()) {
                        int drained = consumeVis(aspect, 10);
                        if (drained > 0) {
                            addFractionalToContainer(aspect, drained);
                        }
                    } else {
                        AspectList primalComponents = reduceToPrimals(new AspectList().add(aspect, 1));
                        int actuallyDrained = 100;
                        for (Aspect primal : primalComponents.getAspects()) {
                            int drained = consumeVis(primal, 10);
                            if (drained < actuallyDrained) {
                                actuallyDrained = drained;
                            }
                        }
                        if (actuallyDrained > 0) {
                            addFractionalToContainer(aspect, actuallyDrained);
                        }
                    }
                }
            }
        } else {
            rotation += increment;
            if (rotation > 360.0F) {
                rotation -= 360.0F;
            }
        }
    }

    public AspectList getMaxAspects() {
        return aspectsMax;
    }

    public boolean addFractionalToContainer(Aspect tag, int amount) {
        if (aspects.getAmount(tag) < aspectsMax.getAmount(tag)) {
            fractionalAspects.add(tag, amount);
            while (fractionalAspects.getAmount(tag) > 100) {
                addToContainer(tag, 1);
                fractionalAspects.remove(tag, 100);
            }
            return true;
        }
        return false;
    }

    public @Nullable Aspect chooseRandomFilteredFromSource(AspectList filter, boolean preserve) {
        int min = preserve ? 1 : 0;
        ArrayList<Aspect> validaspects = new ArrayList<>();
        for (Aspect prim : aspects.getAspects()) {
            if (filter.getAmount(prim) > 0 && aspects.getAmount(prim) > min) {
                validaspects.add(prim);
            }
        }
        if (validaspects.isEmpty()) {
            return null;
        }
        Aspect asp = validaspects.get(level != null ? level.getRandom().nextInt(validaspects.size()) : 0);
        if (asp != null && aspects.getAmount(asp) > min) {
            return asp;
        }
        return null;
    }

    @Override
    public InteractionResult onWandUse(Level level, BlockPos pos, Player player, ItemStack wand) {
        WandKeys.TARGET.set(wand, worldPosition);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUsingWandTick(ItemStack wand, Player player, int count) {
        Level useLevel = player.level();
        HitResult hit = player.pick(player.blockInteractionRange(), 1.0F, true);
        if (!(hit instanceof BlockHitResult blockHit)
            || hit.getType() != HitResult.Type.BLOCK
            || !blockHit.getBlockPos().equals(worldPosition)) {
            player.stopUsingItem();
            return;
        }
        if (count % 5 == 0) {
            int tap = 1;
            if (ResearchGate.isComplete(player, "NODETAPPER1")) {
                tap++;
            }
            if (ResearchGate.isComplete(player, "NODETAPPER2")) {
                tap++;
            }
            boolean preserve = !player.isShiftKeyDown()
                && ResearchGate.isComplete(player, "NODEPRESERVE")
                && !Wands.getRod(wand).tag().equals("wood")
                && !Wands.getCap(wand).tag().equals("iron");
            boolean success = false;
            boolean mfu = false;
            Aspect aspect = chooseRandomFilteredFromSource(Wands.getAspectsWithRoom(wand), preserve);
            if (aspect != null) {
                int amt = getAspects().getAmount(aspect);
                if (tap > amt) {
                    tap = amt;
                }
                if (preserve && tap == amt) {
                    tap--;
                }
                if (tap > 0) {
                    int rem = Wands.addVis(wand, aspect, tap, !useLevel.isClientSide());
                    if (rem < tap) {
                        drainColor = aspect.getColor();
                        if (!useLevel.isClientSide()) {
                            takeFromContainer(aspect, tap - rem);
                            mfu = true;
                        }
                        success = true;
                    }
                }
            }
            if (success) {
                targetColor = drainColor;
            }
            if (mfu) {
                sync();
            }
        }
        if (useLevel.isClientSide()) {
            int r = targetColor >> 16 & 0xFF;
            int g = targetColor >> 8 & 0xFF;
            int b = targetColor & 0xFF;
            int r2 = (color >> 16 & 0xFF) * 4;
            int g2 = (color >> 8 & 0xFF) * 4;
            int b2 = (color & 0xFF) * 4;
            color = (r + r2) / 5 << 16 | (g + g2) / 5 << 8 | (b + b2) / 5;
        }
    }

    @Override
    public String getId() {
        return "SYNTHETIC";
    }

    @Override
    public AspectList getAspectsBase() {
        return aspectsMax;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.NORMAL;
    }

    @Override
    public void setNodeType(NodeType nodeType) {
    }

    @Override
    public @Nullable NodeModifier getNodeModifier() {
        return null;
    }

    @Override
    public void setNodeModifier(@Nullable NodeModifier nodeModifier) {
    }

    @Override
    public int getNodeVisBase(Aspect aspect) {
        return aspectsMax.getAmount(aspect);
    }

    @Override
    public void setNodeVisBase(Aspect aspect, int nodeVisBase) {
    }

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public boolean isSource() {
        return false;
    }

    @Override
    public AspectList getAspects() {
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
        aspectsMax = aspects;
        this.aspects = new AspectList();
        fractionalAspects = new AspectList();
        for (Aspect asp : aspects.getAspectsSortedAmount()) {
            this.aspects.add(asp, 0);
            fractionalAspects.add(asp, 0);
        }
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        aspects.add(tag, amount);
        int toReturn = amount;
        if (aspectsMax.getAmount(tag) < aspects.getAmount(tag)) {
            toReturn -= aspectsMax.getAmount(tag) - aspects.getAmount(tag);
            aspects.reduce(tag, aspectsMax.getAmount(tag) - aspects.getAmount(tag));
        }
        sync();
        return toReturn;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return aspects.reduce(tag, amount);
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return aspects.getAmount(tag) >= amount;
    }

    @Override
    public int containerContains(Aspect tag) {
        return aspects.getAmount(tag);
    }

    private static AspectList reduceToPrimals(AspectList source) {
        AspectList result = new AspectList();
        for (Aspect aspect : source.getAspects()) {
            int amount = source.getAmount(aspect);
            if (aspect.isPrimal()) {
                result.add(aspect, amount);
                continue;
            }
            Aspect[] components = aspect.getComponents();
            AspectList parts = new AspectList();
            parts.add(components[0], amount);
            parts.add(components[1], amount);
            AspectList reduced = reduceToPrimals(parts);
            for (Aspect primal : reduced.getAspects()) {
                result.add(primal, reduced.getAmount(primal));
            }
        }
        return result;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        aspectsMax = input.read("aspectsMax", AspectList.CODEC).orElseGet(AspectList::new);
        aspects = input.read("aspects", AspectList.CODEC).orElseGet(AspectList::new);
        fractionalAspects = input.read("aspectsFractional", AspectList.CODEC).orElseGet(AspectList::new);
        drainColor = input.getIntOr("draincolor", 16777215);
        targetColor = drainColor;
        color = drainColor;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.store("aspectsMax", AspectList.CODEC, aspectsMax);
        output.store("aspects", AspectList.CODEC, aspects);
        output.store("aspectsFractional", AspectList.CODEC, fractionalAspects);
        output.putInt("draincolor", drainColor);
    }
}
