package com.canoestudio.retrofuturethewildupdate.client.particle;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleSonicBoom extends Particle {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[16];

    static {
        for (int i = 0; i < 16; ++i) {
            TEXTURES[i] = new ResourceLocation(RTWU.ID, "textures/particles/sonic_boom_" + i + ".png");
        }
    }

    public ParticleSonicBoom(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
        this.particleMaxAge = 16;
        this.particleScale = 5.5f;
        this.particleGravity = 0.0f;
        this.particleAlpha = 1.0f;
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
        if (this.particleAge > 13) {
            this.particleAlpha -= 0.3f;
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks,
                                float rotX, float rotZ, float rotYZ, float rotXY, float rotXZ) {
        int frameIndex = (int) (this.particleAge / (float) this.particleMaxAge * 16.0f);
        if (frameIndex < 0) frameIndex = 0;
        if (frameIndex >= 16) frameIndex = 15;

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURES[frameIndex]);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        GlStateManager.depthMask(false);
        GlStateManager.alphaFunc(516, 0.003921569f);

        float scale = 0.1f * this.particleScale;
        float minU = 0.0f;
        float maxU = 1.0f;
        float minV = 0.0f;
        float maxV = 1.0f;

        float x = (float) (this.prevPosX + (this.posX - this.prevPosX) * partialTicks - interpPosX);
        float y = (float) (this.prevPosY + (this.posY - this.prevPosY) * partialTicks - interpPosY);
        float z = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

        int j = 240;
        int k = 240;

        buffer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        buffer.pos(x - rotX * scale - rotXY * scale, y - rotZ * scale, z - rotYZ * scale - rotXZ * scale)
            .tex(maxU, maxV).color(1.0f, 1.0f, 1.0f, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(x - rotX * scale + rotXY * scale, y + rotZ * scale, z - rotYZ * scale + rotXZ * scale)
            .tex(maxU, minV).color(1.0f, 1.0f, 1.0f, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(x + rotX * scale + rotXY * scale, y + rotZ * scale, z + rotYZ * scale + rotXZ * scale)
            .tex(minU, minV).color(1.0f, 1.0f, 1.0f, this.particleAlpha).lightmap(j, k).endVertex();
        buffer.pos(x + rotX * scale - rotXY * scale, y - rotZ * scale, z + rotYZ * scale - rotXZ * scale)
            .tex(minU, maxV).color(1.0f, 1.0f, 1.0f, this.particleAlpha).lightmap(j, k).endVertex();
        Tessellator.getInstance().draw();

        GlStateManager.depthMask(true);
        GlStateManager.disableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }
}
