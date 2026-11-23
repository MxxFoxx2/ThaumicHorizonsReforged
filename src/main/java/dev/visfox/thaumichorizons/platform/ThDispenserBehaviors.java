package dev.visfox.thaumichorizons.platform;

import dev.visfox.thaumichorizons.entity.AlchemitePrimedEntity;
import dev.visfox.thaumichorizons.entity.BlastPhialProjectile;
import dev.visfox.thaumichorizons.entity.EggIncubatedEntity;
import dev.visfox.thaumichorizons.entity.SyringeProjectile;
import dev.visfox.thaumichorizons.item.SyringeInjectionItem;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.material.FluidState;

public final class ThDispenserBehaviors {
    private ThDispenserBehaviors() {
    }

    public static void register() {
        DispenserBlock.registerBehavior(ThBlocks.ALCHEMITE.get().asItem(), new AlchemiteBehavior());
        DispenserBlock.registerBehavior(ThItems.SYRINGE_INJECTION.get(), new SyringeBehavior());
        DispenserBlock.registerBehavior(ThItems.EGG_INCUBATED.get(), new EggBehavior());
        DispenserBlock.registerBehavior(ThItems.BOAT_GREATWOOD.get(), new BoatBehavior(ThEntities.BOAT_GREATWOOD.get(), false));
        DispenserBlock.registerBehavior(ThItems.BOAT_THAUMIUM.get(), new BoatBehavior(ThEntities.BOAT_THAUMIUM.get(), true));
    }

    private static BlockPos target(BlockSource source, Direction facing) {
        return source.pos().relative(facing);
    }

    private static final class AlchemiteBehavior extends DefaultDispenseItemBehavior {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            BlockPos target = target(source, facing);
            Level level = source.level();
            AlchemitePrimedEntity primed = new AlchemitePrimedEntity(level,
                target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F, null);
            level.addFreshEntity(primed);
            level.playSound(null, target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F,
                SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
            stack.shrink(1);
            return stack;
        }
    }

    private static final class SyringeBehavior extends DefaultDispenseItemBehavior {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            BlockPos target = target(source, facing);
            Level level = source.level();
            ItemStack projectileStack = stack.copyWithCount(1);
            if (stack.getItem() instanceof SyringeInjectionItem syringe && syringe.isPhial(stack)) {
                BlastPhialProjectile projectile = new BlastPhialProjectile(level,
                    target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F, projectileStack, facing);
                level.addFreshEntity(projectile);
            } else {
                SyringeProjectile projectile = new SyringeProjectile(level,
                    target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F, projectileStack, facing);
                level.addFreshEntity(projectile);
            }
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.level().levelEvent(1002, source.pos(), 0);
        }
    }

    private static final class EggBehavior extends DefaultDispenseItemBehavior {
        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            BlockPos target = target(source, facing);
            Level level = source.level();
            EggIncubatedEntity egg = new EggIncubatedEntity(level,
                target.getX() + 0.5F, target.getY() + 0.5F, target.getZ() + 0.5F, stack.copyWithCount(1));
            egg.shoot(facing.getStepX(), facing.getStepY() + 0.1F, facing.getStepZ(), 1.1F, 6.0F);
            level.addFreshEntity(egg);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playSound(BlockSource source) {
            source.level().levelEvent(1002, source.pos(), 0);
        }
    }

    private static final class BoatBehavior extends DefaultDispenseItemBehavior {
        private final EntityType<? extends Boat> type;
        private final boolean lava;

        private BoatBehavior(EntityType<? extends Boat> type, boolean lava) {
            this.type = type;
            this.lava = lava;
        }

        @Override
        protected ItemStack execute(BlockSource source, ItemStack stack) {
            Direction facing = source.state().getValue(DispenserBlock.FACING);
            BlockPos front = target(source, facing);
            Level level = source.level();
            FluidState fluid = level.getFluidState(front);
            boolean onFluid = fluid.is(FluidTags.WATER) || (lava && fluid.is(FluidTags.LAVA));
            if (!onFluid && !level.getBlockState(front).isAir()) {
                return stack;
            }

            Boat boat = type.create(level, EntitySpawnReason.DISPENSER);
            if (boat == null) {
                return stack;
            }
            boat.setPos(source.pos().getX() + 0.5 + facing.getStepX() * 1.25,
                source.pos().getY() + facing.getStepY() * 1.125 + (onFluid ? 1.0 : 0.0),
                source.pos().getZ() + 0.5 + facing.getStepZ() * 1.25);
            boat.setYRot(switch (facing) {
                case NORTH -> 90.0F;
                case WEST -> 180.0F;
                case EAST -> 0.0F;
                default -> -90.0F;
            });
            level.addFreshEntity(boat);
            stack.shrink(1);
            return stack;
        }
    }
}
