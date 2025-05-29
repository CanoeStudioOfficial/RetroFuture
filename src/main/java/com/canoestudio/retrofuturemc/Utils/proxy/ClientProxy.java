package com.canoestudio.retrofuturemc.utils.proxy;

import com.canoestudio.retrofuturemc.contents.mobs.brownmooshrooms.EntityBrownMooshroom;
import com.canoestudio.retrofuturemc.contents.mobs.brownmooshrooms.RenderBrownMooshroom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        RenderingRegistry.registerEntityRenderingHandler(EntityBrownMooshroom.class, RenderBrownMooshroom::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

//    @SubscribeEvent
//    public static void fovEvent(EntityViewRenderEvent.FOVModifier event) {
//        Minecraft mc = Minecraft.getMinecraft();
//        if (mc.gameSettings.thirdPersonView != 0) return;
//        EntityPlayerSP player = mc.player;
//        if (player.getItemInUseCount() <= 0) return;
//        if (player.getActiveItemStack().getItem() != DeeperDepthsItems.SPYGLASS) return;
//        event.setFOV(0.1f);
//    }
}