package com.canoestudio.retrofuturemc.contents.blocks;

import com.canoestudio.retrofuturemc.contents.SimpleBlockCreator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class WeatheringCopperBlock extends SimpleBlockCreator {
    public WeatheringCopperBlock(String name) {
        super(name, Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
        setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        CopperBehavior.tryWeather(worldIn, pos, state, rand);
    }
}
