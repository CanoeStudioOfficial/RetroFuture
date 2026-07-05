package com.canoestudio.retrofuturemc.contents.items;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class ItemPowderSnowBucket extends Item {
    public ItemPowderSnowBucket() {
        setTranslationKey(Tags.MOD_ID + ".powder_snow_bucket");
        setRegistryName("powder_snow_bucket");
        setCreativeTab(CREATIVE_TABS);
        setMaxStackSize(1);
        setContainerItem(Items.BUCKET);
        ModItems.ITEMS.add(this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        RayTraceResult ray = rayTrace(worldIn, playerIn, false);
        if (ray == null || ray.typeOfHit != RayTraceResult.Type.BLOCK) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }

        BlockPos hitPos = ray.getBlockPos();
        BlockPos placePos = worldIn.getBlockState(hitPos).getBlock().isReplaceable(worldIn, hitPos) && ray.sideHit == EnumFacing.UP ? hitPos : hitPos.offset(ray.sideHit);
        if (!worldIn.isBlockModifiable(playerIn, hitPos) || !playerIn.canPlayerEdit(placePos, ray.sideHit, stack)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (tryPlacePowderSnow(playerIn, worldIn, placePos)) {
            playerIn.addStat(StatList.getObjectUseStats(this));
            return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.capabilities.isCreativeMode ? stack : new ItemStack(Items.BUCKET));
        }

        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    public boolean tryPlacePowderSnow(EntityPlayer player, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Material material = state.getMaterial();
        boolean replaceable = state.getBlock().isReplaceable(world, pos);
        if (!world.isAirBlock(pos) && material.isSolid() && !replaceable) {
            return false;
        }

        if (!world.isRemote) {
            if (!material.isLiquid() && replaceable) {
                world.destroyBlock(pos, true);
            }
            world.setBlockState(pos, ModBlocks.POWDER_SNOW.getDefaultState(), 11);
        }

        world.playSound(player, pos, net.minecraft.init.SoundEvents.BLOCK_SNOW_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        return true;
    }
}
