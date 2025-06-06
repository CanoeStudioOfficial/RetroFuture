package com.canoestudio.retrofuturemc;

import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import com.canoestudio.retrofuturemc.utils.proxy.CommonProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class RetroFuturemc {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Tags.MOD_ID);

    @SidedProxy(clientSide = "com.canoestudio.retrofuturemc.utils.proxy.ClientProxy", serverSide = "com.canoestudio.retrofuturemc.utils.proxy.CommonProxy")
    public static CommonProxy proxy;
    public static ResourceLocation loc(String name) {
        return new ResourceLocation(Tags.MOD_ID, name.toLowerCase());
    }

    public static String locStr(String name) {
        return loc(name).toString();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) { proxy.postInit(event); }
}