package dev.visfox.thaumichorizons.item.lenses;

import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jspecify.annotations.Nullable;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.lib.THSounds;

public final class LensManager {
    public static long nightVisionOffTime;

    private LensManager() {
    }

    public static void changeLens(ItemStack goggles, Player player, String lens) {
        TreeMap<String, Integer> lenses = new TreeMap<>();
        HashMap<Integer, Integer> pouchSlots = new HashMap<>();
        int pouchcount = 0;
        ItemStack item = null;
        if (TcReforgedPort.baubles.isAvailable()) {
            for (int slot = 0; slot < TcReforgedPort.baubles.slotCount(); slot++) {
                ItemStack bauble = TcReforgedPort.baubles.get(player, slot);
                if (bauble.getItem() instanceof ItemLensCase) {
                    pouchcount++;
                    pouchSlots.put(pouchcount, slot - 4);
                    ItemStack[] contents = ItemLensCase.getContents(bauble);
                    for (int index = 0; index < contents.length; index++) {
                        if (contents[index].getItem() instanceof ILens lensItem) {
                            lenses.put(lensItem.lensName(), index + pouchcount * 1000);
                        }
                    }
                }
            }
        }
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            item = player.getInventory().getItem(slot);
            if (item.getItem() instanceof ILens lensItem) {
                lenses.put(lensItem.lensName(), slot);
            }
            if (item.getItem() instanceof ItemLensCase) {
                pouchcount++;
                pouchSlots.put(pouchcount, slot);
                ItemStack[] inv = ItemLensCase.getContents(item);
                for (int q = 0; q < inv.length; q++) {
                    if (inv[q].getItem() instanceof ILens lensItem) {
                        lenses.put(lensItem.lensName(), q + pouchcount * 1000);
                    }
                }
            }
        }
        ItemStack oldLens = null;
        if (!lens.equals("REMOVE") && !lenses.isEmpty()) {
            String next = lens;
            if (!lenses.containsKey(lens)) {
                next = lenses.higherKey(lens);
            }
            if (next == null) {
                next = lenses.firstKey();
            }
            int reference = lenses.get(next);
            if (reference < 1000) {
                item = player.getInventory().getItem(reference).copy();
                player.getInventory().setItem(reference, ItemStack.EMPTY);
            } else {
                int pouchIndex = reference / 1000;
                int lensSlot = reference - pouchIndex * 1000;
                if (pouchSlots.containsKey(pouchIndex)) {
                    int pouchSlot = pouchSlots.get(pouchIndex);
                    ItemStack pouch = pouchSlot >= 0
                        ? player.getInventory().getItem(pouchSlot)
                        : TcReforgedPort.baubles.get(player, pouchSlot + 4);
                    if (pouch.getItem() instanceof ItemLensCase) {
                        ItemStack[] inv = ItemLensCase.getContents(pouch);
                        if (inv[lensSlot].getItem() instanceof ILens) {
                            item = inv[lensSlot].copy();
                            inv[lensSlot] = ItemStack.EMPTY;
                            ItemLensCase.setContents(pouch, inv);
                        }
                    }
                }
            }
            if (item == null || item.isEmpty()) {
                return;
            }
            THSounds.play(player.level(), player.position(), "cameraticks", SoundSource.PLAYERS, 0.3F, 1.0F);
            String currentLens = getLensId(goggles);
            oldLens = getLensItem(currentLens);
            if (!currentLens.isEmpty() && (addLensToPouch(player, oldLens, pouchSlots)
                || player.getInventory().add(oldLens))) {
                setLensItem(goggles, item);
            } else if (currentLens.isEmpty()) {
                setLensItem(goggles, item);
            } else if (!addLensToPouch(player, item, pouchSlots)) {
                player.getInventory().add(item);
            }
        } else {
            String currentLens = getLensId(goggles);
            oldLens = getLensItem(currentLens);
            if (!currentLens.isEmpty() && (addLensToPouch(player, oldLens, pouchSlots)
                || player.getInventory().add(oldLens))) {
                setLensItem(goggles, null);
                THSounds.play(player.level(), player.position(), "cameraticks", SoundSource.PLAYERS, 0.3F, 0.9F);
            }
        }
        if (oldLens != null && !oldLens.isEmpty() && oldLens.getItem() instanceof ILens oldLensItem) {
            oldLensItem.handleRemoval(player);
        }
    }

    private static boolean addLensToPouch(Player player, ItemStack lens, HashMap<Integer, Integer> pouchSlots) {
        if (lens == null || lens.isEmpty()) {
            return false;
        }
        for (int pouchSlot : pouchSlots.values()) {
            ItemStack pouch = pouchSlot >= 0
                ? player.getInventory().getItem(pouchSlot)
                : TcReforgedPort.baubles.get(player, pouchSlot + 4);
            if (!(pouch.getItem() instanceof ItemLensCase)) {
                continue;
            }
            ItemStack[] inv = ItemLensCase.getContents(pouch);
            for (int q = 0; q < inv.length; q++) {
                if (inv[q].isEmpty()) {
                    inv[q] = lens.copy();
                    ItemLensCase.setContents(pouch, inv);
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack getLensItem(String lens) {
        Item item = getLens(lens);
        return item == null ? ItemStack.EMPTY : new ItemStack(item);
    }

    public static @Nullable Item getLens(String lens) {
        String id = switch (lens) {
            case "LensFire" -> "lensfire";
            case "LensWater" -> "lenswater";
            case "LensEarth" -> "lensearth";
            case "LensAir" -> "lensair";
            case "LensOrderEntropy" -> "lensorderentropy";
            default -> null;
        };
        if (id == null) {
            return null;
        }
        return BuiltInRegistries.ITEM.getValue(ThaumicHorizons.id(id));
    }

    public static String getLensId(ItemStack goggles) {
        return goggles.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getStringOr("Lens", "");
    }

    public static void setLensItem(ItemStack goggles, @Nullable ItemStack lens) {
        CustomData data = goggles.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag tag = data.copyTag();
        int lensIndex = tag.getIntOr("LensIndex", -1);
        net.minecraft.world.item.component.ItemLore loreComponent = goggles.get(DataComponents.LORE);
        List<Component> lines = loreComponent == null ? List.of() : loreComponent.lines();
        java.util.ArrayList<Component> updated = new java.util.ArrayList<>(lines);
        if (lens == null || lens.isEmpty()) {
            tag.remove("Lens");
            if (lensIndex >= 0 && lensIndex < updated.size()) {
                updated.remove(lensIndex);
            }
            tag.putInt("LensIndex", -1);
        } else {
            tag.remove("Lens");
            tag.putString("Lens", ((ILens) lens.getItem()).lensName());
            if (lensIndex >= 0 && lensIndex < updated.size()) {
                updated.remove(lensIndex);
            }
            tag.putInt("LensIndex", updated.size());
            updated.add(lens.getHoverName());
        }
        goggles.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        goggles.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(updated, List.of()));
    }

    public static boolean isEffectGrantedByLens(@Nullable MobEffectInstance effect) {
        return effect != null && effect.getDuration() <= 255 && !effect.isInfiniteDuration();
    }

    public static void markNightVisionOff() {
        LensManager.nightVisionOffTime = System.currentTimeMillis() + 100L;
    }
}
