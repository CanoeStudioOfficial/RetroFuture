//package com.canoestudio.retrofuturemc.contents.world.gen.lushcaves;
//
//import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
//import com.canoestudio.retrofuturemc.contents.world.CaveGenerator;
//import com.canoestudio.retrofuturemc.noise.OpenSimplexNoise;
//import net.minecraft.block.Block;
//import net.minecraft.init.Blocks;
//import net.minecraft.world.World;
//import net.minecraft.world.chunk.ChunkPrimer;
//
//import java.util.Random;
//
//public class LushCaveGen implements CaveGenerator {
//    private static final float NOISE_SCALE = 0.04F;
//    private static final float NOISE_THRESHOLD = 0.3F;
//    private static final float WILD_ROOTS_CHANCE = 0.2F;
//    private static final int MIN_Y = 10;
//    private static final int MAX_Y = 54;
//    private static final int CHUNK_SIZE = 16;
//
//    public void generate(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
//        this.generateLushCaveGen(world, chunkX, chunkZ, primer);
//    }
//
//    private void generateLushCaveGen(World world, int chunkX, int chunkZ, ChunkPrimer primer) {
//        Random rand = new Random((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L ^ world.getSeed());
//        long seed = world.getSeed();
//        int baseX = chunkX * 16;
//        int baseZ = chunkZ * 16;
//        this.generateCaveHalls(primer, seed, baseX, baseZ);
//        this.generateWildRoots(primer, rand);
//    }
//
//    private void generateCaveHalls(ChunkPrimer primer, long seed, int baseX, int baseZ) {
//        for(int x = 0; x < 16; ++x) {
//            for(int z = 0; z < 16; ++z) {
//                for(int y = 10; y < 54; ++y) {
//                    double nx = (double)((float)(baseX + x) * 0.04F);
//                    double ny = (double)((float)y * 0.04F);
//                    double nz = (double)((float)(baseZ + z) * 0.04F);
//                    float noise = OpenSimplexNoise.noise3_ImproveXY(seed, nx, ny, nz);
//                    if (noise > 0.3F) {
//                        primer.setBlockState(x, y, z, Blocks.setBlockState.getDefaultState());
//                    }
//                }
//            }
//        }
//
//    }
//
//    private void generateWildRoots(ChunkPrimer primer, Random rand) {
//        for(int x = 0; x < 16; ++x) {
//            for(int z = 0; z < 16; ++z) {
//                for(int y = 70; y >= 10; --y) {
//                    Block current = primer.getBlockState(x, y, z).getBlock();
//                    Block below = primer.getBlockState(x, y - 1, z).getBlock();
//                    if (current != Blocks.AIR && below == Blocks.AIR && rand.nextFloat() < 0.2F) {
//                        int maxLength = this.calculateMaxRootLength(primer, x, z, y);
//                        if (maxLength <= 0) {
//                            break;
//                        }
//
//                        int rootLength = 1 + rand.nextInt(Math.max(1, maxLength / 2));
//
//                        for(int i = 1; i <= rootLength && i <= maxLength; ++i) {
//                            primer.setBlockState(x, y - i, z, ModBlocks.CAVE_VINE_PLANT.getDefaultState());
//                        }
//                        break;
//                    }
//                }
//            }
//        }
//
//    }
//
//    private int calculateMaxRootLength(ChunkPrimer primer, int x, int z, int startY) {
//        int maxLength = 0;
//
//        for(int dy = startY - 1; dy > 5 && primer.setBlockState(x, dy, z).getBlock() == Blocks.AIR; --dy) {
//            ++maxLength;
//        }
//
//        return maxLength;
//    }
//
//}
