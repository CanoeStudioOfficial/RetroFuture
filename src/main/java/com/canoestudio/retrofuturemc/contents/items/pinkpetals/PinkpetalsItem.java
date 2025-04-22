package com.canoestudio.retrofuturemc.contents.items.pinkpetals;

import com.canoestudio.retrofuturemc.contents.blocks.pinkpetals.PinkPetals;
import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.canoestudio.retrofuturemc.sounds.ModSoundEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class PinkpetalsItem extends Item {
    private Block block; // 关联的方块（例如 PinkPetals 方块）

    public PinkpetalsItem(String name) {
        setRegistryName(name);
        setTranslationKey(name + "_" + Tags.MOD_ID);
        setCreativeTab(CREATIVE_TABS);
        ModItems.ITEMS.add(this);
    }

    // 设置关联的方块（例如在注册时调用）
    public void setBlock(Block block) {
        this.block = block;
    }

    // 右键点击逻辑（修复 NullPointerException）
    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        // 可以在这里添加右键逻辑（例如抛掷花瓣）
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    // 方块使用逻辑（原 onItemUse）
    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos clickPos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (world == null || player == null) {
            return EnumActionResult.FAIL;
        }

        ItemStack stack = player.getHeldItem(hand);
        if (stack.isEmpty()) {
            return EnumActionResult.FAIL;
        }

        IBlockState clickBlockState = world.getBlockState(clickPos);
        Block clickBlock = clickBlockState.getBlock();
        BlockPos placeBlockPos = clickPos.offset(facing);

        // 升级落英
        if (clickBlock == this.block) {
            placeBlockPos = clickPos;
            int level = clickBlockState.getValue(PinkPetals.LEVEL);

            if (level >= 4) {
                return EnumActionResult.FAIL;
            }

            IBlockState newState = clickBlockState.withProperty(PinkPetals.LEVEL, level + 1);
            placeBlock(world, placeBlockPos, newState);
            stack.shrink(1);
            return EnumActionResult.SUCCESS;
        }

        // 首次放置落英（可吞噬草或雪）
        if (clickBlock == Blocks.TALLGRASS || (clickBlock == Blocks.SNOW_LAYER && clickBlock.getMetaFromState(clickBlockState) == 0)) {
            placeBlockPos = clickPos;
        }

        if (canPlaceOn(world, placeBlockPos)) {
            IBlockState oldState = world.getBlockState(placeBlockPos);
            Block oldBlock = oldState.getBlock();

            // 禁止放在液体中
            if (oldBlock instanceof net.minecraft.block.BlockLiquid) {
                return EnumActionResult.FAIL;
            }

            // 允许放在空气、草或单层雪上
            if (oldBlock == Blocks.AIR || oldBlock == Blocks.TALLGRASS || (oldBlock == Blocks.SNOW_LAYER && oldBlock.getMetaFromState(oldState) == 0)) {
                EnumFacing playerFacing = player.getHorizontalFacing();
                int axis = getAxisFromFacing(playerFacing);

                IBlockState newState = this.block.getDefaultState()
                        .withProperty(PinkPetals.AXIS, axis);

                placeBlock(world, placeBlockPos, newState);
                stack.shrink(1);
                return EnumActionResult.SUCCESS;
            }
        }

        return EnumActionResult.FAIL;
    }

    // 检查是否可以放置在目标位置（原 downBlockIsGrass）
    private boolean canPlaceOn(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos.down());
        Block block = state.getBlock();

        return block == Blocks.DIRT ||
                block == Blocks.GRASS ||
                block == Blocks.MYCELIUM ||
                block == Blocks.FARMLAND ||
                (block.getRegistryName().toString().contains("moss") && block.isFullCube(state));
    }

    // 根据玩家朝向获取轴（原代码逻辑）
    private int getAxisFromFacing(EnumFacing facing) {
        switch (facing) {
            case EAST:  return 2;
            case SOUTH: return 3;
            case WEST:  return 4;
            default:    return 1; // NORTH
        }
    }

    // 放置方块并播放音效
    private void placeBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 2);
        if (!world.isRemote) {
            ModSoundEvent.playSound(world, pos, "block.cherry_leaves.place");
        }
    }
}