package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.Compat;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandInteractable;

public class VortexBlockEntity extends ThBlockEntity implements WandInteractable {
    private static final int MAX_COUNT = 2400;
    public int count;
    public int beams;
    public AspectList aspects = new AspectList();
    boolean ateDevices;
    public boolean collapsing;
    public boolean createdDimension;
    public boolean cheat;
    public ArrayList<ItemStack> items = new ArrayList<>();
    private int lastSyncedBeams = -1;

    public VortexBlockEntity(BlockPos pos, BlockState state) {
        super(ThBlockEntities.VORTEX.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VortexBlockEntity entity) {
        entity.tick();
    }

    private void tick() {
        if (level == null) {
            return;
        }
        if (!level.isClientSide() && beams != lastSyncedBeams) {
            sync();
            lastSyncedBeams = beams;
        }
        if (collapsing) {
            count++;
            if (count > 25) {
                if (level.isClientSide()) {
                    Compat.port().fx().burst(level, Vec3.atCenterOf(worldPosition), 16777215, 16);
                }
                level.removeBlock(worldPosition, false);
            }
            return;
        }
        if (count < 50) {
            count++;
        } else {
            if (!ateDevices) {
                if (!cheat) {
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            for (int dz = -2; dz <= 2; dz++) {
                                if (dx != 0 || dy != 0 || dz != 0) {
                                    BlockPos target = worldPosition.offset(dx, dy, dz);
                                    level.removeBlock(target, false);
                                    if (level.isClientSide()) {
                                        Compat.port().fx().burst(level, Vec3.atCenterOf(target), 16777215, 16);
                                    }
                                }
                            }
                        }
                    }
                }
                ateDevices = true;
            }
            if (beams < 6 && !cheat) {
                handleHungryNode();
            } else {
                handleVoidCrafting();
            }
            if (!cheat) {
                count += Math.max(0, 6 - beams);
            }
            if (count > MAX_COUNT && !cheat) {
                collapsing = true;
                count = 0;
            }
        }
    }

    void handleVoidCrafting() {
        if (level == null || level.isClientSide()) {
            return;
        }
        Item seed = Compat.port().items().lookup(voidSeedId());
        if (seed == null) {
            return;
        }
        List<ItemEntity> ents = level.getEntitiesOfClass(ItemEntity.class, new AABB(worldPosition).inflate(1.0, 1.0, 1.0));
        for (ItemEntity item : ents) {
            ItemStack stack = item.getItem();
            if (stack.is(seed)) {
                items.add(new ItemStack(ThItems.VOID_PUTTY.get(), stack.getCount()));
                item.discard();
            } else if (stack.is(ThItems.CRYSTAL_WAND.get())) {
                items.add(new ItemStack(ThItems.WAND_CASTING_DISPOSABLE.get()));
                item.discard();
            }
        }
    }

    private static Identifier voidSeedId() {
        return Identifier.fromNamespaceAndPath("thaumcraft", "void_seed");
    }

    void handleHungryNode() {
        if (level == null) {
            return;
        }
        List<Entity> ents = level.getEntities((Entity) null, new AABB(worldPosition).inflate(15.0, 15.0, 15.0));
        if (!ents.isEmpty()) {
            for (Entity eo : ents) {
                if (!(eo instanceof Player player) || !player.getAbilities().invulnerable) {
                    if (eo.isAlive()) {
                        double d = getDistanceTo(eo.getX(), eo.getY(), eo.getZ());
                        if (d < 2.0) {
                            if (eo instanceof FallingBlockEntity) {
                                eo.discard();
                            } else if (level instanceof ServerLevel serverLevel) {
                                eo.hurtServer(serverLevel, level.damageSources().fellOutOfWorld(),
                                    (float) (3.0 - beams / 2.0));
                            }
                        }
                    }
                    double dx = (worldPosition.getX() + 0.5 - eo.getX()) / 15.0;
                    double dy = (worldPosition.getY() + 0.5 - eo.getY()) / 15.0;
                    double dz = (worldPosition.getZ() + 0.5 - eo.getZ()) / 15.0;
                    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
                    double var7 = 1.0 - dist;
                    double modifier = 2.0 - beams / 3.0;
                    if (var7 <= 0.0) {
                        continue;
                    }
                    var7 *= var7;
                    eo.setDeltaMovement(eo.getDeltaMovement().add(
                        dx / dist * var7 * 0.15 * modifier,
                        dy / dist * var7 * 0.25 * modifier,
                        dz / dist * var7 * 0.15 * modifier));
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            if (!level.isClientSide() && (beams <= 0 || level.getRandom().nextInt(Math.max(1, beams)) == 0)) {
                int tx2 = worldPosition.getX() + level.getRandom().nextInt(16) - level.getRandom().nextInt(16);
                int ty2 = worldPosition.getY() + level.getRandom().nextInt(16) - level.getRandom().nextInt(16);
                int tz2 = worldPosition.getZ() + level.getRandom().nextInt(16) - level.getRandom().nextInt(16);
                BlockPos probe = new BlockPos(tx2, ty2, tz2);
                if (!level.hasChunksAt(worldPosition.offset(-16, -16, -16), worldPosition.offset(16, 16, 16))) {
                    continue;
                }
                if (ty2 > level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, tx2, tz2)) {
                    ty2 = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, tx2, tz2);
                }
                BlockHitResult mop = level.clip(new net.minecraft.world.level.ClipContext(
                    Vec3.atCenterOf(worldPosition),
                    new Vec3(tx2 + 0.5, ty2 + 0.5, tz2 + 0.5),
                    net.minecraft.world.level.ClipContext.Block.COLLIDER,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                    net.minecraft.world.phys.shapes.CollisionContext.empty()));
                if (worldPosition.distSqr(mop.getBlockPos()) < 256.0) {
                    BlockPos pos = mop.getBlockPos();
                    BlockState bi2 = level.getBlockState(pos);
                    if (!bi2.isAir()) {
                        float h = bi2.getDestroySpeed(level, pos);
                        if (h >= 0.0F && h < 10.0F) {
                            level.destroyBlock(pos, true);
                        }
                    }
                }
            }
        }
    }

    public double getDistanceTo(double par1, double par3, double par5) {
        double var7 = worldPosition.getX() + 0.5 - par1;
        double var8 = worldPosition.getY() + 0.5 - par3;
        double var9 = worldPosition.getZ() + 0.5 - par5;
        return var7 * var7 + var8 * var8 + var9 * var9;
    }

    public void generateAspects(net.minecraft.util.RandomSource random) {
        aspects = new AspectList();
        int numAspects = random.nextInt(4) + 1;
        for (int a = 0; a < numAspects; a++) {
            if (random.nextInt(3) == 0) {
                aspects.add(Aspect.getCompoundAspects().get(random.nextInt(Aspect.getCompoundAspects().size())),
                    random.nextInt(30));
            } else {
                aspects.add(Aspect.getPrimalAspects().get(random.nextInt(Aspect.getPrimalAspects().size())),
                    random.nextInt(30));
            }
        }
        sync();
    }

    @Override
    public InteractionResult onWandUse(Level level, BlockPos pos, Player player, ItemStack wand) {
        if (!level.isClientSide() && !items.isEmpty()) {
            ItemStack item = items.get(0);
            ItemEntity key = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5, item);
            level.addFreshEntity(key);
            items.remove(0);
            sync();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        count = input.getIntOr("count", 0);
        beams = input.getIntOr("beams", 0);
        ateDevices = input.getBooleanOr("ateDevices", false);
        collapsing = input.getBooleanOr("collapsing", false);
        createdDimension = input.getBooleanOr("createdDimension", false);
        cheat = input.getBooleanOr("cheat", false);
        aspects = input.read("aspects", AspectList.CODEC).orElseGet(AspectList::new);
        items = new ArrayList<>(input.read("items", ItemStack.OPTIONAL_CODEC.listOf()).orElseGet(ArrayList::new));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("count", count);
        output.putInt("beams", beams);
        output.putBoolean("ateDevices", ateDevices);
        output.putBoolean("collapsing", collapsing);
        output.putBoolean("createdDimension", createdDimension);
        output.putBoolean("cheat", cheat);
        output.store("aspects", AspectList.CODEC, aspects);
        if (!items.isEmpty()) {
            output.store("items", ItemStack.OPTIONAL_CODEC.listOf(), items);
        }
    }
}
