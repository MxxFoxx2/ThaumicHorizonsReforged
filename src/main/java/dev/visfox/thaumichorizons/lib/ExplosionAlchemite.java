package dev.visfox.thaumichorizons.lib;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.compat.ItemCompat;
import dev.visfox.thaumichorizons.entity.InvulnerableItemEntity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.node.Node;
import thaumcraft.node.NodeModifier;
import thaumcraft.node.NodeType;

public class ExplosionAlchemite {
    private final Level level;
    private final @Nullable Entity exploder;
    private double explosionX;
    private double explosionY;
    private double explosionZ;
    private float explosionSize;
    private final List<BlockPos> affectedBlockPositions = new ArrayList<>();

    public ExplosionAlchemite(Level level, @Nullable Entity exploder, double x, double y, double z, float size) {
        this.level = level;
        this.exploder = exploder;
        this.explosionX = x;
        this.explosionY = y;
        this.explosionZ = z;
        this.explosionSize = size;
    }

    public void doExplosionA() {
        HashSet<BlockPos> hashset = new HashSet<>();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
                        double d0 = i / 15.0F * 2.0F - 1.0F;
                        double d1 = j / 15.0F * 2.0F - 1.0F;
                        double d2 = k / 15.0F * 2.0F - 1.0F;
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 /= d3;
                        d1 /= d3;
                        d2 /= d3;
                        float f2 = this.explosionSize * (0.7F + this.level.getRandom().nextFloat() * 0.6F);
                        double d5 = this.explosionX;
                        double d6 = this.explosionY;
                        double d7 = this.explosionZ;
                        for (float f3 = 0.3F; f2 > 0.0F; f2 -= f3 * 0.75F) {
                            BlockPos blockpos = BlockPos.containing(d5, d6, d7);
                            BlockState block = this.level.getBlockState(blockpos);
                            if (!this.level.isInWorldBounds(blockpos)) {
                                break;
                            }
                            if (this.level.getBlockEntity(blockpos) instanceof Node) {
                                hashset.add(blockpos);
                            } else {
                                FluidState fluidstate = this.level.getFluidState(blockpos);
                                Optional<Float> optional = getExplosionResistance(blockpos, block, fluidstate);
                                if (optional.isPresent()) {
                                    f2 -= (optional.get() + 0.3F) * f3;
                                }
                                if (f2 > 0.0F) {
                                    hashset.add(blockpos);
                                }
                            }
                            d5 += d0 * f3;
                            d6 += d1 * f3;
                            d7 += d2 * f3;
                        }
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(hashset);
        this.explosionSize *= 2.0F;
        int i = Mth.floor(this.explosionX - this.explosionSize - 1.0);
        int j = Mth.floor(this.explosionX + this.explosionSize + 1.0);
        int k = Mth.floor(this.explosionY - this.explosionSize - 1.0);
        int i2 = Mth.floor(this.explosionY + this.explosionSize + 1.0);
        int m = Mth.floor(this.explosionZ - this.explosionSize - 1.0);
        int j3 = Mth.floor(this.explosionZ + this.explosionSize + 1.0);
        List<Entity> list = this.level.getEntities(this.exploder,
            new AABB(i, k, m, j, i2, j3));
        Vec3 vec3 = new Vec3(this.explosionX, this.explosionY, this.explosionZ);
        for (Entity entity : list) {
            double d8 = Math.sqrt(entity.distanceToSqr(vec3)) / this.explosionSize;
            if (d8 <= 1.0) {
                double d5 = entity.getX() - this.explosionX;
                double d6 = entity.getEyeY() - this.explosionY;
                double d7 = entity.getZ() - this.explosionZ;
                double d9 = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
                if (d9 != 0.0) {
                    d5 /= d9;
                    d6 /= d9;
                    d7 /= d9;
                    double d10 = (1.0 - d8) * getBlockDensity(vec3, entity.getBoundingBox());
                    double damage = ((d10 * d10 + d10) / 2.0 * 16.0 * this.explosionSize + 1.0);
                    if (this.level instanceof ServerLevel serverLevel) {
                        entity.hurtServer(serverLevel, serverLevel.damageSources().magic(), (float) (int) damage);
                    }
                    entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d10, d6 * d10, d7 * d10));
                }
            }
        }
        this.explosionSize /= 2.0F;
    }

    private float getBlockDensity(Vec3 vec3, AABB aabb) {
        double d0 = 1.0 / ((aabb.maxX - aabb.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((aabb.maxY - aabb.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((aabb.maxZ - aabb.minZ) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (d0 < 0.0 || d1 < 0.0 || d2 < 0.0) {
            return 0.0F;
        }
        int hits = 0;
        int total = 0;
        for (double d5 = 0.0; d5 <= 1.0; d5 += d0) {
            for (double d6 = 0.0; d6 <= 1.0; d6 += d1) {
                for (double d7 = 0.0; d7 <= 1.0; d7 += d2) {
                    double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                    double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                    double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                    Vec3 probe = new Vec3(d8 + d3, d9, d10 + d4);
                    if (this.level.clip(new net.minecraft.world.level.ClipContext(probe, vec3,
                        net.minecraft.world.level.ClipContext.Block.COLLIDER,
                        net.minecraft.world.level.ClipContext.Fluid.NONE, net.minecraft.world.phys.shapes.CollisionContext.empty())).getType()
                        == net.minecraft.world.phys.HitResult.Type.MISS) {
                        hits++;
                    }
                    total++;
                }
            }
        }
        return (float) hits / (float) total;
    }

    private Optional<Float> getExplosionResistance(BlockPos pos, BlockState block, FluidState fluid) {
        if (block.isAir() && fluid.isEmpty()) {
            return Optional.empty();
        }
        float resistance = Math.max(block.getBlock().getExplosionResistance(), fluid.getExplosionResistance());
        return Optional.of(resistance);
    }

    public void doExplosionB(boolean spawnParticles) {
        if (this.level instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, this.explosionX, this.explosionY, this.explosionZ,
                SoundEvents.GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 4.0F,
                (1.0F + (serverLevel.getRandom().nextFloat() - serverLevel.getRandom().nextFloat()) * 0.2F) * 0.7F);
        }
        if (spawnParticles) {
            if (this.explosionSize >= 2.0F) {
                this.level.addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER,
                    this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
            } else {
                this.level.addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION,
                    this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
            }
        }
        if (this.level instanceof ServerLevel serverLevel) {
            ItemCompat itemCompat = Compat.port().items();
            boolean itemsAvailable = itemCompat.isAvailable();
            Item essenceItem = itemsAvailable
                ? itemCompat.lookup(Identifier.fromNamespaceAndPath("thaumcraft", "crystallized_essence"))
                : null;
            for (BlockPos blockpos : this.affectedBlockPositions) {
                BlockState block = serverLevel.getBlockState(blockpos);
                if (serverLevel.getBlockEntity(blockpos) instanceof Node node) {
                    double d = serverLevel.getRandom().nextDouble();
                    if (d < 0.25) {
                        if (essenceItem != null
                            && dropCrystallizedEssence(serverLevel, blockpos, node.getAspects(), essenceItem)
                            && Compat.port().fx().isAvailable()) {
                            Compat.port().fx().blocksplosion(serverLevel, blockpos, 0xFFFFFF);
                        }
                        serverLevel.destroyBlock(blockpos, false);
                    } else if (d < 0.5) {
                        node.setNodeModifier(NodeModifier.FADING);
                        serverLevel.sendBlockUpdated(blockpos, block, block, 3);
                    } else {
                        node.setNodeModifier(null);
                        node.setNodeType(NodeType.UNSTABLE);
                        serverLevel.sendBlockUpdated(blockpos, block, block, 3);
                    }
                } else if (!block.isAir()) {
                    Item blockItem = block.getBlock().asItem();
                    AspectList aspects = !itemsAvailable || blockItem == Items.AIR
                        ? null
                        : itemCompat.getObjectAspects(new ItemStack(blockItem));
                    if (essenceItem != null && aspects != null && !aspects.isEmpty()
                        && dropCrystallizedEssence(serverLevel, blockpos, aspects, essenceItem)) {
                        if (Compat.port().fx().isAvailable()) {
                            Compat.port().fx().blocksplosion(serverLevel, blockpos, 0xFFFFFF);
                        }
                        serverLevel.destroyBlock(blockpos, false, this.exploder);
                    } else {
                        serverLevel.destroyBlock(blockpos, true, this.exploder);
                    }
                }
            }
        }
    }

    private static boolean dropCrystallizedEssence(ServerLevel level, BlockPos pos, AspectList aspects, Item item) {
        boolean dropped = false;
        for (Aspect aspect : aspects.getAspects()) {
            int amount = aspects.getAmount(aspect);
            if (amount <= 0) {
                continue;
            }
            ItemStack essence = new ItemStack(item, amount);
            Compat.port().items().setSingleAspect(essence, aspect, 1);
            level.addFreshEntity(new InvulnerableItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5,
                pos.getZ() + 0.5, essence));
            dropped = true;
        }
        return dropped;
    }
}
