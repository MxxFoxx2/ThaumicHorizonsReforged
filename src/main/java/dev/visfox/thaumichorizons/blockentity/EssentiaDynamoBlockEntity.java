package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.essentia.EssentiaTransport;
import thaumcraft.vis.VisNet;
import thaumcraft.vis.VisNode;

public class EssentiaDynamoBlockEntity extends VisNode implements EssentiaTransport {
    private AspectList primalsActuallyProvided = new AspectList();
    private AspectList primalsProvided = new AspectList();
    public @Nullable Aspect essentia;
    public int ticksProvided;
    public float rise;
    public float rotation;
    public float rotation2;

    public EssentiaDynamoBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.ESSENTIA_DYNAMO.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, EssentiaDynamoBlockEntity entity) {
        entity.tick(level);
    }

    private void tick(Level tickLevel) {
        tickNetwork(tickLevel);
        if (ticksProvided >= 0) {
            if (rise < 0.3F) {
                rise += 0.02F;
            } else {
                rotation2 += 2.0F;
                if (rotation2 >= 360.0F) {
                    rotation2 -= 360.0F;
                }
            }
            rotation += 2.0F;
            if (rotation >= 360.0F) {
                rotation -= 360.0F;
            }
        } else if (rise > 0.0F || rotation2 != 0.0F) {
            if (rotation2 > 0.0F) {
                rotation2 -= 8.0F;
                if (rotation2 < 0.0F) {
                    rotation2 = 0.0F;
                }
            } else if (rise > 0.0F) {
                rise -= 0.02F;
            }
        }
        if (ticksProvided > 0 && essentia != null) {
            primalsProvided = reduceToPrimals(new AspectList().add(essentia, 1));
            if (primalsProvided.isEmpty()) {
                return;
            }
            int numEach = 12 / primalsProvided.size();
            for (Aspect asp : primalsProvided.getAspects()) {
                int num = primalsProvided.getAmount(asp);
                if (num > numEach) {
                    primalsProvided.reduce(asp, num - numEach);
                } else if (num < numEach) {
                    primalsProvided.add(asp, numEach - num);
                }
            }
            primalsActuallyProvided = primalsProvided.copy();
            ticksProvided--;
            if (!tickLevel.isClientSide()) {
                sync();
            }
        } else if (ticksProvided == 0) {
            if (!tickLevel.isClientSide() && !drawEssentia()) {
                ticksProvided--;
                killMe();
                primalsProvided = new AspectList();
                primalsActuallyProvided = new AspectList();
                sync();
            }
        } else if (!tickLevel.isClientSide() && ticksProvided < 0) {
            drawEssentia();
        }
    }

    public void killMe() {
        if (level != null && !level.isClientSide()) {
            VisNet.removeSource(level, this);
        }
        removeThisNode();
    }

    boolean drawEssentia() {
        if (level == null) {
            return false;
        }
        BlockEntity te = level.getBlockEntity(worldPosition.below());
        if (te instanceof EssentiaTransport ic && ic.isConnectable(Direction.UP)) {
            if (!ic.canOutputTo(Direction.UP)) {
                return false;
            }
            Aspect ta = null;
            if (ic.getEssentiaAmount(Direction.UP) > 0
                && ic.getSuctionAmount(Direction.UP) < getSuctionAmount(Direction.DOWN)
                && getSuctionAmount(Direction.DOWN) >= ic.getMinimumSuction()) {
                ta = ic.getEssentiaType(Direction.UP);
            }
            if (ta != null && ic.takeEssentia(ta, 1, Direction.UP) == 1) {
                addEssentia(ta, 1, Direction.DOWN);
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRange() {
        return 0;
    }

    @Override
    public boolean isSource() {
        return ticksProvided > 0;
    }

    @Override
    public int consumeVis(Aspect aspect, int amount) {
        int drain = Math.min(primalsActuallyProvided.getAmount(aspect), amount);
        if (drain > 0) {
            primalsActuallyProvided.reduce(aspect, drain);
        }
        return drain;
    }

    @Override
    public boolean isConnectable(@Nullable Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public boolean canInputFrom(@Nullable Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public boolean canOutputTo(@Nullable Direction face) {
        return false;
    }

    @Override
    public void setSuction(@Nullable Aspect aspect, int amount) {
    }

    @Override
    public @Nullable Aspect getSuctionType(@Nullable Direction face) {
        return null;
    }

    @Override
    public int getSuctionAmount(@Nullable Direction face) {
        if (ticksProvided <= 20) {
            return 128;
        }
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, @Nullable Direction face) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, @Nullable Direction face) {
        ticksProvided += 21;
        essentia = aspect;
        if (level != null && !level.isClientSide()) {
            VisNet.addSource(level, this);
            sync();
        }
        setChanged();
        return 1;
    }

    @Override
    public @Nullable Aspect getEssentiaType(@Nullable Direction face) {
        return essentia;
    }

    @Override
    public int getEssentiaAmount(@Nullable Direction face) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
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
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input.tag(), input.lookup());
        essentia = input.read("essentia", Aspect.CODEC).orElse(null);
        ticksProvided = input.getIntOr("ticks", 0);
        primalsProvided = input.read("primalsProvided", AspectList.CODEC).orElseGet(AspectList::new);
        primalsActuallyProvided = primalsProvided.copy();
        if (ticksProvided > 0 && essentia == null) {
            ticksProvided = 0;
        }
        if (ticksProvided < 0) {
            killMe();
        }
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output.tag(), output.lookup());
        if (essentia != null) {
            output.store("essentia", Aspect.CODEC, essentia);
        }
        output.putInt("ticks", ticksProvided);
        output.store("primalsProvided", AspectList.CODEC, primalsProvided);
    }
}
