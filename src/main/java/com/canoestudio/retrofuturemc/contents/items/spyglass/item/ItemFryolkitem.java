package com.canoestudio.retrofuturemc.contents.items.spyglass.item;

import com.canoestudio.retrofuturemc.RetroFuturemc;
import com.canoestudio.retrofuturemc.retrofuturemc.Tags;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFryolkitem extends Item {
    public ItemFryolkitem() {
        setMaxDamage(0);
        setMaxStackSize(64);
        setTranslationKey("fryolkitem");
        setRegistryName(Tags.MOD_ID, "fryolkitem"); // 使用你的MODID
        setCreativeTab(CreativeTabs.MISC);
    }

    // 注册方法（在Mod主类中调用）
    public static void register() {
        ForgeRegistries.ITEMS.register(new ItemFryolkitem());
    }

    // 客户端模型注册（在Mod主类的ModelRegistryEvent中调用）
    @SideOnly(Side.CLIENT)
    public static void registerModel() {
        ModelLoader.setCustomModelResourceLocation(
                ForgeRegistries.ITEMS.getValue(new ResourceLocation(Tags.MOD_ID, "fryolkitem")),
                0,
                new ModelResourceLocation(new ResourceLocation(Tags.MOD_ID, "fryolkitem"), "inventory")
        );
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 0;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 1.0F;
    }
}