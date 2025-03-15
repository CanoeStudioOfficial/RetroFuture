package com.canoestudio.retrofuturemc.content.blocks;

import com.canoestudio.retrofuturemc.content.BlockCreator;
import com.canoestudio.retrofuturemc.content.blocks.dripLeaf.BigDripleaf;
import com.canoestudio.retrofuturemc.content.blocks.dripLeaf.DripleafStem;
import com.canoestudio.retrofuturemc.content.blocks.dripLeaf.SmallDripleaf;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> BLOCKITEMS = new ArrayList<>();

    public static final Block DeepSlate = new BlockCreator("DeepSlate", 3, 0, "pickaxe");
    public static final Block SMALL_DRIPLEAF = new SmallDripleaf();
    public static final Block BIG_DRIPLEAF = new BigDripleaf();
    public static final Block DRIPLEAF_STEM = new DripleafStem();

}
