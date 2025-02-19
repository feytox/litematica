package fi.dy.masa.litematica.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.world.ChunkSchematic;
import fi.dy.masa.litematica.world.SchematicWorldHandler;
import fi.dy.masa.litematica.world.WorldSchematic;
import fi.dy.masa.malilib.interfaces.IRangeChangeListener;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public class SchematicWorldRefresher implements IRangeChangeListener
{
    public static final SchematicWorldRefresher INSTANCE = new SchematicWorldRefresher();

    private final MinecraftClient mc = MinecraftClient.getInstance();

    @Override
    public void updateAll()
    {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();

        if (world != null)
        {
            DataManager.getSchematicPlacementManager().setVisibleSubChunksNeedsUpdate();
            final int minY = world.getBottomY();
            final int maxY = world.getTopY() - 1;
            this.updateBetweenY(minY, maxY);
        }
    }

    @Override
    public void updateBetweenX(int minX, int maxX)
    {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();

        if (world != null && this.mc.world != null)
        {
            DataManager.getSchematicPlacementManager().setVisibleSubChunksNeedsUpdate();
            final int xMin = Math.min(minX, maxX);
            final int xMax = Math.max(minX, maxX);
            final int cxMin = (xMin >> 4);
            final int cxMax = (xMax >> 4);
            final int minY = world.getBottomY();
            final int maxY = world.getTopY() - 1;
            Long2ObjectMap<ChunkSchematic> schematicChunks = world.getChunkProvider().getLoadedChunks();

            for (ChunkSchematic chunk : schematicChunks.values())
            {
                ChunkPos pos = chunk.getPos();

                // Only mark chunks that are actually rendered (if the schematic world contains more chunks)
                if (pos.x >= cxMin && pos.x <= cxMax && chunk.isEmpty() == false &&
                    WorldUtils.isClientChunkLoaded(this.mc.world, pos.x, pos.z))
                {
                    minX = Math.max( pos.x << 4      , xMin);
                    maxX = Math.min((pos.x << 4) + 15, xMax);
                    world.scheduleChunkRenders( minX, minY, (pos.z << 4)     ,
                                                maxX, maxY, (pos.z << 4) + 15);
                }
            }
        }
    }

    @Override
    public void updateBetweenY(int minY, int maxY)
    {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();

        if (world != null && this.mc.world != null)
        {
            DataManager.getSchematicPlacementManager().setVisibleSubChunksNeedsUpdate();
            Long2ObjectMap<ChunkSchematic> schematicChunks = world.getChunkProvider().getLoadedChunks();

            for (ChunkSchematic chunk : schematicChunks.values())
            {
                ChunkPos pos = chunk.getPos();

                // Only mark chunks that are actually rendered (if the schematic world contains more chunks)
                if (chunk.isEmpty() == false && WorldUtils.isClientChunkLoaded(this.mc.world, pos.x, pos.z))
                {
                    world.scheduleChunkRenders((pos.x << 4)     , minY, (pos.z << 4)     ,
                                               (pos.x << 4) + 15, maxY, (pos.z << 4) + 15);
                }
            }
        }
    }

    @Override
    public void updateBetweenZ(int minZ, int maxZ)
    {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();

        if (world != null && this.mc.world != null)
        {
            DataManager.getSchematicPlacementManager().setVisibleSubChunksNeedsUpdate();
            final int zMin = Math.min(minZ, maxZ);
            final int zMax = Math.max(minZ, maxZ);
            final int czMin = (zMin >> 4);
            final int czMax = (zMax >> 4);
            final int minY = world.getBottomY();
            final int maxY = world.getTopY() - 1;
            Long2ObjectMap<ChunkSchematic> schematicChunks = world.getChunkProvider().getLoadedChunks();

            for (ChunkSchematic chunk : schematicChunks.values())
            {
                ChunkPos pos = chunk.getPos();

                // Only mark chunks that are actually rendered (if the schematic world contains more chunks)
                if (pos.z >= czMin && pos.z <= czMax && chunk.isEmpty() == false &&
                    WorldUtils.isClientChunkLoaded(this.mc.world, pos.x, pos.z))
                {
                    minZ = Math.max( pos.z << 4      , zMin);
                    maxZ = Math.min((pos.z << 4) + 15, zMax);
                    world.scheduleChunkRenders((pos.x << 4)     , minY, minZ,
                                               (pos.x << 4) + 15, maxY, maxZ);
                }
            }
        }
    }

    public void markSchematicChunksForRenderUpdate(int chunkX, int chunkZ)
    {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();

        if (world != null && this.mc.world != null)
        {
            if (world.getChunkProvider().isChunkLoaded(chunkX, chunkZ) &&
                WorldUtils.isClientChunkLoaded(this.mc.world, chunkX, chunkZ))
            {
                world.scheduleChunkRenders(chunkX, chunkZ);
            }
        }
    }

    public void markSchematicChunksForRenderUpdate(int chunkX, int chunkY, int chunkZ)
    {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();

        if (world != null && this.mc.world != null)
        {
            if (world.getChunkProvider().isChunkLoaded(chunkX, chunkZ) &&
                WorldUtils.isClientChunkLoaded(this.mc.world, chunkX, chunkZ))
            {
                world.scheduleBlockRenders(chunkX, chunkY, chunkZ);
            }
        }
    }

    public void markSchematicChunkForRenderUpdate(BlockPos pos)
    {
        WorldSchematic world = SchematicWorldHandler.getSchematicWorld();

        if (world != null && this.mc.world != null)
        {
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;

            if (world.getChunkProvider().isChunkLoaded(chunkX, chunkZ) &&
                WorldUtils.isClientChunkLoaded(this.mc.world, chunkX, chunkZ))
            {
                world.scheduleBlockRenders(chunkX, pos.getY() >> 4, chunkZ);
            }
        }
    }
}
