package com.canoestudio.retrofuturemc.contents.items.spyglass;

import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;


import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class ItemSpyglass extends Item {

    public ItemSpyglass(String name)
    {
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setMaxStackSize(1);

        setCreativeTab(CREATIVE_TABS);

        ModItems.ITEMS.add(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            return super.onItemRightClick(world, player, hand);
        }

        // 客户端处理
        if (player.isHandActive() && player.getActiveHand() == hand) {
            player.stopActiveHand();
            SpyglassHandler.updateItemUsage(false, hand);
        } else {
            player.setActiveHand(hand);
            SpyglassHandler.updateItemUsage(true, hand);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }


}