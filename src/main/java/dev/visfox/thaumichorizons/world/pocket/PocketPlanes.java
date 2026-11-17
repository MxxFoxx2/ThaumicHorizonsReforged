package dev.visfox.thaumichorizons.world.pocket;

import com.mojang.serialization.Codec;
import dev.visfox.thaumichorizons.ThaumicHorizons;
import dev.visfox.thaumichorizons.ThaumicHorizonsConfig;
import dev.visfox.thaumichorizons.blockentity.VortexBlockEntity;
import dev.visfox.thaumichorizons.registry.ThBlocks;
import java.util.HashMap;
import java.util.LinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;
import thaumcraft.aspect.Aspect;
import thaumcraft.aspect.AspectList;

public class PocketPlanes extends SavedData {
    public static final Codec<PocketPlanes> CODEC = CompoundTag.CODEC.xmap(PocketPlanes::load, PocketPlanes::save);
    public static final SavedDataType<PocketPlanes> TYPE = new SavedDataType<>(ThaumicHorizons.id("pocketplane"),
        PocketPlanes::new, CODEC);
    public static final LinkedList<PocketPlaneData> planes = new LinkedList<>();
    public static final HashMap<Integer, BlockPos> positions = new HashMap<>();
    public static int pocketPlaneMAXID = 0;

    private static final int Y_CENTER = 128;

    public PocketPlanes() {
    }

    public static PocketPlanes get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
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
        ListTag planeNBT = tag.getListOrEmpty("Data");
        pocketPlaneMAXID = tag.getIntOr("MaxID", 0);
        for (int i = 0; i < planeNBT.size(); ++i) {
            planes.add(PocketPlaneData.read(planeNBT.getCompoundOrEmpty(i)));
        }
        positions.clear();
        CompoundTag positionz = tag.getCompoundOrEmpty("Positions");
        for (String id : positionz.keySet()) {
            int[] pos = positionz.getIntArray(id).orElse(new int[] { 0, 0, 0 });
            positions.put(Integer.valueOf(id),
                pos.length >= 3 ? new BlockPos(pos[0], pos[1], pos[2]) : BlockPos.ZERO);
        }
        return data;
    }

    public static void generatePocketPlane(AspectList aspects, PocketPlaneData data, MinecraftServer server,
            int vortexX, int vortexY, int vortexZ, int returnID) {
        if (!ThaumicHorizonsConfig.pocketPlaneEnabled()) {
            return;
        }
        ServerLevel world = PocketDimension.level(server);
        if (world == null) {
            return;
        }
        int xCenter = 0;
        int zCenter = 256 * planes.size();
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
        Block cosmetic = dev.visfox.thaumichorizons.lib.TcBlocks.block("obsidian_tile");
        if (cosmetic == null) {
            cosmetic = net.minecraft.world.level.block.Blocks.OBSIDIAN;
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
            vortex.dimensionID = planes.size();
            vortex.setChanged();
        }
        data.clearPortal(1);
        data.clearPortal(2);
        data.clearPortal(3);
        data.clearPortal(4);
        planes.add(data);
        positions.put(pocketPlaneMAXID, new BlockPos(vortexX, vortexY, vortexZ));
        ++pocketPlaneMAXID;
        PocketPlanes.get(server).setDirty();
    }

    public static int firstAvailablePortal(int num) {
        if (!validPlane(num)) {
            return 0;
        }
        PocketPlaneData data = planes.get(num);
        if (isPortalUnused(data.portalA)) {
            return 1;
        }
        if (isPortalUnused(data.portalB)) {
            return 2;
        }
        if (isPortalUnused(data.portalC)) {
            return 3;
        }
        if (isPortalUnused(data.portalD)) {
            return 4;
        }
        return 0;
    }

    private static boolean isPortalUnused(@Nullable int[] portal) {
        return portal == null || portal.length < 3
            || portal[0] == 0 && portal[1] == 0 && portal[2] == 0;
    }

    public static void destroyPortal(int id, int which, MinecraftServer server) {
        ServerLevel world = PocketDimension.level(server);
        if (world == null || !validPlane(id)) {
            return;
        }
        PocketPlaneData data = planes.get(id);
        if (which == 1) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id + data.radius, ThBlocks.VOID.get());
                }
            }
            data.clearPortal(1);
        } else if (which == 2) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id - data.radius, ThBlocks.VOID.get());
                }
            }
            data.clearPortal(2);
        } else if (which == 3) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setBlockPublic(world, data.radius, y, 256 * id + z, ThBlocks.VOID.get());
                }
            }
            data.clearPortal(3);
        } else if (which == 4) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setBlockPublic(world, -data.radius, y, 256 * id + z, ThBlocks.VOID.get());
                }
            }
            data.clearPortal(4);
        }
        PocketPlanes.get(server).setDirty();
    }

    public static void makePortal(int id, int which, int xCoord, int yCoord, int zCoord,
            String dimension, MinecraftServer server) {
        ServerLevel world = PocketDimension.level(server);
        if (world == null || !validPlane(id) || net.minecraft.resources.Identifier.tryParse(dimension) == null) {
            return;
        }
        PocketPlaneData data = planes.get(id);
        if (which == 1) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, x, y, 256 * id + data.radius, 0);
                    PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id + data.radius + 1, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension);
        } else if (which == 2) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, x, y, 256 * id - data.radius, 2);
                    PocketPlaneGenerator.setBlockPublic(world, x, y, 256 * id - data.radius - 1, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension);
        } else if (which == 3) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, data.radius, y, 256 * id + z, 1);
                    PocketPlaneGenerator.setBlockPublic(world, data.radius + 1, y, 256 * id + z, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension);
        } else if (which == 4) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    PocketPlaneGenerator.setPortalBlock(world, -data.radius, y, 256 * id + z, 3);
                    PocketPlaneGenerator.setBlockPublic(world, -data.radius - 1, y, 256 * id + z, ThBlocks.VOID.get());
                }
            }
            data.setPortal(which, xCoord, yCoord, zCoord, dimension);
        }
        PocketPlanes.get(server).setDirty();
    }

    public static int planeIndexAt(int z) {
        return (z + 128) / 256;
    }

    public static @Nullable BlockPos returnPosition(int planeNum) {
        return positions.get(planeNum);
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
