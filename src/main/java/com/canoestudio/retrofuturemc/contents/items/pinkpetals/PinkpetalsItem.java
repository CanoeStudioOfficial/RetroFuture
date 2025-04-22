package com.canoestudio.retrofuturemc.contents.items.pinkpetals;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
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
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class PinkpetalsItem extends Item {

    public PinkpetalsItem(String name) {
        setRegistryName(name.toLowerCase());
        setTranslationKey(name.toLowerCase() + "_" + Tags.MOD_ID);
        setCreativeTab(CREATIVE_TABS);

        ModItems.ITEMS.add(this);
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
        if (clickBlock == ModBlocks.PINK_PETALS) {
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
        if (downBlockIsGrass(world, placeBlockPos)) {
            IBlockState oldBlockState = world.getBlockState(placeBlockPos);
            Block oldBlock = oldBlockState.getBlock();

            // 不能放液体里
            if (oldBlock instanceof net.minecraft.block.BlockLiquid)
                return EnumActionResult.FAIL;

            // 防止吞方块 ; 可以吞草丛和一层的雪
            if (oldBlock == Blocks.AIR || oldBlock == Blocks.TALLGRASS || (oldBlock == Blocks.SNOW_LAYER && oldBlock.getMetaFromState(oldBlockState) == 0)) {
                /*获取玩家朝向*/facing = player.getHorizontalFacing();

                int axis = getAxisFromFacing(facing);//面朝北方

                IBlockState state = ModBlocks.PINK_PETALS.getDefaultState().withProperty(PinkPetals.AXIS, axis);

                /*放置方块*/placeBlock(world, placeBlockPos, state);
                /*消耗一个物品*/player.getHeldItem(hand).shrink(1);
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

    public static boolean downBlockIsGrass(World world, BlockPos pos) {
        IBlockState blockState = world.getBlockState(pos.down());
        Block block = blockState.getBlock();

        return  block == Blocks.DIRT ||
                block == Blocks.GRASS ||
                block == Blocks.MYCELIUM ||
                block == Blocks.FARMLAND ||
                (
                        block.getRegistryName().toString().contains("moss") &&
                                block.isFullCube(blockState)
                );
    }

    // 放置方块并播放音效
    private void placeBlock(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 2);
        if (!world.isRemote) {
            ModSoundEvent.playSound(world, pos, "block.cherry_leaves.place");
        }
    }
}