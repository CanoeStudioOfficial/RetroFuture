package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockSculkCatalyst extends Block implements ITileEntityProvider {

    public static final PropertyBool BLOOM = PropertyBool.create("bloom");
    private static final int BLOOM_TICKS = 8;

    public BlockSculkCatalyst() {
        super(Material.ROCK);
        this.setRegistryName(RTWU.ID, "sculk_catalyst");
        this.setTranslationKey(RTWU.ID + ".sculk_catalyst");
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(1.5f);
        this.setResistance(1.5f);
        this.setSoundType(SoundType.SLIME);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BLOOM, false));
    }

    public void bloomFromDeath(World world, BlockPos catalystPos, EntityLivingBase deadEntity) {
        this.bloomFromDeath(world, catalystPos, deadEntity, Math.max(1, Math.min(1000, (int) Math.ceil(deadEntity.getMaxHealth() / 5.0f))));
    }

    public void bloomFromDeath(World world, BlockPos catalystPos, EntityLivingBase deadEntity, int charge) {
        if (world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(catalystPos);
        if (tile instanceof TileEntitySculkCatalyst && !((TileEntitySculkCatalyst) tile).canBloom()) {
            return;
        }

        world.setBlockState(catalystPos, this.getDefaultState().withProperty(BLOOM, true), 3);
        world.scheduleUpdate(catalystPos, this, BLOOM_TICKS);
        world.playSound(null, catalystPos, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 1.2f, 0.8f);

        if (tile instanceof TileEntitySculkCatalyst) {
            ((TileEntitySculkCatalyst) tile).addChargeCursor(new BlockPos(deadEntity).up(), Math.max(1, Math.min(1000, charge)));
            ((TileEntitySculkCatalyst) tile).markBlooming(BLOOM_TICKS + 20);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && state.getValue(BLOOM)) {
            worldIn.setBlockState(pos, state.withProperty(BLOOM, false), 3);
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BLOOM) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BLOOM, (meta & 1) != 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BLOOM);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySculkCatalyst();
    }
}
