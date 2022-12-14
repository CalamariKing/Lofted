package com.loftedstudios.loftedmod.mixin.client;

import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BoatEntity.class)
public interface BoatEntityAccessor {
    @Accessor
    void setTicksUnderwater(float ticksUnderwater);
    @Accessor
    BoatEntity.Location getLocation();
}