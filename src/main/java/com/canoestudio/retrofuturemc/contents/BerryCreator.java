package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVine;
import com.canoestudio.retrofuturemc.contents.blocks.CaveVine.CaveVinePlant;
import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.canoestudio.retrofuturemc.sounds.ModSoundHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class BerryCreator extends ItemFood implements IPlantable {
    public BerryCreator(String name, int hunger, float saturationModifier)
    {
        super(hunger, saturationModifier, false);
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setNoRepair();
        setCreativeTab(CREATIVE_TABS);

        ModItems.ITEMS.add(this);
    }

    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        ItemStack itemstack = player.getHeldItem(hand);
        IBlockState state = worldIn.getBlockState(pos);
        if (facing == EnumFacing.DOWN && player.canPlayerEdit(pos.offset(facing), facing, itemstack) && worldIn.isAirBlock(pos.down()))
        {
            if(state.getBlock() == ModBlocks.CAVE_VINE)
            {
                worldIn.setBlockState(pos, ModBlocks.CAVE_VINE_PLANT.getDefaultState().withProperty(CaveVinePlant.BERRIES, state.getValue(CaveVinePlant.BERRIES)));
                worldIn.setBlockState(pos.down(), ModBlocks.CAVE_VINE.getDefaultState().withProperty(CaveVine.AGE, 1));

                if(!player.capabilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }

                if(worldIn.isRemote)
                {
                    worldIn.playSound(player, pos, ModSoundHandler.BLOCK_CAVE_VINES_PLACE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                }

                return EnumActionResult.SUCCESS;
            }
            else if(state.isOpaqueCube() || state.getBlock() == ModBlocks.CAVE_VINE_PLANT)
            {
                worldIn.setBlockState(pos.down(), ModBlocks.CAVE_VINE.getDefaultState().withProperty(CaveVine.AGE, 1));

                if (!player.capabilities.isCreativeMode)
                {
                    itemstack.shrink(1);
                }

                if(worldIn.isRemote)
                {
                    worldIn.playSound(player, pos, ModSoundHandler.BLOCK_CAVE_VINES_PLACE, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
                }

                return EnumActionResult.SUCCESS;
            }

            return EnumActionResult.FAIL;
        }
        else
        {
            return EnumActionResult.FAIL;
        }
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Plains;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        return ModBlocks.CAVE_VINE.getDefaultState();
    }
}