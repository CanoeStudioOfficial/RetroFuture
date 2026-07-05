package com.canoestudio.retrofuturemc.contents.blocks;

import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

public class PowderSnowBlock extends Block {
    private static final AxisAlignedBB FALLING_COLLISION_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.9D, 1.0D);

    public PowderSnowBlock() {
        super(Material.SNOW);
        setTranslationKey(Tags.MOD_ID + ".powder_snow");
        setRegistryName("powder_snow");
        setHardness(0.25F);
        setResistance(0.25F);
        setSoundType(SoundType.SNOW);
        setCreativeTab(CREATIVE_TABS);

        ModBlocks.BLOCKS.add(this);
        ModBlocks.BLOCKITEMS.add(new ItemBlock(this).setRegistryName("powder_snow"));
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        if (worldIn instanceof World) {
            // 1.12 does not pass CollisionContext, so the event handler handles leather boot walking.
        }
        return NULL_AABB;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        entityIn.motionX *= 0.9D;
        entityIn.motionY *= 1.5D;
        entityIn.motionZ *= 0.9D;
        if (!worldIn.isRemote && entityIn.isBurning() && (entityIn instanceof EntityLivingBase || worldIn.getGameRules().getBoolean("mobGriefing"))) {
            entityIn.extinguish();
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, net.minecraft.util.EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
