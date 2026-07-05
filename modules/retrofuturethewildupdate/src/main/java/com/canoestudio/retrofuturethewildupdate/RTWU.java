package com.canoestudio.retrofuturethewildupdate;

import com.canoestudio.retrofuturethewildupdate.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MOD_ID, name = Tags.MOD_NAME, version = Tags.VERSION)
public class RTWU {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);
    public static final String ID = Tags.MOD_ID;

    @SidedProxy(
        clientSide = "com.canoestudio.retrofuturethewildupdate.proxy.ClientProxy",
        serverSide = "com.canoestudio.retrofuturethewildupdate.proxy.CommonProxy"
    )
    public static CommonProxy proxy;

    @Mod.Instance(Tags.MOD_ID)
    public static RTWU instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        LOGGER.info("Hello From {}!", Tags.MOD_NAME);
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    public static net.minecraft.util.ResourceLocation prefix(String name) {
        return new net.minecraft.util.ResourceLocation(ID, name);
    }
}
