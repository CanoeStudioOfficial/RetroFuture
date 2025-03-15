package com.canoestudio.retrofuturemc.registry;

import com.canoestudio.retrofuturemc.content.blocks.Blocks;
import com.canoestudio.retrofuturemc.content.items.Items;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

import static com.canoestudio.retrofuturemc.content.blocks.Blocks.BLOCKITEMS;
import static com.canoestudio.retrofuturemc.retrofuturemc.Tags.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class ContentRegister {
    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        for (Block block : Blocks.BLOCKS) {
            event.getRegistry().register(block);
        }
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        for (Item item : Items.ITEMS) {
            event.getRegistry().register(item);
        }
        for (Item blockitem : BLOCKITEMS) {
            event.getRegistry().register(blockitem);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (Item item : Items.ITEMS) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
        }
        for (Item blockitem : BLOCKITEMS) {
            ModelLoader.setCustomModelResourceLocation(blockitem, 0, new ModelResourceLocation(Objects.requireNonNull(blockitem.getRegistryName()), "inventory"));
        }
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        registerModels();
    }
}