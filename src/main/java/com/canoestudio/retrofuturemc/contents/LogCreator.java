package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.BlockLog;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class LogCreator extends BlockLog {
    private final boolean flammable; // 控制是否可燃烧

    // 主构造方法（可自定义燃烧属性）
    public LogCreator(String name, int hardness, int harvestlevel, String toolclass, boolean flammable) {
        this.flammable = flammable;
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setHardness(hardness);
        setHarvestLevel(toolclass, harvestlevel);
        setSoundType(SoundType.WOOD);
        setCreativeTab(CREATIVE_TABS);
        setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, EnumAxis.Y));

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }

    // 简化构造方法（默认可燃烧）
    public LogCreator(String name, int hardness, int harvestlevel, String toolclass) {
        this(name, hardness, harvestlevel, toolclass, true);
    }

    // ===== 方块状态方法 =====
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LOG_AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LOG_AXIS, EnumAxis.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumAxis) state.getValue(LOG_AXIS)).ordinal();
    }

    // ===== 原木特性方法 =====
    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return true;
    }

    // ===== 燃烧控制方法 =====
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return flammable ? 5 : 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return flammable ? 5 : 0;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return flammable;
    }
}