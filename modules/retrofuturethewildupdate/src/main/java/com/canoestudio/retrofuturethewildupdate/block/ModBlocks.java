package com.canoestudio.retrofuturethewildupdate.block;

import com.canoestudio.retrofuturethewildupdate.RTWU;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = RTWU.ID)
public final class ModBlocks {

    public static final Block SCULK = new BlockSculk();
    public static final Block SCULK_VEIN = new BlockSculkVein();
    public static final BlockSculkSensor SCULK_SENSOR = new BlockSculkSensor();
    public static final BlockSculkShrieker SCULK_SHRIEKER = new BlockSculkShrieker();
    public static final BlockSculkCatalyst SCULK_CATALYST = new BlockSculkCatalyst();

    private static final Block[] BLOCKS = {
        SCULK,
        SCULK_VEIN,
        SCULK_SENSOR,
        SCULK_SHRIEKER,
        SCULK_CATALYST
    };

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(BLOCKS);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        for (Block block : BLOCKS) {
            registry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
        }
    }

    public static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEntitySculkSensor.class, key("sculk_sensor"));
        GameRegistry.registerTileEntity(TileEntitySculkShrieker.class, key("sculk_shrieker"));
        GameRegistry.registerTileEntity(TileEntitySculkCatalyst.class, key("sculk_catalyst"));
    }

    private static ResourceLocation key(String name) {
        return new ResourceLocation(RTWU.ID, name);
    }

    private ModBlocks() {
    }
}
