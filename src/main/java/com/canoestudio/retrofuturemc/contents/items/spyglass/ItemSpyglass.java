package com.canoestudio.retrofuturemc.contents.items.spyglass;



import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.canoestudio.retrofuturemc.sounds.ModSoundEvent;
import com.canoestudio.retrofuturemc.sounds.RetroFuturemcSoundEvents;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
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
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1200;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {

        player.setActiveHand(hand);
        ItemStack stack = player.getHeldItem(hand);
        player.playSound(RetroFuturemcSoundEvents.SPYGLASS_USE, 1, 1);
        if (player instanceof EntityPlayerMP) player.addStat(StatList.getObjectUseStats(stack.getItem()));
        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int tickCount) {
        entity.playSound(RetroFuturemcSoundEvents.SPYGLASS_STOP_USING, 1, 1);
    }

}