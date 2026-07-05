package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.canoestudio.retrofuturemc.sounds.ModSoundHandler;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

/**
 * 自定义树叶方块类，支持可配置的燃烧特性
 */
public class LeafCreator extends BlockLeaves {
    // 自定义树叶音效
    public static final SoundType AZALEA_LEAVES = new SoundType(
            1.0F, 1.0F,
            ModSoundHandler.BLOCK_AZALEA_LEAVES_BREAK,
            ModSoundHandler.BLOCK_AZALEA_LEAVES_STEP,
            ModSoundHandler.BLOCK_AZALEA_PLACE,
            ModSoundHandler.BLOCK_AZALEA_LEAVES_HIT,
            ModSoundHandler.BLOCK_AZALEA_LEAVES_FALL
    );

    private final boolean flammable;

    /**
     * 创建默认的可燃树叶
     * @param name 树叶注册名称
     */
    public LeafCreator(String name) {
        this(name, true); // 默认设置为可燃
    }

    /**
     * 完整构造函数
     * @param name 树叶注册名称
     * @param flammable 是否可燃
     */
    public LeafCreator(String name, boolean flammable) {
        super();
        this.flammable = flammable;

        // 基础属性设置
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setCreativeTab(CREATIVE_TABS);
        setSoundType(AZALEA_LEAVES);

        // 初始化方块状态
        setDefaultState(blockState.getBaseState()
                .withProperty(CHECK_DECAY, true)
                .withProperty(DECAYABLE, true));

        // 注册方块和对应的物品
        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this)
                .setRegistryName(name.toLowerCase()));
    }

    /* 可燃性相关方法 */
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return flammable ? 60 : 0; // 与原版树叶相同的可燃性值
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return flammable ? 30 : 0; // 与原版树叶相同的火焰传播速度
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return flammable;
    }

    /* 渲染相关方法 */
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return Blocks.LEAVES.getRenderLayer();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return Blocks.LEAVES.isOpaqueCube(state);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world,
                                        BlockPos pos, EnumFacing side) {
        return Blocks.LEAVES.shouldSideBeRendered(state, world, pos, side);
    }

    @Override
    public boolean isTranslucent(IBlockState state) {
        return true;
    }

    /* 方块状态管理 */
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CHECK_DECAY, DECAYABLE);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(DECAYABLE) ? 2 : 0) +
                (state.getValue(CHECK_DECAY) ? 1 : 0);
    }

    /* 树叶类型和剪切行为 */
    @Override
    public BlockPlanks.EnumType getWoodType(int meta) {
        return BlockPlanks.EnumType.OAK;
    }

    @Nonnull
    @Override
    public List<ItemStack> onSheared(@Nonnull ItemStack item, IBlockAccess world,
                                     BlockPos pos, int fortune) {
        return NonNullList.withSize(1, new ItemStack(this));
    }
}