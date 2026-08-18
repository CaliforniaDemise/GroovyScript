package com.cleanroommc.groovyscript.compat.mods.immersivepetroleum;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.github.bsideup.jabel.Desugar;
import flaxbeard.immersivepetroleum.api.energy.FuelHandler;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

@RegistryDescription(category = RegistryDescription.Category.ENTRIES)
public class PortableGenerator extends VirtualizedRegistry<PortableGenerator.Entry> {

    private final Map<String, Integer> fuelAmounts = FuelHandler.getFuelAmountsPerTick();
    private final Map<String, Integer> fuelFluxes = FuelHandler.getFuelFluxesPerTick();

    @Override
    public void onReload() {
        removeScripted().forEach(this::remove);
        restoreFromBackup().forEach(this::add);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("fluid('lubricant') * 5, 128"))
    public boolean add(FluidStack fluid, int energyAmount) {
        Entry entry = add(fluid.getFluid(), fluid.amount, energyAmount);
        if (entry != null) {
            addScripted(entry);
            return true;
        }
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.REMOVAL, example = @Example("fluid('gasoline')"))
    public boolean remove(FluidStack fluid) {
        Entry entry = remove(fluid.getFluid());
        if (entry != null) {
            addBackup(entry);
            return true;
        }
        return false;
    }

    @GroovyBlacklist
    private Entry add(Fluid fluid, int amount, int energyAmount) {
        if (fluid == null) return null;
        Integer amountOut = fuelAmounts.put(fluid.getName(), amount);
        if (amountOut == null) return null;
        Integer energyAmountOut = fuelFluxes.put(fluid.getName(), energyAmount);
        if (energyAmountOut == null) {
            fuelAmounts.remove(fluid.getName());
            return null;
        }
        return new Entry(fluid, amountOut, energyAmountOut);
    }

    @GroovyBlacklist
    private Entry remove(Fluid fluid) {
        if (fluid == null) return null;
        Integer amount = fuelAmounts.remove(fluid.getName());
        if (amount == null) return null;
        Integer energyAmount = fuelFluxes.remove(fluid.getName());
        if (energyAmount == null) {
            fuelAmounts.put(fluid.getName(), amount);
            return null;
        }
        return new Entry(fluid, amount, energyAmount);
    }

    @GroovyBlacklist
    private void add(Entry entry) {
        fuelAmounts.put(entry.fluid.getName(), entry.energyAmount);
        fuelFluxes.put(entry.fluid.getName(), entry.energyAmount);
    }

    @GroovyBlacklist
    private void remove(Entry entry) {
        fuelAmounts.remove(entry.fluid.getName());
        fuelFluxes.remove(entry.fluid.getName());
    }

    @Desugar
    record Entry(Fluid fluid, int amount, int energyAmount) {}
}
