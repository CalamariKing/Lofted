package com.loftedstudios.loftedmod;

import com.loftedstudios.loftedmod.blocks.LoftedBlocks;
import com.loftedstudios.loftedmod.client.LoftedColourProviders;
import com.loftedstudios.loftedmod.entities.ModEntityTypes;
import com.loftedstudios.loftedmod.entities.client.AirShipEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class LoftedClientMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(LoftedBlocks.LOFTED_GRASS, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(LoftedBlocks.LOFTED_FERN, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(LoftedBlocks.FEYWOOD_BRANCH, RenderLayer.getCutout());


        LoftedColourProviders.registerColourProviders();
        EntityRendererRegistry.INSTANCE.register(ModEntityTypes.AIRSHIP, AirShipEntityRenderer::new);
    }
}
