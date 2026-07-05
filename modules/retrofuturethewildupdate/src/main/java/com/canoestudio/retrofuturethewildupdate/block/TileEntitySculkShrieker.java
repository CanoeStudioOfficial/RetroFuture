package com.canoestudio.retrofuturethewildupdate.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

import java.util.UUID;

public class TileEntitySculkShrieker extends TileEntity implements ITickable {

    private int cooldown;
    private int warningLevel;
    private UUID triggerPlayer;

    public boolean canShriek() {
        return this.cooldown <= 0;
    }

    public void markShrieking(int cooldownTicks) {
        this.markShrieking(cooldownTicks, 0, null);
    }

    public void markShrieking(int cooldownTicks, int warningLevel, UUID triggerPlayer) {
        this.cooldown = cooldownTicks;
        this.warningLevel = warningLevel;
        this.triggerPlayer = triggerPlayer;
        this.markDirty();
    }

    public int getWarningLevel() {
        return this.warningLevel;
    }

    public EntityPlayer getTriggerPlayer(World world) {
        return this.triggerPlayer == null ? null : world.getPlayerEntityByUUID(this.triggerPlayer);
    }

    public void clearWarningLevel() {
        this.warningLevel = 0;
        this.triggerPlayer = null;
        this.markDirty();
    }

    @Override
    public void update() {
        if (this.cooldown > 0) {
            --this.cooldown;
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("cooldown", this.cooldown);
        compound.setInteger("warning_level", this.warningLevel);
        if (this.triggerPlayer != null) {
            compound.setUniqueId("trigger_player", this.triggerPlayer);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.cooldown = compound.getInteger("cooldown");
        this.warningLevel = compound.getInteger("warning_level");
        this.triggerPlayer = compound.hasUniqueId("trigger_player") ? compound.getUniqueId("trigger_player") : null;
    }
}
