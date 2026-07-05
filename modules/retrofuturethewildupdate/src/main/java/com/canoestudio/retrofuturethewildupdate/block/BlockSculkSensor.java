package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSculkSensor extends Block implements ITileEntityProvider {

    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    private static final int ACTIVE_TICKS = 30;
    private static final int COOLDOWN_TICKS = 10;

    public BlockSculkSensor() {
        super(Material.ROCK);
        this.setRegistryName(RTWU.ID, "sculk_sensor");
        this.setTranslationKey(RTWU.ID + ".sculk_sensor");
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setHardness(1.5f);
        this.setResistance(1.5f);
        this.setSoundType(SoundType.SLIME);
        this.setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE, false).withProperty(POWER, 0));
    }

    public void receiveVibration(World world, BlockPos pos, @Nullable Entity source, int strength) {
        this.receiveVibration(world, pos, source, SculkVibrationDispatcher.vibrationFromLegacyStrength(strength), Math.max(1, Math.min(15, strength)));
    }

    public void receiveVibration(World world, BlockPos pos, @Nullable Entity source, SculkVibration vibration, int redstonePower) {
        if (world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySculkSensor && !((TileEntitySculkSensor) tile).canActivate(world)) {
            return;
        }

        int power = Math.max(1, Math.min(15, redstonePower));
        IBlockState activeState = this.getDefaultState().withProperty(ACTIVE, true).withProperty(POWER, power);
        world.setBlockState(pos, activeState, 3);
        world.scheduleUpdate(pos, this, ACTIVE_TICKS);
        world.notifyNeighborsOfStateChange(pos, this, false);
        world.playSound(null, pos, SoundEvents.BLOCK_NOTE_HARP, SoundCategory.BLOCKS, 0.8f, 0.5f + vibration.getFrequency() * 0.03f);

        if (tile instanceof TileEntitySculkSensor) {
            ((TileEntitySculkSensor) tile).markActivated(world, vibration.getFrequency(), ACTIVE_TICKS + COOLDOWN_TICKS);
        }

        SculkVibrationDispatcher.emitFromSensor(world, pos, source, vibration);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && state.getValue(ACTIVE)) {
            worldIn.setBlockState(pos, this.getDefaultState(), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this, false);
            worldIn.scheduleUpdate(pos, this, COOLDOWN_TICKS);
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(POWER);
    }

    @Override
    public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return blockState.getValue(POWER);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntity tile = worldIn.getTileEntity(pos);
        return blockState.getValue(ACTIVE) && tile instanceof TileEntitySculkSensor
            ? ((TileEntitySculkSensor) tile).getLastVibrationFrequency()
            : 0;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(POWER);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(POWER, Math.max(0, Math.min(15, meta))).withProperty(ACTIVE, meta > 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ACTIVE, POWER);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySculkSensor();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
