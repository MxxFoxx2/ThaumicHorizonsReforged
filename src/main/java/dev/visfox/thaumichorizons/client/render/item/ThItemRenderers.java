package dev.visfox.thaumichorizons.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.block.SpikeBlock;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * 1.21.1 replacement for the 26.x special-model renderer registry: one BEWLR
 * dispatches the legacy per-item meshes by stack and display context.
 *
 * <p>1.21.1 has no {@code minecraft:special} item-model type, so the legacy
 * {@code thaumichorizons:*} special model ids cannot be expressed in item
 * model JSON. The per-item meshes are therefore rendered from this BEWLR,
 * which receives the {@link ItemDisplayContext} that the 26.x {@code select}
 * model used to branch on.
 */
@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class ThItemRenderers {
    private static ThItemRenderer renderer;

    private ThItemRenderers() {
    }

    @SubscribeEvent
    static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions extensions = new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    Minecraft minecraft = Minecraft.getInstance();
                    renderer = new ThItemRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
                }
                return renderer;
            }
        };
        event.registerItem(extensions,
            ThBlocks.SPIKE.get().asItem(),
            ThBlocks.SPIKE_WOOD.get().asItem(),
            ThBlocks.SPIKE_TOOTH.get().asItem(),
            ThItems.SYRINGE_EMPTY.get(),
            ThItems.SYRINGE_BLOOD.get(),
            ThItems.SYRINGE_BLOOD_SAMPLE.get(),
            ThItems.SYRINGE_INJECTION.get(),
            ThItems.CORPSE_EFFIGY.get(),
            ThItems.INJECTOR.get(),
            ThItems.SOUL_JAR_ITEM.get()
        );
    }

    public static final class ThItemRenderer extends BlockEntityWithoutLevelRenderer {
        public ThItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
            super(dispatcher, entityModelSet);
        }

        @Override
        public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
            MultiBufferSource buffers, int light, int overlay) {
            ThItemView view = ThItemView.of(displayContext);
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof SpikeBlock) {
                SpikeItemSpecialModelRenderer.render(stack, view, poseStack, buffers, light, overlay);
            } else if (stack.is(ThItems.SYRINGE_EMPTY.get())
                || stack.is(ThItems.SYRINGE_BLOOD.get())
                || stack.is(ThItems.SYRINGE_BLOOD_SAMPLE.get())
                || stack.is(ThItems.SYRINGE_INJECTION.get())) {
                SyringeItemSpecialModelRenderer.render(stack, view, poseStack, buffers, light, overlay);
            } else if (stack.is(ThItems.CORPSE_EFFIGY.get())) {
                CorpseEffigyItemSpecialModelRenderer.render(stack, view, poseStack, buffers, light, overlay);
            } else if (stack.is(ThItems.INJECTOR.get())) {
                InjectorItemSpecialModelRenderer.render(stack, view, poseStack, buffers, light, overlay);
            } else if (stack.is(ThItems.SOUL_JAR_ITEM.get())) {
                SoulJarItemSpecialModelRenderer.render(stack, view, poseStack, buffers, light, overlay);
            }
        }
    }
}
