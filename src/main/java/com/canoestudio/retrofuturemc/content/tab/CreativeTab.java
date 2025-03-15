package com.canoestudio.retrofuturemc.content.tab;

import com.canoestudio.retrofuturemc.content.items.Items;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTab {
    public static final CreativeTabs CREATIVE_TABS = new CreativeTabs(Tags.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(Items.Glow_Berries, 1);
        }
    };
}
