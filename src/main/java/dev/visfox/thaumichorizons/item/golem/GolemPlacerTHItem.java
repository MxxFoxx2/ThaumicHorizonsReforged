package dev.visfox.thaumichorizons.item.golem;

import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import thaumcraft.entity.golem.GolemBase;
import thaumcraft.entity.golem.GolemKeys;
import thaumcraft.entity.golem.GolemType;
import thaumcraft.item.golem.GolemPlacerItem;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class GolemPlacerTHItem extends GolemPlacerItem {
    public GolemPlacerTHItem(Properties properties, GolemType fallback) {
        super(properties, fallback);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() == null || context.getPlayer().isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!context.getLevel().isClientSide()) {
            Level level = context.getLevel();
            Player player = context.getPlayer();
            ItemStack stack = context.getItemInHand();
            BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
            CompoundTag data = EntityGolemTH.GolemBlockData.get(stack);
            @Nullable BlockState blocky = EntityGolemTH.GolemBlockData.stateOf(data);
            int md = data.getIntOr("GolemMd", 0);
            boolean berserk = data.getBooleanOr("Berserk", false);
            boolean kaboom = data.getBooleanOr("Kaboom", false);
            EntityGolemTH golem = new EntityGolemTH(EntityGolemTH.thType(), level);
            golem.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
            golem.setYRot(level.getRandom().nextFloat() * 360.0F);
            golem.loadGolem(golem.getX(), golem.getY(), golem.getZ(), blocky, md, 600,
                GolemKeys.snapshot(stack).advanced(), berserk, kaboom);
            golem.configure(golem.getGolemType(), GolemKeys.snapshot(stack));
            GolemKeys.FLUID.getOrDefault(stack, java.util.Optional.empty()).ifPresent(fluid -> golem.setFluidCarried(fluid.create()));
            golem.setEssentia(thaumcraft.aspect.Aspect.getAspect(GolemKeys.ESSENTIA.getOrDefault(stack, "")),
                GolemKeys.ESSENTIA_AMOUNT.getOrDefault(stack, 0));
            golem.setHealth(golem.getMaxHealth());
            golem.setHome(pos, context.getClickedFace());
            golem.setOwnerUUID(player.getUUID());
            golem.setCustomName(stack.get(DataComponents.CUSTOM_NAME));
            if (!level.noCollision(golem) || !level.addFreshEntity(golem)) {
                return InteractionResult.FAIL;
            }
            stack.consume(1, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
            Consumer<Component> builder, TooltipFlag flag) {
        CompoundTag data = EntityGolemTH.GolemBlockData.get(stack);
        if (data.contains("GolemBlockId") && EntityGolemTH.GolemBlockData.blockOf(data) != null) {
            Block blocky = EntityGolemTH.GolemBlockData.blockOf(data);
            if (blocky == null) {
                builder.accept(Component.translatable("item.thaumichorizons.golemPlacer.voidling")
                    .withStyle(ChatFormatting.GRAY));
            } else {
                builder.accept(new ItemStack(blocky).getHoverName().copy().withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
