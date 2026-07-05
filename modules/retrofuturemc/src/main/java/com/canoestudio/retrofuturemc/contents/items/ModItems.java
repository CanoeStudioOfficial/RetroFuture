package com.canoestudio.retrofuturemc.contents.items;

import com.canoestudio.retrofuturemc.contents.BerryCreator;
import com.canoestudio.retrofuturemc.contents.SimpleItemCreator;
import com.canoestudio.retrofuturemc.contents.items.spyglass.ItemSpyglass;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final ItemFood Glow_Berries = new BerryCreator("Glow_Berries", 4, 0.4f);
    public static final ItemSpyglass SPYGLASS = new ItemSpyglass("Spyglass");
    public static final Item COPPER_INGOT = new SimpleItemCreator("Copper_Ingot");
    public static final Item RAW_COPPER = new SimpleItemCreator("Raw_Copper");
    public static final Item RAW_IRON = new SimpleItemCreator("Raw_Iron");
    public static final Item RAW_GOLD = new SimpleItemCreator("Raw_Gold");
    public static final Item AMETHYST_SHARD = new SimpleItemCreator("Amethyst_Shard");
    public static final Item GLOW_INK_SAC = new SimpleItemCreator("Glow_Ink_Sac");
    public static final Item POWDER_SNOW_BUCKET = new ItemPowderSnowBucket();
}
