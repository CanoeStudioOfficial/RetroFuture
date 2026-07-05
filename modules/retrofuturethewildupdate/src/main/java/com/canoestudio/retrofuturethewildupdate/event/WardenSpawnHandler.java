package com.canoestudio.retrofuturethewildupdate.event;

import com.canoestudio.retrofuturethewildupdate.ModConfig;
import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.entity.Warden;
import com.canoestudio.retrofuturethewildupdate.sounds.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public class WardenSpawnHandler {

    private static final long SPAWN_COOLDOWN = 24000L;
    private static final int CHECK_INTERVAL = 100;
    private static final int SPAWN_CHANCE = 420;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END
            || event.player.world.isRemote
            || event.player.isCreative()
            || event.player.isSpectator()) {
            return;
        }
        if (!ModConfig.general.enableWardenSpawns) {
            return;
        }

        EntityPlayer player = event.player;
        World world = player.world;

        if (world.getTotalWorldTime() % CHECK_INTERVAL != 0L) {
            return;
        }
        if (player.posY > 45.0) {
            return;
        }
        if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            return;
        }

        AxisAlignedBB spawnArea = player.getEntityBoundingBox().grow(100.0, 100.0, 100.0);
        List<Warden> nearbyWardens = world.getEntitiesWithinAABB(Warden.class, spawnArea);
        if (!nearbyWardens.isEmpty()) {
            return;
        }

        NBTTagCompound data = player.getEntityData();
        long lastSpawnTime = data.getLong("rtwu_last_warden_spawn");
        long currentTime = world.getTotalWorldTime();
        if (currentTime - lastSpawnTime < SPAWN_COOLDOWN && lastSpawnTime != 0L) {
            return;
        }

        Random rand = world.rand;
        if (rand.nextInt(SPAWN_CHANCE) != 0) {
            return;
        }

        attemptSurpriseSpawn(player, world, currentTime);
    }

    private static void attemptSurpriseSpawn(EntityPlayer player, World world, long currentTime) {
        Random rand = world.rand;
        for (int i = 0; i < 25; ++i) {
            double angle = rand.nextDouble() * Math.PI * 2.0;
            double dist = 4.0 + rand.nextDouble() * 8.0;
            double spawnX = player.posX + Math.cos(angle) * dist;
            double spawnZ = player.posZ + Math.sin(angle) * dist;
            int yStart = (int) player.posY;
            BlockPos floorPos = null;

            for (int k = -4; k <= 4; ++k) {
                BlockPos checkPos = new BlockPos(spawnX, yStart + k, spawnZ);
                if (world.getBlockState(checkPos.down()).isSideSolid((IBlockAccess) world, checkPos.down(), EnumFacing.UP)
                    && world.isAirBlock(checkPos)
                    && world.isAirBlock(checkPos.up())) {
                    floorPos = checkPos;
                    break;
                }
            }

            if (floorPos != null) {
                double spawnY = floorPos.getY();
                AxisAlignedBB checkBox = new AxisAlignedBB(
                    spawnX - 0.8, spawnY + 0.1, spawnZ - 0.8,
                    spawnX + 0.8, spawnY + 2.5, spawnZ + 0.8
                );
                Vec3d playerEyes = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
                Vec3d spawnTarget = new Vec3d(spawnX, spawnY + 1.0, spawnZ);
                boolean isSameRoom = world.rayTraceBlocks(playerEyes, spawnTarget, false, true, false) == null;

                if (isSameRoom
                    && world.getCollisionBoxes(null, checkBox).isEmpty()
                    && !world.containsAnyLiquid(checkBox)) {

                    Warden warden = new Warden(world);
                    warden.setLocationAndAngles(spawnX, spawnY, spawnZ, rand.nextFloat() * 360.0f, 0.0f);
                    warden.startEmerging();
                    warden.enablePersistence();

                    if (world.spawnEntity(warden)) {
                        AxisAlignedBB groupArea = player.getEntityBoundingBox().grow(100.0, 100.0, 100.0);
                        List<EntityPlayer> nearbyPlayers = world.getEntitiesWithinAABB(EntityPlayer.class, groupArea);
                        for (EntityPlayer groupPlayer : nearbyPlayers) {
                            groupPlayer.getEntityData().setLong("rtwu_last_warden_spawn", currentTime);
                        }
                        world.playSound(null, spawnX, spawnY, spawnZ,
                            ModSounds.WARDEN_EMERGE, SoundCategory.HOSTILE, 1.5f, 1.0f);
                        return;
                    }
                }
            }
        }
    }
}
