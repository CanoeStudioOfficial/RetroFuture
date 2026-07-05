package com.canoestudio.retrofuturemc.contents.items.spyglass;

import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SpyglassHandler {

    public static final float FOV_MULTIPLIER = 0.1F;
    private static final int ANIM_DURATION = 500;
    private static final int OVERLAY_SIZE = 270;
    private static final ResourceLocation OVERLAY = new ResourceLocation(Tags.MOD_ID, "textures/gui/spyglass_scope.png");

    protected static OverlayState overlayState = OverlayState.CLOSED;
    public static long animStartTime = -1;
    public static EnumHand activeHand = null;

    public static float originalFOV = 70.0f;
    public static float currentProgress = 0.0f;
    public static float originalMouseSensitivity = 0.5f;



    // 更新物品使用状态
    public static void updateItemUsage(boolean using, EnumHand hand) {
        activeHand = using ? hand : null;
        if (using == (overlayState == OverlayState.CLOSED)) {
            toggleClientOverlay(hand);
        }
    }

    // 切换覆盖层状态
    public static void toggleClientOverlay(EnumHand hand) {
        Minecraft mc = Minecraft.getMinecraft();

        if (overlayState == OverlayState.CLOSED) {
            if (mc.gameSettings.thirdPersonView == 0) {
                overlayState = OverlayState.OPENING;
                animStartTime = System.currentTimeMillis();
                originalFOV = mc.gameSettings.fovSetting;
                originalMouseSensitivity = mc.gameSettings.mouseSensitivity;
                activeHand = hand;
                mc.gameSettings.mouseSensitivity = originalMouseSensitivity * 0.1f;
            }
        } else {
            overlayState = OverlayState.CLOSED;
            currentProgress = 0.0f;
            activeHand = null;
            mc.gameSettings.mouseSensitivity = originalMouseSensitivity;
        }
    }

    // 客户端Tick事件处理
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        // 更新覆盖层状态
        if (overlayState == OverlayState.OPENING) {
            currentProgress = MathHelper.clamp(
                    (System.currentTimeMillis() - animStartTime) / (float) ANIM_DURATION,
                    0.0f, 1.0f
            );

            if (currentProgress >= 1.0f) {
                overlayState = OverlayState.OPENED;
            }

            mc.gameSettings.mouseSensitivity = originalMouseSensitivity * 0.1f;
        }
        else if (overlayState == OverlayState.OPENED) {
            mc.gameSettings.mouseSensitivity = originalMouseSensitivity * 0.1f;
        }

        // 检查物品是否仍然在使用（HGW留，我(peter)认为这应该在望远镜功能的物品内完成，而不是望远镜api内完成）
        /*
        if (overlayState != OverlayState.CLOSED) {
            ItemStack activeStack = (activeHand == EnumHand.MAIN_HAND) ?
                    player.getHeldItemMainhand() : player.getHeldItemOffhand();

            if (activeStack.isEmpty() || Utils.getLevel(player.getActiveItemStack(),Hydrogenation_tinker.Traits.farSeek) < 0  || player.getActiveHand() != activeHand) {
                toggleClientOverlay(activeHand);
            }
        }
         */
    }

    // FOV更新事件处理
    @SubscribeEvent
    public static void onFOVUpdate(FOVUpdateEvent event) {
        EntityPlayer player = event.getEntity();

        if (player.isHandActive() && overlayState != OverlayState.CLOSED) {
            // 应用望远镜视野效果
            float progress = 1.0f;

            if (overlayState == OverlayState.OPENING) {
                progress = currentProgress;
            }
            float targetFOV = event.getFov() * (1 + (FOV_MULTIPLIER - 1) * progress);
            event.setNewfov(targetFOV);
        }
    }

    // 渲染游戏覆盖层
    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        if (player == null) return;

        // 简化渲染条件
        if (overlayState != OverlayState.CLOSED) {
            renderSpyglassOverlay(mc, event.getResolution());
        }
    }

    // 渲染望远镜覆盖层
    private static void renderSpyglassOverlay(Minecraft mc, ScaledResolution resolution) {
        int width = resolution.getScaledWidth();
        int height = resolution.getScaledHeight();

        // 计算动画进度
        float progress = currentProgress;
        if (overlayState == OverlayState.OPENED) {
            progress = 1.0f;
        }

        // 绘制黑色遮罩
        drawMask(width, height, progress);

        // 绘制望远镜镜头
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // 绑定纹理
        mc.getTextureManager().bindTexture(OVERLAY);

        // 应用弹性动画效果
        float scale = 0.8f + 0.2f * elasticEase(progress);
        int centerX = (width - OVERLAY_SIZE) / 2;
        int centerY = (height - OVERLAY_SIZE) / 2;

        GlStateManager.translate(centerX + OVERLAY_SIZE / 2f, centerY + OVERLAY_SIZE / 2f, 0);
        GlStateManager.scale(scale, scale, 1.0f);
        GlStateManager.translate(-(centerX + OVERLAY_SIZE / 2f), -(centerY + OVERLAY_SIZE / 2f), 0);

        // 绘制纹理
        Gui.drawModalRectWithCustomSizedTexture(
                centerX, centerY,
                0, 0,
                OVERLAY_SIZE, OVERLAY_SIZE,
                OVERLAY_SIZE, OVERLAY_SIZE
        );

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    // 绘制黑色遮罩
    private static void drawMask(int width, int height, float progress) {
        int animSize = (int) (OVERLAY_SIZE * (0.9f + 0.1f * progress));
        int centerX = (width - animSize) / 2;
        int centerY = (height - animSize) / 2;
        int black = 0xFF000000;

        // 上部分
        if (centerY > 0) {
            Gui.drawRect(0, 0, width, centerY, black);
        }

        // 下部分
        if (centerY + animSize < height) {
            Gui.drawRect(0, centerY + animSize, width, height, black);
        }

        // 左部分
        if (centerX > 0) {
            Gui.drawRect(0, centerY, centerX, centerY + animSize, black);
        }

        // 右部分
        if (centerX + animSize < width) {
            Gui.drawRect(centerX + animSize, centerY, width, centerY + animSize, black);
        }
    }

    // 弹性缓动函数
    private static float elasticEase(float t) {
        return (float) (Math.sin(-20.420352248333657 * (t + 1.0f)) * Math.pow(2.0, -10.0f * t)) + 1.0f;
    }

    // 覆盖层状态枚举
    public enum OverlayState {
        CLOSED,
        OPENING,
        OPENED
    }

}