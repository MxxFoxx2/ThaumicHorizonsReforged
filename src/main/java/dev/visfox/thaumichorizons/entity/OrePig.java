package dev.visfox.thaumichorizons.entity;

import dev.visfox.thaumichorizons.registry.ThEntities;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.entity.ai.EatStoneGoal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import javax.annotation.Nullable;

public class OrePig extends Pig {
    private int nuggetPercent;

    public OrePig(EntityType<OrePig> type, Level level) {
        super(type, level);
        this.goalSelector.addGoal(9, new EatStoneGoal(this));
        this.nuggetPercent = 0;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Pig.createAttributes();
    }

    public void eatStone() {
        this.nuggetPercent += this.random.nextInt(15) + 1;
        if (this.nuggetPercent >= 100) {
            this.nuggetPercent -= 100;
            this.excreteNugget();
        }
    }

    private void excreteNugget() {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        int type = this.random.nextInt(75);
        if (type < 6) {
            this.dropNugget(level, new ItemStack(Items.GOLD_NUGGET));
        } else if (type < 12) {
            Item item = tcItem("silver_nugget");
            if (item != null) {
                this.dropNugget(level, new ItemStack(item));
            } else {
                this.excreteNugget();
            }
        } else if (type < 20) {
            Item item = tcItem("copper_nugget");
            if (item != null) {
                this.dropNugget(level, new ItemStack(item));
            } else {
                this.excreteNugget();
            }
        } else if (type < 30) {
            Item item = tcItem("tin_nugget");
            if (item != null) {
                this.dropNugget(level, new ItemStack(item));
            } else {
                this.excreteNugget();
            }
        } else if (type < 40) {
            Item item = tcItem("lead_nugget");
            if (item != null) {
                this.dropNugget(level, new ItemStack(item));
            } else {
                this.excreteNugget();
            }
        } else if (type < 50) {
            Item item = tcItem("thaumium_nugget");
            if (item != null) {
                this.dropNugget(level, new ItemStack(item));
            } else {
                this.excreteNugget();
            }
        } else {
            this.dropNugget(level, new ItemStack(Items.IRON_NUGGET));
        }
    }

    private static @Nullable Item tcItem(String name) {
        return TcReforgedPort.items.lookup(ResourceLocation.fromNamespaceAndPath("thaumcraft", name));
    }

    private void dropNugget(ServerLevel level, ItemStack stack) {
        this.spawnAtLocation(stack, 0.3F);
    }

    @Override
    protected void dropFromLootTable(net.minecraft.world.damagesource.DamageSource source, boolean hitByPlayer) {
        int count = this.random.nextInt(3) + 1 + this.random.nextInt(2);
        for (int index = 0; index < count; ++index) {
            this.spawnAtLocation(new ItemStack(Blocks.STONE), 1.0F);
        }
        if (this.isSaddled()) {
            this.spawnAtLocation(new ItemStack(Items.SADDLE), 1.0F);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.nuggetPercent = tag.contains("percent") ? tag.getInt("percent") : 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("percent", this.nuggetPercent);
    }

    @Override
    public @Nullable OrePig getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ThEntities.ORE_PIG.get().create(level);
    }
}
