package com.canoestudio.retrofuturemc.render;


import net.minecraft.util.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.*;

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
