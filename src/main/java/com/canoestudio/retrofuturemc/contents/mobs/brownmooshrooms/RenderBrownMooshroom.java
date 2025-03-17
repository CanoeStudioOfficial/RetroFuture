package com.canoestudio.retrofuturemc.contents.mobs.brownmooshrooms;

import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

//Based on copypaste from RenderMooshroom of course.
public class RenderBrownMooshroom extends RenderLiving<EntityBrownMooshroom> {
    private static final ResourceLocation BROWN_MOOSHROOM_TEXTURES = new ResourceLocation(Tags.MOD_ID, "textures/entity/brown_mooshroom.png");

    public RenderBrownMooshroom(RenderManager rm) {
        super(rm, new ModelCow(), 0.7F);
        this.addLayer(new LayerBrownMooshroomMushroom(this));
    }

    public ModelCow getMainModel() {
        return (ModelCow)super.getMainModel();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityBrownMooshroom entity) {
        return BROWN_MOOSHROOM_TEXTURES;
    }
}