package dev.visfox.thaumichorizons.client;

import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.item.lenses.ILens;
import dev.visfox.thaumichorizons.item.lenses.LensManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = ThaumicHorizons.MODID, value = Dist.CLIENT)
public final class LensEffectsHandler {
    private LensEffectsHandler() {
    }

    @SubscribeEvent
    static void render(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        boolean wearing = ArcaneLensHud.isWearingGoggles(player);
        MobEffectInstance effect = player.getEffect(MobEffects.NIGHT_VISION);
        if (LensManager.nightVisionOffTime > 0L && !wearing && LensManager.isEffectGrantedByLens(effect)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
            LensManager.nightVisionOffTime = 0L;
        }
        if (!wearing) {
            return;
        }

        ItemStack goggles = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack lensStack = LensManager.getLensItem(LensManager.getLensId(goggles));
        if (lensStack.getItem() instanceof ILens lens) {
            lens.handleRender(player, event.getPartialTick().getGameTimeDeltaPartialTick(false));
        }
    }
}
