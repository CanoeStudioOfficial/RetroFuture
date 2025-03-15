package com.canoestudio.retrofuturemc.content.blocks;

import com.canoestudio.retrofuturemc.content.BlockCreator;
import com.canoestudio.retrofuturemc.content.items.ItemBlockCandle;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.*;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockCandle extends BlockCreator {

    public static final PropertyBool LIT = PropertyBool.create("lit");
    public static final PropertyInteger CANDLES = PropertyInteger.create("candles", 1, 4);
    private static final AxisAlignedBB[] AABBS = {
            new AxisAlignedBB(0.4375, 0, 0.4375, 0.5625, 0.375, 0.5625),
            new AxisAlignedBB(0.3125, 0, 0.375, 0.6875, 0.375, 0.5625),
            new AxisAlignedBB(0.3125, 0, 0.375, 0.625, 0.375, 0.6875),
            new AxisAlignedBB(0.3125, 0, 0.3125, 0.6875, 0.375, 0.625)
    };

    private final Vec3d[][] wickSpot = new Vec3d[][]{
            {new Vec3d(0.5, 0.5, 0.5)},
            {new Vec3d(0.375, 0.4375, 0.5), new Vec3d(0.625, 0.5, 0.4375)},
            {new Vec3d(0.375, 0.4375, 0.5), new Vec3d(0.5625, 0.5, 0.4375), new Vec3d(0.5, 0.3125, 0.625)},
            {new Vec3d(0.375, 0.4375, 0.375), new Vec3d(0.5625, 0.5, 0.375), new Vec3d(0.4375, 0.3125, 0.5625), new Vec3d(0.625, 0.4375, 0.5625)}
    };

    private final EnumDyeColor color;

    public BlockCandle(String name, EnumDyeColor color) {
        super(name, 1, 1, "pickaxe");
        this.color = color;
        this.setSoundType(SoundType.CLOTH);
        this.setDefaultState(this.getBlockState().getBaseState().withProperty(LIT, false).withProperty(CANDLES, 1));
        Blocks.BLOCKITEMS.add(new ItemBlockCandle(this).setRegistryName(name.toLowerCase()));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, CANDLES, LIT);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.isSneaking() && stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() == state.getBlock()) {
            int candles = state.getValue(CANDLES);
            if (candles >= 4) return false;
            player.swingArm(hand);
            world.playSound(null, pos, SoundType.CLOTH.getPlaceSound(), SoundCategory.BLOCKS, 0.8f, 1.0f);
            world.setBlockState(pos, state.withProperty(CANDLES, candles + 1), 3);
            if (!player.isCreative()) stack.shrink(1);
            return true;
        }
        if (!player.isSneaking() && state.getValue(LIT)) {
            player.swingArm(hand);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.setBlockState(pos, state.withProperty(LIT, false), 3);
            return true;
        }
        return false;
    }

    @Override
    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (entity.isBurning()) light(world, world.getBlockState(pos), pos);
        super.onEntityWalk(world, pos, entity);
    }

    public static void light(World world, IBlockState state, BlockPos pos) {
        world.setBlockState(pos, state.withProperty(LIT, true), 3);
    }

    @Override
    public MapColor getMapColor(IBlockState state, IBlockAccess world, BlockPos pos) {
        return color == null ? MapColor.SAND : MapColor.getBlockColor(color);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return state.getValue(LIT) ? state.getValue(CANDLES) * 3 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(LIT, meta > 3).withProperty(CANDLES, (meta % 4) + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(LIT) ? 4 : 0) + state.getValue(CANDLES) - 1;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return AABBS[state.getValue(CANDLES) - 1];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (!state.getValue(LIT)) return;
        int candles = state.getValue(CANDLES);
        for (int c = 0; c < candles; c++) {
            if (rand.nextInt(4) == 0) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                        pos.getX() + wickSpot[candles - 1][c].x,
                        pos.getY() + wickSpot[candles - 1][c].y,
                        pos.getZ() + wickSpot[candles - 1][c].z,
                        0, 0, 0);
            }
        }
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}