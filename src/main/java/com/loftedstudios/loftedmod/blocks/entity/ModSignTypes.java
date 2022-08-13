package com.loftedstudios.loftedmod.blocks.entity;

import com.loftedstudios.loftedmod.mixin.SignTypeAccessor;
import net.minecraft.util.SignType;

public class ModSignTypes {
    public static final SignType FEYWOOD =
            SignTypeAccessor.registerNew(SignTypeAccessor.newSignType("feywood"));
}
