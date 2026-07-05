package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.entity.Warden;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SculkWarningTracker {

    public static final int MAX_WARNING_LEVEL = 4;
    private static final int PLAYER_SEARCH_RADIUS = 16;
    private static final int WARDEN_SEARCH_RADIUS = 48;
    private static final long WARNING_DECAY_INTERVAL = 12000L;
    private static final long WARNING_COOLDOWN = 200L;
    private static final String WARNING_LEVEL = "rtwu_sculk_warning_level";
    private static final String LAST_WARNING_TIME = "rtwu_sculk_last_warning_time";
    private static final String COOLDOWN_UNTIL = "rtwu_sculk_warning_cooldown_until";

    public static int tryWarn(World world, BlockPos pos, EntityPlayer triggerPlayer) {
        if (hasNearbyWarden(world, pos)) {
            return 0;
        }

        List<EntityPlayer> players = getNearbyPlayers(world, pos, triggerPlayer);
        long now = world.getTotalWorldTime();
        for (EntityPlayer player : players) {
            updateDecay(player, now);
            if (isOnCooldown(player, now)) {
                return 0;
            }
        }

        EntityPlayer highestWarningPlayer = players.stream()
            .max(Comparator.comparingInt(SculkWarningTracker::getWarningLevel))
            .orElse(triggerPlayer);
        int warningLevel = Math.min(MAX_WARNING_LEVEL, getWarningLevel(highestWarningPlayer) + 1);

        for (EntityPlayer player : players) {
            NBTTagCompound data = player.getEntityData();
            data.setInteger(WARNING_LEVEL, warningLevel);
            data.setLong(LAST_WARNING_TIME, now);
            data.setLong(COOLDOWN_UNTIL, now + WARNING_COOLDOWN);
        }
        return warningLevel;
    }

    private static boolean hasNearbyWarden(World world, BlockPos pos) {
        return !world.getEntitiesWithinAABB(Warden.class, new AxisAlignedBB(pos).grow(WARDEN_SEARCH_RADIUS)).isEmpty();
    }

    private static List<EntityPlayer> getNearbyPlayers(World world, BlockPos pos, EntityPlayer triggerPlayer) {
        List<EntityPlayer> players = new ArrayList<>();
        for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos).grow(PLAYER_SEARCH_RADIUS))) {
            if (!player.isSpectator() && !player.isCreative() && player.isEntityAlive()) {
                players.add(player);
            }
        }
        if (!players.contains(triggerPlayer)) {
            players.add(triggerPlayer);
        }
        return players;
    }

    private static void updateDecay(EntityPlayer player, long now) {
        NBTTagCompound data = player.getEntityData();
        int warningLevel = data.getInteger(WARNING_LEVEL);
        long lastWarningTime = data.getLong(LAST_WARNING_TIME);
        if (warningLevel <= 0 || lastWarningTime <= 0L) {
            return;
        }

        long elapsedIntervals = (now - lastWarningTime) / WARNING_DECAY_INTERVAL;
        if (elapsedIntervals > 0L) {
            int newLevel = Math.max(0, warningLevel - (int) elapsedIntervals);
            data.setInteger(WARNING_LEVEL, newLevel);
            data.setLong(LAST_WARNING_TIME, now);
        }
    }

    private static boolean isOnCooldown(EntityPlayer player, long now) {
        return player.getEntityData().getLong(COOLDOWN_UNTIL) > now;
    }

    private static int getWarningLevel(EntityPlayer player) {
        return player.getEntityData().getInteger(WARNING_LEVEL);
    }

    private SculkWarningTracker() {
    }
}
