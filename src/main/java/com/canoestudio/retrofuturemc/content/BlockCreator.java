package com.canoestudio.retrofuturemc.content;

import com.canoestudio.retrofuturemc.content.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

import static com.canoestudio.retrofuturemc.content.tab.CreativeTab.CREATIVE_TABS;

public class BlockCreator extends Block {
    public BlockCreator(String name, int hardness, int harvestlevel, String toolclass) {
        super(Material.ROCK);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setHardness(hardness);
        setHarvestLevel(toolclass, harvestlevel);
        setSoundType(SoundType.STONE);
        setCreativeTab(CREATIVE_TABS);

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }
}