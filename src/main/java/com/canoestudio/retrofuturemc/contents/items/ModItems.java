package com.canoestudio.retrofuturemc.contents.items;

import com.canoestudio.retrofuturemc.contents.BerryCreator;
import com.canoestudio.retrofuturemc.contents.items.pinkpetals.PinkpetalsItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final ItemFood Glow_Berries = new BerryCreator("Glow_Berries", 4, 0.4f);
    public static final PinkpetalsItem PINK_PETALS = new PinkpetalsItem("pink_petals");
}
