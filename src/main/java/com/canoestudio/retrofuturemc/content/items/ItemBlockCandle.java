package com.canoestudio.retrofuturemc.content.items;

import com.canoestudio.retrofuturemc.content.blocks.BlockCandle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockCandle extends ItemBlock {

    public ItemBlockCandle(BlockCandle block) {
        super(block);
        this.setRegistryName(block.getRegistryName());
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        if (!player.isSneaking() && state.getBlock() == this.block && state.getValue(BlockCandle.CANDLES) < 4) {
            return EnumActionResult.PASS;
        }
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }
}