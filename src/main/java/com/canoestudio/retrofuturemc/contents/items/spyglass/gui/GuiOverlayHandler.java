package com.canoestudio.retrofuturemc.contents.items.spyglass.gui;

import com.canoestudio.retrofuturemc.contents.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = "retrofuturemc", value = Side.CLIENT)
public class GuiOverlayHandler {
    private static final int ANIM_DURATION = 500;
    private static final int OVERLAY_SIZE = 270;
    private static final ResourceLocation OVERLAY = new ResourceLocation("retrofuturemc:textures/gui/spyglass_overlay.png");
    private static final PlayerOverlayState playerState = new PlayerOverlayState();

    static {
        MinecraftForge.EVENT_BUS.register(GuiOverlayHandler.class);
    }

    public static PlayerOverlayState getPlayerState() {
        return playerState;
    }

    public static boolean isClosed() {
        return playerState.overlayState == State.CLOSED;
    }

    public static void updateItemUsage(boolean using, EnumHand hand) {
        playerState.isItemInUse = using;
        playerState.setActiveHand(using ? hand : null);

        if (using && isClosed()) {
            toggleOverlay(hand);
        } else if (!using && !isClosed()) {
            toggleOverlay(hand);
        }
    }

    public static void toggleOverlay(EnumHand hand) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        if (isClosed()) {
            if (mc.gameSettings.thirdPersonView == 0) {
                playerState.overlayState = State.OPENING;
                playerState.animStartTime = Minecraft.getSystemTime();
                playerState.originalFOV = mc.gameSettings.fovSetting;
                playerState.originalMouseSensitivity = mc.gameSettings.mouseSensitivity;
                playerState.setActiveHand(hand);
                mc.gameSettings.mouseSensitivity = playerState.originalMouseSensitivity * 0.1f;
            }
        } else {
            playerState.overlayState = State.CLOSING;
            playerState.animStartTime = Minecraft.getSystemTime();
            startFOVRecovery();
        }
    }

    private static void startFOVRecovery() {
        if (!playerState.isFOVRecovering) {
            playerState.isFOVRecovering = true;
            playerState.fovRecoverStartTime = Minecraft.getSystemTime();
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        long currentTime = Minecraft.getSystemTime();

        // State updates
        if (playerState.overlayState == State.OPENING) {
            float progress = MathHelper.clamp(
                    (currentTime - playerState.animStartTime) / (float)ANIM_DURATION,
                    0.0f, 1.0f
            );
            playerState.currentProgress = progress;

            if (progress >= 1.0f) {
                playerState.overlayState = State.OPENED;
            }
            mc.gameSettings.mouseSensitivity = playerState.originalMouseSensitivity * 0.1f;
        }
        else if (playerState.overlayState == State.CLOSING) {
            float progress = MathHelper.clamp(
                    (currentTime - playerState.animStartTime) / (float)ANIM_DURATION,
                    0.0f, 1.0f
            );
            playerState.currentProgress = 1.0f - progress;

            if (progress >= 1.0f) {
                playerState.overlayState = State.CLOSED;
                mc.gameSettings.mouseSensitivity = playerState.originalMouseSensitivity;
                playerState.currentProgress = 0.0f;
            }
        }

        // FOV recovery
        if (playerState.isFOVRecovering) {
            float recoverProgress = MathHelper.clamp(
                    (currentTime - playerState.fovRecoverStartTime) / (float)ANIM_DURATION,
                    0.0f, 1.0f
            );

            if (recoverProgress >= 1.0f) {
                playerState.isFOVRecovering = false;
                mc.gameSettings.fovSetting = playerState.originalFOV;
            }
        }

        // Item consistency check
        if (!isClosed() && playerState.getActiveHand() != null) {
            ItemStack activeStack = mc.player.getHeldItem(playerState.getActiveHand());
            if (activeStack.getItem() != ModItems.SPYGLASS) {
                toggleOverlay(playerState.getActiveHand());
            }
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.world == null) return;

        if (!isClosed() && playerState.getActiveHand() != null) {
            renderOverlayComponents(mc.displayWidth, mc.displayHeight, event.getPartialTicks());
            updateFOV(event.getPartialTicks());
        }
    }

    private static void renderOverlayComponents(int width, int height, float partialTicks) {
        float progress = MathHelper.clamp(playerState.currentProgress + partialTicks / ANIM_DURATION * 1000, 0.0f, 1.0f);

        // Draw mask
        drawOptimizedMask(width, height, progress);

        // Draw spyglass effect
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();

        try {
            Minecraft.getMinecraft().getTextureManager().bindTexture(OVERLAY);

            float scale = 0.8f + 0.2f * elasticEase(progress);
            int centerX = (width - OVERLAY_SIZE) / 2;
            int centerY = (height - OVERLAY_SIZE) / 2;

            GlStateManager.translate(centerX + OVERLAY_SIZE/2f, centerY + OVERLAY_SIZE/2f, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-(centerX + OVERLAY_SIZE/2f), -(centerY + OVERLAY_SIZE/2f), 0);

            Gui.drawModalRectWithCustomSizedTexture(
                    centerX, centerY, 0, 0,
                    OVERLAY_SIZE, OVERLAY_SIZE,
                    OVERLAY_SIZE, OVERLAY_SIZE
            );
        } finally {
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
        }
    }

    private static void drawOptimizedMask(int width, int height, float progress) {
        int animSize = (int)(OVERLAY_SIZE * (0.9f + 0.1f * progress));
        int centerX = (width - animSize) / 2;
        int centerY = (height - animSize) / 2;

        if (centerY > 0) Gui.drawRect(0, 0, width, centerY, 0xFF000000);
        if (centerY + animSize < height) Gui.drawRect(0, centerY + animSize, width, height, 0xFF000000);
        if (centerX > 0) Gui.drawRect(0, centerY, centerX, centerY + animSize, 0xFF000000);
        if (centerX + animSize < width) Gui.drawRect(centerX + animSize, centerY, width, centerY + animSize, 0xFF000000);
    }

    private static void updateFOV(float partialTicks) {
        float targetFOV = playerState.originalFOV;

        if (playerState.overlayState == State.OPENING || playerState.overlayState == State.OPENED) {
            float progress = playerState.currentProgress;
            targetFOV *= 1.0f - quinticEaseOut(progress) * 0.9f;
        } else if (playerState.isFOVRecovering) {
            float recoverProgress = MathHelper.clamp(
                    (Minecraft.getSystemTime() - playerState.fovRecoverStartTime) / (float)ANIM_DURATION,
                    0.0f, 1.0f
            );
            targetFOV *= 1.0f - 0.9f * (1.0f - quinticEaseOut(recoverProgress));
        }

        Minecraft mc = Minecraft.getMinecraft();
        float currentFOV = mc.gameSettings.fovSetting;
        float newFOV = currentFOV + (targetFOV - currentFOV) * 0.5f * partialTicks * 20.0f;
        mc.gameSettings.fovSetting = MathHelper.clamp(newFOV, Math.min(targetFOV, currentFOV), Math.max(targetFOV, currentFOV));
    }

    private static float quinticEaseOut(float t) {
        float t2 = t - 1.0f;
        return t2 * t2 * t2 * t2 * t2 + 1.0f;
    }

    private static float elasticEase(float t) {
        return (float)(Math.sin(-20.420352248333657 * (t + 1.0f)) * Math.pow(2.0, -10.0f * t)) + 1.0f;
    }

    private enum State {
        CLOSED, OPENING, OPENED, CLOSING
    }

    public static class PlayerOverlayState {
        State overlayState = State.CLOSED;
        long animStartTime = -1L;
        float originalFOV = 70.0f;
        float currentProgress = 0.0f;
        boolean isFOVRecovering = false;
        long fovRecoverStartTime = -1L;
        boolean isItemInUse = false;
        float originalMouseSensitivity = 0.5f;
        private EnumHand activeHand = null;

        public EnumHand getActiveHand() {
            return activeHand;
        }

        public void setActiveHand(EnumHand hand) {
            this.activeHand = hand;
        }
    }
}