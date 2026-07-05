package com.canoestudio.retrofuturethewildupdate.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = com.canoestudio.retrofuturethewildupdate.RTWU.ID)
public final class SculkVibrationDispatcher {

    private static final int SENSOR_RANGE = 8;
    private static final int SHRIEKER_RANGE = 8;
    private static final int CATALYST_RANGE = 8;
    private static final Map<UUID, BlockPos> LAST_PLAYER_STEP = new HashMap<>();

    public static void emit(World world, BlockPos sourcePos, @Nullable Entity source, int strength) {
        emit(world, sourcePos, source, vibrationFromLegacyStrength(strength));
    }

    public static void emit(World world, BlockPos sourcePos, @Nullable Entity source, SculkVibration vibration) {
        if (world.isRemote) {
            return;
        }
        notifySculkBlocks(world, sourcePos, source, vibration, true);
    }

    public static void emitFromSensor(World world, BlockPos sensorPos, @Nullable Entity source, int strength) {
        emitFromSensor(world, sensorPos, source, vibrationFromLegacyStrength(strength));
    }

    public static void emitFromSensor(World world, BlockPos sensorPos, @Nullable Entity source, SculkVibration vibration) {
        if (world.isRemote) {
            return;
        }
        notifySculkBlocks(world, sensorPos, source, vibration, false);
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;
        if (world.isRemote || entity.isSneaking() || entity.ticksExisted % 5 != 0) {
            return;
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            UUID id = player.getUniqueID();
            BlockPos current = player.getPosition();
            BlockPos previous = LAST_PLAYER_STEP.get(id);
            if (previous != null && previous.equals(current)) {
                return;
            }
            LAST_PLAYER_STEP.put(id, current);
        }

        double speedSq = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
        if (entity.onGround && speedSq > 0.0025 && !isDampeningBlock(world.getBlockState(entity.getPosition()))) {
            emit(world, entity.getPosition(), entity, SculkVibration.STEP);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        emit(event.getWorld(), event.getPos(), event.getPlayer(), SculkVibration.BLOCK_DESTROY);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.PlaceEvent event) {
        if (isDampeningBlock(event.getPlacedBlock())) {
            return;
        }
        emit(event.getWorld(), event.getPos(), event.getPlayer(), SculkVibration.BLOCK_PLACE);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        emit(event.getWorld(), event.getPos(), event.getEntityPlayer(), SculkVibration.BLOCK_ACTIVATE);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        emit(entity.world, entity.getPosition(), entity, SculkVibration.ENTITY_DAMAGE);
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;
        if (world.isRemote) {
            return;
        }

        emit(world, entity.getPosition(), entity, SculkVibration.ENTITY_DIE);
    }

    @SubscribeEvent
    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;
        if (world.isRemote || event.getDroppedExperience() <= 0) {
            return;
        }

        if (notifyCatalysts(world, entity.getPosition(), entity, Math.min(1000, event.getDroppedExperience()))) {
            event.setDroppedExperience(0);
        }
    }

    private static void notifySculkBlocks(World world, BlockPos sourcePos, @Nullable Entity source, SculkVibration vibration, boolean includeSensors) {
        int range = Math.max(SENSOR_RANGE, SHRIEKER_RANGE);
        for (BlockPos pos : BlockPos.getAllInBoxMutable(sourcePos.add(-range, -range, -range), sourcePos.add(range, range, range))) {
            IBlockState state = world.getBlockState(pos);
            double distanceSq = pos.distanceSq(sourcePos);
            BlockPos listenerPos = pos.toImmutable();
            if (isOccludedByWool(world, sourcePos, listenerPos)) {
                continue;
            }

            if (includeSensors && state.getBlock() instanceof BlockSculkSensor && distanceSq <= SENSOR_RANGE * SENSOR_RANGE) {
                ((BlockSculkSensor) state.getBlock()).receiveVibration(world, listenerPos, source, vibration, redstoneStrengthFromDistance(distanceSq, SENSOR_RANGE));
            } else if (state.getBlock() instanceof BlockSculkShrieker && distanceSq <= SHRIEKER_RANGE * SHRIEKER_RANGE) {
                ((BlockSculkShrieker) state.getBlock()).receiveVibration(world, listenerPos, source, vibration);
            }
        }
    }

    private static boolean notifyCatalysts(World world, BlockPos deathPos, EntityLivingBase deadEntity, int charge) {
        boolean consumed = false;
        for (BlockPos pos : BlockPos.getAllInBoxMutable(deathPos.add(-CATALYST_RANGE, -CATALYST_RANGE, -CATALYST_RANGE), deathPos.add(CATALYST_RANGE, CATALYST_RANGE, CATALYST_RANGE))) {
            IBlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof BlockSculkCatalyst && pos.distanceSq(deathPos) <= CATALYST_RANGE * CATALYST_RANGE) {
                ((BlockSculkCatalyst) state.getBlock()).bloomFromDeath(world, pos.toImmutable(), deadEntity, charge);
                consumed = true;
            }
        }
        return consumed;
    }

    private static int redstoneStrengthFromDistance(double distanceSq, int listenerRadius) {
        double distance = Math.sqrt(distanceSq);
        double powerScale = 15.0D / listenerRadius;
        return Math.max(1, 15 - MathHelper.floor(powerScale * distance));
    }

    private static boolean isOccludedByWool(World world, BlockPos sourcePos, BlockPos listenerPos) {
        Vec3d start = new Vec3d(sourcePos.getX() + 0.5D, sourcePos.getY() + 0.5D, sourcePos.getZ() + 0.5D);
        Vec3d end = new Vec3d(listenerPos.getX() + 0.5D, listenerPos.getY() + 0.5D, listenerPos.getZ() + 0.5D);
        double distance = start.distanceTo(end);
        int samples = Math.max(1, MathHelper.ceil(distance * 4.0D));

        for (int i = 1; i < samples; ++i) {
            double t = (double) i / samples;
            BlockPos sample = new BlockPos(
                start.x + (end.x - start.x) * t,
                start.y + (end.y - start.y) * t,
                start.z + (end.z - start.z) * t
            );
            if (!sample.equals(sourcePos) && !sample.equals(listenerPos) && isVibrationOccludingBlock(world.getBlockState(sample))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDampeningBlock(IBlockState state) {
        return state.getBlock() == Blocks.WOOL || state.getBlock() == Blocks.CARPET;
    }

    private static boolean isVibrationOccludingBlock(IBlockState state) {
        return state.getBlock() == Blocks.WOOL;
    }

    public static SculkVibration vibrationFromLegacyStrength(int strength) {
        if (strength >= 15) {
            return SculkVibration.ENTITY_DIE;
        }
        if (strength >= 13) {
            return SculkVibration.BLOCK_PLACE;
        }
        if (strength >= 12) {
            return SculkVibration.BLOCK_DESTROY;
        }
        if (strength >= 8) {
            return SculkVibration.ENTITY_DAMAGE;
        }
        if (strength >= 6) {
            return SculkVibration.ENTITY_INTERACT;
        }
        return SculkVibration.STEP;
    }

    private SculkVibrationDispatcher() {
    }
}
