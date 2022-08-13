package com.loftedstudios.loftedmod.item;

import com.loftedstudios.loftedmod.LoftedModMain;
import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class LoftedItemGroup {
    public static final ItemGroup LOFTED_ITEMGROUP = FabricItemGroupBuilder.build(new Identifier(LoftedModMain.MOD_ID, "lofted_itemgroup"),
            () -> new ItemStack(LoftedBlocks.LOFTED_FERN));
}
