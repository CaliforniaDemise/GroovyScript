package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public abstract class ValueHandler extends VirtualizedRegistry<FluidStack> {

    private final Map<String, Integer> map;

    protected ValueHandler(Map<String, Integer> map) {
        this.map = map;
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(fluid -> map.remove(fluid.getFluid().getName()));
        restoreFromBackup().forEach(fluid -> map.put(fluid.getFluid().getName(), fluid.amount));
    }

    public boolean add(FluidStack fluid) {
        return fluid != null && map.put(fluid.getFluid().getName(), fluid.amount) != null && doAddScripted(fluid);
    }

    public boolean remove(FluidStack fluid) {
        return fluid != null && map.remove(fluid.getFluid().getName()) != null && doAddBackup(fluid);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        map.forEach((k, v) -> doAddBackup(FluidRegistry.getFluidStack(k, v)));
        map.clear();
    }
}
