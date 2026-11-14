package dev.visfox.thaumichorizons.item.lenses;

import java.util.Arrays;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemContainerContents;
import dev.visfox.thaumichorizons.menu.ContainerCase;

public class ItemLensCase extends Item {
    private static final Component TITLE = Component.translatable("container.lenscase");

    public ItemLensCase(Properties properties) {
        super(properties);
    }

    public static Item.Properties properties(Item.Properties properties) {
        return properties.stacksTo(1).rarity(Rarity.RARE);
    }

    @Override
    public InteractionResult use(net.minecraft.world.level.Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            int selectedSlot = serverPlayer.getInventory().getSelectedSlot();
            serverPlayer.openMenu(new SimpleMenuProvider(
                (windowId, inventory, p) -> new ContainerCase(windowId, inventory, selectedSlot), TITLE),
                buffer -> buffer.writeVarInt(selectedSlot));
        }
        return InteractionResult.SUCCESS;
    }

    public static ItemStack[] getContents(ItemStack stack) {
        ItemStack[] contents = new ItemStack[18];
        java.util.Arrays.fill(contents, ItemStack.EMPTY);
        ItemContainerContents container = stack.get(DataComponents.CONTAINER);
        if (container != null) {
            net.minecraft.core.NonNullList<ItemStack> list =
                net.minecraft.core.NonNullList.withSize(18, ItemStack.EMPTY);
            container.copyInto(list);
            for (int i = 0; i < 18; i++) {
                contents[i] = list.get(i);
            }
        }
        return contents;
    }

    public static void setContents(ItemStack stack, ItemStack[] contents) {
        ItemStack[] padded = Arrays.copyOf(contents, 18);
        for (int i = 0; i < 18; i++) {
            if (padded[i] == null) {
                padded[i] = ItemStack.EMPTY;
            }
        }
        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(Arrays.asList(padded)));
    }
}
