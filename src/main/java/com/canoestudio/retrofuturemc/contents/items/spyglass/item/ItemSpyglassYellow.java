package com.canoestudio.retrofuturemc.contents.items.spyglass.item;

import com.canoestudio.retrofuturemc.RetroFuturemc;
import com.canoestudio.retrofuturemc.contents.items.spyglass.event.GuiOverlayHandler;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityParrot;
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

import javax.annotation.Nullable;
import java.util.List;

public class ItemSpyglassYellow extends ItemSpyglass {

    private static final ResourceLocation SPYGLASS_RESOURCE = new ResourceLocation(Tags.MOD_ID, "spyglassyellow");
    private static final ResourceLocation USE_SOUND_RESOURCE = new ResourceLocation(Tags.MOD_ID, "use_yellow");
    private static final ResourceLocation STOP_SOUND_RESOURCE = new ResourceLocation(Tags.MOD_ID, "stop_yellow");

    public ItemSpyglassYellow() {
        super();
        setCreativeTab(CreativeTabs.TOOLS);
    }

    public static void register(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemSpyglassYellow());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    @SideOnly(Side.CLIENT)
    public static void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                ForgeRegistries.ITEMS.getValue(SPYGLASS_RESOURCE),
                0,
                new ModelResourceLocation(SPYGLASS_RESOURCE, "inventory")
        );
    }


    protected void playUseSound(World world, EntityPlayer player) {
        SoundEvent sound = SoundEvent.REGISTRY.getObject(USE_SOUND_RESOURCE);
        if (sound != null) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }


    protected void playStopSound(World world, EntityLivingBase entity) {
        SoundEvent sound = SoundEvent.REGISTRY.getObject(STOP_SOUND_RESOURCE);
        if (sound != null) {
            world.playSound(null, entity.posX, entity.posY, entity.posZ,
                    sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
    }


    protected void handleSpecialEntity(Entity target, EntityPlayer player) {
        if (target instanceof EntityParrot) {
            target.world.playSound(null, target.posX, target.posY, target.posZ,
                    SoundEvents.ENTITY_PARROT_AMBIENT,
                    SoundCategory.NEUTRAL, 1.5F, 1.0F);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add("§6特殊能力: §e可以吸引鹦鹉的注意");
        tooltip.add("§7按住右键使用望远镜");
    }

    public static class EventHandler {
        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.START &&
                    event.player == Minecraft.getMinecraft().player &&
                    !isHoldingSpyglass(event.player) &&
                    GuiOverlayHandler.isActive()) {
                GuiOverlayHandler.toggleOverlay(null);
            }
        }

        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public void onPerspectiveChange(EntityViewRenderEvent.CameraSetup event) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null &&
                    player.getActiveItemStack().getItem() instanceof ItemSpyglassYellow &&
                    Minecraft.getMinecraft().gameSettings.thirdPersonView != 0) {
                GuiOverlayHandler.toggleOverlay(null);
            }
        }

        private boolean isHoldingSpyglass(EntityPlayer player) {
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();
            return (mainHand.getItem() instanceof ItemSpyglassYellow) ||
                    (offHand.getItem() instanceof ItemSpyglassYellow);
        }
    }
}