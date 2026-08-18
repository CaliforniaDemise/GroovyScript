package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.core.mixin.immersivepetroleum.FuelHandlerAccessor;
import net.minecraftforge.fluids.FluidStack;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class MotorboatFuel extends ValueHandler {

    public MotorboatFuel() {
        super(FuelHandlerAccessor.getMotorboatAmountTick());
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('lava') * 5"))
    @Override
    public boolean add(FluidStack fluid) {
        return super.add(fluid);
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("fluid('gasoline')"))
    @Override
    public boolean remove(FluidStack fluid) {
        return super.remove(fluid);
    }
}
