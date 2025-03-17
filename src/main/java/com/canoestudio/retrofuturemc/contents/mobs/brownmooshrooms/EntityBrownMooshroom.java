package com.canoestudio.retrofuturemc.contents.mobs.brownmooshrooms;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;

public class EntityBrownMooshroom extends EntityMooshroom {
    public EntityBrownMooshroom(World world) {
        super(world);
    }

    @Override
    public EntityMooshroom createChild(EntityAgeable other) {
        return new EntityBrownMooshroom(world);
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
        //Copypaste of super#onSheared

        this.setDead();
        ((net.minecraft.world.WorldServer)this.world).spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, false, this.posX, this.posY + (double)(this.height / 2.0F), this.posZ, 1, 0.0D, 0.0D, 0.0D, 0.0D);

        EntityCow entitycow = new EntityCow(this.world);
        entitycow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
        entitycow.setHealth(this.getHealth());
        entitycow.renderYawOffset = this.renderYawOffset;

        if (this.hasCustomName()) {
            entitycow.setCustomNameTag(this.getCustomNameTag());
        }

        this.world.spawnEntity(entitycow);

        java.util.List<ItemStack> ret = new java.util.ArrayList<ItemStack>();
        for (int i = 0; i < 5; ++i) {
            ret.add(new ItemStack(Blocks.BROWN_MUSHROOM)); //CHANGE: Change the mushroom duhh
        }

        this.playSound(SoundEvents.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
        return ret;
    }
}