package com.canoestudio.retrofuturemc.contents.blocks;

import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class AmethystClusterBlock extends FluidloggableDirectionalBlock {
    private final int height;
    private final int offset;
    private final boolean dropsShard;

    public AmethystClusterBlock(String name, int height, int offset, boolean dropsShard) {
        super(Material.GLASS);
        this.height = height;
        this.offset = offset;
        this.dropsShard = dropsShard;
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setHardness(1.5F);
        setResistance(3.0F);
        setHarvestLevel("pickaxe", 0);
        setSoundType(SoundType.GLASS);
        setCreativeTab(CREATIVE_TABS);
        setLightLevel(dropsShard ? 5.0F / 15.0F : 1.0F / 15.0F);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.UP));

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
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

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!canBlockStay(worldIn, pos, state)) {
            dropBlockAsItem(worldIn, pos, state, 0);
            restoreFluidOrAir(worldIn, pos, state, 3);
            return;
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    private boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        return canAttach(world, pos, state.getValue(FACING));
    }

    private boolean canAttach(World world, BlockPos pos, EnumFacing facing) {
        BlockPos supportPos = pos.offset(facing.getOpposite());
        IBlockState support = world.getBlockState(supportPos);
        return support.isSideSolid(world, supportPos, facing);
    }

    private void restoreFluidOrAir(World world, BlockPos pos, IBlockState state, int flags) {
        FluidState fluidState = FluidloggedUtils.getFluidState(world, pos, state);
        world.setBlockState(pos, fluidState.getFluid() == FluidRegistry.WATER ? fluidState.getState() : net.minecraft.init.Blocks.AIR.getDefaultState(), flags);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(FACING);
        double min = offset / 16.0D;
        double max = (16 - offset) / 16.0D;
        double h = height / 16.0D;

        switch (facing) {
            case DOWN:
                return new AxisAlignedBB(min, 1.0D - h, min, max, 1.0D, max);
            case NORTH:
                return new AxisAlignedBB(min, min, 1.0D - h, max, max, 1.0D);
            case SOUTH:
                return new AxisAlignedBB(min, min, 0.0D, max, max, h);
            case WEST:
                return new AxisAlignedBB(1.0D - h, min, min, 1.0D, max, max);
            case EAST:
                return new AxisAlignedBB(0.0D, min, min, h, max, max);
            case UP:
            default:
                return new AxisAlignedBB(min, 0.0D, min, max, h, max);
        }
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
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return dropsShard ? ModItems.AMETHYST_SHARD : Item.getItemFromBlock(this);
    }

    @Override
    public int quantityDropped(Random random) {
        return dropsShard ? 4 : 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (!dropsShard) {
            return 1;
        }
        return Math.min(16, quantityDropped(random) + random.nextInt(fortune + 1));
    }

    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta & 7));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }
}
