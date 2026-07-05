package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockSculk extends Block {

    public BlockSculk() {
        super(Material.ROCK);
        this.setRegistryName(RTWU.ID, "sculk");
        this.setTranslationKey(RTWU.ID + ".sculk");
        this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        this.setHardness(0.2f);
        this.setResistance(0.2f);
        this.setSoundType(SoundType.SLIME);
    }
}
