package com.canoestudio.retrofuturmc.content.tab;

import com.canoestudio.retrofuturmc.retrofuturmc.Tags;
import com.canoestudio.retrofuturmc.content.items.Items;
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
