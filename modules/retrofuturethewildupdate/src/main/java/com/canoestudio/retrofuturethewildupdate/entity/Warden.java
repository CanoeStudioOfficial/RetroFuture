package com.canoestudio.retrofuturethewildupdate.entity;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.sounds.ModSounds;
import com.canoestudio.retrofuturethewildupdate.potion.ModPotions;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Warden extends EntityMob {

    public static final DataParameter<Integer> POSE = EntityDataManager.createKey(Warden.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> ANGER_LEVEL = EntityDataManager.createKey(Warden.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> SONIC_BOOM_CHARGE = EntityDataManager.createKey(Warden.class, DataSerializers.VARINT);

    private static final int MAX_HEALTH = 500;
    private static final int ATTACK_DAMAGE = 45;
    private static final int DARKNESS_INTERVAL = 120;
    private static final int DARKNESS_DURATION = 260;
    private static final int MELEE_UNTIL_BOOM = 60;
    private static final int BOOM_MAX_CHARGE = 40;
    private static final int BOOM_COOLDOWN_TIME = 100;

    private int ticksSinceLastMelee;
    private int sonicBoomCooldown;
    public int animationTimer;
    private int heartbeatTimer;
    private int tendrilTimer;
    private int ticksWithoutSight;
    private int idleTimer;
    private EntityLivingBase previousTarget;
    public Map<UUID, Integer> angerMap;

    public Warden(World worldIn) {
        super(worldIn);
        this.ticksSinceLastMelee = 0;
        this.sonicBoomCooldown = 0;
        this.animationTimer = 0;
        this.heartbeatTimer = 40;
        this.tendrilTimer = 80;
        this.ticksWithoutSight = 0;
        this.idleTimer = 0;
        this.previousTarget = null;
        this.angerMap = new HashMap<>();
        this.setSize(1.9f, 2.4f);
        this.isImmuneToFire = true;
        this.experienceValue = 50;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        livingdata = super.onInitialSpawn(difficulty, livingdata);
        this.startEmerging();
        return livingdata;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2, true));
        this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 0.8));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 16.0f));
        this.tasks.addTask(4, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(POSE, WardenPose.IDLE.ordinal());
        this.dataManager.register(ANGER_LEVEL, 0);
        this.dataManager.register(SONIC_BOOM_CHARGE, 0);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (POSE.equals(key)) {
            WardenPose pose = this.getPose();
            if (pose == WardenPose.ROARING) {
                this.animationTimer = 84;
            } else if (pose == WardenPose.DIGGING) {
                this.animationTimer = 100;
            } else if (pose == WardenPose.EMERGING) {
                this.animationTimer = 134;
            } else if (pose == WardenPose.SNIFFING) {
                this.animationTimer = 84;
            } else {
                this.animationTimer = 0;
            }
        }
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.sonicBoomCooldown > 0) {
            --this.sonicBoomCooldown;
        }
        if (this.animationTimer > 0) {
            --this.animationTimer;
        }
        if (this.world.isRemote) {
            this.handleClientParticles();
            return;
        }
        if (this.heartbeatTimer > 0) {
            --this.heartbeatTimer;
        } else {
            this.playSound(ModSounds.WARDEN_HEARTBEAT, 1.5f, 1.0f);
            this.heartbeatTimer = 40 + this.rand.nextInt(70);
        }
        if (this.tendrilTimer > 0) {
            --this.tendrilTimer;
        } else {
            this.playSound(ModSounds.WARDEN_TENDRIL, 1.0f, 1.0f);
            this.tendrilTimer = 60 + this.rand.nextInt(50);
        }

        EntityLivingBase target = this.getAttackTarget();
        int currentCharge = this.getBoomCharge();

        if (target != null && target != this.previousTarget && this.animationTimer == 0 && currentCharge == 0) {
            this.setPose(WardenPose.ROARING);
            this.playSound(ModSounds.WARDEN_ROAR, 3.0f, 1.0f);
            this.previousTarget = target;
            this.getNavigator().clearPath();
        } else if (target == null && this.previousTarget != null) {
            this.previousTarget = null;
        }

        if (this.animationTimer == 0 && target == null && currentCharge == 0) {
            int randIdle = this.rand.nextInt(200);
            if (randIdle == 0) {
                this.setPose(WardenPose.SNIFFING);
                this.playSound(ModSounds.WARDEN_SNIFF, 2.0f, 1.0f);
            } else if (randIdle == 1) {
                this.playSound(ModSounds.WARDEN_NEARBY_CLOSER, 1.5f, 1.0f);
            }
        }

        if (this.getPose() != WardenPose.IDLE) {
            this.motionX = 0.0;
            this.motionZ = 0.0;
            this.getNavigator().clearPath();
            if (this.animationTimer == 0) {
                if (this.getPose() == WardenPose.DIGGING) {
                    this.setDead();
                    return;
                }
                this.setPose(WardenPose.IDLE);
            }
            return;
        }

        if (target != null && !target.isDead) {
            this.idleTimer = 0;
            if (!this.getEntitySenses().canSee(target)) {
                ++this.ticksWithoutSight;
                if (this.ticksWithoutSight > 100 && currentCharge == 0) {
                    this.setAttackTarget(null);
                    this.getNavigator().clearPath();
                    this.ticksWithoutSight = 0;
                    this.ticksSinceLastMelee = 0;
                    return;
                }
            } else {
                this.ticksWithoutSight = 0;
            }
            ++this.ticksSinceLastMelee;
            double distSq = this.getDistanceSq(target);
            boolean isVertical = Math.abs(this.posY - target.posY) > 2.5;
            boolean isFar = distSq > 100.0;
            boolean isStuck = this.ticksSinceLastMelee > MELEE_UNTIL_BOOM;
            if ((isVertical || isFar || isStuck) && this.sonicBoomCooldown <= 0 && currentCharge == 0) {
                this.startSonicBoom();
            }
        } else {
            this.ticksSinceLastMelee = 0;
            this.ticksWithoutSight = 0;
            if (currentCharge > 0) {
                this.setBoomCharge(0);
            }
            ++this.idleTimer;
            if (this.idleTimer >= 1200) {
                if (this.isInWater() || this.isInLava()) {
                    this.setDead();
                } else {
                    this.startDigging();
                }
                return;
            }
        }

        if (currentCharge > 0) {
            this.getNavigator().clearPath();
            this.motionX = 0.0;
            this.motionZ = 0.0;
            if (target != null) {
                this.getLookHelper().setLookPositionWithEntity(target, 30.0f, 30.0f);
                this.rotationYaw = this.rotationYawHead;
            }
            this.setBoomCharge(currentCharge + 1);
            if (currentCharge >= BOOM_MAX_CHARGE) {
                this.performSonicBoom(target);
                this.setBoomCharge(0);
                this.sonicBoomCooldown = BOOM_COOLDOWN_TIME;
                this.ticksSinceLastMelee = 0;
            }
        }

        if (this.ticksExisted % DARKNESS_INTERVAL == 0) {
            this.applyDarkness();
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return source != DamageSource.IN_WALL
            && this.getPose() != WardenPose.EMERGING
            && this.getPose() != WardenPose.DIGGING
            && super.attackEntityFrom(source, amount);
    }

    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }

    public int getAnimationTimer() {
        return this.animationTimer;
    }

    private void startSonicBoom() {
        this.setBoomCharge(1);
        this.playSound(ModSounds.WARDEN_SNIFF, 4.0f, 0.5f);
    }

    private void handleClientParticles() {
        int charge = this.dataManager.get(SONIC_BOOM_CHARGE);
        if (charge > 35) {
            Vec3d look = this.getLook(1.0f);
            for (int i = 0; i < 20; ++i) {
                double dist = 1.5 + i;
                double px = this.posX + look.x * dist;
                double py = this.posY + this.getEyeHeight() + look.y * dist;
                double pz = this.posZ + look.z * dist;
                RTWU.proxy.spawnSonicBoom(this.world, px, py, pz);
            }
        }
    }

    private void performSonicBoom(EntityLivingBase target) {
        if (target == null) {
            return;
        }
        this.playSound(ModSounds.WARDEN_SONIC_BOOM, 3.0f, 1.0f);
        target.attackEntityFrom(
            DamageSource.causeMobDamage(this).setDamageBypassesArmor().setMagicDamage(),
            13.0f
        );
        double d0 = target.posX - this.posX;
        double d2 = target.posZ - this.posZ;
        target.knockBack(this, 1.5f, d0, d2);
    }

    private void applyDarkness() {
        List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class,
            this.getEntityBoundingBox().grow(20.0));
        for (EntityPlayer player : players) {
            player.addPotionEffect(new PotionEffect(ModPotions.DARKNESS, DARKNESS_DURATION, 0, true, true));
        }
    }

    public void setPose(WardenPose pose) {
        this.dataManager.set(POSE, pose.ordinal());
        if (pose == WardenPose.ROARING) {
            this.animationTimer = 84;
        }
        if (pose == WardenPose.DIGGING) {
            this.animationTimer = 100;
        }
        if (pose == WardenPose.EMERGING) {
            this.animationTimer = 134;
        }
        if (pose == WardenPose.SNIFFING) {
            this.animationTimer = 84;
        }
    }

    public WardenPose getPose() {
        return WardenPose.values()[MathHelper.clamp(this.dataManager.get(POSE), 0, WardenPose.values().length - 1)];
    }

    private void setBoomCharge(int charge) {
        this.dataManager.set(SONIC_BOOM_CHARGE, charge);
    }

    private int getBoomCharge() {
        return this.dataManager.get(SONIC_BOOM_CHARGE);
    }

    public void startEmerging() {
        this.setPose(WardenPose.EMERGING);
    }

    public void startDigging() {
        this.setPose(WardenPose.DIGGING);
        this.world.playSound(null, this.posX, this.posY, this.posZ,
            ModSounds.WARDEN_DIG, SoundCategory.HOSTILE, 2.0f, 1.0f);
    }

    @Override
    public ItemStack getPickedResult(net.minecraft.util.math.RayTraceResult target) {
        return new ItemStack(com.canoestudio.retrofuturethewildupdate.item.ModItems.WARDEN_EGG);
    }

    @Override
    protected net.minecraft.util.SoundEvent getAmbientSound() {
        return ModSounds.WARDEN_AMBIENT;
    }

    @Override
    protected net.minecraft.util.SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.WARDEN_HURT;
    }

    @Override
    protected net.minecraft.util.SoundEvent getDeathSound() {
        return ModSounds.WARDEN_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(ModSounds.WARDEN_STEP, 0.35f, 1.0f);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean flag = super.attackEntityAsMob(entityIn);
        if (flag) {
            this.playSound(ModSounds.WARDEN_ATTACK_IMPACT, 1.0f, 1.0f);
            this.ticksSinceLastMelee = 0;
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entityIn;
                if (player.isActiveItemStackBlocking()) {
                    player.getCooldownTracker().setCooldown(player.getActiveItemStack().getItem(), 100);
                    player.resetActiveHand();
                }
            }
        }
        return flag;
    }

    @Override
    public boolean getCanSpawnHere() {
        return this.posY <= 42.0 && super.getCanSpawnHere();
    }

    public enum WardenPose {
        IDLE,
        ROARING,
        DIGGING,
        EMERGING,
        SNIFFING
    }
}
