package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class PlanksCreator extends Block {
    private final boolean flammable;

    /**
     * 最简构造方法（默认使用原版木板的燃烧参数：flammability=20, spreadSpeed=5）
     * @param name 方块名称（自动添加modid前缀）
     * @param hardness 硬度（原版木板为2.0F）
     * @param harvestLevel 采集等级（木板通常为0）
     * @param toolClass 工具类型（"axe"）
     */
    public PlanksCreator(String name, float hardness, int harvestLevel, String toolClass) {
        this(name, hardness, harvestLevel, toolClass, true); // 默认可燃烧
    }

    /**
     * 完整构造方法（可自定义燃烧行为）
     * @param name 方块名称
     * @param hardness 硬度
     * @param harvestLevel 采集等级
     * @param toolClass 工具类型
     * @param flammable 是否可燃烧
     */
    public PlanksCreator(String name, float hardness, int harvestLevel, String toolClass, boolean flammable) {
        super(Material.WOOD);
        this.flammable = flammable;

        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setHardness(hardness);
        setHarvestLevel(toolClass, harvestLevel);
        setSoundType(SoundType.WOOD);
        setCreativeTab(CREATIVE_TABS);

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }

    // ===== 燃烧控制（与原版木板一致）=====
    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return flammable ? 20 : 0; // 原版木板 flammability=20
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return flammable ? 5 : 0; // 原版木板 spreadSpeed=5
    }

    @Override
    public boolean isFlammable(IBlockAccess world, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return flammable;
    }

    // ===== 木板特性 =====
    @Override
    public boolean isWood(IBlockAccess world, BlockPos pos) {
        return true; // 标记为木材类型
    }

    @Override
    public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false; // 木板不能支撑树叶
    }
}