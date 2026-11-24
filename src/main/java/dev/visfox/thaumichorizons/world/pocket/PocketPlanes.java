package dev.visfox.thaumichorizons.world.pocket;

import dev.visfox.thaumichorizons.lib.TagInput;

import com.mojang.serialization.Codec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.ThaumicHorizonsConfig;
import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import java.util.HashMap;
import java.util.LinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;

public class PocketPlanes extends SavedData {
    public static final Codec<PocketPlanes> CODEC = CompoundTag.CODEC.xmap(PocketPlanes::load, PocketPlanes::save);
    public static final SavedData.Factory<PocketPlanes> TYPE = new SavedData.Factory<>(PocketPlanes::new,
        (tag, lookup) -> CODEC.parse(lookup.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow());
    public static final LinkedList<PocketPlaneData> planes = new LinkedList<>();
    public static final HashMap<Integer, BlockPos> positions = new HashMap<>();
    public static int pocketPlaneMAXID = 0;

    private static final int Y_CENTER = 128;
    private static @Nullable MinecraftServer activeServer;

    public PocketPlanes() {
    }

    public static PocketPlanes get(MinecraftServer server) {
        // These collections are retained for the legacy static access pattern,
        // but their contents must never leak from one integrated server/world
        // into another one running in the same client JVM.
        if (activeServer != server) {
            planes.clear();
            positions.clear();
            pocketPlaneMAXID = 0;
            activeServer = server;
        }
        return server.overworld().getDataStorage().computeIfAbsent(TYPE, "pocketplane");
    }

    public static void onServerStopped(MinecraftServer server) {
        if (activeServer == server) {
            planes.clear();
            positions.clear();
            pocketPlaneMAXID = 0;
            activeServer = null;
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.merge(save(this));
        return tag;
    }

    private static CompoundTag save(PocketPlanes data) {
        CompoundTag tag = new CompoundTag();
        CompoundTag positionz = new CompoundTag();
        for (HashMap.Entry<Integer, BlockPos> idToPos : positions.entrySet()) {
            positionz.putIntArray(idToPos.getKey().toString(),
                new int[] { idToPos.getValue().getX(), idToPos.getValue().getY(), idToPos.getValue().getZ() });
        }
        tag.putInt("MaxID", pocketPlaneMAXID);
        tag.put("Positions", positionz);
        ListTag planeNBT = new ListTag();
        for (PocketPlaneData plane : planes) {
            if (plane != null) {
                planeNBT.add(plane.write(new CompoundTag()));
            }
        }
        tag.put("Data", planeNBT);
        return tag;
    }

    private static PocketPlanes load(CompoundTag tag) {
        PocketPlanes data = new PocketPlanes();
        planes.clear();
        ListTag planeNBT = TagInput.getListOrEmpty(tag, "Data");
        int storedMaxId = TagInput.getIntOr(tag, "MaxID", 0);
        for (int i = 0; i < planeNBT.size(); ++i) {
            planes.add(PocketPlaneData.read(TagInput.getCompoundOrEmpty(planeNBT, i)));
        }

        // Plane IDs are dense and append-only: the list index is also the
        // generated plane's Z-slice (256 * id). Never trust a stale counter
        // independently of the saved plane list.
        pocketPlaneMAXID = planes.size();
        if (storedMaxId != pocketPlaneMAXID) {
            ThaumicHorizons.LOGGER.warn(
                "Pocket Plane SavedData MaxID {} does not match {} saved planes; repairing the next plane ID",
                storedMaxId, pocketPlaneMAXID);
        }

        positions.clear();
        CompoundTag positionz = TagInput.getCompoundOrEmpty(tag, "Positions");
        for (String id : TagInput.keySet(positionz)) {
            final int planeId;
            try {
                planeId = Integer.parseInt(id);
            } catch (NumberFormatException exception) {
                ThaumicHorizons.LOGGER.warn("Ignoring invalid Pocket Plane return-position key '{}'", id);
                continue;
            }

            int[] pos = TagInput.getIntArrayOr(positionz, id);
            if (planeId < 0 || planeId >= planes.size() || pos == null || pos.length < 3) {
                ThaumicHorizons.LOGGER.warn("Ignoring malformed Pocket Plane return-position entry '{}'", id);
                continue;
            }
            positions.put(planeId, new BlockPos(pos[0], pos[1], pos[2]));
        }
        return data;
    }

    public static boolean generatePocketPlane(AspectList aspects, PocketPlaneData data, MinecraftServer server,
            int vortexX, int vortexY, int vortexZ, String returnDimension) {
        PocketPlanes saved = get(server);
        if (!ThaumicHorizonsConfig.pocketPlaneEnabled()
                || net.minecraft.resources.ResourceLocation.tryParse(returnDimension) == null) {
            return false;
        }
        ServerLevel world = PocketDimension.level(server);
        if (world == null) {
            return false;
        }

        int planeId = planes.size();
        if (pocketPlaneMAXID != planeId) {
            ThaumicHorizons.LOGGER.warn(
                "Pocket Plane next ID {} does not match {} existing planes; repairing before generation",
                pocketPlaneMAXID, planeId);
            pocketPlaneMAXID = planeId;
        }

        PocketPlaneGenerator.beginGeneration(world);
        data.sourceDimension = returnDimension;
        int xCenter = 0;
        int zCenter = 256 * planeId;
        if (aspects.visSize() * 0.75F < 128.0F) {
            data.radius = (int) Math.max(32.0F, aspects.visSize() * 0.75F);
        } else {
            data.radius = 127;
        }
        data.color = PocketPlaneGenerator.getColor(aspects);
        ResourceKey<Biome> bio = PocketPlaneGenerator.setBiome(xCenter, Y_CENTER, zCenter, data, world, aspects);
        data.biome = bio;
        int noise = PocketPlaneGenerator.fastFloor(PocketPlaneGenerator.calcNoise(aspects));
        int life = PocketPlaneGenerator.fastFloor(PocketPlaneGenerator.calcLife(aspects));
        PocketPlaneGenerator.drawLayers(xCenter, Y_CENTER, zCenter, data, world, aspects, noise, bio, life);
        PocketPlaneGenerator.drawCaves(xCenter, Y_CENTER, zCenter, data, world, aspects, noise);
        PocketPlaneGenerator.drawPockets(xCenter, Y_CENTER, zCenter, data, world, aspects, noise);
        PocketPlaneGenerator.drawRavines(xCenter, Y_CENTER, zCenter, data, world, aspects, noise);
        PocketPlaneGenerator.drawClouds(xCenter, Y_CENTER, zCenter, data, world, aspects, noise);
        PocketPlaneGenerator.drawSurfaceFeatures(xCenter, Y_CENTER, zCenter, data, world, aspects, noise, life);
        PocketPlaneGenerator.drawUndergroundFeatures(xCenter, Y_CENTER, zCenter, data, world, aspects, noise, life);
        PocketPlaneGenerator.drawLeviathanBones(xCenter, Y_CENTER, zCenter, data, world, aspects, noise);
        PocketPlaneGenerator.addEffects(data, aspects);
        PocketPlaneGenerator.drawRings(xCenter, Y_CENTER, zCenter, data, world, aspects);
        PocketPlaneGenerator.drawSphere(xCenter, Y_CENTER, zCenter, data.radius, ThBlocks.VOID.get(), 0, world);
        // Legacy blockCosmeticSolid metadata 6 maps to TC4R arcane_stone.
        Block cosmetic = dev.visfox.thaumichorizons.lib.TcBlocks.block("arcane_stone");
        if (cosmetic == null) {
            cosmetic = net.minecraft.world.level.block.Blocks.STONE_BRICKS;
        }
        for (int x = -2; x <= 2; ++x) {
            for (int z = -2; z <= 2; ++z) {
                PocketPlaneGenerator.drawPlatform(world, xCenter + x, Y_CENTER, zCenter + z, cosmetic);
            }
        }
        world.setBlock(new BlockPos(xCenter, Y_CENTER + 1, zCenter), ThBlocks.VORTEX.get().defaultBlockState(), 2);
        BlockEntity te = world.getBlockEntity(new BlockPos(xCenter, Y_CENTER + 1, zCenter));
        if (te instanceof VortexBlockEntity vortex) {
            vortex.cheat = true;
            vortex.createdDimension = true;
            vortex.dimensionID = planeId;
            vortex.setChanged();
        }
        data.clearPortal(1);
        data.clearPortal(2);
        data.clearPortal(3);
        data.clearPortal(4);
        planes.add(data);
        positions.put(planeId, new BlockPos(vortexX, vortexY, vortexZ));
        pocketPlaneMAXID = planeId + 1;
        saved.setDirty();
        return true;
    }

    public static int firstAvailablePortal(int num) {
        if (!validPlane(num)) {
            return 0;
        }
        PocketPlaneData data = planes.get(num);
        if (isPortalUnused(data, 1)) {
            return 1;
        }
        if (isPortalUnused(data, 2)) {
            return 2;
        }
        if (isPortalUnused(data, 3)) {
            return 3;
        }
        if (isPortalUnused(data, 4)) {
            return 4;
        }
        return 0;
    }

    private static boolean isPortalUnused(PocketPlaneData data, int which) {
        int[] portal = data.portal(which);
        // A usable endpoint needs coordinates, an orientation flag and a valid
        // dimension key. Incomplete/corrupt records must not consume one of the
        // four portal slots permanently.
        String dimension = data.portalDimension(which);
        return portal == null || portal.length < 4
            || (portal[0] == 0 && portal[1] == 0 && portal[2] == 0)
            || (portal[3] != 0 && portal[3] != 1)
            || dimension.isEmpty()
            || net.minecraft.resources.ResourceLocation.tryParse(dimension) == null;
    }

    public static void destroyPortal(int id, int which, MinecraftServer server) {
        if (!validPlane(id) || which < 1 || which > 4) {
            return;
        }
        PocketPlaneData data = planes.get(id);
        ServerLevel world = PocketDimension.level(server);
        // Always release the saved portal index, even if the dimension is
        // unavailable (for example after changing the startup config).
        if (world != null) {
            if (which == 1) {
                for (int x = -1; x <= 1; ++x) {
                    for (int y = 126; y <= 128; ++y) {
                        PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id + data.radius, ThBlocks.VOID.get());
                    }
                }
            } else if (which == 2) {
                for (int x = -1; x <= 1; ++x) {
                    for (int y = 126; y <= 128; ++y) {
                        PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id - data.radius, ThBlocks.VOID.get());
                    }
                }
            } else if (which == 3) {
                for (int z = -1; z <= 1; ++z) {
                    for (int y = 126; y <= 128; ++y) {
                        PocketPlaneGenerator.setBlockPublic(world, data.radius, y, 256 * id + z, ThBlocks.VOID.get());
                    }
                }
            } else {
                for (int z = -1; z <= 1; ++z) {
                    for (int y = 126; y <= 128; ++y) {
                        PocketPlaneGenerator.setBlockPublic(world, -data.radius, y, 256 * id + z, ThBlocks.VOID.get());
                    }
                }
            }
        }
        data.clearPortal(which);
        PocketPlanes.get(server).setDirty();
    }

    public static void makePortal(int id, int which, int xCoord, int yCoord, int zCoord,
            String dimension, boolean xAligned, MinecraftServer server) {
        ServerLevel world = PocketDimension.level(server);
        if (world == null || !validPlane(id) || net.minecraft.resources.ResourceLocation.tryParse(dimension) == null) {
            return;
        }
        PocketPlaneData data = planes.get(id);
        if (which == 1) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, x, y, 256 * id + data.radius, 0,
                        new BlockPos(xCoord, yCoord, zCoord), id, which, dimension, xAligned);
                    PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id + data.radius + 1, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension, xAligned);
        } else if (which == 2) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, x, y, 256 * id - data.radius, 2,
                        new BlockPos(xCoord, yCoord, zCoord), id, which, dimension, xAligned);
                    PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id - data.radius - 1, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension, xAligned);
        } else if (which == 3) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, data.radius, y, 256 * id + z, 1,
                        new BlockPos(xCoord, yCoord, zCoord), id, which, dimension, xAligned);
                    PocketPlaneGenerator.setBlockPublic(world, data.radius + 1, y, 256 * id + z, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension, xAligned);
        } else if (which == 4) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, -data.radius, y, 256 * id + z, 3,
                        new BlockPos(xCoord, yCoord, zCoord), id, which, dimension, xAligned);
                    PocketPlaneGenerator.setBlockPublic(world, -data.radius - 1, y, 256 * id + z, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension, xAligned);
        }
        PocketPlanes.get(server).setDirty();
    }

    public static int planeIndexAt(int z) {
        return (z + 128) / 256;
    }

    public static @Nullable BlockPos returnPosition(int planeNum) {
        return positions.get(planeNum);
    }

    public static String returnDimension(int planeNum) {
        return validPlane(planeNum) ? planes.get(planeNum).sourceDimension : "";
    }

    public static boolean validPlane(int planeNum) {
        return planeNum >= 0 && planeNum < planes.size();
    }

    public static AspectList primalCost() {
        AspectList cost = new AspectList();
        cost.add(Aspect.WATER, 250);
        cost.add(Aspect.EARTH, 250);
        cost.add(Aspect.ORDER, 250);
        cost.add(Aspect.FIRE, 250);
        cost.add(Aspect.AIR, 250);
        cost.add(Aspect.ENTROPY, 250);
        return cost;
    }
}
