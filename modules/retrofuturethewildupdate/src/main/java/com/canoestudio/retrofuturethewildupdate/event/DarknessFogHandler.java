package com.canoestudio.retrofuturethewildupdate.event;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.potion.ModPotions;
import com.canoestudio.retrofuturethewildupdate.sounds.ModSounds;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = {Side.CLIENT}, modid = RTWU.ID)
public class DarknessFogHandler {

    private static final ResourceLocation VIGNETTE_TEX = new ResourceLocation(RTWU.ID, "textures/misc/vignette.png");

    private static float fadeFactor = 0.0f;
    private static boolean hadDarknessLastTick = false;
    private static float heartbeatPulse = 0.0f;
    private static boolean forcedHotbarLightmap = false;
    private static float previousHotbarLightmapX = 0.0f;
    private static float previousHotbarLightmapY = 0.0f;

    private static final float FOG_DENSITY_MAX = 1.2f;
    private static final float FOG_DENSITY_MIN = 0.15f;
    private static final float PULSE_SPEED = 0.065f;
    private static final float EFFECTIVE_SIGHT = 14.0f;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            return;
        }

        boolean hasDarkness = mc.player.isPotionActive(ModPotions.DARKNESS);

        if (hasDarkness) {
            if (!hadDarknessLastTick) {
                fadeFactor = 0.0f;
            }
            fadeFactor = Math.min(1.0f, fadeFactor + 0.033f);
        } else {
            fadeFactor = Math.max(0.0f, fadeFactor - 0.025f);
        }
        hadDarknessLastTick = hasDarkness;

        if (heartbeatPulse > 0.0f) {
            heartbeatPulse -= 0.05f;
            if (heartbeatPulse < 0.0f) {
                heartbeatPulse = 0.0f;
            }
        }
    }

    @SubscribeEvent
    public static void onSoundPlay(PlaySoundEvent event) {
        if (event.getSound() != null && ModSounds.WARDEN_HEARTBEAT != null
            && event.getSound().getSoundLocation().equals(ModSounds.WARDEN_HEARTBEAT.getRegistryName())) {
            heartbeatPulse = 1.0f;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFogColor(EntityViewRenderEvent.FogColors event) {
        if (fadeFactor <= 0.0f) {
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || !player.isPotionActive(ModPotions.DARKNESS)) {
            return;
        }
        float lerp = fadeFactor;
        event.setRed(event.getRed() * (1.0f - lerp));
        event.setGreen(event.getGreen() * (1.0f - lerp));
        event.setBlue(event.getBlue() * (1.0f - lerp));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (fadeFactor <= 0.0f) {
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || !player.isPotionActive(ModPotions.DARKNESS)) {
            return;
        }
        if (event.getState().getMaterial() == Material.WATER
            || event.getState().getMaterial() == Material.LAVA) {
            return;
        }

        float time = player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        float pulse = (float) Math.sin(time * PULSE_SPEED) * 0.5f + 0.5f;
        pulse = pulse * pulse;

        float heartBoost = heartbeatPulse * 0.3f;
        float effectivePulse = Math.min(1.0f, pulse + heartBoost);

        float density = FOG_DENSITY_MIN + (FOG_DENSITY_MAX - FOG_DENSITY_MIN) * (1.0f - effectivePulse);
        density *= fadeFactor;

        event.setDensity(density);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HELMET) {
            return;
        }
        if (fadeFactor <= 0.01f) {
            return;
        }
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null || !player.isPotionActive(ModPotions.DARKNESS)) {
            return;
        }

        PotionEffect effect = player.getActivePotionEffect(ModPotions.DARKNESS);
        float effectFade = 1.0f;
        if (effect != null) {
            int duration = effect.getDuration();
            if (duration < 30) {
                effectFade = duration / 30.0f;
            }
        }

        float time = player.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        float pulse = (float) Math.sin(time * PULSE_SPEED) * 0.5f + 0.5f;
        pulse = pulse * pulse;

        float heartBoost = heartbeatPulse * 0.3f;
        float effectivePulse = Math.min(1.0f, pulse + heartBoost);

        float darkness = (1.0f - effectivePulse) * fadeFactor * effectFade;
        float alpha = 0.15f + darkness * 0.8f;
        alpha = Math.min(alpha, 0.95f);

        ScaledResolution sr = event.getResolution();
        int sw = sr.getScaledWidth();
        int sh = sr.getScaledHeight();

        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
        );

        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(VIGNETTE_TEX);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int a = (int) (alpha * 255);
        buffer.pos(0.0, sh, -90.0).tex(0.0, 1.0).color(0, 0, 0, a).endVertex();
        buffer.pos(sw, sh, -90.0).tex(1.0, 1.0).color(0, 0, 0, a).endVertex();
        buffer.pos(sw, 0.0, -90.0).tex(1.0, 0.0).color(0, 0, 0, a).endVertex();
        buffer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).color(0, 0, 0, a).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHotbarPre(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }

        previousHotbarLightmapX = OpenGlHelper.lastBrightnessX;
        previousHotbarLightmapY = OpenGlHelper.lastBrightnessY;
        forcedHotbarLightmap = true;
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHotbarPost(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR || !forcedHotbarLightmap) {
            return;
        }

        OpenGlHelper.setLightmapTextureCoords(
            OpenGlHelper.lightmapTexUnit,
            previousHotbarLightmapX,
            previousHotbarLightmapY
        );
        forcedHotbarLightmap = false;
    }
}
