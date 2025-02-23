package com.cleanroommc.groovyscript.compat.mods.immersiveengineering;

import blusunrize.immersiveengineering.api.crafting.AlloyRecipe;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AlloyKiln extends VirtualizedRegistry<AlloyRecipe> {

    public AlloyKiln() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    @ApiStatus.Internal
    public void onReload() {
        removeScripted().forEach(recipe -> AlloyRecipe.recipeList.removeIf(r -> r == recipe));
        AlloyRecipe.recipeList.addAll(restoreFromBackup());
    }

    public void add(AlloyRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            AlloyRecipe.recipeList.add(recipe);
        }
    }

    public AlloyRecipe add(ItemStack output, IIngredient input0, IIngredient input1, int time) {
        AlloyRecipe recipe = new AlloyRecipe(output, ImmersiveEngineering.toIngredientStack(input0), ImmersiveEngineering.toIngredientStack(input1), time);
        add(recipe);
        return recipe;
    }

    public boolean remove(AlloyRecipe recipe) {
        if (AlloyRecipe.recipeList.removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    public void removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output)) {
            GroovyLog.msg("Error removing Immersive Engineering Alloy Kiln recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        List<AlloyRecipe> recipes = AlloyRecipe.removeRecipes(output);
        if (recipes.isEmpty()) {
            GroovyLog.msg("Error removing Immersive Engineering Alloy Kiln recipe")
                    .add("no recipes found for {}", output)
                    .error()
                    .post();
            return;
        }
        recipes.forEach(this::addBackup);
    }

    public void removeByInput(ItemStack input, ItemStack input1) {
        if (GroovyLog.msg("Error removing Immersive Engineering Alloy Kiln recipe")
                .add(IngredientHelper.isEmpty(input), () -> "input 1 must not be empty")
                .add(IngredientHelper.isEmpty(input1), () -> "input 2 must not be empty")
                .error()
                .postIfNotEmpty()) {
            return;
        }
        AlloyRecipe recipe = AlloyRecipe.findRecipe(input, input1);
        if (recipe != null) {
            remove(recipe);
        } else {
            GroovyLog.msg("Error removing Immersive Engineering Alloy Kiln recipe")
                    .add("no recipes found for {} and {}", input, input1)
                    .error()
                    .post();
        }
    }

    public SimpleObjectStream<AlloyRecipe> streamRecipes() {
        return new SimpleObjectStream<>(AlloyRecipe.recipeList).setRemover(this::remove);
    }

    public void removeAll() {
        AlloyRecipe.recipeList.forEach(this::addBackup);
        AlloyRecipe.recipeList.clear();
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<AlloyRecipe> {

        private int time;

        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Engineering Alloy Kiln recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 2, 2, 1, 1);
            validateFluids(msg);
            if (time < 0) time = 200;
        }

        @Override
        public @Nullable AlloyRecipe register() {
            if (!validate()) return null;
            AlloyRecipe recipe = new AlloyRecipe(output.get(0), ImmersiveEngineering.toIngredientStack(input.get(0)), ImmersiveEngineering.toIngredientStack(input.get(1)), time);
            ModSupport.IMMERSIVE_ENGINEERING.get().alloyKiln.add(recipe);
            return recipe;
        }
    }

}
