package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.IGrowable;
import java.util.Random;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class MossCarpetCreator extends Block implements IGrowable {
    protected static final AxisAlignedBB CARPET_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

    public MossCarpetCreator(String name) {
        super(Material.PLANTS);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setHardness(0.1F);
        setSoundType(SoundType.PLANT);
        setCreativeTab(CREATIVE_TABS);

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }

    // 实现IGrowable接口方法
    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        // 在周围3x3范围内随机生成苔藓地毯
        for(int i = -1; i <= 1; i++) {
            for(int j = -1; j <= 1; j++) {
                if(rand.nextFloat() < 0.5F) { // 50%的几率生成
                    BlockPos targetPos = pos.add(i, 0, j);

                    // 检查目标位置是否适合放置苔藓地毯
                    if(worldIn.isAirBlock(targetPos) && canPlaceBlockAt(worldIn, targetPos)) {
                        worldIn.setBlockState(targetPos, this.getDefaultState(), 3);
                    }
                }
            }
        }

        // 有几率在下方生成苔藓块
        if(rand.nextFloat() < 0.2F) { // 20%的几率
            BlockPos belowPos = pos.down();
            IBlockState belowState = worldIn.getBlockState(belowPos);

            if(isMossable(belowState)) {
                worldIn.setBlockState(belowPos, ModBlocks.MOSS_BLOCK.getDefaultState(), 3);
            }
        }
    }

    private boolean isMossable(IBlockState state) {
        Block block = state.getBlock();
        return block == Blocks.STONE || block == ModBlocks.DeepSlate ||
                block == Blocks.MYCELIUM || block == Blocks.DIRT ||
                block == Blocks.GRASS || block == Blocks.COBBLESTONE;
    }

    // 原有方法保持不变
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CARPET_AABB;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return super.canPlaceBlockAt(worldIn, pos) && this.canBlockStay(worldIn, pos);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        this.checkForDrop(worldIn, pos, state);
    }

    private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.canBlockStay(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
        return true;
    }

    private boolean canBlockStay(World worldIn, BlockPos pos) {
        return !worldIn.isAirBlock(pos.down());
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return 20;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return 60;
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isRemote) {
            entityIn.motionX *= 0.95D;
            entityIn.motionZ *= 0.95D;
        }
    }
}