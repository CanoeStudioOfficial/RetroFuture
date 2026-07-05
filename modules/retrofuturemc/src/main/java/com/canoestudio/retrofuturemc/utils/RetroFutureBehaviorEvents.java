package com.canoestudio.retrofuturemc.utils;

import com.canoestudio.retrofuturemc.contents.blocks.CandleCakeBlock;
import com.canoestudio.retrofuturemc.contents.blocks.CopperBehavior;
import com.canoestudio.retrofuturemc.contents.blocks.ModBlocks;
import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.sounds.ModSoundHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RetroFutureBehaviorEvents {
    private static final DamageSource FREEZE = new DamageSource("freeze").setDamageBypassesArmor();
    private static final int TICKS_TO_FREEZE = 140;
    private static final int FREEZE_DAMAGE_INTERVAL = 40;
    private final Map<UUID, Integer> frozenTicks = new HashMap<>();

    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        EntityPlayer player = event.getEntityPlayer();
        ItemStack stack = event.getItemStack();

        if (handleCopperInteraction(world, pos, state, player, stack, event)) {
            return;
        }

        if (handlePowderSnowPickup(world, pos, state, player, stack, event)) {
            return;
        }

        handleCandleCakePlacement(world, pos, state, player, stack, event);
    }

    private boolean handleCopperInteraction(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack stack, PlayerInteractEvent.RightClickBlock event) {
        if (!CopperBehavior.isCopper(state.getBlock()) || stack.isEmpty()) {
            return false;
        }

        if (isAxe(stack) && CopperBehavior.canScrape(state)) {
            if (!world.isRemote) {
                CopperBehavior.scrape(world, pos, state);
                world.playSound(null, pos, ModSoundHandler.STRIP_WOOD, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.capabilities.isCreativeMode) {
                    stack.damageItem(1, player);
                }
            }
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            return true;
        }

        if (isWax(stack) && CopperBehavior.canWax(state)) {
            if (!world.isRemote) {
                CopperBehavior.wax(world, pos, state);
                world.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_SLIME_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!player.capabilities.isCreativeMode) {
                    stack.shrink(1);
                }
            }
            event.setCanceled(true);
            event.setCancellationResult(EnumActionResult.SUCCESS);
            return true;
        }

        return false;
    }

    private boolean handlePowderSnowPickup(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack stack, PlayerInteractEvent.RightClickBlock event) {
        if (state.getBlock() != ModBlocks.POWDER_SNOW || stack.getItem() != Items.BUCKET) {
            return false;
        }

        if (!world.isRemote) {
            world.setBlockToAir(pos);
            world.playSound(null, pos, net.minecraft.init.SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
                ItemStack filled = new ItemStack(ModItems.POWDER_SNOW_BUCKET);
                if (stack.isEmpty()) {
                    player.setHeldItem(event.getHand(), filled);
                } else if (!player.inventory.addItemStackToInventory(filled)) {
                    player.dropItem(filled, false);
                }
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
        return true;
    }

    private void handleCandleCakePlacement(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack stack, PlayerInteractEvent.RightClickBlock event) {
        if (state.getBlock() != Blocks.CAKE || state.getValue(BlockCake.BITES) != 0 || !(stack.getItem() instanceof ItemBlock)) {
            return;
        }

        Block candle = ((ItemBlock) stack.getItem()).getBlock();
        CandleCakeBlock cake = CandleCakeBlock.byCandle(candle);
        if (cake == null) {
            return;
        }

        if (!world.isRemote) {
            world.setBlockState(pos, cake.getDefaultState(), 3);
            world.playSound(null, pos, cake.getSoundType(cake.getDefaultState(), world, pos, player).getPlaceSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!player.capabilities.isCreativeMode) {
                stack.shrink(1);
            }
        }

        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setCanceled(true);
        event.setCancellationResult(EnumActionResult.SUCCESS);
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = entity.world;
        if (entity.isDead) {
            frozenTicks.remove(entity.getUniqueID());
            return;
        }
        if (world.isRemote) {
            return;
        }

        BlockPos feet = new BlockPos(entity.posX, entity.getEntityBoundingBox().minY + 0.05D, entity.posZ);
        BlockPos belowFeet = new BlockPos(entity.posX, entity.getEntityBoundingBox().minY - 0.05D, entity.posZ);
        boolean inPowderSnow = world.getBlockState(feet).getBlock() == ModBlocks.POWDER_SNOW || world.getBlockState(belowFeet).getBlock() == ModBlocks.POWDER_SNOW;

        if (inPowderSnow && wearsLeatherBoots(entity) && !entity.isSneaking() && entity.motionY <= 0.0D) {
            BlockPos surface = world.getBlockState(feet).getBlock() == ModBlocks.POWDER_SNOW ? feet : belowFeet;
            double targetY = surface.getY() + 1.0D;
            if (entity.posY < targetY && entity.posY > surface.getY() + 0.2D) {
                entity.setPosition(entity.posX, targetY, entity.posZ);
                entity.motionY = 0.0D;
                entity.fallDistance = 0.0F;
                entity.onGround = true;
            }
        }

        updateFreeze(entity, inPowderSnow && !wearsLeatherArmor(entity));
    }

    private void updateFreeze(EntityLivingBase entity, boolean freezing) {
        UUID id = entity.getUniqueID();
        int ticks = frozenTicks.containsKey(id) ? frozenTicks.get(id) : 0;
        if (freezing) {
            ticks = Math.min(TICKS_TO_FREEZE, ticks + 1);
        } else {
            ticks = Math.max(0, ticks - 2);
        }

        if (ticks == 0) {
            frozenTicks.remove(id);
            return;
        }

        frozenTicks.put(id, ticks);
        if (ticks >= TICKS_TO_FREEZE && entity.ticksExisted % FREEZE_DAMAGE_INTERVAL == 0) {
            entity.attackEntityFrom(FREEZE, 1.0F);
        }
    }

    private boolean isAxe(ItemStack stack) {
        return stack.getItem().getToolClasses(stack).contains("axe");
    }

    private boolean isWax(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        int[] ids = OreDictionary.getOreIDs(stack);
        for (int id : ids) {
            String name = OreDictionary.getOreName(id);
            if ("wax".equals(name) || "honeycomb".equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean wearsLeatherBoots(EntityLivingBase entity) {
        return entity.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.LEATHER_BOOTS;
    }

    private boolean wearsLeatherArmor(EntityLivingBase entity) {
        ItemStack head = entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        ItemStack chest = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack legs = entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        return isLeather(head.getItem()) || isLeather(chest.getItem()) || isLeather(legs.getItem()) || isLeather(feet.getItem());
    }

    private boolean isLeather(Item item) {
        return item == Items.LEATHER_HELMET || item == Items.LEATHER_CHESTPLATE || item == Items.LEATHER_LEGGINGS || item == Items.LEATHER_BOOTS;
    }
}
