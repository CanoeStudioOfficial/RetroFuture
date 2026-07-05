package com.canoestudio.retrofuturethewildupdate.potion;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionDarkness extends Potion {

    private static final ResourceLocation SPRITE = RTWU.prefix("textures/gui/darkness.png");

    public PotionDarkness() {
        super(true, 0);
        this.setPotionName("effect.darkness");
        this.setIconIndex(0, 0);
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return true;
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return true;
    }

    @Override
    public boolean shouldRenderHUD(PotionEffect effect) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        try {
            Minecraft.getMinecraft().renderEngine.bindTexture(SPRITE);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
            );
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            net.minecraft.client.renderer.Tessellator tessellator = net.minecraft.client.renderer.Tessellator.getInstance();
            net.minecraft.client.renderer.BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(7, net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX);
            buffer.pos(x + 6, y + 7 + 18, z).tex(0.0, 1.0).endVertex();
            buffer.pos(x + 6 + 18, y + 7 + 18, z).tex(1.0, 1.0).endVertex();
            buffer.pos(x + 6 + 18, y + 7, z).tex(1.0, 0.0).endVertex();
            buffer.pos(x + 6, y + 7, z).tex(0.0, 0.0).endVertex();
            tessellator.draw();
        } finally {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
            );
            GlStateManager.disableBlend();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(SPRITE);
        return super.getStatusIconIndex();
    }
}
