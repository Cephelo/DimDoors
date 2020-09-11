package org.dimdev.dimcore.schematic.v2;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;

@SuppressWarnings("CodeBlock2Expr")
public class Schematic {
    public static final Codec<Schematic> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                Codec.INT.fieldOf("Version").forGetter(Schematic::getVersion),
                Codec.INT.fieldOf("Data Version").forGetter(Schematic::getDataVersion),
                SchematicMetadata.CODEC.optionalFieldOf("Metadata", SchematicMetadata.EMPTY).forGetter(Schematic::getMetadata),
                Codec.SHORT.fieldOf("Width").forGetter(Schematic::getWidth),
                Codec.SHORT.fieldOf("Height").forGetter(Schematic::getHeight),
                Codec.SHORT.fieldOf("Length").forGetter(Schematic::getLength),
                Vec3i.field_25123.fieldOf("Offset").forGetter(Schematic::getOffset),
                Codec.INT.fieldOf("PaletteMax").forGetter(Schematic::getPaletteMax),
                SchematicBlockPalette.CODEC.fieldOf("Palette").forGetter(Schematic::getBlockPalette),
                Codec.BYTE_BUFFER.fieldOf("BlockData").forGetter(Schematic::getBlockData),
                Codec.list(CompoundTag.CODEC).fieldOf("BlockEntities").forGetter(Schematic::getBlockEntities),
                Codec.list(CompoundTag.CODEC).fieldOf("Entities").forGetter(Schematic::getEntities)
                ).apply(instance, Schematic::new);
    });

    private final int version;
    private final int dataVersion;
    private final SchematicMetadata metadata;
    private final short width;
    private final short height;
    private final short length;
    private final Vec3i offset;
    private final int paletteMax;
    private final Map<BlockState, Integer> blockPalette;
    private final ByteBuffer blockData;
    private final List<CompoundTag> blockEntities;
    private final List<CompoundTag> entities;

    public Schematic(int version, int dataVersion, SchematicMetadata metadata, short width, short height, short length, Vec3i offset, int paletteMax, Map<BlockState, Integer> blockPalette, ByteBuffer blockData, List<CompoundTag> blockEntities, List<CompoundTag> entities) {
        this.version = version;
        this.dataVersion = dataVersion;
        this.metadata = metadata;
        this.width = width;
        this.height = height;
        this.length = length;
        this.offset = offset;
        this.paletteMax = paletteMax;
        this.blockPalette = blockPalette;
        this.blockData = blockData;
        this.blockEntities = blockEntities;
        this.entities = entities;
    }

    public int getVersion() {
        return this.version;
    }

    public int getDataVersion() {
        return this.dataVersion;
    }

    public SchematicMetadata getMetadata() {
        return this.metadata;
    }

    public short getWidth() {
        return this.width;
    }

    public short getHeight() {
        return this.height;
    }

    public short getLength() {
        return this.length;
    }

    public Vec3i getOffset() {
        return this.offset;
    }

    public int getPaletteMax() {
        return this.paletteMax;
    }

    public Map<BlockState, Integer> getBlockPalette() {
        return this.blockPalette;
    }

    public ByteBuffer getBlockData() {
        return this.blockData;
    }

    public List<CompoundTag> getBlockEntities() {
        return this.blockEntities;
    }

    public List<CompoundTag> getEntities() {
        return this.entities;
    }

    public static SchematicBlockSample blockSample(Schematic schem) {
        return new SchematicBlockSample(schem);
    }

    public static SchematicBlockSample blockSample(Schematic schem, StructureWorldAccess world) {
        return blockSample(schem).withWorld(world);
    }
}
