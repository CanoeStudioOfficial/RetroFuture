package com.canoestudio.retrofuturethewildupdate.client.renderer;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import com.canoestudio.retrofuturethewildupdate.client.models.ModelWarden;
import com.canoestudio.retrofuturethewildupdate.entity.Warden;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class RenderWarden extends RenderLiving<Warden> {

    private static final ResourceLocation TEXTURE =
        new ResourceLocation(RTWU.ID, "textures/model/warden/warden.png");
    private static final ResourceLocation BIOLUMINESCENT_LAYER =
        new ResourceLocation(RTWU.ID, "textures/model/warden/warden_bioluminescent_layer.png");
    private static final ResourceLocation HEART_LAYER =
        new ResourceLocation(RTWU.ID, "textures/model/warden/warden_heart.png");
    private static final ResourceLocation PULSATING_SPOTS_1 =
        new ResourceLocation(RTWU.ID, "textures/model/warden/warden_pulsating_spots_1.png");
    private static final ResourceLocation PULSATING_SPOTS_2 =
        new ResourceLocation(RTWU.ID, "textures/model/warden/warden_pulsating_spots_2.png");

    public RenderWarden(RenderManager manager) {
        super(manager, new ModelWarden(), 0.9f);
        this.addLayer(new WardenGlowLayer(this));
    }

    @Override
    protected void preRenderCallback(Warden entity, float partialTickTime) {
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
    }

    @Override
    protected ResourceLocation getEntityTexture(Warden entity) {
        return TEXTURE;
    }

    private class WardenGlowLayer implements LayerRenderer<Warden> {

        private final RenderWarden renderer;

        public WardenGlowLayer(RenderWarden renderer) {
            this.renderer = renderer;
        }

        @Override
        public void doRenderLayer(Warden entity, float limbSwing, float limbSwingAmount,
                                   float partialTicks, float ageInTicks, float netHeadYaw,
                                   float headPitch, float scale) {
            ModelWarden model = (ModelWarden) this.renderer.getMainModel();
            float lastBrightnessX = OpenGlHelper.lastBrightnessX;
            float lastBrightnessY = OpenGlHelper.lastBrightnessY;

            try {
                GlStateManager.enableBlend();
                GlStateManager.disableAlpha();
                GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
                );
                GlStateManager.depthMask(!entity.isInvisible());
                GlStateManager.disableLighting();
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0f, 0.0f);
                Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

                this.renderer.bindTexture(BIOLUMINESCENT_LAYER);
                this.renderParts(model.getBioluminescentLayerModelParts(), scale);

                float time = entity.ticksExisted + partialTicks;
                float pulse1 = Math.max(0.0f, (float) Math.cos(time * 0.045f) * 0.25f);
                float pulse2 = Math.max(0.0f, (float) Math.cos(time * 0.045f + (float) Math.PI) * 0.25f);

                if (pulse1 > 0.0f) {
                    this.renderer.bindTexture(PULSATING_SPOTS_1);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, pulse1);
                    this.renderParts(model.getPulsatingSpotsLayerModelParts(), scale);
                }
                if (pulse2 > 0.0f) {
                    this.renderer.bindTexture(PULSATING_SPOTS_2);
                    GlStateManager.color(1.0f, 1.0f, 1.0f, pulse2);
                    this.renderParts(model.getPulsatingSpotsLayerModelParts(), scale);
                }

                int anger = entity.getDataManager().get(Warden.ANGER_LEVEL);
                float speed = 0.15f + anger / 80.0f * 0.25f;
                float dynamicHeartPulse = 0.3f + 0.7f * (float) Math.sin(ageInTicks * speed);

                this.renderer.bindTexture(TEXTURE);
                GlStateManager.color(1.0f, 1.0f, 1.0f, dynamicHeartPulse);
                this.renderParts(model.getTendrilsLayerModelParts(), scale);

                this.renderer.bindTexture(HEART_LAYER);
                GlStateManager.color(1.0f, 1.0f, 1.0f, dynamicHeartPulse);
                this.renderParts(model.getHeartLayerModelParts(), scale);
            } finally {
                Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
                this.renderer.setLightmap(entity);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.depthMask(true);
                GlStateManager.enableLighting();
                GlStateManager.enableAlpha();
                GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
                );
                GlStateManager.disableBlend();
            }
        }

        private void renderParts(List<ModelRenderer> parts, float scale) {
            for (ModelRenderer part : parts) {
                part.render(scale);
            }
        }

        @Override
        public boolean shouldCombineTextures() {
            return false;
        }
    }
}
