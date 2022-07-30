package com.loftedstudios.loftedmod.mixin.client;

import com.loftedstudios.loftedmod.world.dimension.LoftedDimension;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import javax.annotation.Nullable;

@Mixin(WorldRenderer.class)
public abstract class VoidDarknessMixin {
    @Shadow
    @Nullable
    private ClientWorld world;
    // Thanks to gudenau from the ParadiseLost team for making this easy
    @ModifyVariable(
            method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/world/ClientWorld$Properties;getSkyDarknessHeight(Lnet/minecraft/world/HeightLimitView;)D",
                    shift = At.Shift.BY,
                    by = 3
            )
    )
    private double dontRenderVoid(double original){
        return world != null && world.getRegistryKey() == LoftedDimension.LOFTED_WORLD_KEY ? 0 : original;
    }
}
