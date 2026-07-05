package com.canoestudio.retrofuturemc.contents.blocks;

import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.BlockOre;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class RetroFutureOreBlock extends BlockOre {
    private final DropType dropType;

    public RetroFutureOreBlock(String name, DropType dropType, float hardness, float resistance, int harvestLevel) {
        this.dropType = dropType;
        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name.toLowerCase());
        setHardness(hardness);
        setResistance(resistance);
        setHarvestLevel("pickaxe", harvestLevel);
        setSoundType(SoundType.STONE);
        setCreativeTab(CREATIVE_TABS);

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName(name.toLowerCase()));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        switch (dropType) {
            case COAL:
                return Items.COAL;
            case IRON:
                return ModItems.RAW_IRON;
            case COPPER:
                return ModItems.RAW_COPPER;
            case GOLD:
                return ModItems.RAW_GOLD;
            case REDSTONE:
                return Items.REDSTONE;
            case EMERALD:
                return Items.EMERALD;
            case LAPIS:
                return Items.DYE;
            case DIAMOND:
                return Items.DIAMOND;
            default:
                return Item.getItemFromBlock(this);
        }
    }

    @Override
    public int quantityDropped(Random random) {
        if (dropType == DropType.LAPIS) {
            return 4 + random.nextInt(5);
        }
        if (dropType == DropType.REDSTONE) {
            return 4 + random.nextInt(2);
        }
        if (dropType == DropType.COPPER) {
            return 2 + random.nextInt(3);
        }
        return 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, Random random) {
        if (fortune > 0 && dropType != DropType.SELF) {
            int bonus = random.nextInt(fortune + 2) - 1;
            if (bonus < 0) {
                bonus = 0;
            }
            return quantityDropped(random) * (bonus + 1);
        }
        return quantityDropped(random);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return dropType == DropType.LAPIS ? 4 : 0;
    }

    @Override
    public int getExpDrop(IBlockState state, IBlockAccess world, BlockPos pos, int fortune) {
        Random rand = world instanceof World ? ((World) world).rand : new Random();
        switch (dropType) {
            case COAL:
                return MathHelper.getInt(rand, 0, 2);
            case DIAMOND:
            case EMERALD:
                return MathHelper.getInt(rand, 3, 7);
            case LAPIS:
            case REDSTONE:
                return MathHelper.getInt(rand, 2, 5);
            default:
                return 0;
        }
    }

    public enum DropType {
        SELF,
        COAL,
        IRON,
        COPPER,
        GOLD,
        REDSTONE,
        EMERALD,
        LAPIS,
        DIAMOND
    }
}
