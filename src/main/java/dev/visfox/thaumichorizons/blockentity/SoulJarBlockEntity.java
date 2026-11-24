package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.lib.EntityPlacement;

import dev.visfox.thaumichorizons.item.SoulJarKeys.SoulJarData;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import javax.annotation.Nullable;
import thaumcraft.wand.WandInteractable;

public class SoulJarBlockEntity extends ThBlockEntity implements WandInteractable {
    public @Nullable SoulJarData jarData;
    public @Nullable Entity entity;
    public boolean drop = true;

    public SoulJarBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.SOUL_JAR.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SoulJarBlockEntity entity) {
        if (entity.entity == null && entity.jarData != null && !entity.jarData.soul() && level instanceof ServerLevel serverLevel) {
            entity.entity = EntityNbt.load(entity.jarData.entity(), serverLevel);
        }
    }

    @Override
    public InteractionResult onWandUse(Level level, BlockPos pos, Player player, ItemStack wand) {
        SoulJarData data = jarData;
        if (data == null || data.soul()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            // Rebuild and successfully add the entity before consuming the jar.
            Entity ent = EntityNbt.load(data.entity(), serverLevel);
            if (ent == null) {
                return InteractionResult.PASS;
            }
            if (ent instanceof TamableAnimal tameable && !tameable.isTame()) {
                tameable.setTame(true, true); tameable.setOwnerUUID(player.getUUID());
            }
            EntityPlacement.snapTo(ent, pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, 0.0F, 0.0F);
            if (!serverLevel.addFreshEntity(ent)) {
                return InteractionResult.PASS;
            }

            drop = false;
            if (!level.removeBlock(pos, false)) {
                drop = true;
                ent.discard();
                return InteractionResult.PASS;
            }
        }
        level.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 0.9F + level.getRandom().nextFloat() * 0.2F);
        player.swing(player.getUsedItemHand(), false);
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        jarData = input.read("jarData", SoulJarData.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (jarData != null) {
            output.store("jarData", SoulJarData.CODEC, jarData);
        }
    }

    public @Nullable LivingEntity getContainedLiving() {
        return entity instanceof LivingEntity living ? living : null;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }
}
