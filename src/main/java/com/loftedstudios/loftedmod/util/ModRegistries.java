package com.loftedstudios.loftedmod.util;

import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;

public class ModRegistries {
    public static void registerModStuffs(){
        registerFlammableBlock();
        registerModStrippables();
    }

    private static void registerFlammableBlock(){
        FlammableBlockRegistry instance = FlammableBlockRegistry.getDefaultInstance();

        instance.add(LoftedBlocks.FEYWOOD_LOG, 5, 5);
        instance.add(LoftedBlocks.FEYWOOD_PLANKS, 5, 5);
        instance.add(LoftedBlocks.FEYWOOD_STAIRS, 5, 5);
        instance.add(LoftedBlocks.FEYWOOD_SLAB, 5, 5);
        instance.add(LoftedBlocks.FEYWOOD_FENCE, 5, 5);
        instance.add(LoftedBlocks.FEYWOOD_FENCE_GATE, 5, 5);
    }

    private static void registerModStrippables(){
        StrippableBlockRegistry.register(LoftedBlocks.FEYWOOD_LOG, LoftedBlocks.STRIPPED_FEYWOOD_LOG);
        StrippableBlockRegistry.register(LoftedBlocks.FEYWOOD_WOOD, LoftedBlocks.STRIPPED_FEYWOOD_WOOD);
    }
}
