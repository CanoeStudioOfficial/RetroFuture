package com.canoestudio.retrofuturethewildupdate.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntitySculkSensor extends TileEntity implements ITickable {

    private int cooldown;
    private int lastVibrationFrequency;

    public boolean canActivate(World world) {
        return this.cooldown <= 0 && world.getTotalWorldTime() >= this.getLastActivationTime() + 5L;
    }

    public void markActivated(World world, int vibrationFrequency, int cooldownTicks) {
        this.cooldown = cooldownTicks;
        this.lastVibrationFrequency = vibrationFrequency;
        this.getTileData().setLong("last_activation", world.getTotalWorldTime());
        this.markDirty();
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
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
        compound.setInteger("last_vibration_frequency", this.lastVibrationFrequency);
        compound.setLong("last_activation", this.getLastActivationTime());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.cooldown = compound.getInteger("cooldown");
        this.lastVibrationFrequency = compound.getInteger("last_vibration_frequency");
        this.getTileData().setLong("last_activation", compound.getLong("last_activation"));
    }

    private long getLastActivationTime() {
        return this.getTileData().getLong("last_activation");
    }
}
