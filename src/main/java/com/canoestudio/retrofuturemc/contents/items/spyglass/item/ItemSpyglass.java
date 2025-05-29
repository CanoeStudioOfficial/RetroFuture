package com.canoestudio.retrofuturemc.contents.items.spyglass.item;

import com.canoestudio.retrofuturemc.RetroFuturemc;
import com.canoestudio.retrofuturemc.contents.items.spyglass.event.GuiOverlayHandler;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemSpyglass extends Item {
    // 实体类型与命令的映射
    private static final Map<Class<? extends Entity>, String> ENTITY_COMMANDS = new HashMap<>();

    public ItemSpyglass() {
        setMaxDamage(0);
        setMaxStackSize(1);
        setTranslationKey("spyglass");
        setRegistryName(Tags.MOD_ID, "spyglass");
        setCreativeTab(CreativeTabs.TOOLS);
        addPropertyOverride(new ResourceLocation("model"), (stack, world, entity) -> {
            if (entity == null) return 1.0F;
            return (entity.isHandActive() && entity.getActiveItemStack().getItem() instanceof ItemSpyglass) ? 2.0F : 0.0F;
        });
    }

    // 注册方法
    public static void register(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemSpyglass());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    // 客户端模型注册
    @SideOnly(Side.CLIENT)
    public static void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(Tags.MOD_ID, "spyglass")),
                0,
                new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, "spyglass"), "inventory")
        );
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000; // 望远镜使用持续时间
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (world.isRemote) {
            GuiOverlayHandler.toggleOverlay(stack);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvent.REGISTRY.getObject(new ResourceLocation(Tags.MOD_ID, "use")),
                    SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

        player.setActiveHand(hand);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
        if (world.isRemote && entity == Minecraft.getMinecraft().player) {
            GuiOverlayHandler.toggleOverlay(null);
        }

        if (!world.isRemote && entity instanceof EntityPlayer) {
            world.playSound(null, entity.posX, entity.posY, entity.posZ,
                    SoundEvent.REGISTRY.getObject(new ResourceLocation(Tags.MOD_ID, "stop")),
                    SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }

    // 事件处理器
    public static class EventHandler {
        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START && event.player == Minecraft.getMinecraft().player) {
                EntityPlayer player = event.player;
                boolean isHoldingSpyglass = player.getHeldItemMainhand().getItem() instanceof ItemSpyglass ||
                        player.getHeldItemOffhand().getItem() instanceof ItemSpyglass;

                if (!isHoldingSpyglass && GuiOverlayHandler.isActive()) {
                    GuiOverlayHandler.toggleOverlay(null);
                }
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public void onPerspectiveChange(EntityViewRenderEvent.CameraSetup event) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null && player.getActiveItemStack().getItem() instanceof ItemSpyglass &&
                    Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
                GuiOverlayHandler.toggleOverlay(null);
            }
        }
    }

    // 视线检测方法
    @Nullable
    private Entity findTargetInSight(EntityPlayer player, double range) {
        Vec3d eyes = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end = eyes.add(look.x * range, look.y * range, look.z * range);

        RayTraceResult result = player.world.rayTraceBlocks(eyes, end, false, true, false);
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity entity = result.entityHit;
            if (shouldTargetEntity(entity)) {
                return entity;
            }
        }

        // 更精确的实体检测
        List<Entity> entities = player.world.getEntitiesWithinAABBExcludingEntity(
                player, player.getEntityBoundingBox().grow(range, range, range));

        Entity closestTarget = null;
        double closestDistance = range * range;

        for (Entity entity : entities) {
            if (shouldTargetEntity(entity)) {
                AxisAlignedBB aabb = getTargetBoundingBox(entity);
                RayTraceResult intercept = aabb.calculateIntercept(eyes, end);

                if (intercept != null) {
                    double distance = eyes.distanceTo(intercept.hitVec);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestTarget = entity;
                    }
                }
            }
        }

        return closestTarget;
    }

    private boolean shouldTargetEntity(Entity entity) {
        if (entity instanceof MultiPartEntityPart) {
            IEntityMultiPart parent = ((MultiPartEntityPart)entity).parent;
            return parent instanceof EntityDragon;
        }
        return entity instanceof EntityParrot ||
                entity instanceof EntityGhast ||
                entity instanceof EntityDragon ||
                entity instanceof EntityEnderman;
    }

    private AxisAlignedBB getTargetBoundingBox(Entity entity) {
        if (entity instanceof EntityEnderman) {
            AxisAlignedBB fullAABB = entity.getEntityBoundingBox();
            double midY = fullAABB.minY + (fullAABB.maxY - fullAABB.minY) * 0.7D;
            return new AxisAlignedBB(
                    fullAABB.minX, midY, fullAABB.minZ,
                    fullAABB.maxX, fullAABB.maxY - 0.3D, fullAABB.maxZ
            ).grow(0.2D);
        }
        return entity.getEntityBoundingBox().grow(0.3D);
    }
}