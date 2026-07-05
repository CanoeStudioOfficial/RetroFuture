package com.canoestudio.retrofuturemc.contents.blocks;


import com.canoestudio.retrofuturemc.contents.*;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVine;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVinePlant;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.BigDripleaf;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.DripleafStem;
import com.canoestudio.retrofuturemc.contents.blocks.dripLeaf.SmallDripleaf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();
    public static final List<Item> BLOCKITEMS = new ArrayList<>();

    public static final Block DeepSlate = new BlockCreator("DeepSlate", 3, 0, "pickaxe");
    public static final Block COBBLED_DEEPSLATE = new SimpleBlockCreator("Cobbled_Deepslate", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final Block POLISHED_DEEPSLATE = new SimpleBlockCreator("Polished_Deepslate", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final Block DEEPSLATE_BRICKS = new SimpleBlockCreator("Deepslate_Bricks", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final Block CRACKED_DEEPSLATE_BRICKS = new SimpleBlockCreator("Cracked_Deepslate_Bricks", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final Block DEEPSLATE_TILES = new SimpleBlockCreator("Deepslate_Tiles", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final Block CRACKED_DEEPSLATE_TILES = new SimpleBlockCreator("Cracked_Deepslate_Tiles", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final Block CHISELED_DEEPSLATE = new SimpleBlockCreator("Chiseled_Deepslate", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);

    public static final Block COBBLED_DEEPSLATE_STAIRS = new StairsCreator("Cobbled_Deepslate_Stairs", COBBLED_DEEPSLATE.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_COBBLED_DEEPSLATE_SLAB = new DoubleSlabCreator("Double_Cobbled_Deepslate_Slab", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator COBBLED_DEEPSLATE_SLAB = makeSlab("Cobbled_Deepslate_Slab", DOUBLE_COBBLED_DEEPSLATE_SLAB);
    public static final Block COBBLED_DEEPSLATE_WALL = new WallCreator("Cobbled_Deepslate_Wall", COBBLED_DEEPSLATE, "pickaxe", 0);
    public static final Block POLISHED_DEEPSLATE_STAIRS = new StairsCreator("Polished_Deepslate_Stairs", POLISHED_DEEPSLATE.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_POLISHED_DEEPSLATE_SLAB = new DoubleSlabCreator("Double_Polished_Deepslate_Slab", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator POLISHED_DEEPSLATE_SLAB = makeSlab("Polished_Deepslate_Slab", DOUBLE_POLISHED_DEEPSLATE_SLAB);
    public static final Block POLISHED_DEEPSLATE_WALL = new WallCreator("Polished_Deepslate_Wall", POLISHED_DEEPSLATE, "pickaxe", 0);
    public static final Block DEEPSLATE_BRICK_STAIRS = new StairsCreator("Deepslate_Brick_Stairs", DEEPSLATE_BRICKS.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_DEEPSLATE_BRICK_SLAB = new DoubleSlabCreator("Double_Deepslate_Brick_Slab", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator DEEPSLATE_BRICK_SLAB = makeSlab("Deepslate_Brick_Slab", DOUBLE_DEEPSLATE_BRICK_SLAB);
    public static final Block DEEPSLATE_BRICK_WALL = new WallCreator("Deepslate_Brick_Wall", DEEPSLATE_BRICKS, "pickaxe", 0);
    public static final Block DEEPSLATE_TILE_STAIRS = new StairsCreator("Deepslate_Tile_Stairs", DEEPSLATE_TILES.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_DEEPSLATE_TILE_SLAB = new DoubleSlabCreator("Double_Deepslate_Tile_Slab", Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator DEEPSLATE_TILE_SLAB = makeSlab("Deepslate_Tile_Slab", DOUBLE_DEEPSLATE_TILE_SLAB);
    public static final Block DEEPSLATE_TILE_WALL = new WallCreator("Deepslate_Tile_Wall", DEEPSLATE_TILES, "pickaxe", 0);

    public static final Block CALCITE = new SimpleBlockCreator("Calcite", Material.ROCK, SoundType.STONE, 0.75F, 0.75F, "pickaxe", 0);
    public static final Block TUFF = new SimpleBlockCreator("Tuff", Material.ROCK, SoundType.STONE, 1.5F, 6.0F, "pickaxe", 0);
    public static final Block SMOOTH_BASALT = new SimpleBlockCreator("Smooth_Basalt", Material.ROCK, SoundType.STONE, 1.25F, 4.2F, "pickaxe", 0);

    public static final Block COPPER_ORE = new RetroFutureOreBlock("Copper_Ore", RetroFutureOreBlock.DropType.COPPER, 3.0F, 3.0F, 1);
    public static final Block DEEPSLATE_COAL_ORE = new RetroFutureOreBlock("Deepslate_Coal_Ore", RetroFutureOreBlock.DropType.COAL, 4.5F, 3.0F, 0);
    public static final Block DEEPSLATE_IRON_ORE = new RetroFutureOreBlock("Deepslate_Iron_Ore", RetroFutureOreBlock.DropType.IRON, 4.5F, 3.0F, 1);
    public static final Block DEEPSLATE_COPPER_ORE = new RetroFutureOreBlock("Deepslate_Copper_Ore", RetroFutureOreBlock.DropType.COPPER, 4.5F, 3.0F, 1);
    public static final Block DEEPSLATE_GOLD_ORE = new RetroFutureOreBlock("Deepslate_Gold_Ore", RetroFutureOreBlock.DropType.GOLD, 4.5F, 3.0F, 2);
    public static final Block DEEPSLATE_REDSTONE_ORE = new RetroFutureOreBlock("Deepslate_Redstone_Ore", RetroFutureOreBlock.DropType.REDSTONE, 4.5F, 3.0F, 2);
    public static final Block DEEPSLATE_EMERALD_ORE = new RetroFutureOreBlock("Deepslate_Emerald_Ore", RetroFutureOreBlock.DropType.EMERALD, 4.5F, 3.0F, 2);
    public static final Block DEEPSLATE_LAPIS_ORE = new RetroFutureOreBlock("Deepslate_Lapis_Ore", RetroFutureOreBlock.DropType.LAPIS, 4.5F, 3.0F, 1);
    public static final Block DEEPSLATE_DIAMOND_ORE = new RetroFutureOreBlock("Deepslate_Diamond_Ore", RetroFutureOreBlock.DropType.DIAMOND, 4.5F, 3.0F, 2);

    public static final Block RAW_COPPER_BLOCK = new SimpleBlockCreator("Raw_Copper_Block", Material.ROCK, SoundType.STONE, 5.0F, 6.0F, "pickaxe", 1);
    public static final Block RAW_IRON_BLOCK = new SimpleBlockCreator("Raw_Iron_Block", Material.ROCK, SoundType.STONE, 5.0F, 6.0F, "pickaxe", 1);
    public static final Block RAW_GOLD_BLOCK = new SimpleBlockCreator("Raw_Gold_Block", Material.ROCK, SoundType.STONE, 5.0F, 6.0F, "pickaxe", 2);

    public static final Block COPPER_BLOCK = new WeatheringCopperBlock("Copper_Block");
    public static final Block EXPOSED_COPPER = new WeatheringCopperBlock("Exposed_Copper");
    public static final Block WEATHERED_COPPER = new WeatheringCopperBlock("Weathered_Copper");
    public static final Block OXIDIZED_COPPER = new SimpleBlockCreator("Oxidized_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block CUT_COPPER = new WeatheringCopperBlock("Cut_Copper");
    public static final Block EXPOSED_CUT_COPPER = new WeatheringCopperBlock("Exposed_Cut_Copper");
    public static final Block WEATHERED_CUT_COPPER = new WeatheringCopperBlock("Weathered_Cut_Copper");
    public static final Block OXIDIZED_CUT_COPPER = new SimpleBlockCreator("Oxidized_Cut_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_COPPER_BLOCK = new SimpleBlockCreator("Waxed_Copper_Block", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_EXPOSED_COPPER = new SimpleBlockCreator("Waxed_Exposed_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_WEATHERED_COPPER = new SimpleBlockCreator("Waxed_Weathered_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_OXIDIZED_COPPER = new SimpleBlockCreator("Waxed_Oxidized_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_CUT_COPPER = new SimpleBlockCreator("Waxed_Cut_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_EXPOSED_CUT_COPPER = new SimpleBlockCreator("Waxed_Exposed_Cut_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_WEATHERED_CUT_COPPER = new SimpleBlockCreator("Waxed_Weathered_Cut_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final Block WAXED_OXIDIZED_CUT_COPPER = new SimpleBlockCreator("Waxed_Oxidized_Cut_Copper", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);

    public static final Block CUT_COPPER_STAIRS = new WeatheringCopperStairs("Cut_Copper_Stairs", CUT_COPPER.getDefaultState());
    public static final DoubleSlabCreator DOUBLE_CUT_COPPER_SLAB = new WeatheringCopperDoubleSlab("Double_Cut_Copper_Slab");
    public static final SingleSlabCreator CUT_COPPER_SLAB = makeWeatheringMetalSlab("Cut_Copper_Slab", DOUBLE_CUT_COPPER_SLAB);
    public static final Block EXPOSED_CUT_COPPER_STAIRS = new WeatheringCopperStairs("Exposed_Cut_Copper_Stairs", EXPOSED_CUT_COPPER.getDefaultState());
    public static final DoubleSlabCreator DOUBLE_EXPOSED_CUT_COPPER_SLAB = new WeatheringCopperDoubleSlab("Double_Exposed_Cut_Copper_Slab");
    public static final SingleSlabCreator EXPOSED_CUT_COPPER_SLAB = makeWeatheringMetalSlab("Exposed_Cut_Copper_Slab", DOUBLE_EXPOSED_CUT_COPPER_SLAB);
    public static final Block WEATHERED_CUT_COPPER_STAIRS = new WeatheringCopperStairs("Weathered_Cut_Copper_Stairs", WEATHERED_CUT_COPPER.getDefaultState());
    public static final DoubleSlabCreator DOUBLE_WEATHERED_CUT_COPPER_SLAB = new WeatheringCopperDoubleSlab("Double_Weathered_Cut_Copper_Slab");
    public static final SingleSlabCreator WEATHERED_CUT_COPPER_SLAB = makeWeatheringMetalSlab("Weathered_Cut_Copper_Slab", DOUBLE_WEATHERED_CUT_COPPER_SLAB);
    public static final Block OXIDIZED_CUT_COPPER_STAIRS = new StairsCreator("Oxidized_Cut_Copper_Stairs", OXIDIZED_CUT_COPPER.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_OXIDIZED_CUT_COPPER_SLAB = new DoubleSlabCreator("Double_Oxidized_Cut_Copper_Slab", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator OXIDIZED_CUT_COPPER_SLAB = makeMetalSlab("Oxidized_Cut_Copper_Slab", DOUBLE_OXIDIZED_CUT_COPPER_SLAB);
    public static final Block WAXED_CUT_COPPER_STAIRS = new StairsCreator("Waxed_Cut_Copper_Stairs", WAXED_CUT_COPPER.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_WAXED_CUT_COPPER_SLAB = new DoubleSlabCreator("Double_Waxed_Cut_Copper_Slab", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator WAXED_CUT_COPPER_SLAB = makeMetalSlab("Waxed_Cut_Copper_Slab", DOUBLE_WAXED_CUT_COPPER_SLAB);
    public static final Block WAXED_EXPOSED_CUT_COPPER_STAIRS = new StairsCreator("Waxed_Exposed_Cut_Copper_Stairs", WAXED_EXPOSED_CUT_COPPER.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_WAXED_EXPOSED_CUT_COPPER_SLAB = new DoubleSlabCreator("Double_Waxed_Exposed_Cut_Copper_Slab", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator WAXED_EXPOSED_CUT_COPPER_SLAB = makeMetalSlab("Waxed_Exposed_Cut_Copper_Slab", DOUBLE_WAXED_EXPOSED_CUT_COPPER_SLAB);
    public static final Block WAXED_WEATHERED_CUT_COPPER_STAIRS = new StairsCreator("Waxed_Weathered_Cut_Copper_Stairs", WAXED_WEATHERED_CUT_COPPER.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_WAXED_WEATHERED_CUT_COPPER_SLAB = new DoubleSlabCreator("Double_Waxed_Weathered_Cut_Copper_Slab", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator WAXED_WEATHERED_CUT_COPPER_SLAB = makeMetalSlab("Waxed_Weathered_Cut_Copper_Slab", DOUBLE_WAXED_WEATHERED_CUT_COPPER_SLAB);
    public static final Block WAXED_OXIDIZED_CUT_COPPER_STAIRS = new StairsCreator("Waxed_Oxidized_Cut_Copper_Stairs", WAXED_OXIDIZED_CUT_COPPER.getDefaultState(), "pickaxe", 0);
    public static final DoubleSlabCreator DOUBLE_WAXED_OXIDIZED_CUT_COPPER_SLAB = new DoubleSlabCreator("Double_Waxed_Oxidized_Cut_Copper_Slab", Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
    public static final SingleSlabCreator WAXED_OXIDIZED_CUT_COPPER_SLAB = makeMetalSlab("Waxed_Oxidized_Cut_Copper_Slab", DOUBLE_WAXED_OXIDIZED_CUT_COPPER_SLAB);

    public static final Block AMETHYST_BLOCK = new SimpleBlockCreator("Amethyst_Block", Material.ROCK, SoundType.GLASS, 1.5F, 3.0F, "pickaxe", 0);
    public static final Block BUDDING_AMETHYST = new BuddingAmethystBlock();
    public static final Block AMETHYST_CLUSTER = new AmethystClusterBlock("Amethyst_Cluster", 7, 3, true);
    public static final Block LARGE_AMETHYST_BUD = new AmethystClusterBlock("Large_Amethyst_Bud", 5, 3, false);
    public static final Block MEDIUM_AMETHYST_BUD = new AmethystClusterBlock("Medium_Amethyst_Bud", 4, 4, false);
    public static final Block SMALL_AMETHYST_BUD = new AmethystClusterBlock("Small_Amethyst_Bud", 3, 4, false);

    public static final Block TINTED_GLASS = new GlassCreator("Tinted_Glass", 0.3F);
    public static final Block GLOW_LICHEN = new GlowLichenBlock();
    public static final Block POWDER_SNOW = new PowderSnowBlock();
    public static final Block LIGHTNING_ROD = new LightningRodBlock("Lightning_Rod");

    public static final Block SMALL_DRIPLEAF = new SmallDripleaf();
    public static final Block BIG_DRIPLEAF = new BigDripleaf();
    public static final Block DRIPLEAF_STEM = new DripleafStem();

    public static final Block CAVE_VINE_PLANT = new CaveVinePlant("Cave_Vines_Plant");
    public static final Block CAVE_VINE = new CaveVine("Cave_Vines");

    public static final BlockLeaves Azalea_Leaves = new LeafCreator("Azalea_Leaves");
    public static final Block ROOTED_DIRT = new RootedDirtBlock();
    public static final Block HANGING_ROOTS = new HangingRootsBlock();
    public static final Block MOSS_BLOCK = new MossCreator("Moss_Block");;
    public static final BlockLeaves Flowering_Azalea_Leaves = new LeafCreator("Flowering_Azalea_Leaves");

    public static final Block Azalea = new AzaleaCreator("Azalea");
    public static final Block Flowering_Azalea = new AzaleaCreator("Flowering_Azalea");


    public static final Block MOSS_CARPET = new MossCarpetCreator("Moss_Carpet");
    public static final Block SPORE_BLOSSOM = new SporeBlossomBlock();
    public static final Block DRIPSTONE_BLOCK = new SimpleBlockCreator("Dripstone_Block", Material.ROCK, SoundType.STONE, 1.5F, 3.0F, "pickaxe", 0);
    public static final PointedDripstoneBlock POINTED_DRIPSTONE = new PointedDripstoneBlock();

    public static final Block CANDLE = new CandleBlock("Candle");
    public static final Block WHITE_CANDLE = new CandleBlock("White_Candle");
    public static final Block ORANGE_CANDLE = new CandleBlock("Orange_Candle");
    public static final Block MAGENTA_CANDLE = new CandleBlock("Magenta_Candle");
    public static final Block LIGHT_BLUE_CANDLE = new CandleBlock("Light_Blue_Candle");
    public static final Block YELLOW_CANDLE = new CandleBlock("Yellow_Candle");
    public static final Block LIME_CANDLE = new CandleBlock("Lime_Candle");
    public static final Block PINK_CANDLE = new CandleBlock("Pink_Candle");
    public static final Block GRAY_CANDLE = new CandleBlock("Gray_Candle");
    public static final Block LIGHT_GRAY_CANDLE = new CandleBlock("Light_Gray_Candle");
    public static final Block CYAN_CANDLE = new CandleBlock("Cyan_Candle");
    public static final Block PURPLE_CANDLE = new CandleBlock("Purple_Candle");
    public static final Block BLUE_CANDLE = new CandleBlock("Blue_Candle");
    public static final Block BROWN_CANDLE = new CandleBlock("Brown_Candle");
    public static final Block GREEN_CANDLE = new CandleBlock("Green_Candle");
    public static final Block RED_CANDLE = new CandleBlock("Red_Candle");
    public static final Block BLACK_CANDLE = new CandleBlock("Black_Candle");

    public static final Block CANDLE_CAKE = new CandleCakeBlock("Candle_Cake", (CandleBlock) CANDLE);
    public static final Block WHITE_CANDLE_CAKE = new CandleCakeBlock("White_Candle_Cake", (CandleBlock) WHITE_CANDLE);
    public static final Block ORANGE_CANDLE_CAKE = new CandleCakeBlock("Orange_Candle_Cake", (CandleBlock) ORANGE_CANDLE);
    public static final Block MAGENTA_CANDLE_CAKE = new CandleCakeBlock("Magenta_Candle_Cake", (CandleBlock) MAGENTA_CANDLE);
    public static final Block LIGHT_BLUE_CANDLE_CAKE = new CandleCakeBlock("Light_Blue_Candle_Cake", (CandleBlock) LIGHT_BLUE_CANDLE);
    public static final Block YELLOW_CANDLE_CAKE = new CandleCakeBlock("Yellow_Candle_Cake", (CandleBlock) YELLOW_CANDLE);
    public static final Block LIME_CANDLE_CAKE = new CandleCakeBlock("Lime_Candle_Cake", (CandleBlock) LIME_CANDLE);
    public static final Block PINK_CANDLE_CAKE = new CandleCakeBlock("Pink_Candle_Cake", (CandleBlock) PINK_CANDLE);
    public static final Block GRAY_CANDLE_CAKE = new CandleCakeBlock("Gray_Candle_Cake", (CandleBlock) GRAY_CANDLE);
    public static final Block LIGHT_GRAY_CANDLE_CAKE = new CandleCakeBlock("Light_Gray_Candle_Cake", (CandleBlock) LIGHT_GRAY_CANDLE);
    public static final Block CYAN_CANDLE_CAKE = new CandleCakeBlock("Cyan_Candle_Cake", (CandleBlock) CYAN_CANDLE);
    public static final Block PURPLE_CANDLE_CAKE = new CandleCakeBlock("Purple_Candle_Cake", (CandleBlock) PURPLE_CANDLE);
    public static final Block BLUE_CANDLE_CAKE = new CandleCakeBlock("Blue_Candle_Cake", (CandleBlock) BLUE_CANDLE);
    public static final Block BROWN_CANDLE_CAKE = new CandleCakeBlock("Brown_Candle_Cake", (CandleBlock) BROWN_CANDLE);
    public static final Block GREEN_CANDLE_CAKE = new CandleCakeBlock("Green_Candle_Cake", (CandleBlock) GREEN_CANDLE);
    public static final Block RED_CANDLE_CAKE = new CandleCakeBlock("Red_Candle_Cake", (CandleBlock) RED_CANDLE);
    public static final Block BLACK_CANDLE_CAKE = new CandleCakeBlock("Black_Candle_Cake", (CandleBlock) BLACK_CANDLE);

    private static SingleSlabCreator makeSlab(String name, DoubleSlabCreator doubleSlab) {
        SingleSlabCreator slab = new SingleSlabCreator(name, Material.ROCK, SoundType.STONE, 3.5F, 6.0F, "pickaxe", 0);
        slab.registerItem(doubleSlab);
        return slab;
    }

    private static SingleSlabCreator makeMetalSlab(String name, DoubleSlabCreator doubleSlab) {
        SingleSlabCreator slab = new SingleSlabCreator(name, Material.IRON, SoundType.METAL, 3.0F, 6.0F, "pickaxe", 0);
        slab.registerItem(doubleSlab);
        return slab;
    }

    private static SingleSlabCreator makeWeatheringMetalSlab(String name, DoubleSlabCreator doubleSlab) {
        return new WeatheringCopperSlab(name, doubleSlab);
    }
}
