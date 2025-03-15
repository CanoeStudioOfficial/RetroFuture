package com.canoestudio.retrofuturemc.content;

import com.canoestudio.retrofuturemc.content.items.Items;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.item.ItemFood;

import static com.canoestudio.retrofuturemc.content.tab.CreativeTab.CREATIVE_TABS;

public class FoodCreator extends ItemFood
{
    public FoodCreator(String name, int hunger, float saturationModifier)
    {
        super(hunger, saturationModifier, false);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setNoRepair();
        setCreativeTab(CREATIVE_TABS);

        Items.ITEMS.add(this);
    }
}