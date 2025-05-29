package com.canoestudio.retrofuturemc.mixin;

import com.canoestudio.retrofuturemc.contents.items.ModItems;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Shadow
    public abstract ItemStack getActiveItemStack();

    @Inject(method = "onItemUseFinish", at = @At("HEAD"), cancellable = true)
    public void onItemUseFinish(CallbackInfo callback) {
        if (getActiveItemStack().getItem() == ModItems.SPYGLASS) callback.cancel();
    }

}