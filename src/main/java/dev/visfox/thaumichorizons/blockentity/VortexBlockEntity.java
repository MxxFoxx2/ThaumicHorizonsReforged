package dev.visfox.thaumichorizons.blockentity;

import dev.visfox.thaumichorizons.compat.tc.TcReforgedPort;
import dev.visfox.thaumichorizons.ThaumicHorizonsConfig;
import dev.visfox.thaumichorizons.world.pocket.PocketDimension;
import dev.visfox.thaumichorizons.world.pocket.PocketPlaneData;
import dev.visfox.thaumichorizons.world.pocket.PocketPlanes;
import dev.visfox.thaumichorizons.registry.ThBlockEntities;
import dev.visfox.thaumichorizons.registry.ThItems;
import dev.visfox.thaumichorizons.entity.EntityGolemTH;
import dev.visfox.thaumichorizons.lib.THSounds;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.state.BlockState;
import dev.visfox.thaumichorizons.compat.ValueInput;
import dev.visfox.thaumichorizons.compat.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;
import thaumcraft.wand.WandInteractable;
import thaumcraft.platform.Teleport;

public class VortexBlockEntity extends ThBlockEntity implements WandInteractable {
    private static final int MAX_COUNT = 2400;
    public int count;
    public int beams;
    public AspectList aspects = new AspectList();
    boolean ateDevices;
    public boolean collapsing;
    public boolean createdDimension;
    public int dimensionID;
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
        boolean inPocketPlane = level.dimension().equals(PocketDimension.POCKET_PLANE);
        if (!level.isClientSide() && beams != lastSyncedBeams) {
            sync();
            lastSyncedBeams = beams;
        }
        if (collapsing) {
            count++;
            if (count > 25) {
                if (!level.isClientSide() && createdDimension && level instanceof ServerLevel source
                        && PocketPlanes.validPlane(dimensionID)) {
                    ServerLevel pocket = PocketDimension.level(source.getServer());
                    if (pocket != null) {
                        pocket.removeBlock(new BlockPos(0, 129, 256 * dimensionID), false);
                    }
                }
                if (level.isClientSide()) {
                    TcReforgedPort.fx.burst(level, Vec3.atCenterOf(worldPosition), 16777215, 16);
                }
                level.removeBlock(worldPosition, false);
            }
            return;
        }
        if (count < 50) {
            count++;
        } else {
            if (!ateDevices) {
                if (!cheat && !inPocketPlane) {
                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dy = -2; dy <= 2; dy++) {
                            for (int dz = -2; dz <= 2; dz++) {
                                if (dx != 0 || dy != 0 || dz != 0) {
                                    BlockPos target = worldPosition.offset(dx, dy, dz);
                                    level.removeBlock(target, false);
                                    if (level.isClientSide()) {
                                        TcReforgedPort.fx.burst(level, Vec3.atCenterOf(target), 16777215, 16);
                                    }
                                }
                            }
                        }
                    }
                }
                ateDevices = true;
            }
            if (beams < 6 && !cheat && !inPocketPlane) {
                handleHungryNode();
            } else if (!createdDimension) {
                handlePocketPlaneStuff();
            } else {
                handleVoidCrafting();
            }
            if (!cheat) {
                count += Math.max(0, 6 - beams);
            }
            if (count > MAX_COUNT && !cheat && !inPocketPlane) {
                collapsing = true;
                count = 0;
            }
            if (createdDimension) {
                handleDimensionTravel();
            }
        }
    }

    private void handlePocketPlaneStuff() {
        if (!(level instanceof ServerLevel source) || level.isClientSide() || createdDimension) {
            return;
        }
        Item primordialPearl = TcReforgedPort.items.lookup(
            ResourceLocation.fromNamespaceAndPath("thaumcraft", "primordial_pearl"));
        List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class,
            new AABB(worldPosition).inflate(1.0, 1.0, 1.0));
        if (primordialPearl != null && ThaumicHorizonsConfig.pocketPlaneEnabled()) {
            for (ItemEntity item : entities) {
                if (item.getItem().is(primordialPearl) && createPocketPlane(source, item)) {
                    item.discard();
                    return;
                }
            }
        }
        handleVoidCrafting();
    }

    private boolean createPocketPlane(ServerLevel source, ItemEntity pearl) {
        MinecraftServer server = source.getServer();
        PocketPlanes.get(server);
        // Plane IDs are dense list indexes. Read the canonical next ID rather
        // than the cached counter, which generatePocketPlane() may repair.
        int newPlaneID = PocketPlanes.planes.size();
        PocketPlaneData data = new PocketPlaneData();

        // Match the reference: renamed pearls name the plane; otherwise use
        // the thrower's name plus the localized Pocket Plane suffix.
        ItemStack pearlStack = pearl.getItem();
        Component customName = pearlStack.get(DataComponents.CUSTOM_NAME);
        if (customName != null) {
            data.name = customName.getString();
        } else {
            Entity thrower = pearl.getOwner();
            data.name = thrower == null
                ? Component.translatable("tc.research_name.pocketPlane").getString()
                : thrower.getName().getString() + Component.translatable("thaumichorizons.pocketplane").getString();
        }
        boolean created = PocketPlanes.generatePocketPlane(aspects, data, server,
            worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
            source.dimension().location().toString());
        if (!created) {
            return false;
        }
        dimensionID = newPlaneID;
        createdDimension = true;
        setChanged();
        sync();
        return true;
    }

    private void handleDimensionTravel() {
        if (!(level instanceof ServerLevel current) || level.isClientSide()) {
            return;
        }
        MinecraftServer server = current.getServer();
        PocketPlanes.get(server);
        List<ServerPlayer> players = current.getEntitiesOfClass(ServerPlayer.class, new AABB(worldPosition));
        for (ServerPlayer player : players) {
            if (player.isPassenger() || player.isVehicle() || player.getPortalCooldown() > 0) {
                continue;
            }

            ServerLevel destinationLevel;
            Vec3 destination;
            if (current.dimension().equals(PocketDimension.POCKET_PLANE)) {
                if (!PocketPlanes.validPlane(dimensionID)) {
                    continue;
                }
                BlockPos returnPos = PocketPlanes.returnPosition(dimensionID);
                String returnDimension = PocketPlanes.returnDimension(dimensionID);
                ResourceLocation identifier = ResourceLocation.tryParse(returnDimension);
                if (returnPos == null || identifier == null) {
                    continue;
                }
                ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, identifier);
                destinationLevel = server.getLevel(dimensionKey);
                if (destinationLevel == null) {
                    continue;
                }
                destination = new Vec3(returnPos.getX(), returnPos.getY(), returnPos.getZ());
            } else {
                destinationLevel = PocketDimension.level(server);
                if (destinationLevel == null || !PocketPlanes.validPlane(dimensionID)) {
                    continue;
                }
                destination = new Vec3(0.5, 129.0, 256 * dimensionID + 0.5);
            }

            Entity teleported = Teleport.to(player, destinationLevel, destination,
                player.getYRot(), player.getXRot());
            if (teleported != null) {
                teleported.setPortalCooldown(200);
            }
        }
    }

    void handleVoidCrafting() {
        if (!(level instanceof ServerLevel server) || level.isClientSide()) {
            return;
        }
        Item voidMetal = TcReforgedPort.items.lookup(voidMetalIngotId());
        Item salisMundus = TcReforgedPort.items.lookup(salisMundusId());
        List<ItemEntity> ents = level.getEntitiesOfClass(ItemEntity.class,
            new AABB(worldPosition).inflate(1.0, 1.0, 1.0));
        for (ItemEntity item : ents) {
            ItemStack stack = item.getItem();
            if (voidMetal != null && stack.is(voidMetal)) {
                // Legacy itemResource metadata 16: void metal ingot -> Void Putty.
                items.add(new ItemStack(ThItems.VOID_PUTTY.get(), stack.getCount()));
                item.discard();
            } else if (salisMundus != null && stack.is(salisMundus)) {
                // Legacy itemResource metadata 14: each Salis Mundus releases 1–4 Wisps.
                TcReforgedPort.items.spawnWisps(server, worldPosition, aspects, stack.getCount());
                item.discard();
            } else if (stack.is(ThItems.CRYSTAL_WAND.get())) {
                items.add(new ItemStack(ThItems.WAND_CASTING_DISPOSABLE.get()));
                item.discard();
            } else if (stack.is(ThItems.GOLEM_POWDER.get()) && item.getOwner() != null) {
                // The original requires a thrower so the temporary golems have an owner.
                Entity owner = item.getOwner();
                for (int i = 0; i < stack.getCount(); i++) {
                    EntityGolemTH golem = new EntityGolemTH(EntityGolemTH.thType(), server);
                    golem.loadGolem(worldPosition.getX() + 0.5D, worldPosition.getY(),
                        worldPosition.getZ() + 0.5D, null, 0, -420, false, false, false);
                    golem.setOwnerUUID(owner.getUUID());
                    golem.setHome(worldPosition, Direction.UP);
                    THSounds.play(server, Vec3.atCenterOf(worldPosition),
                        "wand", net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, 1.0F);
                    server.addFreshEntity(golem);
                    server.broadcastEntityEvent(golem, (byte) 7);
                }
                item.discard();
            }
        }
    }

    private static ResourceLocation voidMetalIngotId() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "void_metal_ingot");
    }

    private static ResourceLocation salisMundusId() {
        return ResourceLocation.fromNamespaceAndPath("thaumcraft", "salis_mundus");
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
                                eo.hurt(level.damageSources().fellOutOfWorld(), (float) (3.0 - beams / 2.0));
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
        dimensionID = input.getIntOr("dimensionID", 0);
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
        output.putInt("dimensionID", dimensionID);
        output.putBoolean("cheat", cheat);
        output.store("aspects", AspectList.CODEC, aspects);
        if (!items.isEmpty()) {
            output.store("items", ItemStack.OPTIONAL_CODEC.listOf(), items);
        }
    }
}
