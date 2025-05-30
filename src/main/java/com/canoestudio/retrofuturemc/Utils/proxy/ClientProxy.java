package com.canoestudio.retrofuturemc.utils.proxy;

import com.canoestudio.retrofuturemc.RetroFuturemc;
import com.canoestudio.retrofuturemc.contents.items.ModItems;
import com.canoestudio.retrofuturemc.contents.mobs.brownmooshrooms.EntityBrownMooshroom;
import com.canoestudio.retrofuturemc.contents.mobs.brownmooshrooms.RenderBrownMooshroom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {


    public static ResourceLocation SPYGLASS_TEXTURE = RetroFuturemc.loc("textures/misc/spyglass_scope.png");
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

    @SubscribeEvent
    public static void fovEvent(EntityViewRenderEvent.FOVModifier event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.thirdPersonView != 0) return;
        EntityPlayerSP player = mc.player;
        if (player.getItemInUseCount() <= 0) return;
        if (player.getActiveItemStack().getItem() != ModItems.SPYGLASS) return;
        event.setFOV(0.1f * event.getFOV());

    }
    @SubscribeEvent
    public static void postRenderOverlay(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if (player == null || event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        if (player.getItemInUseCount() <= 0) return;
        if (player.getActiveItemStack().getItem() != ModItems.SPYGLASS) return;
        ScaledResolution resolution = event.getResolution();
        int height = resolution.getScaledHeight();
        int width = (resolution.getScaledWidth() - height) / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(0, height, 0).color(0, 0, 0, 1f).endVertex();
        bufferbuilder.pos(width, height, 0).color(0, 0, 0, 1f).endVertex();
        bufferbuilder.pos(width, 0, 0).color(0, 0, 0, 1f).endVertex();
        bufferbuilder.pos(0, 0, 0).color(0, 0, 0, 1f).endVertex();
        tessellator.draw();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(width + height, height, 0).color(0, 0, 0, 1f).endVertex();
        bufferbuilder.pos(resolution.getScaledWidth(), height, 0).color(0, 0, 0, 1f).endVertex();
        bufferbuilder.pos(resolution.getScaledWidth(), 0, 0).color(0, 0, 0, 1f).endVertex();
        bufferbuilder.pos(width + height, 0, 0).color(0, 0, 0, 1f).endVertex();
        tessellator.draw();
        mc.getTextureManager().bindTexture(SPYGLASS_TEXTURE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(width, height, 0).tex(0, 1).endVertex();
        bufferbuilder.pos(width + height, height, 0).tex(1, 1).endVertex();
        bufferbuilder.pos(width + height, 0, 0).tex(1, 0).endVertex();
        bufferbuilder.pos(width, 0, 0).tex(0, 0).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }
}