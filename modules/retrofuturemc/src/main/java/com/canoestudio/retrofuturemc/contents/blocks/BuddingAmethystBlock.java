package com.canoestudio.retrofuturemc.contents.blocks;

import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.Random;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class BuddingAmethystBlock extends net.minecraft.block.Block {
    private static final int GROWTH_CHANCE = 5;

    public BuddingAmethystBlock() {
        super(Material.ROCK);
        setTranslationKey(Tags.MOD_ID + ".budding_amethyst");
        setRegistryName("budding_amethyst");
        setHardness(1.5F);
        setResistance(3.0F);
        setHarvestLevel("pickaxe", 0);
        setSoundType(SoundType.GLASS);
        setCreativeTab(CREATIVE_TABS);
        setTickRandomly(true);

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName("budding_amethyst"));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (worldIn.isRemote || rand.nextInt(GROWTH_CHANCE) != 0) {
            return;
        }

        EnumFacing growDirection = EnumFacing.values()[rand.nextInt(EnumFacing.values().length)];
        BlockPos growPos = pos.offset(growDirection);
        IBlockState target = worldIn.getBlockState(growPos);
        net.minecraft.block.Block nextStage = null;

        if (canClusterGrowAtState(worldIn, growPos, target)) {
            nextStage = ModBlocks.SMALL_AMETHYST_BUD;
        } else if (target.getBlock() == ModBlocks.SMALL_AMETHYST_BUD && target.getValue(AmethystClusterBlock.FACING) == growDirection) {
            nextStage = ModBlocks.MEDIUM_AMETHYST_BUD;
        } else if (target.getBlock() == ModBlocks.MEDIUM_AMETHYST_BUD && target.getValue(AmethystClusterBlock.FACING) == growDirection) {
            nextStage = ModBlocks.LARGE_AMETHYST_BUD;
        } else if (target.getBlock() == ModBlocks.LARGE_AMETHYST_BUD && target.getValue(AmethystClusterBlock.FACING) == growDirection) {
            nextStage = ModBlocks.AMETHYST_CLUSTER;
        }

        if (nextStage != null) {
            IBlockState newState = nextStage.getDefaultState().withProperty(AmethystClusterBlock.FACING, growDirection);
            FluidState fluidState = getFluidState(worldIn, growPos, target);
            worldIn.setBlockState(growPos, newState, 3);
            if (fluidState.getFluid() == FluidRegistry.WATER) {
                FluidloggedUtils.setFluidState(worldIn, growPos, worldIn.getBlockState(growPos), fluidState, false, 3);
            }
        }
    }

    private static boolean canClusterGrowAtState(World world, BlockPos pos, IBlockState state) {
        return state.getBlock() == Blocks.AIR || state.getMaterial() == Material.WATER || FluidloggedUtils.getFluidState(world, pos, state).getFluid() == FluidRegistry.WATER;
    }

    private static FluidState getFluidState(World world, BlockPos pos, IBlockState state) {
        return state.getMaterial() == Material.WATER ? FluidState.of(state) : FluidloggedUtils.getFluidState(world, pos, state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return false;
    }
}
