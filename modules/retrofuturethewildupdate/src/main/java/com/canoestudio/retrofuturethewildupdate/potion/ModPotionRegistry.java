package com.canoestudio.retrofuturethewildupdate.potion;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public class ModPotionRegistry {

    @SubscribeEvent
    public static void onRegisterPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().register(
            new PotionDarkness().setRegistryName(RTWU.ID, "darkness")
        );
    }
}
