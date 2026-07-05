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
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class CandleBlock extends Block implements IFluidloggable {
    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyInteger CANDLES = PropertyInteger.create("candles", 1, 4);
    private static final AxisAlignedBB[] AABBS = {
            new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 0.4375D, 0.5625D),
            new AxisAlignedBB(0.3125D, 0.0D, 0.375D, 0.6875D, 0.4375D, 0.5625D),
            new AxisAlignedBB(0.3125D, 0.0D, 0.375D, 0.625D, 0.4375D, 0.6875D),
            new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.4375D, 0.625D)
    };
    private static final Vec3d[][] WICK_OFFSETS = {
            {new Vec3d(0.5D, 0.5D, 0.5D)},
            {new Vec3d(0.375D, 0.4375D, 0.5D), new Vec3d(0.625D, 0.5D, 0.4375D)},
            {new Vec3d(0.5D, 0.3125D, 0.625D), new Vec3d(0.375D, 0.4375D, 0.5D), new Vec3d(0.5625D, 0.5D, 0.4375D)},
            {new Vec3d(0.4375D, 0.3125D, 0.5625D), new Vec3d(0.625D, 0.4375D, 0.5625D), new Vec3d(0.375D, 0.4375D, 0.375D), new Vec3d(0.5625D, 0.5D, 0.375D)}
    };

    public CandleBlock(String name) {
        super(Material.CLOTH);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setHardness(0.1F);
        setResistance(0.1F);
        setSoundType(SoundType.CLOTH);
        setCreativeTab(CREATIVE_TABS);
        setDefaultState(blockState.getBaseState().withProperty(CANDLES, 1).withProperty(LIT, false));

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABBS[state.getValue(CANDLES) - 1];
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
    public int getLightValue(IBlockState state) {
        return state.getValue(LIT) ? state.getValue(CANDLES) * 3 : 0;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        IBlockState down = worldIn.getBlockState(pos.down());
        return down.isSideSolid(worldIn, pos.down(), EnumFacing.UP);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canPlaceBlockAt(worldIn, pos)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            restoreFluidOrAir(worldIn, pos, state, 3);
            return;
        }
        scheduleContainedFluidTick(worldIn, pos, state);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem() == net.minecraft.item.Item.getItemFromBlock(this) && state.getValue(CANDLES) < 4) {
            if (!worldIn.isRemote) {
                worldIn.setBlockState(pos, state.withProperty(CANDLES, state.getValue(CANDLES) + 1), 3);
                worldIn.playSound(null, pos, blockSoundType.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundType.getVolume() + 1.0F) / 2.0F, blockSoundType.getPitch() * 0.8F);
                if (!playerIn.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            return true;
        }
        if (stack.isEmpty() && state.getValue(LIT)) {
            if (!worldIn.isRemote) {
                extinguish(worldIn, pos, state, 3);
            }
            return true;
        }
        if (stack.getItem() == net.minecraft.init.Items.FLINT_AND_STEEL && !state.getValue(LIT) && FluidloggedUtils.getFluidState(worldIn, pos, state).getFluid() != FluidRegistry.WATER) {
            if (!worldIn.isRemote) {
                worldIn.setBlockState(pos, state.withProperty(LIT, true), 3);
                worldIn.playSound(null, pos, net.minecraft.init.SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.4F + 0.8F);
                stack.damageItem(1, playerIn);
            }
            return true;
        }
        return false;
    }

    public static void extinguish(World world, BlockPos pos, IBlockState state, int flags) {
        world.setBlockState(pos, state.withProperty(LIT, false), flags);
        for (Vec3d offset : WICK_OFFSETS[state.getValue(CANDLES) - 1]) {
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z, 0.0D, 0.1D, 0.0D);
        }
        world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.35F, 2.0F + world.rand.nextFloat() * 0.4F);
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

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
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
        if (here.getValue(LIT)) {
            extinguish(world, pos, here, blockFlags);
        }
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
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CANDLES, (meta & 3) + 1).withProperty(LIT, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CANDLES) - 1 + (state.getValue(LIT) ? 4 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {CANDLES, LIT});
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, java.util.Random random) {
        return state.getValue(CANDLES);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, java.util.Random rand) {
        if (!stateIn.getValue(LIT)) {
            return;
        }

        for (Vec3d offset : WICK_OFFSETS[stateIn.getValue(CANDLES) - 1]) {
            if (rand.nextFloat() < 0.3F) {
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z, 0.0D, 0.0D, 0.0D);
            }
            worldIn.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + offset.x, pos.getY() + offset.y, pos.getZ() + offset.z, 0.0D, 0.0D, 0.0D);
        }
    }
}
