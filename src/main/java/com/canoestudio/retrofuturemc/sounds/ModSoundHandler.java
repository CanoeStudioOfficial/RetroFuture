package com.canoestudio.retrofuturemc.sounds;

import com.canoestudio.retrofuturemc.RetroFuturemc;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ModSoundHandler {
    //To add a sound, remember assets.mmf.sounds.json
    public static final List<ModSoundEvent> SOUNDS = new ArrayList<>();

    public static SoundEvent BLOCK_LANTER_BREAK = new ModSoundEvent("block.lantern.break");
    public static SoundEvent BLOCK_LANTER_STEP = new ModSoundEvent("block.lantern.step");
    public static SoundEvent BLOCK_LANTER_PLACE = new ModSoundEvent("block.lantern.place");
    public static SoundEvent BLOCK_LANTER_HIT = new ModSoundEvent("block.lantern.hit");
    public static SoundEvent BLOCK_LANTER_FALL = new ModSoundEvent("block.lantern.fall");

    public static SoundEvent BLOCK_CHAIN_BREAK = new ModSoundEvent("block.chain.break");
    public static SoundEvent BLOCK_CHAIN_STEP = new ModSoundEvent("block.chain.step");
    public static SoundEvent BLOCK_CHAIN_PLACE = new ModSoundEvent("block.chain.place");
    public static SoundEvent BLOCK_CHAIN_HIT = new ModSoundEvent("block.chain.hit");
    public static SoundEvent BLOCK_CHAIN_FALL = new ModSoundEvent("block.chain.fall");

    public static SoundEvent BLOCK_BARREL_OPEN = new ModSoundEvent("block.barrel.open");
    public static SoundEvent BLOCK_BARREL_CLOSE = new ModSoundEvent("block.barrel.close");

    public static SoundEvent STRIP_WOOD = new ModSoundEvent("item.axe.strip");

    public static SoundEvent BLOCK_BERRY_BREAK = new ModSoundEvent("block.sweet_berry_bush.break");
    public static SoundEvent BLOCK_BERRY_PLACE = new ModSoundEvent("block.sweet_berry_bush.place");

    public static SoundEvent PICK_BERRIES = new ModSoundEvent("item.sweet_berries.pick_from_bush");

    public static SoundEvent BLOCK_COMPOSTER_EMPTY = new ModSoundEvent("block.composter.empty");
    public static SoundEvent BLOCK_COMPOSTER_FILL = new ModSoundEvent("block.composter.fill");
    public static SoundEvent BLOCK_COMPOSTER_FILL_SUCCESS = new ModSoundEvent("block.composter.fill_success");
    public static SoundEvent BLOCK_COMPOSTER_REDAY = new ModSoundEvent("block.composter.ready");

    public static SoundEvent BLOCK_DEEPSLATE_BREAK = new ModSoundEvent("block.deepslate.break");
    public static SoundEvent BLOCK_DEEPSLATE_STEP = new ModSoundEvent("block.deepslate.step");
    public static SoundEvent BLOCK_DEEPSLATE_PLACE = new ModSoundEvent("block.deepslate.place");
    public static SoundEvent BLOCK_DEEPSLATE_HIT = new ModSoundEvent("block.deepslate.hit");
    public static SoundEvent BLOCK_DEEPSLATE_FALL = new ModSoundEvent("block.deepslate.fall");

    public static SoundEvent BLOCK_DEEPSLATE_BRICKS_BREAK = new ModSoundEvent("block.deepslate_bricks.break");
    public static SoundEvent BLOCK_DEEPSLATE_BRICKS_STEP = new ModSoundEvent("block.deepslate_bricks.step");
    public static SoundEvent BLOCK_DEEPSLATE_BRICKS_PLACE = new ModSoundEvent("block.deepslate_bricks.place");
    public static SoundEvent BLOCK_DEEPSLATE_BRICKS_HIT = new ModSoundEvent("block.deepslate_bricks.hit");
    public static SoundEvent BLOCK_DEEPSLATE_BRICKS_FALL = new ModSoundEvent("block.deepslate_bricks.fall");

    public static SoundEvent BLOCK_CAVE_VINES_BREAK = new ModSoundEvent("block.cave_vines.break");
    public static SoundEvent BLOCK_CAVE_VINES_STEP = new ModSoundEvent("block.cave_vines.step");
    public static SoundEvent BLOCK_CAVE_VINES_PLACE = new ModSoundEvent("block.cave_vines.place");
    public static SoundEvent BLOCK_CAVE_VINES_HIT = new ModSoundEvent("block.cave_vines.hit");
    public static SoundEvent BLOCK_CAVE_VINES_FALL = new ModSoundEvent("block.cave_vines.fall");

    public static SoundEvent BLOCK_PUMPKIN_CARVE = new ModSoundEvent("block.pumpkin.carve");

    public static SoundEvent BLOCK_AMETHYST_CHIME = new ModSoundEvent("block.amethyst_block.chime");

    public static SoundEvent BLOCK_AMETHYST_BREAK = new ModSoundEvent("block.amethyst_block.break");
    public static SoundEvent BLOCK_AMETHYST_STEP = new ModSoundEvent("block.amethyst_block.step");
    public static SoundEvent BLOCK_AMETHYST_PLACE = new ModSoundEvent("block.amethyst_block.place");
    public static SoundEvent BLOCK_AMETHYST_HIT = new ModSoundEvent("block.amethyst_block.hit");
    public static SoundEvent BLOCK_AMETHYST_FALL = new ModSoundEvent("block.amethyst_block.fall");

    public static SoundEvent BLOCK_AMETHYST_CLUSTER_BREAK = new ModSoundEvent("block.amethyst_cluster.break");
    public static SoundEvent BLOCK_AMETHYST_CLUSTER_STEP = new ModSoundEvent("block.amethyst_cluster.step");
    public static SoundEvent BLOCK_AMETHYST_CLUSTER_PLACE = new ModSoundEvent("block.amethyst_cluster.place");
    public static SoundEvent BLOCK_AMETHYST_CLUSTER_HIT = new ModSoundEvent("block.amethyst_cluster.hit");
    public static SoundEvent BLOCK_AMETHYST_CLUSTER_FALL = new ModSoundEvent("block.amethyst_cluster.fall");

    public static SoundEvent BLOCK_MOSS_BREAK = new ModSoundEvent("block.moss.break");
    public static SoundEvent BLOCK_MOSS_STEP = new ModSoundEvent("block.moss.step");
    public static SoundEvent BLOCK_MOSS_PLACE = new ModSoundEvent("block.moss.place");
    public static SoundEvent BLOCK_MOSS_HIT = new ModSoundEvent("block.moss.hit");
    public static SoundEvent BLOCK_MOSS_FALL = new ModSoundEvent("block.moss.fall");

    public static SoundEvent BLOCK_AZALEA_BREAK = new ModSoundEvent("block.azalea.break");
    public static SoundEvent BLOCK_AZALEA_STEP = new ModSoundEvent("block.azalea.step");
    public static SoundEvent BLOCK_AZALEA_PLACE = new ModSoundEvent("block.azalea.place");
    public static SoundEvent BLOCK_AZALEA_HIT = new ModSoundEvent("block.azalea.hit");
    public static SoundEvent BLOCK_AZALEA_FALL = new ModSoundEvent("block.azalea.fall");

    public static SoundEvent BLOCK_AZALEA_LEAVES_BREAK = new ModSoundEvent("block.azalea_leaves.break");
    public static SoundEvent BLOCK_AZALEA_LEAVES_STEP = new ModSoundEvent("block.azalea_leaves.step");
    public static SoundEvent BLOCK_AZALEA_LEAVES_PLACE = new ModSoundEvent("block.azalea_leaves.place");
    public static SoundEvent BLOCK_AZALEA_LEAVES_HIT = new ModSoundEvent("block.azalea_leaves.hit");
    public static SoundEvent BLOCK_AZALEA_LEAVES_FALL = new ModSoundEvent("block.azalea_leaves.fall");

    public static SoundEvent BLOCK_HANGING_ROOTS_BREAK = new ModSoundEvent("block.hanging_roots.break");
    public static SoundEvent BLOCK_HANGING_ROOTS_STEP = new ModSoundEvent("block.hanging_roots.step");
    public static SoundEvent BLOCK_HANGING_ROOTS_PLACE = new ModSoundEvent("block.hanging_roots.place");
    public static SoundEvent BLOCK_HANGING_ROOTS_HIT = new ModSoundEvent("block.hanging_roots.hit");
    public static SoundEvent BLOCK_HANGING_ROOTS_FALL = new ModSoundEvent("block.hanging_roots.fall");

    public static SoundEvent BLOCK_ROOTED_DIRT_BREAK = new ModSoundEvent("block.rooted_dirt.break");
    public static SoundEvent BLOCK_ROOTED_DIRT_STEP = new ModSoundEvent("block.rooted_dirt.step");
    public static SoundEvent BLOCK_ROOTED_DIRT_PLACE = new ModSoundEvent("block.rooted_dirt.place");
    public static SoundEvent BLOCK_ROOTED_DIRT_HIT = new ModSoundEvent("block.rooted_dirt.hit");
    public static SoundEvent BLOCK_ROOTED_DIRT_FALL = new ModSoundEvent("block.rooted_dirt.fall");

    public static SoundEvent BLOCK_BIG_DRIPLEAF_BREAK = new ModSoundEvent("block.big_dripleaf.break");
    public static SoundEvent BLOCK_BIG_DRIPLEAF_STEP = new ModSoundEvent("block.big_dripleaf.step");
    public static SoundEvent BLOCK_BIG_DRIPLEAF_PLACE = new ModSoundEvent("block.big_dripleaf.place");
    public static SoundEvent BLOCK_BIG_DRIPLEAF_HIT = new ModSoundEvent("block.big_dripleaf.hit");
    public static SoundEvent BLOCK_BIG_DRIPLEAF_FALL = new ModSoundEvent("block.big_dripleaf.fall");


    public static void soundRegister()
    {
        ForgeRegistries.SOUND_EVENTS.registerAll(ModSoundHandler.SOUNDS.toArray(new SoundEvent[0]));
    }

}