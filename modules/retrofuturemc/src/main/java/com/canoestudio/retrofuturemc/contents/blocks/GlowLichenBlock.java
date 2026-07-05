package com.canoestudio.retrofuturemc.contents.blocks;

import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import git.jbredwards.fluidlogged_api.api.block.IFluidloggable;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import git.jbredwards.fluidlogged_api.api.world.IWorldProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class GlowLichenBlock extends Block implements IFluidloggable {
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");

    private static final AxisAlignedBB FULL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    public GlowLichenBlock() {
        super(Material.VINE);
        setTranslationKey(Tags.MOD_ID + ".glow_lichen");
        setRegistryName("glow_lichen");
        setHardness(0.2F);
        setResistance(0.2F);
        setSoundType(SoundType.PLANT);
        setCreativeTab(CREATIVE_TABS);
        setLightLevel(7.0F / 15.0F);
        setDefaultState(blockState.getBaseState()
                .withProperty(UP, false)
                .withProperty(DOWN, false)
                .withProperty(NORTH, true)
                .withProperty(EAST, false)
                .withProperty(SOUTH, false)
                .withProperty(WEST, false));

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName("glow_lichen"));
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getStateForFace(facing);
    }

    private IBlockState getStateForFace(EnumFacing face) {
        return getDefaultState()
                .withProperty(UP, face == EnumFacing.UP)
                .withProperty(DOWN, face == EnumFacing.DOWN)
                .withProperty(NORTH, face == EnumFacing.NORTH)
                .withProperty(EAST, face == EnumFacing.EAST)
                .withProperty(SOUTH, face == EnumFacing.SOUTH)
                .withProperty(WEST, face == EnumFacing.WEST);
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return canAttach(worldIn, pos, side);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (canAttach(worldIn, pos, facing)) {
                return true;
            }
        }
        return false;
    }

    private boolean canAttach(World world, BlockPos pos, EnumFacing face) {
        BlockPos supportPos = pos.offset(face.getOpposite());
        return world.getBlockState(supportPos).isSideSolid(world, supportPos, face);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        IBlockState updated = removeUnsupportedFaces(worldIn, pos, state);
        if (!hasAnyFace(updated)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            restoreFluidOrAir(worldIn, pos, state, 3);
            return;
        }
        if (updated != state) {
            worldIn.setBlockState(pos, updated, 2);
        }
        scheduleContainedFluidTick(worldIn, pos, updated);
    }

    private IBlockState removeUnsupportedFaces(World world, BlockPos pos, IBlockState state) {
        IBlockState updated = state;
        for (EnumFacing face : EnumFacing.values()) {
            if (hasFace(updated, face) && !canAttach(world, pos, face)) {
                updated = setFace(updated, face, false);
            }
        }
        return updated;
    }

    private boolean hasAnyFace(IBlockState state) {
        for (EnumFacing face : EnumFacing.values()) {
            if (hasFace(state, face)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFace(IBlockState state, EnumFacing face) {
        return state.getValue(propertyFor(face));
    }

    private IBlockState setFace(IBlockState state, EnumFacing face, boolean value) {
        return state.withProperty(propertyFor(face), value);
    }

    private PropertyBool propertyFor(EnumFacing face) {
        switch (face) {
            case UP:
                return UP;
            case DOWN:
                return DOWN;
            case EAST:
                return EAST;
            case SOUTH:
                return SOUTH;
            case WEST:
                return WEST;
            case NORTH:
            default:
                return NORTH;
        }
    }

    private void restoreFluidOrAir(World world, BlockPos pos, IBlockState state, int flags) {
        FluidState fluidState = FluidloggedUtils.getFluidState(world, pos, state);
        world.setBlockState(pos, fluidState.getFluid() == FluidRegistry.WATER ? fluidState.getState() : net.minecraft.init.Blocks.AIR.getDefaultState(), flags);
    }

    private void scheduleContainedFluidTick(World world, BlockPos pos, IBlockState state) {
        FluidState fluidState = FluidloggedUtils.getFluidState(world, pos, state);
        if (fluidState.getFluid() == FluidRegistry.WATER) {
            world.scheduleUpdate(pos, fluidState.getState().getBlock(), fluidState.getState().getBlock().tickRate(world));
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_AABB;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFluidValid(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Fluid fluid) {
        return FluidloggedUtils.isCompatibleFluid(FluidRegistry.WATER, fluid);
    }

    @Override
    public boolean isFluidloggable(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull FluidState fluidState) {
        return fluidState.isEmpty() || fluidState.isFluidloggable() && isFluidValid(state, IWorldProvider.getWorld(world), pos, fluidState.getFluid());
    }

    @Nonnull
    @Override
    public EnumActionResult onFluidFill(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState here, @Nonnull FluidState newFluid, int blockFlags) {
        return EnumActionResult.PASS;
    }

    @Nonnull
    @Override
    public EnumActionResult onFluidDrain(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState here, int blockFlags) {
        return EnumActionResult.PASS;
    }

    @Override
    public boolean canFluidFlow(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState here, @Nonnull EnumFacing side) {
        return true;
    }

    @Override
    public boolean canFluidConnect(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull IBlockState here, @Nonnull EnumFacing side) {
        return true;
    }

    @Override
    public boolean overrideApplyDefaultsSetting() {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {UP, DOWN, NORTH, EAST, SOUTH, WEST});
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        for (EnumFacing face : EnumFacing.values()) {
            if (hasFace(state, face)) {
                return face.getIndex();
            }
        }
        return EnumFacing.NORTH.getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getStateForFace(EnumFacing.byIndex(meta & 7));
    }
}
