package com.canoestudio.retrofuturemc.sounds;

import com.canoestudio.retrofuturemc.RetroFuturemc;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class RetroFuturemcSoundEvents {

    private static List<SoundEvent> soundList = new ArrayList<SoundEvent>();

    public static final SoundEvent SPYGLASS_STOP_USING = readyForRegistry(RetroFuturemc.loc("item.spyglass.stop_using"));
    public static final SoundEvent SPYGLASS_USE = readyForRegistry(RetroFuturemc.loc("item.spyglass.use"));

    public static void registerSounds()
    { for (SoundEvent sounds : soundList) ForgeRegistries.SOUND_EVENTS.register(sounds); }

    public static SoundEvent readyForRegistry(ResourceLocation resourceIn)
    {
        SoundEvent event = new SoundEvent(resourceIn);
        event.setRegistryName(resourceIn.toString());
        soundList.add(event);
        return event;
    }



}

