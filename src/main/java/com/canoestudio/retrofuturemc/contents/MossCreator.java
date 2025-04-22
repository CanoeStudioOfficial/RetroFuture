package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.SmallDripleaf;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.canoestudio.retrofuturemc.sounds.ModSoundHandler;
import net.minecraft.block.*;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.Random;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class MossCreator extends Block implements IGrowable {

    public static final SoundType MOSS = new SoundType(1.0F, 1.0F, ModSoundHandler.BLOCK_MOSS_BREAK, ModSoundHandler.BLOCK_MOSS_STEP, ModSoundHandler.BLOCK_MOSS_PLACE, ModSoundHandler.BLOCK_MOSS_HIT, ModSoundHandler.BLOCK_MOSS_FALL);

    public MossCreator(String name) {
        super(Material.GRASS, Material.GRASS.getMaterialMapColor());

        setHardness(0.1F);
        setResistance(0.1F);
        setHarvestLevel("axe", 0);
        setSoundType(MOSS);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setCreativeTab(CREATIVE_TABS);

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }

    @Deprecated
    public EnumPushReaction getMobilityFlag(IBlockState state) { return EnumPushReaction.DESTROY; }

    @Override
    public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, net.minecraftforge.common.IPlantable plantable) {
        if(super.canSustainPlant(state, world, pos, direction, plantable)) {
            return true;
        }

        EnumPlantType plantType = plantable.getPlantType(world, pos.offset(direction));

        switch(plantType) {
            case Beach:
                boolean hasWater = (world.getBlockState(pos.east()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.west()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.north()).getMaterial() == Material.WATER ||
                        world.getBlockState(pos.south()).getMaterial() == Material.WATER);
                return hasWater;
            case Plains:
                return true;
            case Desert:
                boolean isDeadBush = plantable.getPlant(world, pos.offset(direction)).getBlock() == Blocks.DEADBUSH;
                return isDeadBush;
            default:
                return false;
        }
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) { return true; }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) { return true; }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if(worldIn.isAirBlock(pos.up()))
        {
            for(int i = -3; i <= 3; i++)
            {
                for(int j = -3; j <= 3; j++)
                {
                    BlockPos pos1 = new BlockPos(pos.getX() + i, pos.getY(), pos.getZ() + j);

                    if(rand.nextInt(10) < 7)
                        mossBlock(worldIn, pos1, rand);
                }
            }
        }
    }

    private void mossBlock(World worldIn, BlockPos pos, Random rand)
    {
        if(isMossable(worldIn.getBlockState(pos.up())))
        {
            pos = pos.up();
        }
        else if(worldIn.isAirBlock(pos))
        {
            pos = pos.down();
        }

        if(isMossable(worldIn.getBlockState(pos)) && canPlantMoss(worldIn, pos.up()))
        {
            worldIn.setBlockState(pos, ModBlocks.MOSS_BLOCK.getDefaultState(), 3);
        }

        plantMoss(worldIn, pos, rand);
    }

    private boolean isMossable(IBlockState state)
    {
        Block block = state.getBlock();
        return block == Blocks.STONE || block == ModBlocks.DeepSlate || block == Blocks.MYCELIUM || block == Blocks.DIRT || block == Blocks.GRASS;
    }

    private void plantMoss(World world, BlockPos pos, Random random)
    {
        BlockPos newPos = pos.up();
        if(canPlantMoss(world, newPos))
        {
            IBlockState stateAzalea = ModBlocks.Azalea.getDefaultState();
            IBlockState stateCarpet = ModBlocks.MOSS_CARPET.getDefaultState();
            IBlockState stateGrass = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);
            float chance = random.nextFloat();

            Block block = world.getBlockState(pos).getBlock();
            Boolean flag1 = block instanceof BlockCarpet || block instanceof BlockBush || block instanceof BlockLiquid || block instanceof BlockAir;

            Block block1 = world.getBlockState(pos.up()).getBlock();
            Boolean flag2 = block1 instanceof BlockBush;

            if(chance <= 0.025 && ((AzaleaCreator)ModBlocks.Flowering_Azalea).canBlockStay(world, newPos, stateAzalea) && !flag2)
            {
                world.setBlockState(newPos, stateAzalea, 3);
                return;
            }
            if(chance <= 0.04375 && ((AzaleaCreator)ModBlocks.Azalea).canBlockStay(world, newPos, stateAzalea) && !flag2)
            {
                world.setBlockState(newPos, stateAzalea, 3);
                return;
            }
            if(chance <= 0.0625 && Blocks.TALLGRASS.canBlockStay(world, newPos, stateGrass) && !flag2)
            {
                Blocks.DOUBLE_PLANT.placeAt(world, newPos, BlockDoublePlant.EnumPlantType.GRASS, 3);
                return;
            }
            if(chance <= 0.0715 && Blocks.TALLGRASS.canBlockStay(world, newPos, stateGrass) && !flag2)
            {
                ((SmallDripleaf)ModBlocks.SMALL_DRIPLEAF).placeAt(world, newPos, EnumFacing.byHorizontalIndex(random.nextInt(4)), 3);
                return;
            }
            if(chance <= 0.15625 && world.isAirBlock(newPos) && !flag1 && !flag2)
            {
                world.setBlockState(newPos, stateCarpet, 3);
                return;
            }
            if(chance <= 0.3125 && Blocks.TALLGRASS.canBlockStay(world, newPos, stateGrass) && !flag2)
            {
                world.setBlockState(newPos, stateGrass, 3);
            }
        }
    }

    private boolean canPlantMoss(World world, BlockPos pos)
    {
        return world.isAirBlock(pos) || world.getBlockState(pos).getBlock().isReplaceable(world, pos) || world.getBlockState(pos).getBlock() instanceof BlockBush;
    }
}

