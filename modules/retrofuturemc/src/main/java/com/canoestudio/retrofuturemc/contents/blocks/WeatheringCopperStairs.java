package com.canoestudio.retrofuturemc.contents.blocks;

import com.canoestudio.retrofuturemc.contents.StairsCreator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WeatheringCopperStairs extends StairsCreator {
    public WeatheringCopperStairs(String name, IBlockState modelState) {
        super(name, modelState, "pickaxe", 0);
        setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        CopperBehavior.tryWeather(worldIn, pos, state, rand);
    }
}
