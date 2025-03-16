package com.canoestudio.retrofuturemc.content.blocks.dripLeaf;


import com.canoestudio.retrofuturemc.Utils.IHasModel;
import com.canoestudio.retrofuturemc.content.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.content.items.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static com.canoestudio.retrofuturemc.content.tab.CreativeTab.CREATIVE_TABS;

public class DripleafStem extends BlockBush implements IHasModel, IGrowable
{
    public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;

    public DripleafStem()
    {
        super(Material.VINE);

        setHardness(0.0F);

        setTranslationKey("big_dripleaf_stem");
        setRegistryName("big_dripleaf_stem");
        setCreativeTab(CREATIVE_TABS);
        setSoundType(BigDripleaf.DRIPLEAF);
        setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModels() {  }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return FULL_BLOCK_AABB; }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());

        boolean flag1 = soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);

        boolean flag2 = soil.getBlock() == this;

        boolean flag3 = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);

        return (flag1 || flag2) && flag3;
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        IBlockState top = worldIn.getBlockState(pos.up());
        boolean flag1 = top.getBlock() == this || top.getBlock() == ModBlocks.BIG_DRIPLEAF;

        IBlockState soil = worldIn.getBlockState(pos.down());
        boolean flag2 = soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this)
                || soil.getBlock() == this;

        return flag1 && flag2;
    }

    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) { return false; }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) { return Item.getItemFromBlock(ModBlocks.BIG_DRIPLEAF); }

    @Deprecated // Forge: Use more sensitive version below: getPickBlock
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) { return new ItemStack(ModBlocks.BIG_DRIPLEAF); }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) { return true; }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) { return true; }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        EnumFacing facing = state.getValue(FACING);

        if(worldIn.getBlockState(pos.up()).getBlock() == this)
        {
            this.grow(worldIn, rand, pos.up(), state);
        }
        else if(worldIn.getBlockState(pos.up()).getBlock() == ModBlocks.BIG_DRIPLEAF)
        {
            worldIn.setBlockState(pos.up(), this.getDefaultState().withProperty(FACING, facing), 2);
            worldIn.setBlockState(pos.up(2), ModBlocks.BIG_DRIPLEAF.getDefaultState().withProperty(FACING, facing), 3);
        }
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
}