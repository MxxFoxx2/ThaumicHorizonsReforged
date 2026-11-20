package dev.visfox.thaumichorizons.item;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.infusion.CreatureInfusions;
import dev.visfox.thaumichorizons.infusion.InfusionEntityEvents;
import dev.visfox.thaumichorizons.infusion.ThInfusionRecipes;
import dev.visfox.thaumichorizons.lib.EntityRay;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public final class InfusionCheatItem extends Item {
    private final int infusionId;

    public InfusionCheatItem(Properties properties, int infusionId) {
        super(properties);
        this.infusionId = infusionId;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }
        Entity target = EntityRay.getPointedEntity(level, player, 1.5);
        if (!(target instanceof LivingEntity entity) || !isValidTarget(entity)) {
            return InteractionResult.PASS;
        }
        for (ThInfusionRecipes.CreatureInfusionRecipe recipe : ThInfusionRecipes.getCritterRecipes()) {
            if (recipe.getID() != infusionId || !(recipe.getRecipeOutput() instanceof Map<?, ?> output)
                || CreatureInfusions.hasInfusion(entity, infusionId)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Map<String, Integer> modifiers = (Map<String, Integer>) output;
            applyModifier(entity, Attributes.MOVEMENT_SPEED, modifiers.getOrDefault("generic.movementSpeed", 0), 0.1);
            applyModifier(entity, Attributes.MAX_HEALTH, modifiers.getOrDefault("generic.maxHealth", 0), 1.0);
            applyModifier(entity, Attributes.ATTACK_DAMAGE, modifiers.getOrDefault("generic.attackDamage", 0), 1.0);
            for (Map.Entry<String, Integer> entry : modifiers.entrySet()) {
                if (!entry.getKey().startsWith("generic.")) {
                    CreatureInfusions.addInfusion(entity, entry.getValue());
                    if (entry.getValue() == InfusionEntityEvents.INSTILLED_LOYALTY) {
                        CreatureInfusions.setOwner(entity, serverPlayer.getName().getString());
                    }
                }
            }
            if (entity instanceof Mob mob) {
                mob.setPersistenceRequired();
            }
            InfusionEntityEvents.applyInfusions(entity);
            if (Compat.port().fx().isAvailable()) {
                Compat.port().fx().burst(level,
                    new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), 0xFFFFFF, 16);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    private static boolean isValidTarget(LivingEntity entity) {
        return !entity.getType().builtInRegistryHolder().is(EntityTypeTags.UNDEAD)
            && !(entity instanceof Player) && !(entity instanceof Villager) && !(entity instanceof AbstractGolem)
            && !ThInfusionRecipes.isBanned(entity.getType(), entity);
    }

    private static void applyModifier(LivingEntity entity, Holder<Attribute> attribute, int value, double divisor) {
        if (value <= 0) {
            return;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.addTransientModifier(new AttributeModifier(
                Identifier.fromNamespaceAndPath("thaumichorizons", "cheat_" + attribute.getRegisteredName().replace(":", "_")),
                value / divisor, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }
    }
}
