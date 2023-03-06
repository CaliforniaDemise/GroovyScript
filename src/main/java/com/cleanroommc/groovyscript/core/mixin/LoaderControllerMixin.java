package com.cleanroommc.groovyscript.core.mixin;

import com.cleanroommc.groovyscript.GroovyScript;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.LoaderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LoadController.class, remap = false)
public class LoaderControllerMixin {

    @Inject(method = "distributeStateMessage(Lnet/minecraftforge/fml/common/LoaderState;[Ljava/lang/Object;)V", at = @At("HEAD"))
    public void preInit(LoaderState state, Object[] eventData, CallbackInfo ci) {
        if (state == LoaderState.PREINITIALIZATION) {
            GroovyScript.initializeGroovyPreInit();
        }
    }
}