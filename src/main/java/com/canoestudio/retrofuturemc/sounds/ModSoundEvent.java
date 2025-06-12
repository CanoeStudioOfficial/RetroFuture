package com.canoestudio.retrofuturemc.sounds;


import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModSoundEvent extends SoundEvent {
    public ModSoundEvent(String path) {
        super(new ResourceLocation(Tags.MOD_ID, path));
        ModSoundHandler.SOUNDS.add(this);
        setRegistryName(path);
    }

    public static void playSound(World world, BlockPos pos, String soundName) {
        playSound(world, pos, Tags.MOD_ID, soundName, 1.0F, 1.0F);
    }
    public static void playSound(World world, BlockPos pos, String soundName, float a, float b) {
        playSound(world, pos, Tags.MOD_ID, soundName, a, b);
    }
    public static void playSound(World world, BlockPos pos, String modId, String soundName) {
        playSound(world, pos, modId, soundName, 1.0F, 1.0F);
    }

    public static void playSound(World world, BlockPos pos, String modId, String soundName, float a, float b) {
        //播放音效
        world.playSound(null, pos, new SoundEvent(new ResourceLocation(modId, soundName)), SoundCategory.BLOCKS, a, b);
    }
}
