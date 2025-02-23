package com.cleanroommc.groovyscript.compat.mods.botania;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.RecipePetals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Apothecary extends VirtualizedRegistry<RecipePetals> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public Apothecary() {
        super(Alias.generateOf("PetalApothecary"));
    }

    @Override
    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(BotaniaAPI.petalRecipes::remove);
        BotaniaAPI.petalRecipes.addAll(restoreFromBackup());
    }

    public RecipePetals add(ItemStack output, IIngredient... inputs) {
        RecipePetals recipe = new RecipePetals(output, Arrays.stream(inputs).map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict()
                                                                                                                     : i.getMatchingStacks()[0]).toArray());
        add(recipe);
        return recipe;
    }

    public void add(RecipePetals recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        BotaniaAPI.petalRecipes.add(recipe);
    }

    public boolean remove(RecipePetals recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        return BotaniaAPI.petalRecipes.remove(recipe);
    }

    public boolean removeByOutput(IIngredient output) {
        if (BotaniaAPI.petalRecipes.removeIf(recipe -> {
            boolean found = output.test(recipe.getOutput());
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Apothecary recipe")
                .add("could not find recipe with output {}", output)
                .error()
                .post();
        return false;
    }

    public boolean removeByInput(IIngredient... inputs) {
        List<Object> converted = Arrays.stream(inputs).map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict()
                                                                                               : i.getMatchingStacks()[0]).collect(Collectors.toList());
        if (BotaniaAPI.petalRecipes.removeIf(recipe -> {
            boolean found = converted.stream().allMatch(o -> recipe.getInputs().stream().anyMatch(i -> o instanceof String || i instanceof String ? o.equals(i)
                                                                                                                                                  : ItemStack.areItemStacksEqual((ItemStack) i, (ItemStack) o)));
            if (found) addBackup(recipe);
            return found;
        })) return true;

        GroovyLog.msg("Error removing Botania Apothecary recipe")
                .add("could not find recipe with inputs {}", converted)
                .error()
                .post();
        return false;
    }

    public boolean removeByInputs(IIngredient... inputs) {
        return removeByInput(inputs);
    }

    public void removeAll() {
        BotaniaAPI.petalRecipes.forEach(this::addBackup);
        BotaniaAPI.petalRecipes.clear();
    }

    public SimpleObjectStream<RecipePetals> streamRecipes() {
        return new SimpleObjectStream<>(BotaniaAPI.petalRecipes).setRemover(this::remove);
    }

    public class RecipeBuilder extends AbstractRecipeBuilder<RecipePetals> {

        @Override
        public String getErrorMsg() {
            return "Error adding Botania Apothecary recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateFluids(msg, 0, 0, 0, 0);
            validateItems(msg, 1, 20, 1, 1);
        }

        @Override
        public @Nullable RecipePetals register() {
            if (!validate()) return null;
            RecipePetals recipe = new RecipePetals(output.get(0), input.stream().map(i -> i instanceof OreDictIngredient ? ((OreDictIngredient) i).getOreDict()
                                                                                                                         : i.getMatchingStacks()[0]).toArray());
            add(recipe);
            return recipe;
        }
    }
}
