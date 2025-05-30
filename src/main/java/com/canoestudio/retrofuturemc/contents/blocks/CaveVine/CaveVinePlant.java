package com.canoestudio.retrofuturemc.contents.blocks.CaveVine;


import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.canoestudio.retrofuturemc.sounds.ModSoundHandler;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.util.Random;

import static com.canoestudio.retrofuturemc.contents.tab.CreativeTab.CREATIVE_TABS;

@Mod.EventBusSubscriber(modid = Tags.MOD_ID)
public class CaveVinePlant extends BlockBush implements IGrowable {

    public static final SoundType CAVE_VINES = new SoundType(1.0F, 1.0F, ModSoundHandler.BLOCK_CAVE_VINES_BREAK, ModSoundHandler.BLOCK_CAVE_VINES_STEP, ModSoundHandler.BLOCK_CAVE_VINES_PLACE, ModSoundHandler.BLOCK_CAVE_VINES_HIT, ModSoundHandler.BLOCK_CAVE_VINES_FALL);

    public static final PropertyBool BERRIES = PropertyBool.create("berries");

    public CaveVinePlant(String name)
    {
        super(Material.PLANTS);
        setHardness(0.0F);
        setLightOpacity(0);

        this.setSoundType(CAVE_VINES);

        setTranslationKey(Tags.MOD_ID + "." + name.toLowerCase());
        setRegistryName(name);
        setCreativeTab(CREATIVE_TABS);
        this.setDefaultState(this.getDefaultState().withProperty(BERRIES, false));

        ModBlocks.BLOCKS.add(this);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        entity.onGround = true;

        World worldIn = entity.getEntityWorld();

        if(worldIn.isRemote)
        {
            if(entity == Minecraft.getMinecraft().player)
            {
                if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
                {
                    entity.motionY += 0.03;
                }
            }
        }

        entity.fallDistance = 0;
        return true;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if(state.getValue(BERRIES))
            return 14;
        return 0;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {BERRIES});
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
        return !state.getValue(BERRIES);
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        return !state.getValue(BERRIES);
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
        worldIn.setBlockState(pos, this.getDefaultState().withProperty(BERRIES, true), 2);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        if(meta == 1)
            return this.getDefaultState().withProperty(BERRIES, true);
        return this.getDefaultState().withProperty(BERRIES, false);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        if(state.getValue(BERRIES))
            return 1;
        return 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        if(state.getValue(BERRIES))
        {
            return ModItems.Glow_Berries;
        }
        return Items.AIR;
    }

    /**
     * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
     * Block.removedByPlayer
     */
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        if (!worldIn.isRemote && EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0)
        {
            player.addStat(StatList.getBlockStats(this));
            spawnAsEntity(worldIn, pos, new ItemStack(ModItems.Glow_Berries));
        }

        super.harvestBlock(worldIn, player, pos, state, te, stack);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(ModItems.Glow_Berries);
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        boolean flag1 = playerIn.getHeldItemOffhand().getItem() == ModItems.Glow_Berries;
        boolean flag2 = playerIn.getHeldItemMainhand().getItem() == ModItems.Glow_Berries;
        boolean flag3 = playerIn.getHeldItemOffhand().getItem() instanceof ItemBlock;
        boolean flag4 = playerIn.getHeldItemOffhand().getItem() instanceof ItemBlock;

        boolean flag = flag1 || flag2 || flag3 || flag4;

        if(facing == EnumFacing.DOWN && flag)
        {
            return false;
        }
        if(state.getValue(BERRIES))
        {
            if (!worldIn.isRemote)
            {
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.Glow_Berries)));
                worldIn.setBlockState(pos, state.withProperty(BERRIES, false));
            }

            if (worldIn.isRemote)
            {
                worldIn.playSound(playerIn, pos, ModSoundHandler.PICK_BERRIES, SoundCategory.BLOCKS, 1.0F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            }
        }

        return false;
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState top = worldIn.getBlockState(pos.up());
        if(top.getBlock() == ModBlocks.CAVE_VINE || top.getBlock() == ModBlocks.CAVE_VINE_PLANT)
        {
            return true;
        }
        else
        {
            return top.isSideSolid(worldIn, pos.up(), EnumFacing.DOWN);
        }
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return this.canSustainBush(worldIn, pos.up(), worldIn.getBlockState(pos.up()));
    }

    /**
     * Return true if the block can sustain a Bush
     */
    protected boolean canSustainBush(World worldIn, BlockPos pos, IBlockState state)
    {
        if(state.getBlock() == ModBlocks.CAVE_VINE || state.getBlock() == ModBlocks.CAVE_VINE_PLANT)
        {
            return true;
        }
        return state.isSideSolid(worldIn, pos, EnumFacing.DOWN);
    }

    protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.canBlockStay(worldIn, pos, state))
        {
            worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
    }
}
