package com.canoestudio.retrofuturemc.contents.items.spyglass.item;

import com.canoestudio.retrofuturemc.contents.items.spyglass.event.GuiOverlayHandler;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
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

public class ItemSpyglassRed extends ItemSpyglass {

    public ItemSpyglassRed() {
        super();
        setCreativeTab(CreativeTabs.TOOLS);
    }

    // 注册方法
    public static void register(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemSpyglassRed());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    // 客户端模型注册
    @SideOnly(Side.CLIENT)
    public static void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(Tags.MOD_ID, "spyglassred")),
                0,
                new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, "spyglassred"), "inventory")
        );
    }


    protected void playUseSound(World world, EntityPlayer player) {
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvent.REGISTRY.getObject(new ResourceLocation(Tags.MOD_ID, "use_red")),
                SoundCategory.PLAYERS, 1.0F, 1.0F);
    }


    protected void playStopSound(World world, EntityLivingBase entity) {
        world.playSound(null, entity.posX, entity.posY, entity.posZ,
                SoundEvent.REGISTRY.getObject(new ResourceLocation(Tags.MOD_ID, "stop_red")),
                SoundCategory.PLAYERS, 1.0F, 1.0F);
    }

    // 红色望远镜特有功能

    protected void handleSpecialEntity(Entity target, EntityPlayer player) {
        if (target instanceof EntityEnderman) {
            EntityEnderman enderman = (EntityEnderman)target;
            enderman.setAttackTarget(player);
            target.world.playSound(null, target.posX, target.posY, target.posZ,
                    SoundEvents.ENTITY_ENDERMEN_STARE,
                    SoundCategory.HOSTILE, 1.0F, 1.0F);
        }
    }

    // 事件处理器
    public static class EventHandler {
        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (shouldHandleEvent(event)) {
                EntityPlayer player = event.player;
                if (!isHoldingSpyglass(player) && GuiOverlayHandler.isActive()) {
                    GuiOverlayHandler.toggleOverlay(null);
                }
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public void onPerspectiveChange(EntityViewRenderEvent.CameraSetup event) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (shouldHandlePerspectiveChange(player)) {
                GuiOverlayHandler.toggleOverlay(null);
            }
        }

        private boolean shouldHandleEvent(TickEvent.PlayerTickEvent event) {
            return event.phase == TickEvent.Phase.START &&
                    event.player == Minecraft.getMinecraft().player;
        }

        private boolean isHoldingSpyglass(EntityPlayer player) {
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();
            return (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemSpyglassRed) ||
                    (!offHand.isEmpty() && offHand.getItem() instanceof ItemSpyglassRed);
        }

        private boolean shouldHandlePerspectiveChange(EntityPlayerSP player) {
            return player != null &&
                    player.getActiveItemStack().getItem() instanceof ItemSpyglassRed &&
                    Minecraft.getMinecraft().gameSettings.thirdPersonView != 0;
        }
    }
}