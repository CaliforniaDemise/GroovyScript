package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.OilGenRecipe;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

public class OilGen extends VirtualizedRegistry<OilGenRecipe> {

    public OilGen() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES::remove);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.addAll(restoreFromBackup());
    }

    public OilGenRecipe add(Fluid input, int amount, int time) {
        return add(input.getName(), amount, time);
    }

    public OilGenRecipe add(String input, int amount, int time) {
        OilGenRecipe recipe = new OilGenRecipe(input, amount, time);
        add(recipe);
        return recipe;
    }

    public void add(OilGenRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.add(recipe);
    }

    public boolean remove(OilGenRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.remove(recipe);
        return true;
    }

    public boolean removeByInput(FluidStack fluid) {
        return this.removeByInput(fluid.getFluid());
    }

    public boolean removeByInput(Fluid fluid) {
        return this.removeByInput(fluid.getName());
    }

    public boolean removeByInput(String fluid) {
        return ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.removeIf(recipe -> {
            boolean found = fluid.equals(recipe.fluidName);
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public void removeAll() {
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES.clear();
    }

    public SimpleObjectStream<OilGenRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.OIL_GENERATOR_RECIPES)
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<OilGenRecipe> {

        private int amount;
        private int time;

        public RecipeBuilder fluidInput(FluidStack fluid) {
            this.fluidInput.add(fluid);
            if (this.amount == 0) this.amount = fluid.amount;
            return this;
        }

        public RecipeBuilder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Oil Gen recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 0, 0);
            msg.add(amount < 0, "amount must be a non negative integer, yet it was {}", amount);
            msg.add(time < 0, "time must be a non negative integer, yet it was {}", time);
        }

        @Override
        public @Nullable OilGenRecipe register() {
            if (!validate()) return null;
            OilGenRecipe recipe = new OilGenRecipe(fluidInput.get(0).getFluid().getName(), amount, time);
            ModSupport.ACTUALLY_ADDITIONS.get().oilGen.add(recipe);
            return recipe;
        }
    }
}
