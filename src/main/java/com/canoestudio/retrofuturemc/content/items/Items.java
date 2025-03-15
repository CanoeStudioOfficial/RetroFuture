package com.canoestudio.retrofuturemc.content.items;

import com.canoestudio.retrofuturemc.content.FoodCreator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;

import java.util.ArrayList;
import java.util.List;

public class Items {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final ItemFood Glow_Berries = new FoodCreator("Glow_Berries", 4, 0.4f);
}
