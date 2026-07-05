package com.canoestudio.retrofuturethewildupdate.client.models;

import com.canoestudio.retrofuturethewildupdate.entity.Warden;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ModelWarden extends ModelBase {

    private static final float TO_RADS = 0.017453292f;

    public ModelRenderer root;
    public ModelRenderer bone;
    public ModelRenderer body;
    public ModelRenderer head;
    public ModelRenderer rightTendril;
    public ModelRenderer leftTendril;
    public ModelRenderer leftLeg;
    public ModelRenderer leftArm;
    public ModelRenderer leftRibcage;
    public ModelRenderer rightArm;
    public ModelRenderer rightLeg;
    public ModelRenderer rightRibcage;

    public final List<ModelRenderer> tendrilsLayerModelParts;
    public final List<ModelRenderer> heartLayerModelParts;
    public final List<ModelRenderer> bioluminescentLayerModelParts;
    public final List<ModelRenderer> pulsatingSpotsLayerModelParts;

    private float tendrilAnimationFactor;

    public ModelWarden() {
        this.tendrilAnimationFactor = 1.0f;
        this.textureWidth = 128;
        this.textureHeight = 128;

        this.root = new ModelRenderer(this, 0, 0);
        this.root.setRotationPoint(0.0f, 0.0f, 0.0f);

        this.bone = new ModelRenderer(this, 0, 0);
        this.bone.setRotationPoint(0.0f, 24.0f, 0.0f);
        this.root.addChild(this.bone);

        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0f, -21.0f, 0.0f);
        this.body.setTextureOffset(0, 0).addBox(-9.0f, -13.0f, -4.0f, 18, 21, 11);
        this.bone.addChild(this.body);

        this.rightRibcage = new ModelRenderer(this, 90, 11);
        this.rightRibcage.setRotationPoint(-7.0f, -2.0f, -4.0f);
        this.rightRibcage.addBox(-2.0f, -11.0f, -0.1f, 9, 21, 0);
        this.body.addChild(this.rightRibcage);

        this.leftRibcage = new ModelRenderer(this, 90, 11);
        this.leftRibcage.mirror = true;
        this.leftRibcage.setRotationPoint(7.0f, -2.0f, -4.0f);
        this.leftRibcage.addBox(-7.0f, -11.0f, -0.1f, 9, 21, 0);
        this.leftRibcage.mirror = false;
        this.body.addChild(this.leftRibcage);

        this.head = new ModelRenderer(this, 0, 32);
        this.head.setRotationPoint(0.0f, -13.0f, 0.0f);
        this.head.addBox(-8.0f, -16.0f, -5.0f, 16, 16, 10);
        this.body.addChild(this.head);

        this.rightTendril = new ModelRenderer(this, 52, 32);
        this.rightTendril.setRotationPoint(-8.0f, -12.0f, 0.0f);
        this.rightTendril.addBox(-16.0f, -13.0f, 0.0f, 16, 16, 0);
        this.head.addChild(this.rightTendril);

        this.leftTendril = new ModelRenderer(this, 58, 0);
        this.leftTendril.setRotationPoint(8.0f, -12.0f, 0.0f);
        this.leftTendril.addBox(0.0f, -13.0f, 0.0f, 16, 16, 0);
        this.head.addChild(this.leftTendril);

        this.rightArm = new ModelRenderer(this, 44, 50);
        this.rightArm.setRotationPoint(-13.0f, -13.0f, 1.0f);
        this.rightArm.addBox(-4.0f, 0.0f, -4.0f, 8, 28, 8);
        this.body.addChild(this.rightArm);

        this.leftArm = new ModelRenderer(this, 0, 58);
        this.leftArm.setRotationPoint(13.0f, -13.0f, 1.0f);
        this.leftArm.addBox(-4.0f, 0.0f, -4.0f, 8, 28, 8);
        this.body.addChild(this.leftArm);

        this.rightLeg = new ModelRenderer(this, 76, 48);
        this.rightLeg.setRotationPoint(-5.9f, -13.0f, 0.0f);
        this.rightLeg.addBox(-3.1f, 0.0f, -3.0f, 6, 13, 6);
        this.bone.addChild(this.rightLeg);

        this.leftLeg = new ModelRenderer(this, 76, 76);
        this.leftLeg.setRotationPoint(5.9f, -13.0f, 0.0f);
        this.leftLeg.addBox(-2.9f, 0.0f, -3.0f, 6, 13, 6);
        this.bone.addChild(this.leftLeg);

        this.tendrilsLayerModelParts = Collections.unmodifiableList(Arrays.asList(this.leftTendril, this.rightTendril));
        this.heartLayerModelParts = Collections.unmodifiableList(Arrays.asList(this.body));
        this.bioluminescentLayerModelParts = Collections.unmodifiableList(Arrays.asList(this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg));
        this.pulsatingSpotsLayerModelParts = Collections.unmodifiableList(Arrays.asList(this.body, this.head, this.leftArm, this.rightArm, this.leftLeg, this.rightLeg));
    }

    public void setTendrilAnimationFactor(float factor) {
        this.tendrilAnimationFactor = factor;
    }

    private float lerpRotation(float time, float[] frames, float[] degrees) {
        if (time <= frames[0]) return degrees[0] * TO_RADS;
        if (time >= frames[frames.length - 1]) return degrees[frames.length - 1] * TO_RADS;
        for (int i = 0; i < frames.length - 1; ++i) {
            if (time >= frames[i] && time <= frames[i + 1]) {
                float t = (time - frames[i]) / (frames[i + 1] - frames[i]);
                return (degrees[i] + t * (degrees[i + 1] - degrees[i])) * TO_RADS;
            }
        }
        return degrees[0] * TO_RADS;
    }

    private float lerpPosition(float time, float[] frames, float[] pos) {
        if (time <= frames[0]) return pos[0];
        if (time >= frames[frames.length - 1]) return pos[frames.length - 1];
        for (int i = 0; i < frames.length - 1; ++i) {
            if (time >= frames[i] && time <= frames[i + 1]) {
                float t = (time - frames[i]) / (frames[i + 1] - frames[i]);
                return pos[i] + t * (pos[i + 1] - pos[i]);
            }
        }
        return pos[0];
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                   float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.root.rotationPointY = 0.0f;
        this.bone.rotationPointY = 24.0f;
        this.body.rotateAngleX = 0.0f;
        this.body.rotateAngleY = 0.0f;
        this.body.rotateAngleZ = 0.0f;
        this.head.rotateAngleX = 0.0f;
        this.head.rotateAngleY = 0.0f;
        this.head.rotateAngleZ = 0.0f;
        this.rightArm.rotateAngleX = 0.0f;
        this.rightArm.rotateAngleY = 0.0f;
        this.rightArm.rotateAngleZ = 0.0f;
        this.leftArm.rotateAngleX = 0.0f;
        this.leftArm.rotateAngleY = 0.0f;
        this.leftArm.rotateAngleZ = 0.0f;
        this.rightLeg.rotateAngleX = 0.0f;
        this.leftLeg.rotateAngleX = 0.0f;
        this.leftArm.setRotationPoint(13.0f, -13.0f, 1.0f);
        this.rightArm.setRotationPoint(-13.0f, -13.0f, 1.0f);

        if (!(entityIn instanceof Warden)) {
            super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
            return;
        }

        Warden warden = (Warden) entityIn;
        Warden.WardenPose pose = warden.getPose();

        switch (pose) {
            case ROARING: {
                float timeRoar = (84.0f - warden.getAnimationTimer()) / 20.0f;
                this.body.rotateAngleX = lerpRotation(timeRoar,
                    new float[]{0.0f, 1.24f, 1.6f, 2.08f, 3.0f, 4.2f},
                    new float[]{0.0f, -25.0f, 32.5f, 40.97f, 47.5f, 0.0f});
                this.head.rotateAngleX = lerpRotation(timeRoar,
                    new float[]{0.0f, 1.24f, 1.6f, 2.84f, 4.2f},
                    new float[]{0.0f, -32.5f, -32.5f, -5.0f, 0.0f});
                this.head.rotateAngleZ = MathHelper.sin(timeRoar * 30.0f) * 0.15f;
                this.rightArm.rotateAngleX = lerpRotation(timeRoar,
                    new float[]{0.0f, 0.72f, 1.24f, 1.48f, 2.48f, 2.88f, 4.2f},
                    new float[]{0.0f, -120.0f, -77.5f, 67.5f, 37.5f, 27.6f, 0.0f});
                this.leftArm.rotateAngleX = lerpRotation(timeRoar,
                    new float[]{0.0f, 0.72f, 1.24f, 1.48f, 2.48f, 2.88f, 4.2f},
                    new float[]{0.0f, -125.0f, -76.25f, 62.5f, 37.5f, 25.0f, 0.0f});
                break;
            }
            case DIGGING: {
                float timeDig = (100.0f - warden.getAnimationTimer()) / 20.0f;
                this.root.rotationPointY = lerpPosition(timeDig,
                    new float[]{0.0f, 2.2f, 2.66f, 4.04f, 4.5f},
                    new float[]{0.0f, 11.48f, 20.27f, 22.48f, 40.0f});
                this.body.rotateAngleX = lerpRotation(timeDig,
                    new float[]{0.0f, 0.5f, 1.33f, 2.54f, 3.37f, 4.5f},
                    new float[]{0.0f, 50.0f, 82.28f, 112.28f, 147.28f, 147.5f});
                this.rightArm.rotateAngleX = lerpRotation(timeDig,
                    new float[]{0.0f, 0.5f, 1.0f, 1.75f, 2.2f, 2.54f, 4.37f},
                    new float[]{0.0f, -101.8f, 48.7f, -89.0f, -158.3f, -89.0f, -120.0f});
                this.leftArm.rotateAngleX = lerpRotation(timeDig,
                    new float[]{0.0f, 0.29f, 0.91f, 1.66f, 2.2f, 2.54f, 4.37f},
                    new float[]{0.0f, -63.8f, -86.9f, 63.0f, -153.2f, -87.0f, -120.0f});
                break;
            }
            case EMERGING: {
                float timeEmerge = (134.0f - warden.getAnimationTimer()) / 20.0f;
                this.root.rotationPointY = lerpPosition(timeEmerge,
                    new float[]{0.0f, 1.2f, 3.16f, 3.76f, 4.44f, 5.0f, 6.64f},
                    new float[]{35.0f, 32.0f, 27.0f, 14.0f, 6.0f, 3.0f, 0.0f});
                this.body.rotateAngleX = lerpRotation(timeEmerge,
                    new float[]{0.0f, 3.76f, 4.44f, 5.0f, 6.64f},
                    new float[]{0.0f, 25.0f, 47.5f, 70.0f, 0.0f});
                this.head.rotateAngleX = lerpRotation(timeEmerge,
                    new float[]{0.0f, 1.16f, 1.68f, 2.64f, 3.76f, 4.12f, 5.0f, 6.64f},
                    new float[]{0.0f, -67.5f, -67.5f, -17.5f, 70.0f, 80.0f, 77.5f, 0.0f});
                this.rightArm.rotateAngleX = lerpRotation(timeEmerge,
                    new float[]{0.0f, 1.2f, 1.68f, 2.28f, 3.36f, 4.44f, 5.0f, 6.64f},
                    new float[]{0.0f, -152.5f, -180.0f, -90.0f, -80.0f, -55.0f, -67.5f, 0.0f});
                this.leftArm.rotateAngleX = lerpRotation(timeEmerge,
                    new float[]{0.0f, 1.16f, 1.68f, 3.16f, 4.44f, 5.0f, 6.64f},
                    new float[]{0.0f, -190.0f, -90.0f, -83.5f, -52.5f, -72.5f, 0.0f});
                break;
            }
            case SNIFFING: {
                float timeSniff = (84.0f - warden.getAnimationTimer()) / 20.0f;
                this.body.rotateAngleX = lerpRotation(timeSniff,
                    new float[]{0.0f, 0.56f, 0.96f, 2.2f, 2.8f, 3.32f},
                    new float[]{0.0f, 17.5f, 0.0f, 10.0f, 10.0f, 0.0f});
                this.body.rotateAngleY = lerpRotation(timeSniff,
                    new float[]{0.0f, 0.56f, 0.96f, 2.2f, 2.8f, 3.32f},
                    new float[]{0.0f, 32.5f, 32.5f, 0.0f, -30.0f, 0.0f});
                this.head.rotateAngleX = lerpRotation(timeSniff,
                    new float[]{0.0f, 0.96f, 1.24f, 1.52f, 1.76f, 3.32f},
                    new float[]{0.0f, -22.5f, 0.0f, -35.0f, 0.0f, 0.0f});
                this.head.rotateAngleY = lerpRotation(timeSniff,
                    new float[]{0.0f, 0.68f, 0.96f, 1.52f, 2.28f, 2.88f, 3.32f},
                    new float[]{0.0f, 40.0f, 40.0f, 20.0f, -20.0f, -20.0f, 0.0f});
                break;
            }
            default: {
                int sonicCharge = warden.getDataManager().get(Warden.SONIC_BOOM_CHARGE);
                if (sonicCharge > 0) {
                    float timeBoom = sonicCharge / 20.0f;
                    this.body.rotateAngleX = lerpRotation(timeBoom,
                        new float[]{0.0f, 1.08f, 1.62f, 2.0f},
                        new float[]{0.0f, 47.5f, 55.0f, -32.5f});
                    this.head.rotateAngleX = lerpRotation(timeBoom,
                        new float[]{0.0f, 1.0f, 1.75f, 2.0f},
                        new float[]{0.0f, 67.5f, 80.0f, -45.0f});
                    this.rightArm.rotateAngleX = lerpRotation(timeBoom,
                        new float[]{0.0f, 0.87f, 1.66f, 2.0f},
                        new float[]{0.0f, -42.2f, -72.2f, 73.7f});
                    this.leftArm.rotateAngleX = lerpRotation(timeBoom,
                        new float[]{0.0f, 0.87f, 1.66f, 2.0f},
                        new float[]{0.0f, -33.8f, -51.3f, 73.7f});
                    this.body.rotateAngleY = MathHelper.sin(ageInTicks * 1.5f) * 0.05f;
                    this.animateHeadLookTarget(netHeadYaw, headPitch);
                    break;
                }
                this.animateHeadLookTarget(netHeadYaw, headPitch);
                this.animateWalk(limbSwing, limbSwingAmount);
                this.animateIdlePose(ageInTicks);
                if (this.swingProgress > 0.0f) {
                    this.applyAttackProgress(this.swingProgress);
                }
                break;
            }
        }
        this.animateTendrils(this.tendrilAnimationFactor, ageInTicks);
    }

    private void animateHeadLookTarget(float netHeadYaw, float headPitch) {
        this.head.rotateAngleX = headPitch * TO_RADS;
        this.head.rotateAngleY = netHeadYaw * TO_RADS;
    }

    private void animateIdlePose(float ageInTicks) {
        float f = ageInTicks * 0.1f;
        this.head.rotateAngleZ += 0.06f * MathHelper.cos(f);
        this.head.rotateAngleX += 0.06f * MathHelper.sin(f);
        this.body.rotateAngleZ += 0.01f * MathHelper.sin(f);
        this.body.rotateAngleX += 0.01f * MathHelper.cos(f);
    }

    private void animateWalk(float limbSwing, float limbSwingAmount) {
        float f = Math.min(0.5f, 3.0f * limbSwingAmount);
        float f2 = limbSwing * 0.8662f;
        float f3 = MathHelper.cos(f2);
        float f4 = MathHelper.sin(f2);
        float f5 = Math.min(0.35f, f);
        this.head.rotateAngleZ += 0.3f * f4 * f;
        this.head.rotateAngleX += 1.2f * MathHelper.cos(f2 + 1.5707964f) * f5;
        this.body.rotateAngleZ += 0.05f * f4 * f;
        this.body.rotateAngleX += 0.5f * f3 * f5;
        this.leftLeg.rotateAngleX = 1.0f * f3 * f;
        this.rightLeg.rotateAngleX = 1.0f * MathHelper.cos(f2 + 3.1415927f) * f;
        this.leftArm.rotateAngleX = -(0.8f * f3 * f);
        this.rightArm.rotateAngleX = -(0.8f * f4 * f);
    }

    public void applyAttackProgress(float progress) {
        float smash = (float) Math.sin(progress * 3.141592653589793);
        float lift = -2.5f * smash;
        this.rightArm.rotateAngleX += lift;
        this.leftArm.rotateAngleX += lift;
        this.rightArm.rotationPointZ = 1.0f - smash * 5.0f;
        this.leftArm.rotationPointZ = 1.0f - smash * 5.0f;
    }

    private void animateTendrils(float entityTendrilValue, float ageInTicks) {
        float angle = (float) Math.sin(ageInTicks * 0.1f) * 0.1f;
        this.leftTendril.rotateAngleX = angle;
        this.rightTendril.rotateAngleX = -angle;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        this.root.render(scale);
    }

    public List<ModelRenderer> getTendrilsLayerModelParts() {
        return this.tendrilsLayerModelParts;
    }

    public List<ModelRenderer> getHeartLayerModelParts() {
        return this.heartLayerModelParts;
    }

    public List<ModelRenderer> getBioluminescentLayerModelParts() {
        return this.bioluminescentLayerModelParts;
    }

    public List<ModelRenderer> getPulsatingSpotsLayerModelParts() {
        return this.pulsatingSpotsLayerModelParts;
    }
}
