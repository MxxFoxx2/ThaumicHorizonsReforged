package dev.visfox.thaumichorizons.registry;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.blockentity.BloodInfuserBlockEntity;
import dev.visfox.thaumichorizons.blockentity.InspiratronBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulExtractorBlockEntity;
import dev.visfox.thaumichorizons.blockentity.SoulforgeBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VisDynamoBlockEntity;
import dev.visfox.thaumichorizons.blockentity.VatBlockEntity;
import dev.visfox.thaumichorizons.container.BloodInfuserMenu;
import dev.visfox.thaumichorizons.menu.ContainerCase;
import dev.visfox.thaumichorizons.container.FingersMenu;
import dev.visfox.thaumichorizons.container.InjectorMenu;
import dev.visfox.thaumichorizons.container.InspiratronMenu;
import dev.visfox.thaumichorizons.container.SoulExtractorMenu;
import dev.visfox.thaumichorizons.container.SoulforgeMenu;
import dev.visfox.thaumichorizons.container.VisDynamoMenu;
import dev.visfox.thaumichorizons.container.VatMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ThMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, ThaumicHorizons.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<ContainerCase>> CASE = MENUS.register("case",
        () -> IMenuTypeExtension.create((id, inventory, buf) -> new ContainerCase(id, inventory, buf.readVarInt())));
    public static final DeferredHolder<MenuType<?>, MenuType<VatMenu>> VAT = MENUS.register("vat",
        () -> withPos((id, inventory, pos) -> new VatMenu(id, inventory,
            blockEntity(inventory, pos, VatBlockEntity.class))));
    public static final DeferredHolder<MenuType<?>, MenuType<SoulExtractorMenu>> SOUL_SIEVE =
        MENUS.register("soulsieve", () -> withPos((id, inventory, pos) -> new SoulExtractorMenu(id, inventory,
            blockEntity(inventory, pos, SoulExtractorBlockEntity.class))));
    public static final DeferredHolder<MenuType<?>, MenuType<InspiratronMenu>> INSPIRATRON =
        MENUS.register("inspiratron", () -> withPos((id, inventory, pos) -> new InspiratronMenu(id, inventory,
            blockEntity(inventory, pos, InspiratronBlockEntity.class))));
    public static final DeferredHolder<MenuType<?>, MenuType<SoulforgeMenu>> SOULFORGE =
        MENUS.register("soulforge", () -> withPos((id, inventory, pos) -> new SoulforgeMenu(id, inventory,
            blockEntity(inventory, pos, SoulforgeBlockEntity.class))));
    public static final DeferredHolder<MenuType<?>, MenuType<BloodInfuserMenu>> BLOOD_INFUSER =
        MENUS.register("bloodinfuser", () -> withPos((id, inventory, pos) -> new BloodInfuserMenu(id, inventory,
            blockEntity(inventory, pos, BloodInfuserBlockEntity.class))));
    public static final DeferredHolder<MenuType<?>, MenuType<InjectorMenu>> INJECTOR = MENUS.register("injector",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new InjectorMenu(id, inventory)));
    public static final DeferredHolder<MenuType<?>, MenuType<FingersMenu>> FINGERS = MENUS.register("fingers",
        () -> IMenuTypeExtension.create((id, inventory, buffer) -> new FingersMenu(id, inventory)));
    public static final DeferredHolder<MenuType<?>, MenuType<VisDynamoMenu>> VIS_DYNAMO =
        MENUS.register("visdynamo", () -> withPos((id, inventory, pos) -> new VisDynamoMenu(id, inventory,
            blockEntity(inventory, pos, VisDynamoBlockEntity.class))));

    private ThMenus() {
    }

    private static <T extends BlockEntity> T blockEntity(Inventory inventory, BlockPos pos, Class<T> type) {
        BlockEntity be = inventory.player.level().getBlockEntity(pos);
        return type.cast(be);
    }

    @FunctionalInterface
    private interface PosFactory<M extends AbstractContainerMenu> {
        M create(int containerId, Inventory inventory, BlockPos pos);
    }

    private static <M extends AbstractContainerMenu> MenuType<M> withPos(PosFactory<M> factory) {
        return IMenuTypeExtension.create((containerId, inventory, buffer) ->
            factory.create(containerId, inventory, buffer.readBlockPos()));
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
