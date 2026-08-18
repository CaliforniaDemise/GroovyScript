package com.cleanroommc.groovyscript.core.mixin.immersivepetroleum;

import flaxbeard.immersivepetroleum.api.energy.FuelHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.HashMap;

@Mixin(value = FuelHandler.class, remap = false)
public interface FuelHandlerAccessor {

    @Accessor
    static HashMap<String, Integer> getMotorboatAmountTick() {
        throw new AssertionError();
    }
}
