package com.cleanroommc.groovyscript.compat.mods.actuallyadditions;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import de.ellpeck.actuallyadditions.api.recipe.CrusherRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class Crusher extends VirtualizedRegistry<CrusherRecipe> {

    public Crusher() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.CRUSHER_RECIPES::remove);
        ActuallyAdditionsAPI.CRUSHER_RECIPES.addAll(restoreFromBackup());
    }

    public CrusherRecipe add(Ingredient input, ItemStack output) {
        return add(input, output, ItemStack.EMPTY, 100);
    }

    public CrusherRecipe add(Ingredient input, ItemStack output, ItemStack secondaryOutput, int chance) {
        CrusherRecipe recipe = new CrusherRecipe(input, output, secondaryOutput, chance);
        add(recipe);
        return recipe;
    }

    public void add(CrusherRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.CRUSHER_RECIPES.add(recipe);
    }

    public boolean remove(CrusherRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.CRUSHER_RECIPES.remove(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        return ActuallyAdditionsAPI.CRUSHER_RECIPES.removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public boolean removeByOutput(ItemStack output) {
        return ActuallyAdditionsAPI.CRUSHER_RECIPES.removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutputOne(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    public void removeAll() {
        ActuallyAdditionsAPI.CRUSHER_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.CRUSHER_RECIPES.clear();
    }

    public SimpleObjectStream<CrusherRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.CRUSHER_RECIPES)
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<CrusherRecipe> {

        private int chance;

        public RecipeBuilder chance(int chance) {
            this.chance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Crusher recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 2);
            validateFluids(msg);
            msg.add(chance < 0 || chance > 100, "chance must be a non negative integer less than 100, yet it was {}", chance);
        }

        @Override
        public @Nullable CrusherRecipe register() {
            if (!validate()) return null;
            CrusherRecipe recipe = new CrusherRecipe(input.get(0).toMcIngredient(), output.get(0), output.size() < 2 ? ItemStack.EMPTY : output.get(1), chance);
            ModSupport.ACTUALLY_ADDITIONS.get().crusher.add(recipe);
            return recipe;
        }
    }
}
