package com.canoestudio.retrofuturemc.content.blocks;

import com.canoestudio.retrofuturemc.content.BlockCreator;
<<<<<<< Updated upstream
import com.canoestudio.retrofuturemc.content.blocks.CaveVine.CaveVine;
import com.canoestudio.retrofuturemc.content.blocks.CaveVine.CaveVinePlant;
import com.canoestudio.retrofuturemc.content.blocks.dripLeaf.BigDripleaf;
import com.canoestudio.retrofuturemc.content.blocks.dripLeaf.DripleafStem;
import com.canoestudio.retrofuturemc.content.blocks.dripLeaf.SmallDripleaf;
import net.minecraft.block.Block;
=======
import com.canoestudio.retrofuturemc.content.LeafCreator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
>>>>>>> Stashed changes
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> BLOCKITEMS = new ArrayList<>();

    public static final Block DeepSlate = new BlockCreator("DeepSlate", 3, 0, "pickaxe");
<<<<<<< Updated upstream
    public static final Block SMALL_DRIPLEAF = new SmallDripleaf();
    public static final Block BIG_DRIPLEAF = new BigDripleaf();
    public static final Block DRIPLEAF_STEM = new DripleafStem();

    public static final Block CAVE_VINE_PLANT = new CaveVinePlant("cave_vines_plant");
    public static final Block CAVE_VINE = new CaveVine("cave_vines");

}
=======
    public static final BlockLeaves Azalea_Leaves = new LeafCreator("Azalea_Leaves");
    public static final BlockLeaves Flowering_Azalea_Leaves = new LeafCreator("Flowering_Azalea_Leaves");
}
>>>>>>> Stashed changes
