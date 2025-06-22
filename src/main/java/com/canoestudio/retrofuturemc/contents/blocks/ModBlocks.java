package com.canoestudio.retrofuturemc.contents.blocks;


import com.canoestudio.retrofuturemc.contents.*;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVine;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVinePlant;
import com.canoestudio.retrofuturemc.contents.blocks.HangingRoots.HangingRoots;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.BigDripleaf;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.DripleafStem;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.SmallDripleaf;
import com.canoestudio.retrofuturemc.contents.blocks.pinkpetals.PinkPetals;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.init.Blocks;
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

    public static final Block CAVE_VINE_PLANT = new CaveVinePlant("Cave_Vines_Plant");
    public static final Block CAVE_VINE = new CaveVine("Cave_Vines");

    public static final BlockLeaves Azalea_Leaves = new LeafCreator("Azalea_Leaves");
    public static final Block ROOTED_DIRT = Blocks.DIRT;
    public static final Block HANGING_ROOTS = Blocks.DIRT;
    public static final Block MOSS_BLOCK = new MossCreator("Moss_Block");;
    public static final BlockLeaves Flowering_Azalea_Leaves = new LeafCreator("Flowering_Azalea_Leaves");

    public static final Block Azalea = new AzaleaCreator("Azalea");
    public static final Block Flowering_Azalea = new AzaleaCreator("Flowering_Azalea");

    public static final Block PINK_PETALS = new PinkPetals("Pink_Petals");
    public static final Block MOSS_CARPET = new MossCarpetCreator("Moss_Carpet");

    public static final Block PLAE_OAK_LOG = new LogCreator("Pale_Oak_Log", 2, 0, "axe");
    public static final Block PLAE_OAK_LEAVES = new LeafCreator("Pale_Oak_Leaves");
    public static final Block PLAE_OAK_PLANKS = new PlanksCreator("Pale_Oak_Planks",2, 0, "axe");

    public static final Block HangingRoots = new HangingRoots("Hanging_Roots");
}
