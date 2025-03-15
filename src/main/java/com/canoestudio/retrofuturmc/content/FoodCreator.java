package com.canoestudio.retrofuturmc.content;

import com.canoestudio.retrofuturmc.retrofuturmc.Tags;
import com.canoestudio.retrofuturmc.content.items.Items;
import net.minecraft.item.ItemFood;

import static com.canoestudio.retrofuturmc.content.tab.CreativeTab.CREATIVE_TABS;

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