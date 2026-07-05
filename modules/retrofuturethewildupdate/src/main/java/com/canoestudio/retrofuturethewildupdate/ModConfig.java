package com.canoestudio.retrofuturethewildupdate;

import net.minecraftforge.common.config.Config;

@Config(modid = RTWU.ID, name = "RetroFutureTheWildUpdate")
public class ModConfig {

    @Config.Name("General Mechanics")
    @Config.LangKey("rtwu.config.general")
    @Config.Comment("Toggle special world mechanics like Warden spawning.")
    public static General general = new General();

    public static class General {
        @Config.Comment("Enable the Warden's natural spawning in deep dark areas.")
        @Config.LangKey("rtwu.config.general.enableWardenSpawns")
        public boolean enableWardenSpawns = true;
    }
}
