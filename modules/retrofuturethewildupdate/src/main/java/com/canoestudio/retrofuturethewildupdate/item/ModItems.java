package com.canoestudio.retrofuturethewildupdate.item;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public class ModItems {

    public static final ItemWardenEgg WARDEN_EGG = new ItemWardenEgg();

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(WARDEN_EGG);
    }
}
