package com.canoestudio.retrofuturemc.contents.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public interface CaveGenerator {
    void generate(World var1, int var2, int var3, ChunkPrimer var4);
}
