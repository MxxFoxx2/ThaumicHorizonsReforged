package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.vis.VisNet;
import thaumcraft.vis.VisNode;
import thaumcraft.wand.WandInteractable;
import thaumcraft.wand.WandItem;
import thaumcraft.wand.WandKeys;

public class VisDynamoBlockEntity extends VisNode implements WandInteractable {
    private AspectList primalsActuallyProvided = new AspectList();
    private AspectList primalsProvided = new AspectList();
    public boolean provideAer = true;
    public boolean provideAqua = true;
    public boolean provideIgnis = true;
    public boolean provideOrdo = true;
    public boolean providePerditio = true;
    public boolean provideTerra = true;
    public int ticksProvided;
    public float rise;
    public float rotation;
    public float rotation2;
    public int drainColor = 16777215;
    public int targetColor = 16777215;
    public int color = 16777215;

    public VisDynamoBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.VIS_DYNAMO.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VisDynamoBlockEntity entity) {
        entity.tick(level);
    }

    private void tick(Level tickLevel) {
        tickNetwork(tickLevel);
        if (ticksProvided > 0) {
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
            ticksProvided--;
            if (ticksProvided == 0) {
                primalsProvided = new AspectList();
                setChanged();
            }
            primalsActuallyProvided = primalsProvided.copy();
        } else if (ticksProvided == 0) {
            ticksProvided--;
            if (!tickLevel.isClientSide()) {
                VisNet.removeSource(tickLevel, this);
            }
            removeThisNode();
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
    }

    public void killMe() {
        if (level != null && !level.isClientSide()) {
            VisNet.removeSource(level, this);
        }
        removeThisNode();
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
    public InteractionResult onWandUse(Level level, BlockPos pos, Player player, ItemStack wand) {
        WandKeys.TARGET.set(wand, worldPosition);
        if (level != null && !level.isClientSide()
            && (provideAer || provideAqua || provideIgnis || provideOrdo || providePerditio || provideTerra)) {
            VisNet.addSource(level, this);
        }
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
            boolean success = false;
            AspectList aspectsToProvide = new AspectList();
            primalsProvided = new AspectList();
            if (provideAer) {
                aspectsToProvide.add(Aspect.AIR, 25);
            }
            if (provideAqua) {
                aspectsToProvide.add(Aspect.WATER, 25);
            }
            if (provideIgnis) {
                aspectsToProvide.add(Aspect.FIRE, 25);
            }
            if (provideOrdo) {
                aspectsToProvide.add(Aspect.ORDER, 25);
            }
            if (providePerditio) {
                aspectsToProvide.add(Aspect.ENTROPY, 25);
            }
            if (provideTerra) {
                aspectsToProvide.add(Aspect.EARTH, 25);
            }
            for (Aspect asp : aspectsToProvide.getAspects()) {
                if (wand.getItem() instanceof WandItem wandItem && wandItem.consumeVis(wand, player, asp, 40, false)) {
                    primalsProvided.add(asp, 5);
                    success = true;
                }
            }
            if (success) {
                ticksProvided = 10;
                int r = 0;
                int g = 0;
                int b = 0;
                int num = 0;
                for (Aspect asp : primalsProvided.getAspects()) {
                    int col = asp.getColor();
                    r += col >> 16 & 0xFF;
                    g += col >> 8 & 0xFF;
                    b += col & 0xFF;
                    num++;
                }
                r /= num;
                g /= num;
                b /= num;
                drainColor = r << 16 | g << 8 | b;
                targetColor = drainColor;
                if (!useLevel.isClientSide()) {
                    sync();
                }
            }
        }
        if (useLevel.isClientSide()) {
            int r2 = targetColor >> 16 & 0xFF;
            int g2 = targetColor >> 8 & 0xFF;
            int b2 = targetColor & 0xFF;
            int r3 = (color >> 16 & 0xFF) * 4;
            int g3 = (color >> 8 & 0xFF) * 4;
            int b3 = (color & 0xFF) * 4;
            color = (r2 + r3) / 5 << 16 | (g2 + g3) / 5 << 8 | (b2 + b3) / 5;
        }
    }
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input.tag(), input.lookup());
        ticksProvided = input.getIntOr("ticks", 0);
        provideAer = input.getBooleanOr("aer", true);
        provideAqua = input.getBooleanOr("aqua", true);
        provideIgnis = input.getBooleanOr("ignis", true);
        provideOrdo = input.getBooleanOr("ordo", true);
        providePerditio = input.getBooleanOr("perditio", true);
        provideTerra = input.getBooleanOr("terra", true);
        primalsProvided = input.read("primalsProvided", AspectList.CODEC).orElseGet(AspectList::new);
        drainColor = input.getIntOr("draincolor", 16777215);
        targetColor = drainColor;
        color = drainColor;
    }
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output.tag(), output.lookup());
        output.putInt("ticks", ticksProvided);
        output.putBoolean("aer", provideAer);
        output.putBoolean("aqua", provideAqua);
        output.putBoolean("ignis", provideIgnis);
        output.putBoolean("ordo", provideOrdo);
        output.putBoolean("perditio", providePerditio);
        output.putBoolean("terra", provideTerra);
        output.store("primalsProvided", AspectList.CODEC, primalsProvided);
        output.putInt("draincolor", drainColor);
    }
}
