package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockSculkVein extends Block {

    public static final PropertyBool DOWN = PropertyBool.create("down");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool UP = PropertyBool.create("up");
    public static final PropertyBool WEST = PropertyBool.create("west");

    private static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
    private static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(0.9375, 0.0, 0.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.0625);
    private static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(0.0, 0.0, 0.9375, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB UP_AABB = new AxisAlignedBB(0.0, 0.9375, 0.0, 1.0, 1.0, 1.0);
    private static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0625, 1.0, 1.0);

    public BlockSculkVein() {
        super(Material.VINE);
        this.setRegistryName(RTWU.ID, "sculk_vein");
        this.setTranslationKey(RTWU.ID + ".sculk_vein");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(0.2f);
        this.setSoundType(SoundType.SLIME);
        this.setLightOpacity(0);
        this.setDefaultState(this.blockState.getBaseState()
            .withProperty(DOWN, false)
            .withProperty(EAST, false)
            .withProperty(NORTH, false)
            .withProperty(SOUTH, false)
            .withProperty(UP, false)
            .withProperty(WEST, false));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        int faces = 0;
        AxisAlignedBB shape = FULL_BLOCK_AABB;
        if (state.getValue(DOWN)) {
            shape = DOWN_AABB;
            faces++;
        }
        if (state.getValue(EAST)) {
            shape = EAST_AABB;
            faces++;
        }
        if (state.getValue(NORTH)) {
            shape = NORTH_AABB;
            faces++;
        }
        if (state.getValue(SOUTH)) {
            shape = SOUTH_AABB;
            faces++;
        }
        if (state.getValue(UP)) {
            shape = UP_AABB;
            faces++;
        }
        if (state.getValue(WEST)) {
            shape = WEST_AABB;
            faces++;
        }
        return faces == 1 ? shape : FULL_BLOCK_AABB;
    }

    @Nullable
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (this.canAttachTo(worldIn, pos, facing)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return this.canAttachTo(worldIn, pos, side.getOpposite());
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
                                            float hitZ, int meta, EntityLivingBase placer) {
        IBlockState state = this.getDefaultState();
        EnumFacing attachment = facing.getOpposite();
        if (this.canAttachTo(worldIn, pos, attachment)) {
            return state.withProperty(getPropertyFor(attachment), true);
        }

        for (EnumFacing fallback : EnumFacing.values()) {
            if (this.canAttachTo(worldIn, pos, fallback)) {
                return state.withProperty(getPropertyFor(fallback), true);
            }
        }

        return state;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        IBlockState checkedState = state;
        for (EnumFacing facing : EnumFacing.values()) {
            PropertyBool property = getPropertyFor(facing);
            if (state.getValue(property) && !this.canAttachTo(worldIn, pos, facing)) {
                checkedState = checkedState.withProperty(property, false);
            }
        }

        if (!hasAnyFace(checkedState)) {
            worldIn.destroyBlock(pos, true);
        } else if (checkedState != state) {
            worldIn.setBlockState(pos, checkedState, 2);
        }
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        SculkVibrationDispatcher.emit(worldIn, pos, entityIn, 2);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(DOWN)) {
            return 1;
        }
        if (state.getValue(EAST)) {
            return 2;
        }
        if (state.getValue(NORTH)) {
            return 3;
        }
        if (state.getValue(SOUTH)) {
            return 4;
        }
        if (state.getValue(UP)) {
            return 5;
        }
        if (state.getValue(WEST)) {
            return 6;
        }
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState();
        switch (meta) {
            case 1:
                return state.withProperty(DOWN, true);
            case 2:
                return state.withProperty(EAST, true);
            case 3:
                return state.withProperty(NORTH, true);
            case 4:
                return state.withProperty(SOUTH, true);
            case 5:
                return state.withProperty(UP, true);
            case 6:
                return state.withProperty(WEST, true);
            default:
                return state;
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DOWN, EAST, NORTH, SOUTH, UP, WEST);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    private boolean canAttachTo(World world, BlockPos pos, EnumFacing face) {
        BlockPos attachPos = pos.offset(face);
        return world.getBlockState(attachPos).isSideSolid(world, attachPos, face.getOpposite());
    }

    private static PropertyBool getPropertyFor(EnumFacing facing) {
        switch (facing) {
            case DOWN:
                return DOWN;
            case EAST:
                return EAST;
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case UP:
                return UP;
            case WEST:
            default:
                return WEST;
        }
    }

    private static boolean hasAnyFace(IBlockState state) {
        return state.getValue(DOWN)
            || state.getValue(EAST)
            || state.getValue(NORTH)
            || state.getValue(SOUTH)
            || state.getValue(UP)
            || state.getValue(WEST);
    }
}
