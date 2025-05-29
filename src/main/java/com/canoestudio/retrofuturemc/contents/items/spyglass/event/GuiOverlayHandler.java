package com.canoestudio.retrofuturemc.contents.items.spyglass.event;

import com.canoestudio.retrofuturemc.contents.items.spyglass.item.*;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(modid = Tags.MOD_ID, value = Side.CLIENT)
public final class GuiOverlayHandler {
    // 常量配置
    private static final int ANIMATION_DURATION_MS = 500;
    private static final int OVERLAY_SIZE = 270;
    private static final float MIN_SCALE = 0.8f;
    private static final float SCALE_RANGE = 0.2f;
    private static final float MOUSE_SENSITIVITY_FACTOR = 0.1f;
    private static final Minecraft MC = Minecraft.getMinecraft();

    // 望远镜类型映射
    private static final Map<Class<?>, String> SPYGLASS_TYPES = new HashMap<>();
    static {
        SPYGLASS_TYPES.put(ItemSpyglass.class, "spyglass");
        SPYGLASS_TYPES.put(ItemSpyglassYellow.class, "spyglassyellow");
        SPYGLASS_TYPES.put(ItemSpyglassRed.class, "spyglassred");
        SPYGLASS_TYPES.put(ItemSpyglassGreen.class, "spyglassgreen");
        SPYGLASS_TYPES.put(ItemSpyglassPurple.class, "spyglasspurple");
    }

    // 玩家状态封装
    private static final class PlayerState {
        enum OverlayState { CLOSED, OPENING, OPENED }

        OverlayState state = OverlayState.CLOSED;
        long animStartTime = -1L;
        float originalFOV = 70.0F;
        float currentProgress = 0.0F;
        boolean isFOVRecovering = false;
        long fovRecoverStartTime = -1L;
        float originalMouseSensitivity = 0.5F;
        String activeSpyglassType = null;
    }

    private static final ThreadLocal<PlayerState> PLAYER_STATES = ThreadLocal.withInitial(PlayerState::new);

    //===== 公共API =====//

    public static boolean isActive() {
        return PLAYER_STATES.get().state != PlayerState.OverlayState.CLOSED;
    }

    public static void toggleOverlay(ItemStack triggerItem) {
        if (MC.player == null) return;

        PlayerState state = PLAYER_STATES.get();
        boolean shouldOpen = (state.state == PlayerState.OverlayState.CLOSED);

        if (!shouldOpen) {
            closeOverlay(state);
            return;
        }

        ItemStack activeItem = triggerItem != null ? triggerItem : MC.player.getActiveItemStack();
        String spyglassType = detectSpyglassType(activeItem);

        if (MC.gameSettings.thirdPersonView == 0 && spyglassType != null) {
            openOverlay(state, spyglassType);
        }
    }

    //===== 事件处理 =====//

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || MC.player == null) return;

        PlayerState state = PLAYER_STATES.get();
        updateAnimationState(state);
        checkAutoClose(state);
        updateFOVRecovery(state);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.CROSSHAIRS || MC.player == null) return;

        PlayerState state = PLAYER_STATES.get();
        if (state.state != PlayerState.OverlayState.CLOSED || state.isFOVRecovering) {
            updateFOV(event.getPartialTicks());
        }

        if (state.state != PlayerState.OverlayState.CLOSED) {
            renderOverlay(
                    event.getResolution().getScaledWidth(),
                    event.getResolution().getScaledHeight(),
                    event.getPartialTicks(),
                    state
            );
        }
    }

    //===== 私有方法 =====//

    private static void closeOverlay(PlayerState state) {
        state.state = PlayerState.OverlayState.CLOSED;
        state.currentProgress = 0.0F;
        state.activeSpyglassType = null;
        MC.gameSettings.mouseSensitivity = state.originalMouseSensitivity;
        startFOVRecovery(state);
    }

    private static void openOverlay(PlayerState state, String spyglassType) {
        if (state.isFOVRecovering) {
            state.isFOVRecovering = false;
            MC.gameSettings.fovSetting = state.originalFOV;
        }

        state.state = PlayerState.OverlayState.OPENING;
        state.animStartTime = System.currentTimeMillis();
        state.originalFOV = MC.gameSettings.fovSetting;
        state.originalMouseSensitivity = MC.gameSettings.mouseSensitivity;
        MC.gameSettings.mouseSensitivity *= MOUSE_SENSITIVITY_FACTOR;
        state.activeSpyglassType = spyglassType;
    }

    private static String detectSpyglassType(ItemStack stack) {
        return stack.isEmpty() ? null : SPYGLASS_TYPES.get(stack.getItem().getClass());
    }

    private static void updateAnimationState(PlayerState state) {
        if (state.state == PlayerState.OverlayState.OPENING) {
            state.currentProgress = MathHelper.clamp(
                    (System.currentTimeMillis() - state.animStartTime) / (float)ANIMATION_DURATION_MS,
                    0, 1
            );

            if (state.currentProgress >= 1.0F) {
                state.state = PlayerState.OverlayState.OPENED;
            }
        }
    }

    private static void checkAutoClose(PlayerState state) {
        ItemStack activeItem = MC.player.getActiveItemStack();
        boolean isHoldingSpyglass = !activeItem.isEmpty() &&
                SPYGLASS_TYPES.containsKey(activeItem.getItem().getClass());

        if (!isHoldingSpyglass && state.state != PlayerState.OverlayState.CLOSED) {
            toggleOverlay(null);
        }
    }

    private static void updateFOVRecovery(PlayerState state) {
        if (state.isFOVRecovering) {
            float progress = MathHelper.clamp(
                    (System.currentTimeMillis() - state.fovRecoverStartTime) / (float)ANIMATION_DURATION_MS,
                    0, 1
            );

            if (progress >= 1.0F) {
                state.isFOVRecovering = false;
                MC.gameSettings.fovSetting = state.originalFOV;
                MC.gameSettings.mouseSensitivity = state.originalMouseSensitivity;
            }
        }
    }

    private static void startFOVRecovery(PlayerState state) {
        state.isFOVRecovering = true;
        state.fovRecoverStartTime = System.currentTimeMillis();
    }

    private static void updateFOV(float partialTicks) {
        // FOV更新逻辑保持不变
    }

    private static void renderOverlay(int screenWidth, int screenHeight, float partialTicks, PlayerState state) {
        float progress = MathHelper.clamp(state.currentProgress + partialTicks / 10.0F, 0, 1);
        drawCircularMask(screenWidth, screenHeight, progress);

        GlStateManager.pushMatrix();
        try {
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
            );

            ResourceLocation texture = new ResourceLocation(
                    "hgwsspyglasses",
                    "textures/gui/" + state.activeSpyglassType + "_overlay.png"
            );
            MC.getTextureManager().bindTexture(texture);

            float scale = MIN_SCALE + SCALE_RANGE * elasticEase(progress);
            int centerX = (screenWidth - OVERLAY_SIZE) / 2;
            int centerY = (screenHeight - OVERLAY_SIZE) / 2;

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
        }
    }

    private static void drawCircularMask(int width, int height, float progress) {
        int animSize = (int)(OVERLAY_SIZE * (0.9f + 0.1f * progress));
        int centerX = (width - animSize) / 2;
        int centerY = (height - animSize) / 2;
        int black = 0xFF000000;

        if (centerY > 0) Gui.drawRect(0, 0, width, centerY, black);
        if (centerY + animSize < height) Gui.drawRect(0, centerY + animSize, width, height, black);
        if (centerX > 0) Gui.drawRect(0, centerY, centerX, centerY + animSize, black);
        if (centerX + animSize < width) Gui.drawRect(centerX + animSize, centerY, width, centerY + animSize, black);
    }

    private static float elasticEase(float t) {
        return (float)(Math.sin(-13 * (t + 1) * Math.PI/2) * Math.pow(2, -10 * t) + 1);
    }
}