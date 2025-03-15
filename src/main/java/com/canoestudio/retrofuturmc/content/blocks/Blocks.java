package com.canoestudio.retrofuturmc.content.blocks;

import com.canoestudio.retrofuturmc.content.BlockCreator;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Blocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> BLOCKITEMS = new ArrayList<>();

    public static final Block DeepSlate = new BlockCreator("DeepSlate", 3, 0, "pickaxe");
}
