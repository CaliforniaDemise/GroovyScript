package com.cleanroommc.groovyscript.core.mixin.immersivepetroleum;

import flaxbeard.immersivepetroleum.api.crafting.LubricantHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = LubricantHandler.class, remap = false)
public interface LubricantHandlerAccessor {

    @Accessor
    static HashMap<String, Integer> getLubricantAmounts() {
        throw new AssertionError();
    }
}
