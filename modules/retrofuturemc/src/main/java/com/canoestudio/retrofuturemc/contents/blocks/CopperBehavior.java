package com.canoestudio.retrofuturemc.contents.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class CopperBehavior {
    private static final float BASE_CHANCE = 0.05688889F;
    private static final Map<Block, CopperInfo> INFO = new HashMap<>();

    private CopperBehavior() {}

    public static void init() {
        registerChain(ModBlocks.COPPER_BLOCK, ModBlocks.EXPOSED_COPPER, ModBlocks.WEATHERED_COPPER, ModBlocks.OXIDIZED_COPPER);
        registerChain(ModBlocks.CUT_COPPER, ModBlocks.EXPOSED_CUT_COPPER, ModBlocks.WEATHERED_CUT_COPPER, ModBlocks.OXIDIZED_CUT_COPPER);
        registerChain(ModBlocks.CUT_COPPER_STAIRS, ModBlocks.EXPOSED_CUT_COPPER_STAIRS, ModBlocks.WEATHERED_CUT_COPPER_STAIRS, ModBlocks.OXIDIZED_CUT_COPPER_STAIRS);
        registerChain(ModBlocks.CUT_COPPER_SLAB, ModBlocks.EXPOSED_CUT_COPPER_SLAB, ModBlocks.WEATHERED_CUT_COPPER_SLAB, ModBlocks.OXIDIZED_CUT_COPPER_SLAB);
        registerChain(ModBlocks.DOUBLE_CUT_COPPER_SLAB, ModBlocks.DOUBLE_EXPOSED_CUT_COPPER_SLAB, ModBlocks.DOUBLE_WEATHERED_CUT_COPPER_SLAB, ModBlocks.DOUBLE_OXIDIZED_CUT_COPPER_SLAB);

        registerWaxed(ModBlocks.WAXED_COPPER_BLOCK, ModBlocks.COPPER_BLOCK, 0);
        registerWaxed(ModBlocks.WAXED_EXPOSED_COPPER, ModBlocks.EXPOSED_COPPER, 1);
        registerWaxed(ModBlocks.WAXED_WEATHERED_COPPER, ModBlocks.WEATHERED_COPPER, 2);
        registerWaxed(ModBlocks.WAXED_OXIDIZED_COPPER, ModBlocks.OXIDIZED_COPPER, 3);
        registerWaxed(ModBlocks.WAXED_CUT_COPPER, ModBlocks.CUT_COPPER, 0);
        registerWaxed(ModBlocks.WAXED_EXPOSED_CUT_COPPER, ModBlocks.EXPOSED_CUT_COPPER, 1);
        registerWaxed(ModBlocks.WAXED_WEATHERED_CUT_COPPER, ModBlocks.WEATHERED_CUT_COPPER, 2);
        registerWaxed(ModBlocks.WAXED_OXIDIZED_CUT_COPPER, ModBlocks.OXIDIZED_CUT_COPPER, 3);
        registerWaxed(ModBlocks.WAXED_CUT_COPPER_STAIRS, ModBlocks.CUT_COPPER_STAIRS, 0);
        registerWaxed(ModBlocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, ModBlocks.EXPOSED_CUT_COPPER_STAIRS, 1);
        registerWaxed(ModBlocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, ModBlocks.WEATHERED_CUT_COPPER_STAIRS, 2);
        registerWaxed(ModBlocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, ModBlocks.OXIDIZED_CUT_COPPER_STAIRS, 3);
        registerWaxed(ModBlocks.WAXED_CUT_COPPER_SLAB, ModBlocks.CUT_COPPER_SLAB, 0);
        registerWaxed(ModBlocks.WAXED_EXPOSED_CUT_COPPER_SLAB, ModBlocks.EXPOSED_CUT_COPPER_SLAB, 1);
        registerWaxed(ModBlocks.WAXED_WEATHERED_CUT_COPPER_SLAB, ModBlocks.WEATHERED_CUT_COPPER_SLAB, 2);
        registerWaxed(ModBlocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, ModBlocks.OXIDIZED_CUT_COPPER_SLAB, 3);
        registerWaxed(ModBlocks.DOUBLE_WAXED_CUT_COPPER_SLAB, ModBlocks.DOUBLE_CUT_COPPER_SLAB, 0);
        registerWaxed(ModBlocks.DOUBLE_WAXED_EXPOSED_CUT_COPPER_SLAB, ModBlocks.DOUBLE_EXPOSED_CUT_COPPER_SLAB, 1);
        registerWaxed(ModBlocks.DOUBLE_WAXED_WEATHERED_CUT_COPPER_SLAB, ModBlocks.DOUBLE_WEATHERED_CUT_COPPER_SLAB, 2);
        registerWaxed(ModBlocks.DOUBLE_WAXED_OXIDIZED_CUT_COPPER_SLAB, ModBlocks.DOUBLE_OXIDIZED_CUT_COPPER_SLAB, 3);
    }

    private static void registerChain(Block unaffected, Block exposed, Block weathered, Block oxidized) {
        register(unaffected, 0, false, exposed, null, null);
        register(exposed, 1, false, weathered, unaffected, null);
        register(weathered, 2, false, oxidized, exposed, null);
        register(oxidized, 3, false, null, weathered, null);
    }

    private static void registerWaxed(Block waxed, Block unwaxed, int age) {
        CopperInfo unwaxedInfo = info(unwaxed);
        INFO.put(waxed, new CopperInfo(age, true, null, unwaxed, unwaxed));
        if (unwaxedInfo != null) {
            unwaxedInfo.waxedBlock = waxed;
        }
    }

    private static void register(Block block, int age, boolean waxed, Block next, Block previous, Block waxedBlock) {
        INFO.put(block, new CopperInfo(age, waxed, next, previous, waxedBlock));
    }

    public static boolean isCopper(Block block) {
        return info(block) != null;
    }

    public static boolean canWax(IBlockState state) {
        CopperInfo info = info(state.getBlock());
        return info != null && !info.waxed && info.waxedBlock() != null;
    }

    public static boolean wax(World world, BlockPos pos, IBlockState state) {
        CopperInfo info = info(state.getBlock());
        if (info == null || info.waxed || info.waxedBlock() == null) {
            return false;
        }

        world.setBlockState(pos, copyCompatibleProperties(state, info.waxedBlock().getDefaultState()), 3);
        return true;
    }

    public static boolean canScrape(IBlockState state) {
        CopperInfo info = info(state.getBlock());
        return info != null && (info.waxed || info.previous != null);
    }

    public static boolean scrape(World world, BlockPos pos, IBlockState state) {
        CopperInfo info = info(state.getBlock());
        if (info == null) {
            return false;
        }

        Block result = info.waxed ? info.unwaxed : info.previous;
        if (result == null) {
            return false;
        }

        world.setBlockState(pos, copyCompatibleProperties(state, result.getDefaultState()), 3);
        return true;
    }

    public static void tryWeather(World world, BlockPos pos, IBlockState state, Random rand) {
        CopperInfo info = info(state.getBlock());
        if (world.isRemote || info == null || info.waxed || info.next == null || rand.nextFloat() >= BASE_CHANCE) {
            return;
        }

        int sameAge = 0;
        int older = 0;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -4; dy <= 4; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    int distance = Math.abs(dx) + Math.abs(dy) + Math.abs(dz);
                    if (distance == 0 || distance > 4) {
                        continue;
                    }

                    mutable.setPos(pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz);
                    CopperInfo other = info(world.getBlockState(mutable).getBlock());
                    if (other == null || other.waxed) {
                        continue;
                    }

                    if (other.age < info.age) {
                        return;
                    }
                    if (other.age > info.age) {
                        older++;
                    } else {
                        sameAge++;
                    }
                }
            }
        }

        float localChance = (older + 1.0F) / (older + sameAge + 1.0F);
        float ageModifier = info.age == 0 ? 0.75F : 1.0F;
        if (rand.nextFloat() < localChance * localChance * ageModifier) {
            world.setBlockState(pos, copyCompatibleProperties(state, info.next.getDefaultState()), 3);
        }
    }

    private static IBlockState copyCompatibleProperties(IBlockState from, IBlockState to) {
        IBlockState result = to;
        for (net.minecraft.block.properties.IProperty<?> property : from.getPropertyKeys()) {
            if (result.getPropertyKeys().contains(property)) {
                result = copyProperty(from, result, property);
            }
        }

        return result;
    }

    private static <T extends Comparable<T>> IBlockState copyProperty(IBlockState from, IBlockState to, net.minecraft.block.properties.IProperty<T> property) {
        return to.withProperty(property, from.getValue(property));
    }

    private static CopperInfo info(Block block) {
        if (INFO.isEmpty()) {
            init();
        }

        return INFO.get(block);
    }

    private static class CopperInfo {
        final int age;
        final boolean waxed;
        final Block next;
        final Block previous;
        final Block unwaxed;
        Block waxedBlock;

        CopperInfo(int age, boolean waxed, Block next, Block previous, Block unwaxed) {
            this.age = age;
            this.waxed = waxed;
            this.next = next;
            this.previous = previous;
            this.unwaxed = unwaxed;
        }

        Block waxedBlock() {
            return waxedBlock;
        }
    }
}
