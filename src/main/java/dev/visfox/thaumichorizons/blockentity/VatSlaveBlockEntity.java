package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;

public class VatSlaveBlockEntity extends BlockEntity {
    boolean bossFound;
    int bossX;
    int bossY;
    int bossZ;

    public VatSlaveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public VatSlaveBlockEntity(BlockPos pos, BlockState state) {
        this(ThBlockEntities.VAT_SLAVE.get(), pos, state);
    }

    public boolean activate(Player player) {
        VatBlockEntity boss = getBoss(-1);
        if (boss == null) {
            return false;
        }
        if (getVariant() != 0) {
            return boss.activate(player, false);
        }
        return boss.activate(player, true);
    }

    public @Nullable VatBlockEntity getBoss(int variantOverride) {
        if (level == null) {
            return null;
        }
        if (!bossFound) {
            int variant = variantOverride;
            if (variant == -1) {
                variant = getVariant();
            }
            if (variant == 0) {
                bossX = worldPosition.getX();
                bossZ = worldPosition.getZ();
                if (level.getBlockEntity(worldPosition.above()) instanceof VatBlockEntity) {
                    bossY = worldPosition.getY() + 1;
                    bossFound = true;
                } else if (level.getBlockEntity(worldPosition.above(2)) instanceof VatBlockEntity) {
                    bossY = worldPosition.getY() + 2;
                    bossFound = true;
                }
            } else if (variant == 10) {
                for (int x = -1; x < 2; x++) {
                    for (int z = -1; z < 2; z++) {
                        BlockPos pos = worldPosition.offset(x, 0, z);
                        if (level.getBlockState(pos).getBlock() == ThBlocks.VAT_INTERIOR.get()
                            && level.getBlockEntity(pos) instanceof VatSlaveBlockEntity slave) {
                            VatBlockEntity boss = slave.getBoss(-1);
                            if (boss != null) {
                                bossX = boss.getBlockPos().getX();
                                bossY = boss.getBlockPos().getY();
                                bossZ = boss.getBlockPos().getZ();
                                bossFound = true;
                            }
                            return boss;
                        }
                    }
                }
            } else if (variant == 4) {
                if (level.getBlockState(worldPosition.above()).getBlock() == ThBlocks.VAT.get()
                    && level.getBlockEntity(worldPosition.above()) instanceof VatSlaveBlockEntity slave) {
                    VatBlockEntity boss = slave.getBoss(-1);
                    if (boss != null) {
                        rememberBoss(boss);
                    }
                    return boss;
                }
            } else if (variant == 5) {
                if (level.getBlockState(worldPosition.above()).getBlock() == ThBlocks.VAT.get()
                    && level.getBlockEntity(worldPosition.above()) instanceof VatSlaveBlockEntity slave) {
                    VatBlockEntity boss = slave.getBoss(-1);
                    if (boss != null) {
                        rememberBoss(boss);
                    }
                    return boss;
                }
                if (level.getBlockState(worldPosition.below()).getBlock() == ThBlocks.VAT.get()
                    && level.getBlockEntity(worldPosition.below()) instanceof VatSlaveBlockEntity slave) {
                    VatBlockEntity boss = slave.getBoss(-1);
                    if (boss != null) {
                        rememberBoss(boss);
                    }
                    return boss;
                }
            } else if (variant == 6) {
                bossX = worldPosition.getX();
                bossZ = worldPosition.getZ();
                if (level.getBlockEntity(worldPosition.above(3)) instanceof VatBlockEntity) {
                    bossY = worldPosition.getY() + 3;
                    bossFound = true;
                }
            }
        }
        if (level.getBlockEntity(new BlockPos(bossX, bossY, bossZ)) instanceof VatBlockEntity vat) {
            return vat;
        }
        return null;
    }

    private void rememberBoss(VatBlockEntity boss) {
        bossX = boss.getBlockPos().getX();
        bossY = boss.getBlockPos().getY();
        bossZ = boss.getBlockPos().getZ();
        bossFound = true;
    }

    int getVariant() {
        BlockState state = getBlockState();
        if (state.getBlock() instanceof dev.visfox.thaumichorizons.block.VatSolidBlock) {
            return state.getValue(dev.visfox.thaumichorizons.block.VatSolidBlock.VARIANT);
        }
        if (state.getBlock() instanceof dev.visfox.thaumichorizons.block.VatBlock) {
            return state.getValue(dev.visfox.thaumichorizons.block.VatBlock.VARIANT);
        }
        return 0;
    }

    public void killMyBoss(int variantOverride) {
        VatBlockEntity boss = getBoss(variantOverride);
        if (boss != null) {
            boss.killMe();
        }
    }

    public @Nullable AspectList getAspects() {
        VatBlockEntity boss = getBoss(-1);
        if (boss != null) {
            return boss.getAspects();
        }
        return null;
    }
}
