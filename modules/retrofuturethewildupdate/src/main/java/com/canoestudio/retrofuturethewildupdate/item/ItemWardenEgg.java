package com.canoestudio.retrofuturethewildupdate.item;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.entity.ModEntities;
import com.canoestudio.retrofuturethewildupdate.entity.Warden;
import com.canoestudio.retrofuturethewildupdate.sounds.ModSounds;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWardenEgg extends Item {

    public ItemWardenEgg() {
        this.setMaxStackSize(16);
        this.setCreativeTab(CreativeTabs.MISC);
        this.setTranslationKey(RTWU.ID + ".warden_egg");
        this.setRegistryName(RTWU.ID, "warden_egg");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,
                                       EnumHand hand, EnumFacing facing,
                                       float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos spawnPos = pos.offset(facing);
            Entity entity = EntityList.createEntityByIDFromName(ModEntities.WARDEN_NAME, world);
            if (entity instanceof Warden) {
                Warden warden = (Warden) entity;
                double x = spawnPos.getX() + 0.5;
                double y = spawnPos.getY();
                double z = spawnPos.getZ() + 0.5;
                warden.setPosition(x, y, z);
                warden.startEmerging();
                world.playSound(null, x, y, z, ModSounds.WARDEN_EMERGE, SoundCategory.HOSTILE, 1.5f, 1.0f);
                warden.enablePersistence();
                world.spawnEntity(warden);
                if (!player.capabilities.isCreativeMode) {
                    player.getHeldItem(hand).shrink(1);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
