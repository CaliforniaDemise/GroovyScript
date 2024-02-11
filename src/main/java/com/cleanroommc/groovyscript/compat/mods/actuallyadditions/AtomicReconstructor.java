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
import de.ellpeck.actuallyadditions.api.lens.ILensItem;
import de.ellpeck.actuallyadditions.api.lens.Lens;
import de.ellpeck.actuallyadditions.api.recipe.LensConversionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

public class AtomicReconstructor extends VirtualizedRegistry<LensConversionRecipe> {

    public AtomicReconstructor() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES::remove);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.addAll(restoreFromBackup());
    }

    public LensConversionRecipe add(Ingredient input, ItemStack output, int energy, ItemStack lens) {
        if (lens.getItem() instanceof ILensItem item) {
            Lens type = item.getLens();
            return add(input, output, energy, type);
        }
        return null;
    }

    public LensConversionRecipe add(Ingredient input, ItemStack output, int energy, Lens type) {
        LensConversionRecipe recipe = new LensConversionRecipe(input, output, energy, type);
        add(recipe);
        return recipe;
    }

    public void add(LensConversionRecipe recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.add(recipe);
    }

    public boolean remove(LensConversionRecipe recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.remove(recipe);
        return true;
    }

    public boolean removeByInput(IIngredient input) {
        if (IngredientHelper.isEmpty(input) {
            GroovyLog.msg("Error removing Actually Additions Atomic Reconstructor recipe")
                    .add("input must not be empty")
                    .error()
                    .post();
        }
        return ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.removeIf(recipe -> {
            boolean found = recipe.getInput().test(IngredientHelper.toItemStack(input));
            if (found) {
                addBackup(recipe);
            }
            return found;
        });
    }

    public boolean removeByOutput(ItemStack output) {
        if (IngredientHelper.isEmpty(output) {
            GroovyLog.msg("Error removing Actually Additions Atomic Reconstructor recipe")
                    .add("output must not be empty")
                    .error()
                    .post();
        }
        return ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.removeIf(recipe -> {
            boolean matches = ItemStack.areItemStacksEqual(recipe.getOutput(), output);
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        });
    }

    public boolean removeByLens(ItemStack stack) {
        if (stack.getItem() instanceof ILensItem lens) {
            return removeByLens(lens.getLens());
        }
        GroovyLog.msg("Error removing Actually Additions Atomic Reconstructor recipe")
                    .add("given item is not lens")
                    .error()
                    .post();
    }

    public boolean removeByLens(Lens type) {
        if (lens == null) return null;
        return ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.removeIf(recipe -> {
            boolean matches = recipe.getType() == type;
            if (matches) {
                addBackup(recipe);
            }
            return matches;
        }
    }

    public void removeAll() {
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.forEach(this::addBackup);
        ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES.clear();
    }

    public SimpleObjectStream<LensConversionRecipe> streamRecipes() {
        return new SimpleObjectStream<>(ActuallyAdditionsAPI.RECONSTRUCTOR_LENS_CONVERSION_RECIPES)
                .setRemover(this::remove);
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<LensConversionRecipe> {

        private int energyUse = 1;

        public RecipeBuilder energyUse(int energyUse) {
            this.energyUse = energyUse;
            return this;
        }

        public RecipeBuilder energy(int energy) {
            this.energyUse = energy;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Actually Additions Atomic Reconstructor recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(energyUse <= 0, "energyUse must be an integer greater than 0, yet it was {}", energyUse);
        }

        @Override
        public @Nullable LensConversionRecipe register() {
            if (!validate()) return null;
            LensConversionRecipe recipe = new LensConversionRecipe(input.get(0).toMcIngredient(), output.get(0), energyUse, ActuallyAdditionsAPI.lensDefaultConversion);
            ModSupport.ACTUALLY_ADDITIONS.get().atomicReconstructor.add(recipe);
            return recipe;
        }
    }
                                                           }
