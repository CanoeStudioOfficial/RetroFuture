package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.entity.Warden;
import com.canoestudio.retrofuturethewildupdate.potion.ModPotions;
import com.canoestudio.retrofuturethewildupdate.sounds.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockSculkShrieker extends Block implements ITileEntityProvider {

    public static final PropertyBool SHRIEKING = PropertyBool.create("shrieking");
    public static final PropertyBool CAN_SUMMON = PropertyBool.create("can_summon");
    private static final AxisAlignedBB SHAPE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
    private static final int SHRIEK_TICKS = 90;

    public BlockSculkShrieker() {
        super(Material.ROCK);
        this.setRegistryName(RTWU.ID, "sculk_shrieker");
        this.setTranslationKey(RTWU.ID + ".sculk_shrieker");
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setHardness(1.5f);
        this.setResistance(1.5f);
        this.setSoundType(SoundType.SLIME);
        this.setDefaultState(this.blockState.getBaseState().withProperty(SHRIEKING, false).withProperty(CAN_SUMMON, true));
    }

    public void receiveVibration(World world, BlockPos pos, @Nullable Entity source, int strength) {
        this.receiveVibration(world, pos, source, SculkVibrationDispatcher.vibrationFromLegacyStrength(strength));
    }

    public void receiveVibration(World world, BlockPos pos, @Nullable Entity source, SculkVibration vibration) {
        if (world.isRemote) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySculkShrieker && !((TileEntitySculkShrieker) tile).canShriek()) {
            return;
        }

        if (!(source instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) source;
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        this.startShrieking(world, pos, player, vibration);
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        this.receiveVibration(worldIn, pos, entityIn, SculkVibration.STEP);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        this.receiveVibration(worldIn, pos, playerIn, 15);
        return true;
    }

    private void startShrieking(World world, BlockPos pos, EntityPlayer player, SculkVibration vibration) {
        IBlockState state = world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockSculkShrieker)) {
            return;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntitySculkShrieker) {
            int warningLevel = state.getValue(CAN_SUMMON)
                ? SculkWarningTracker.tryWarn(world, pos, player)
                : 0;
            if (state.getValue(CAN_SUMMON) && warningLevel <= 0) {
                return;
            }
            ((TileEntitySculkShrieker) tile).markShrieking(SHRIEK_TICKS + 30, warningLevel, player.getUniqueID());
        }

        world.setBlockState(pos, state.withProperty(SHRIEKING, true), 3);
        world.scheduleUpdate(pos, this, SHRIEK_TICKS);
        world.playSound(null, pos, SoundEvents.ENTITY_ENDERMEN_SCREAM, SoundCategory.BLOCKS, 1.7f, 0.45f);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote && state.getValue(SHRIEKING)) {
            worldIn.setBlockState(pos, state.withProperty(SHRIEKING, false), 3);
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TileEntitySculkShrieker) {
                this.respondAfterShriek(worldIn, pos, state, (TileEntitySculkShrieker) tile);
            }
        }
    }

    private void applyDarkness(World world, BlockPos pos) {
        List<EntityPlayer> players = world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(40.0));
        for (EntityPlayer player : players) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.addPotionEffect(new PotionEffect(ModPotions.DARKNESS, 260, 0, true, true));
            }
        }
    }

    private void respondAfterShriek(World world, BlockPos pos, IBlockState state, TileEntitySculkShrieker tile) {
        int warningLevel = tile.getWarningLevel();
        if (!state.getValue(CAN_SUMMON) || warningLevel <= 0 || world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            tile.clearWarningLevel();
            return;
        }

        EntityPlayer player = tile.getTriggerPlayer(world);
        boolean spawned = warningLevel >= SculkWarningTracker.MAX_WARNING_LEVEL && player != null && this.trySpawnWarden(world, pos, player);
        if (!spawned) {
            this.playWarningReply(world, pos, warningLevel);
        }
        this.applyDarkness(world, pos);
        tile.clearWarningLevel();
    }

    private void playWarningReply(World world, BlockPos pos, int warningLevel) {
        float pitch = 0.75f + Math.min(3, warningLevel) * 0.1f;
        world.playSound(null, pos.getX() + world.rand.nextInt(21) - 10, pos.getY() + world.rand.nextInt(11) - 5,
            pos.getZ() + world.rand.nextInt(21) - 10, ModSounds.WARDEN_NEARBY_CLOSER, SoundCategory.HOSTILE, 5.0f, pitch);
    }

    private boolean trySpawnWarden(World world, BlockPos shriekerPos, EntityPlayer player) {
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return false;
        }

        AxisAlignedBB existingArea = new AxisAlignedBB(shriekerPos).grow(48.0);
        if (!world.getEntitiesWithinAABB(Warden.class, existingArea).isEmpty()) {
            return false;
        }

        Random rand = world.rand;
        for (int i = 0; i < 24; ++i) {
            double angle = rand.nextDouble() * Math.PI * 2.0;
            double distance = 5.0 + rand.nextDouble() * 7.0;
            double x = player.posX + Math.cos(angle) * distance;
            double z = player.posZ + Math.sin(angle) * distance;
            int startY = Math.max(1, Math.min(255, (int) player.posY + rand.nextInt(5) - 2));

            for (int yOffset = -5; yOffset <= 5; ++yOffset) {
                BlockPos spawnPos = new BlockPos(x, startY + yOffset, z);
                if (this.canSpawnAt(world, spawnPos)) {
                    Warden warden = new Warden(world);
                    warden.setLocationAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                        rand.nextFloat() * 360.0f, 0.0f);
                    warden.startEmerging();
                    warden.enablePersistence();
                    if (world.spawnEntity(warden)) {
                        world.playSound(null, spawnPos, ModSounds.WARDEN_EMERGE, SoundCategory.HOSTILE, 1.5f, 1.0f);
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    private boolean canSpawnAt(World world, BlockPos pos) {
        AxisAlignedBB box = new AxisAlignedBB(
            pos.getX() - 0.4, pos.getY(), pos.getZ() - 0.4,
            pos.getX() + 1.4, pos.getY() + 2.8, pos.getZ() + 1.4
        );
        return world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP)
            && world.isAirBlock(pos)
            && world.isAirBlock(pos.up())
            && world.getCollisionBoxes(null, box).isEmpty()
            && !world.containsAnyLiquid(box);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return SHAPE;
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int meta = state.getValue(CAN_SUMMON) ? 1 : 0;
        return state.getValue(SHRIEKING) ? meta | 2 : meta;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState()
            .withProperty(CAN_SUMMON, (meta & 1) != 0)
            .withProperty(SHRIEKING, (meta & 2) != 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, SHRIEKING, CAN_SUMMON);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntitySculkShrieker();
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }
}
