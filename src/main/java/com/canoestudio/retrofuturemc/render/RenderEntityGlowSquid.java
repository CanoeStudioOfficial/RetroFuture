package com.canoestudio.retrofuturemc.render;


import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSquid;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.util.ResourceLocation;

public class RenderEntityGlowSquid extends RenderSquid
{
    public static final ResourceLocation TEXTURE;

    public RenderEntityGlowSquid(final RenderManager manager) {
        super(manager);
    }

    protected ResourceLocation getEntityTexture(final EntitySquid entity) {
        return RenderEntityGlowSquid.TEXTURE;
    }

    static {
        TEXTURE = new ResourceLocation("retrofuturemc", "textures/entity/glow_squid.png");
    }
}
