package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.item.Item;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class SimpleItemCreator extends Item {
    public SimpleItemCreator(String name) {
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setCreativeTab(CREATIVE_TABS);

        ModItems.ITEMS.add(this);
    }
}
