package com.canoestudio.retrofuturethewildupdate.entity;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public class ModEntities {

    private static int entityId = 0;

    public static final ResourceLocation WARDEN_NAME = new ResourceLocation(RTWU.ID, "warden");

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> registry = event.getRegistry();
        registry.register(
            EntityEntryBuilder.create()
                .entity(Warden.class)
                .factory(Warden::new)
                .id(WARDEN_NAME, entityId++)
                .name(RTWU.ID + ".warden")
                .tracker(80, 3, true)
                .build()
        );
    }
}
