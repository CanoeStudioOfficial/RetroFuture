package com.canoestudio.retrofuturethewildupdate.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TileEntitySculkCatalyst extends TileEntity implements ITickable {

    private static final int MAX_CURSORS = 32;
    private static final int MAX_CHARGE = 1000;
    private static final int GROWTH_SPAWN_COST = 10;
    private static final int NO_GROWTH_RADIUS = 4;
    private static final int CHARGE_DECAY_RATE = 10;
    private static final int ADDITIONAL_DECAY_RATE = 5;
    private static final BlockPos[] NON_CORNER_NEIGHBORS = createNonCornerNeighbors();

    private int cooldown;
    private final List<ChargeCursor> cursors = new ArrayList<>();

    public boolean canBloom() {
        return this.cooldown <= 0;
    }

    public void markBlooming(int cooldownTicks) {
        this.cooldown = cooldownTicks;
        this.markDirty();
    }

    public void addChargeCursor(BlockPos startPos, int charge) {
        while (charge > 0 && this.cursors.size() < MAX_CURSORS) {
            int currentCharge = Math.min(charge, MAX_CHARGE);
            this.cursors.add(new ChargeCursor(startPos, currentCharge));
            charge -= currentCharge;
        }
        this.markDirty();
    }

    @Override
    public void update() {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        if (!this.world.isRemote && !this.cursors.isEmpty()) {
            this.updateCursors();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("cooldown", this.cooldown);
        NBTTagList cursorList = new NBTTagList();
        for (ChargeCursor cursor : this.cursors) {
            NBTTagCompound cursorTag = new NBTTagCompound();
            cursorTag.setLong("pos", cursor.pos.toLong());
            cursorTag.setInteger("charge", cursor.charge);
            cursorTag.setInteger("decay_delay", cursor.decayDelay);
            cursorTag.setInteger("update_delay", cursor.updateDelay);
            cursorList.appendTag(cursorTag);
        }
        compound.setTag("cursors", cursorList);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.cooldown = compound.getInteger("cooldown");
        this.cursors.clear();
        NBTTagList cursorList = compound.getTagList("cursors", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < cursorList.tagCount() && this.cursors.size() < MAX_CURSORS; ++i) {
            NBTTagCompound cursorTag = cursorList.getCompoundTagAt(i);
            this.cursors.add(new ChargeCursor(
                BlockPos.fromLong(cursorTag.getLong("pos")),
                cursorTag.getInteger("charge"),
                cursorTag.getInteger("decay_delay"),
                cursorTag.getInteger("update_delay")
            ));
        }
    }

    private void updateCursors() {
        Iterator<ChargeCursor> iterator = this.cursors.iterator();
        while (iterator.hasNext()) {
            ChargeCursor cursor = iterator.next();
            if (cursor.isPosUnreasonable(this.pos)) {
                iterator.remove();
                continue;
            }

            cursor.update(this);
            if (cursor.charge <= 0) {
                iterator.remove();
            }
        }
        this.markDirty();
    }

    private int attemptUseCharge(ChargeCursor cursor) {
        IBlockState current = this.world.getBlockState(cursor.pos);
        if (current.getBlock() == ModBlocks.SCULK_VEIN) {
            return this.attemptPlaceSculkFromVein(cursor) ? cursor.charge - 1 : decayCharge(cursor);
        }

        if (cursor.charge != 0 && this.world.rand.nextInt(CHARGE_DECAY_RATE) == 0) {
            boolean closeToCatalyst = cursor.pos.distanceSq(this.pos) < NO_GROWTH_RADIUS * NO_GROWTH_RADIUS;
            if (!closeToCatalyst && current.getBlock() == ModBlocks.SCULK && canPlaceGrowth(cursor.pos)) {
                if (this.world.rand.nextInt(GROWTH_SPAWN_COST) < cursor.charge) {
                    BlockPos growthPos = cursor.pos.up();
                    IBlockState growthState = this.world.rand.nextInt(11) == 0
                        ? ModBlocks.SCULK_SHRIEKER.getDefaultState().withProperty(BlockSculkShrieker.CAN_SUMMON, false)
                        : ModBlocks.SCULK_SENSOR.getDefaultState();
                    this.world.setBlockState(growthPos, growthState, 3);
                    this.world.playSound(null, growthPos, growthState.getBlock().getSoundType(growthState, this.world, growthPos, null).getPlaceSound(),
                        SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                return Math.max(0, cursor.charge - GROWTH_SPAWN_COST);
            }
            return this.world.rand.nextInt(ADDITIONAL_DECAY_RATE) != 0
                ? cursor.charge
                : cursor.charge - (closeToCatalyst ? 1 : getDecayPenalty(cursor));
        }
        return cursor.charge;
    }

    private int decayCharge(ChargeCursor cursor) {
        return this.world.rand.nextInt(CHARGE_DECAY_RATE) == 0 ? cursor.charge / 2 : cursor.charge;
    }

    private int getDecayPenalty(ChargeCursor cursor) {
        double distance = Math.sqrt(cursor.pos.distanceSq(this.pos));
        double outerDistance = Math.max(0.0D, distance - NO_GROWTH_RADIUS);
        double maxReach = 24.0D - NO_GROWTH_RADIUS;
        double distanceFactor = Math.min(1.0D, (outerDistance * outerDistance) / (maxReach * maxReach));
        return Math.max(1, (int) (cursor.charge * distanceFactor * 0.5D));
    }

    private boolean attemptPlaceSculkFromVein(ChargeCursor cursor) {
        IBlockState veinState = this.world.getBlockState(cursor.pos);
        EnumFacing[] directions = EnumFacing.values();
        shuffle(directions);

        for (EnumFacing facing : directions) {
            if (!hasFace(veinState, facing)) {
                continue;
            }

            BlockPos substrate = cursor.pos.offset(facing);
            if (isSculkReplaceable(this.world.getBlockState(substrate))) {
                this.world.setBlockState(substrate, ModBlocks.SCULK.getDefaultState(), 3);
                this.world.playSound(null, substrate, ModBlocks.SCULK.getSoundType(ModBlocks.SCULK.getDefaultState(), this.world, substrate, null).getPlaceSound(),
                    SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.spreadVeinsAround(substrate, facing.getOpposite());
                return true;
            }
        }
        return false;
    }

    private void spreadVeinsAround(BlockPos sculkPos, EnumFacing skip) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == skip) {
                continue;
            }

            BlockPos veinPos = sculkPos.offset(facing);
            IBlockState existing = this.world.getBlockState(veinPos);
            if (existing.getMaterial() == Material.AIR || existing.getBlock().isReplaceable(this.world, veinPos)) {
                this.world.setBlockState(veinPos, ModBlocks.SCULK_VEIN.getDefaultState().withProperty(faceProperty(facing.getOpposite()), true), 3);
            }
        }
    }

    private boolean canPlaceGrowth(BlockPos pos) {
        if (!this.world.isAirBlock(pos.up())) {
            return false;
        }

        int growthCount = 0;
        for (BlockPos check : BlockPos.getAllInBoxMutable(pos.add(-4, 0, -4), pos.add(4, 2, 4))) {
            Block block = this.world.getBlockState(check).getBlock();
            if (block == ModBlocks.SCULK_SENSOR || block == ModBlocks.SCULK_SHRIEKER) {
                ++growthCount;
                if (growthCount > 2) {
                    return false;
                }
            }
        }
        return true;
    }

    private BlockPos getValidMovementPos(BlockPos current) {
        BlockPos[] neighbors = NON_CORNER_NEIGHBORS.clone();
        shuffle(neighbors);
        for (BlockPos offset : neighbors) {
            BlockPos next = current.add(offset);
            if ((this.world.getBlockState(next).getBlock() == ModBlocks.SCULK_VEIN
                || this.world.getBlockState(next).getBlock() == ModBlocks.SCULK
                || this.world.isAirBlock(next))
                && isMovementUnobstructed(current, next)) {
                return next;
            }
        }
        return null;
    }

    private boolean trySpreadVein(BlockPos pos) {
        IBlockState state = this.world.getBlockState(pos);
        if (state.getBlock() == ModBlocks.SCULK || state.getBlock() == ModBlocks.SCULK_CATALYST) {
            return false;
        }

        if (state.getBlock() == ModBlocks.SCULK_VEIN) {
            return true;
        }

        if (!this.world.isAirBlock(pos) && !state.getBlock().isReplaceable(this.world, pos)) {
            return false;
        }

        EnumFacing[] directions = EnumFacing.values();
        shuffle(directions);
        for (EnumFacing facing : directions) {
            BlockPos substrate = pos.offset(facing);
            IBlockState substrateState = this.world.getBlockState(substrate);
            if (substrateState.getBlock() == ModBlocks.SCULK || substrateState.getBlock() == ModBlocks.SCULK_CATALYST || isSculkReplaceable(substrateState)) {
                this.world.setBlockState(pos, ModBlocks.SCULK_VEIN.getDefaultState().withProperty(faceProperty(facing), true), 3);
                return true;
            }
        }
        return false;
    }

    private boolean isMovementUnobstructed(BlockPos from, BlockPos to) {
        if (Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY()) + Math.abs(from.getZ() - to.getZ()) == 1) {
            return true;
        }

        BlockPos delta = to.subtract(from);
        if (delta.getX() != 0 && !this.world.getBlockState(from.offset(delta.getX() < 0 ? EnumFacing.WEST : EnumFacing.EAST))
            .isSideSolid(this.world, from.offset(delta.getX() < 0 ? EnumFacing.WEST : EnumFacing.EAST), delta.getX() < 0 ? EnumFacing.EAST : EnumFacing.WEST)) {
            return true;
        }
        if (delta.getY() != 0 && !this.world.getBlockState(from.offset(delta.getY() < 0 ? EnumFacing.DOWN : EnumFacing.UP))
            .isSideSolid(this.world, from.offset(delta.getY() < 0 ? EnumFacing.DOWN : EnumFacing.UP), delta.getY() < 0 ? EnumFacing.UP : EnumFacing.DOWN)) {
            return true;
        }
        return delta.getZ() != 0 && !this.world.getBlockState(from.offset(delta.getZ() < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH))
            .isSideSolid(this.world, from.offset(delta.getZ() < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH), delta.getZ() < 0 ? EnumFacing.SOUTH : EnumFacing.NORTH);
    }

    private static boolean isSculkReplaceable(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();
        return state.isFullBlock()
            && material != Material.AIR
            && material != Material.WATER
            && material != Material.LAVA
            && material != Material.PORTAL
            && block != Blocks.BEDROCK
            && block != ModBlocks.SCULK
            && block != ModBlocks.SCULK_CATALYST
            && block != ModBlocks.SCULK_SENSOR
            && block != ModBlocks.SCULK_SHRIEKER
            && block != ModBlocks.SCULK_VEIN
            && (material == Material.ROCK
                || material == Material.GROUND
                || material == Material.GRASS
                || material == Material.SAND
                || block == Blocks.GRAVEL
                || block == Blocks.CLAY
                || block == Blocks.END_STONE
                || block == Blocks.SANDSTONE
                || block == Blocks.RED_SANDSTONE);
    }

    private static boolean hasFace(IBlockState state, EnumFacing facing) {
        switch (facing) {
            case DOWN:
                return state.getValue(BlockSculkVein.DOWN);
            case EAST:
                return state.getValue(BlockSculkVein.EAST);
            case NORTH:
                return state.getValue(BlockSculkVein.NORTH);
            case SOUTH:
                return state.getValue(BlockSculkVein.SOUTH);
            case UP:
                return state.getValue(BlockSculkVein.UP);
            case WEST:
            default:
                return state.getValue(BlockSculkVein.WEST);
        }
    }

    private static net.minecraft.block.properties.PropertyBool faceProperty(EnumFacing facing) {
        switch (facing) {
            case DOWN:
                return BlockSculkVein.DOWN;
            case EAST:
                return BlockSculkVein.EAST;
            case NORTH:
                return BlockSculkVein.NORTH;
            case SOUTH:
                return BlockSculkVein.SOUTH;
            case UP:
                return BlockSculkVein.UP;
            case WEST:
            default:
                return BlockSculkVein.WEST;
        }
    }

    private static BlockPos[] createNonCornerNeighbors() {
        List<BlockPos> offsets = new ArrayList<>();
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    if ((x == 0 || y == 0 || z == 0) && !(x == 0 && y == 0 && z == 0)) {
                        offsets.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return offsets.toArray(new BlockPos[0]);
    }

    private void shuffle(Object[] values) {
        for (int i = values.length - 1; i > 0; --i) {
            int j = this.world.rand.nextInt(i + 1);
            Object value = values[i];
            values[i] = values[j];
            values[j] = value;
        }
    }

    private void shuffle(EnumFacing[] values) {
        for (int i = values.length - 1; i > 0; --i) {
            int j = this.world.rand.nextInt(i + 1);
            EnumFacing value = values[i];
            values[i] = values[j];
            values[j] = value;
        }
    }

    private static class ChargeCursor {
        private BlockPos pos;
        private int charge;
        private int decayDelay;
        private int updateDelay;

        private ChargeCursor(BlockPos pos, int charge) {
            this(pos, charge, 1, 0);
        }

        private ChargeCursor(BlockPos pos, int charge, int decayDelay, int updateDelay) {
            this.pos = pos;
            this.charge = charge;
            this.decayDelay = decayDelay;
            this.updateDelay = updateDelay;
        }

        private boolean isPosUnreasonable(BlockPos originPos) {
            return Math.max(Math.max(Math.abs(this.pos.getX() - originPos.getX()), Math.abs(this.pos.getY() - originPos.getY())),
                Math.abs(this.pos.getZ() - originPos.getZ())) > 1024;
        }

        private void update(TileEntitySculkCatalyst catalyst) {
            if (this.charge <= 0) {
                return;
            }

            if (this.updateDelay > 0) {
                --this.updateDelay;
                return;
            }

            catalyst.trySpreadVein(this.pos);
            this.charge = catalyst.attemptUseCharge(this);
            if (this.charge <= 0) {
                return;
            }

            BlockPos next = catalyst.getValidMovementPos(this.pos);
            if (next != null) {
                this.pos = next;
            }

            this.decayDelay = Math.max(0, this.decayDelay - 1);
            this.updateDelay = 1;
        }
    }
}
