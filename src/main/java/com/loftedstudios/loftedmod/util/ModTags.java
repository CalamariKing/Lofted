package com.loftedstudios.loftedmod.util;


import com.loftedstudios.loftedmod.LoftedModMain;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModTags {
    public static class Blocks{

        public static final TagKey<Block> LOFTED_VEGETATION_PLACEABLE = register("lofted_vegetation_placeable");

        public static final TagKey<Block> LOFTED_BASE_STONE = register("lofted_base_stone");


    }
    public static class Items{

    }

    //registry
    private static TagKey<Block> register(String id) {
        return TagKey.of(Registry.BLOCK_KEY, new Identifier(id));
    }

    public static void registerModTags() {
        LoftedModMain.LOGGER.info("Registering ModTags for " + LoftedModMain.MOD_ID);
    }
}
