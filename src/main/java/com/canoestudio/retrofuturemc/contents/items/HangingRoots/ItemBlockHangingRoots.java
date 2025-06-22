package com.canoestudio.retrofuturemc.contents.items.HangingRoots;

import com.canoestudio.retrofuturemc.contents.blocks.HangingRoots.HangingRoots;
import git.jbredwards.fluidlogged_api.api.util.FluidloggedUtils;
import git.jbredwards.fluidlogged_api.api.util.FluidState;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBlockHangingRoots extends ItemBlock {

    public ItemBlockHangingRoots(Block block) {
        super(block);
    }

    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        // 只能从下方放置
        if(side != EnumFacing.DOWN) return false;

        // 检查上方方块是否可附着
        if(!world.getBlockState(pos.up()).isSideSolid(world, pos.up(), EnumFacing.DOWN)) {
            return false;
        }

        // 检查流体状态
        final FluidState fluidState = FluidloggedUtils.getFluidState(world, pos);
        return fluidState.isFluidloggable() &&
                ((HangingRoots)this.block).isFluidloggable(block.getDefaultState(), world, pos, fluidState);
    }
}