package com.canoestudio.retrofuturemc.contents;

import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemSlab;

public class SingleSlabCreator extends RetroFutureSlabBase {
    public SingleSlabCreator(String name, Material material, SoundType soundType, float hardness, float resistance, String toolClass, int harvestLevel) {
        super(name, material, soundType, hardness, resistance, toolClass, harvestLevel);
    }

    public void registerItem(DoubleSlabCreator doubleSlab) {
        ModBlocks.BLOCKITEMS.add(new ItemSlab(this, this, doubleSlab).setRegistryName(getRegistryName()));
    }

    @Override
    public boolean isDouble() {
        return false;
    }
}
