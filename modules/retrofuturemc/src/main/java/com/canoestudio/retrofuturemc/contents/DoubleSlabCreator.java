package com.canoestudio.retrofuturemc.contents;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class DoubleSlabCreator extends RetroFutureSlabBase {
    public DoubleSlabCreator(String name, Material material, SoundType soundType, float hardness, float resistance, String toolClass, int harvestLevel) {
        super(name, material, soundType, hardness, resistance, toolClass, harvestLevel);
    }

    @Override
    public boolean isDouble() {
        return true;
    }
}
