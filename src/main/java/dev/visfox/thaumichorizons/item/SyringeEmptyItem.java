package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import java.util.ArrayList;
import java.util.List;
import dev.visfox.thaumichorizons.lib.EntityNbt;
import dev.visfox.thaumichorizons.lib.EntityRay;
import dev.visfox.thaumichorizons.registry.ThItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import javax.annotation.Nullable;

public class SyringeEmptyItem extends Item {
    public SyringeEmptyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player p, InteractionHand hand) {
        ItemStack stack = p.getItemInHand(hand);
        Entity ent = EntityRay.getPointedEntity(world, p, 1.5);
        if (ent instanceof LivingEntity critter && !(ent instanceof Player)) {
            if (isValidCritter(critter)) {
                CompoundTag critterTag = EntityNbt.save(critter);
                critterTag.remove("Items");
                critterTag.remove("ArmorItem");
                critterTag.remove("SaddleItem");
                critterTag.remove("ChestedHorse");
                List<Integer> infusions = new ArrayList<>();
                for (int infusion : CreatureInfusions.get(critter).infusions()) {
                    infusions.add(infusion);
                }
                SyringeKeys.SampleData data = new SyringeKeys.SampleData(critterTag,
                    critter.getName().getString(), infusions);
                ItemStack bloodSample = new ItemStack(ThItems.SYRINGE_BLOOD_SAMPLE.get());
                SyringeKeys.SAMPLE.set(bloodSample, data);
                if (p.getInventory().add(bloodSample)) {
                    stack.shrink(1);
                    world.playSound(null, p.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        } else {
            ItemStack result = new ItemStack(ThItems.SYRINGE_BLOOD.get());
            if (p.getInventory().add(result)) {
                stack.shrink(1);
                world.playSound(null, p.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }
        return InteractionResultHolder.success(stack);
    }

    public static boolean isValidCritter(LivingEntity critter) {
        return !isUndead(critter) && !(critter instanceof AbstractVillager)
            && (critter.getType().getCategory() == MobCategory.CREATURE
                || critter.getType().getCategory() == MobCategory.AMBIENT
                || critter.getType().getCategory() == MobCategory.WATER_CREATURE);
    }

    public static boolean isUndead(LivingEntity entity) {
        return entity.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD);
    }
}
