package com.canoestudio.retrofuturemc.contents.blocks.HangingRoots;


import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import git.jbredwards.fluidlogged_api.api.block.IFluidloggable;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fluids.FluidRegistry;


import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class HangingRoots extends Block implements IFluidloggable, IPlantable {

    protected static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.125D, 0.625D, 0.125D, 0.875D, 1.0D, 0.875D);

    public HangingRoots(String name) {
        super(Material.PLANTS);
        setSoundType(SoundType.PLANT);
        setCreativeTab(CreativeTabs.DECORATIONS);
        setHardness(0.0F);
        setRegistryName(name);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setCreativeTab(CREATIVE_TABS);
    }



    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        // 检查上方方块是否可附着
        if(!worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos.up(), EnumFacing.DOWN)) {
            return false;
        }

        // 使用Fluidlogged API检查是否可以放置
        final FluidState fluidState = FluidloggedUtils.getFluidState(worldIn, pos);
        return fluidState.isFluidloggable()
                && isFluidloggable(getDefaultState(), worldIn, pos, fluidState)
                && super.canPlaceBlockAt(worldIn, pos);
    }

    @Override
    public boolean isFluidValid(IBlockState state, World world, BlockPos pos, net.minecraftforge.fluids.Fluid fluid) {
        // 只允许水
        return fluid == FluidRegistry.WATER;
    }

    @Override
    public boolean isFluidloggable(IBlockState state, IBlockAccess world, BlockPos pos, FluidState fluidState) {
        // 允许在空气或水中放置
        return fluidState.isEmpty() || isFluidValid(state, (World) world, pos, fluidState.getFluid());
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(worldIn, pos)) {
            worldIn.setBlockToAir(pos);
        }
    }

    protected boolean canBlockStay(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).isSideSolid(worldIn, pos.up(), EnumFacing.DOWN);
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Cave;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return this.getDefaultState();
    }

    @Override
    public boolean overrideApplyDefaultsSetting() {
        return true;
    }
}