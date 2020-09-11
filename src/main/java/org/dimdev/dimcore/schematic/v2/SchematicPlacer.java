package org.dimdev.dimcore.schematic.v2;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dimdev.dimdoors.mixin.ListTagAccessor;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

import net.fabricmc.loader.api.FabricLoader;

public final class SchematicPlacer {
    public static final Logger LOGGER = LogManager.getLogger();

    private SchematicPlacer() {
    }

    public static void place(Schematic schematic, StructureWorldAccess world, BlockPos origin) {
        LOGGER.debug("Placing schematic: {}", schematic.getMetadata().getName());
        for (String id : schematic.getMetadata().getRequiredMods()) {
            if (!FabricLoader.getInstance().isModLoaded(id)) {
                LOGGER.warn("Schematic \"" + schematic.getMetadata().getName() + "\" depends on mod \"" + id + "\", which is missing!");
            }
        }
        int width = schematic.getWidth();
        int height = schematic.getHeight();
        int length = schematic.getLength();
        int originX = origin.getX();
        int originY = origin.getY();
        int originZ = origin.getZ();
        int[][][] blockData = SchematicPlacer.getBlockData(schematic, width, height, length);
        SchematicBlockSample blockSample = Schematic.blockSample(schematic, world);
        BiMap<BlockState, Integer> palette = ImmutableBiMap.copyOf(schematic.getBlockPalette());
        blockSample.place(origin);
        SchematicPlacer.placeEntities(originX, originY, originZ, schematic, world);
        SchematicPlacer.placeBlockEntities(originX, originY, originZ, schematic, blockSample);
    }

    static int[][][] getBlockData(Schematic schematic, int width, int height, int length) {
        byte[] blockDataIntArray = schematic.getBlockData().array();
        int[][][] blockData = new int[width][height][length];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    blockData[x][y][z] = blockDataIntArray[x + z * width + y * width * length];
                }
            }
        }
        return blockData;
    }

    private static void placeEntities(int originX, int originY, int originZ, Schematic schematic, StructureWorldAccess world) {
        List<CompoundTag> entityTags = schematic.getEntities();
        for (CompoundTag tag : entityTags) {
            // Ensures compatibility with worldedit schematics
            if (SchematicPlacer.fixId(tag)){
                System.err.println("An unexpected error occurred parsing this entity");
                System.err.println(tag.toString());
                throw new IllegalStateException("Entity in schematic  \"" + schematic.getMetadata().getName() + "\" did not have an Id tag, nor an id tag!");
            }
            ListTag listTag = Objects.requireNonNull(tag.getList("Pos", 6), "Entity in schematic  \"" + schematic.getMetadata().getName() + "\" did not have a Pos tag!");
            SchematicPlacer.processPos(listTag, originX, originY, originZ, tag);

            EntityType<?> entityType = EntityType.fromTag(tag).orElseThrow(AssertionError::new);
            Entity e = entityType.create(world.toServerWorld());
            // TODO: fail with an exception
            if (e != null) {
                e.fromTag(tag);
                world.spawnEntityAndPassengers(e);
            }
        }
    }

    private static void placeBlockEntities(int originX, int originY, int originZ, Schematic schematic, SchematicBlockSample blockSample) {
        List<CompoundTag> blockEntityTags = schematic.getBlockEntities();
        for (CompoundTag tag : blockEntityTags) {
            if (SchematicPlacer.fixId(tag)) {
                System.err.println("An unexpected error occurred parsing this block entity");
                System.err.println(tag.toString());
                throw new IllegalStateException("Block Entity in schematic  \"" + schematic.getMetadata().getName() + "\" did not have an Id tag, nor an id tag!");
            }
            if (fixPos(tag)) {
                throw new IllegalStateException("Block Entity in schematic  \"" + schematic.getMetadata().getName() + "\" did not have a Pos tag, nor x, y and z tags!");
            }
            ListTag listTag = Objects.requireNonNull(tag.getList("Pos", 6));
            SchematicPlacer.processPos(listTag, originX, originY, originZ, tag);

            BlockPos pos = new BlockPos(tag.getInt("x"),tag.getInt("y"),tag.getInt("z"));
            BlockEntity blockEntity = blockSample.getBlockEntity(pos);
            // TODO: fail with an exception
            if (blockEntity != null) {
                blockEntity.fromTag(blockSample.getWorld().getBlockState(pos), tag);
                blockSample.getWorld().getChunk(pos).setBlockEntity(pos, blockEntity);
            }
        }
    }

    private static boolean fixId(CompoundTag tag) {
        if (!tag.contains("Id") && tag.contains("id")) {
            tag.putString("Id", tag.getString("id"));
        } else if (tag.contains("Id") && !tag.contains("id")) {
            tag.putString("id", tag.getString("Id"));
        }
        return !tag.contains("Id") || !tag.contains("id");
    }

    private static boolean fixPos(CompoundTag tag) {
        if (!tag.contains("Pos") && tag.contains("x") && tag.contains("y") && tag.contains("z")) {
            tag.put("Pos", ListTagAccessor.of(
                    ImmutableList.of(
                            DoubleTag.of(tag.getDouble("x")),
                            DoubleTag.of(tag.getDouble("y")),
                            DoubleTag.of(tag.getDouble("z"))
                    ),
                    (byte) 6
            ));
        } else if (tag.contains("Pos") && !tag.contains("x") && !tag.contains("y") && !tag.contains("z")) {
            tag.put("x", DoubleTag.of(((ListTag) Objects.requireNonNull(tag.get("Pos"))).getDouble(0)));
            tag.put("y", DoubleTag.of(((ListTag) Objects.requireNonNull(tag.get("Pos"))).getDouble(1)));
            tag.put("z", DoubleTag.of(((ListTag) Objects.requireNonNull(tag.get("Pos"))).getDouble(2)));
        }
        return !tag.contains("Pos") || !(tag.contains("x") && tag.contains("y") && tag.contains("z"));
    }

    private static void processPos(ListTag listTag, int originX, int originY, int originZ, CompoundTag tag) {
        double x = listTag.getDouble(0);
        double y = listTag.getDouble(1);
        double z = listTag.getDouble(2);
        tag.remove("Pos");
        tag.put("Pos", ListTagAccessor.of(
                ImmutableList.of(
                        DoubleTag.of(x + originX),
                        DoubleTag.of(y + originY),
                        DoubleTag.of(z + originZ)
                    ),
                (byte) 6
                )
        );
    }
}
