package com.canoestudio.retrofuturemc.contents.blocks.dripLeaf;


import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import git.jbredwards.fluidlogged_api.api.block.IFluidloggable;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import git.jbredwards.fluidlogged_api.api.world.IWorldProvider;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import java.util.Random;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class DripleafStem extends BlockBush implements IGrowable, IFluidloggable
{
    public static final String name = "Big_Dripleaf_Stem";
    public static final PropertyEnum<EnumFacing> FACING = BlockHorizontal.FACING;

    public DripleafStem()
    {
        super(Material.VINE);

        setHardness(0.0F);

        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name);
        setCreativeTab(CREATIVE_TABS);
        setSoundType(BigDripleaf.DRIPLEAF);
        setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.SOUTH));

        ModBlocks.BLOCKS.add(this);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) { return FULL_BLOCK_AABB; }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState soil = worldIn.getBlockState(pos.down());

        boolean flag1 = soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);

        boolean flag2 = soil.getBlock() == this;

        boolean flag3 = worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos);

        FluidState fluidState = FluidloggedUtils.getFluidState(worldIn, pos);
        boolean canPlaceFluid = fluidState.isEmpty() || isFluidValid(getDefaultState(), worldIn, pos, fluidState.getFluid());

        return (flag1 || flag2) && flag3 && canPlaceFluid;
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

    @Override
    public boolean isFluidValid(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Fluid fluid) {
        return FluidloggedUtils.isCompatibleFluid(FluidRegistry.WATER, fluid);
    }

    @Override
    public boolean isFluidloggable(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull FluidState fluidState) {
        if (fluidState.isEmpty()) return true;
        return isFluidValid(state, IWorldProvider.getWorld(world), pos, fluidState.getFluid())
                && (fluidState.isSource() || fluidState.getActualHeight(world, pos) >= 1 && FluidloggedUtils.canCreateSource(fluidState.getState(), IWorldProvider.getWorld(world), pos));
    }

    @Nonnull
    @Override
    public EnumActionResult onFluidFill(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState here, @Nonnull FluidState newFluid, int blockFlags) {
        if (!newFluid.isSource()) {
            if (newFluid.getActualHeight(world, pos) < 1) {
                world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, getStateId(here));
                dropBlockAsItem(world, pos, here, 0);
                world.setBlockState(pos, newFluid.getState(), blockFlags);
                return EnumActionResult.SUCCESS;
            }
            else if (FluidloggedUtils.canCreateSource(newFluid.getState(), world, pos)
                    && FluidloggedUtils.setFluidState(world, pos, here, newFluid.toSource(), false)) {
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    @Nonnull
    @Override
    public EnumActionResult onFluidDrain(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState here, int blockFlags) {
        world.playEvent(Constants.WorldEvents.BREAK_BLOCK_EFFECTS, pos, getStateId(here));
        dropBlockAsItem(world, pos, here, 0);
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), blockFlags);
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean canFluidFlow(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState here, @Nonnull EnumFacing side) {
        return here.getBlockFaceShape(world, pos, side) != BlockFaceShape.SOLID;
    }

    @Override
    public boolean overrideApplyDefaultsSetting() { return true; }

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