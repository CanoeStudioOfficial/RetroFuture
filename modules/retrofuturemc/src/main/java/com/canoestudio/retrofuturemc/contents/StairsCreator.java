package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class StairsCreator extends BlockStairs {
    public StairsCreator(String name, IBlockState modelState, String toolClass, int harvestLevel) {
        super(modelState);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setCreativeTab(CREATIVE_TABS);

        if (toolClass != null) {
            setHarvestLevel(toolClass, harvestLevel);
        }

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }
}
