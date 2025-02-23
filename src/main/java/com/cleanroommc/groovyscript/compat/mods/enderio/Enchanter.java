package com.cleanroommc.groovyscript.compat.mods.enderio;

import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.enderio.recipe.CustomEnchanterRecipe;
import com.cleanroommc.groovyscript.core.mixin.enderio.SimpleRecipeGroupHolderAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.ingredient.OreDictIngredient;
import com.cleanroommc.groovyscript.helper.recipe.IRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import crazypants.enderio.base.recipe.IMachineRecipe;
import crazypants.enderio.base.recipe.MachineRecipeRegistry;
import crazypants.enderio.base.recipe.enchanter.EnchanterRecipe;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Enchanter extends VirtualizedRegistry<EnchanterRecipe> {

    public Enchanter() {
        super();
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @GroovyBlacklist
    public void onReload() {
        removeScripted().forEach(MachineRecipeRegistry.instance::removeRecipe);
        restoreFromBackup().forEach(MachineRecipeRegistry.instance::registerRecipe);
    }

    public void add(EnchanterRecipe recipe) {
        MachineRecipeRegistry.instance.registerRecipe(recipe);
        addScripted(recipe);
    }

    public void add(Enchantment enchantment, IIngredient input) {
        recipeBuilder()
                .enchantment(enchantment)
                .input(input)
                .register();
    }

    public boolean remove(EnchanterRecipe recipe) {
        if (recipe == null) return false;
        MachineRecipeRegistry.instance.removeRecipe(recipe);
        addBackup(recipe);
        return true;
    }

    public void remove(Enchantment enchantment) {
        if (enchantment == null) {
            GroovyLog.get().error("Can't remove EnderIO Enchanter recipe for null enchantment!");
            return;
        }
        List<EnchanterRecipe> recipes = new ArrayList<>();
        for (IMachineRecipe recipe : MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.ENCHANTER).values()) {
            if (recipe instanceof EnchanterRecipe && enchantment == ((EnchanterRecipe) recipe).getEnchantment()) {
                recipes.add((EnchanterRecipe) recipe);
            }
        }
        if (recipes.isEmpty()) {
            GroovyLog.get().error("Can't find EnderIO Enchanter recipe for " + enchantment.getName() + " enchantment!");
        } else {
            for (EnchanterRecipe recipe : recipes) {
                MachineRecipeRegistry.instance.removeRecipe(recipe);
                addBackup(recipe);
            }
        }
    }

    public SimpleObjectStream<EnchanterRecipe> streamRecipes() {
        return new SimpleObjectStream<>((Collection<EnchanterRecipe>) MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.ENCHANTER).values())
                .setRemover(this::remove);
    }

    public void removeAll() {
        MachineRecipeRegistry.instance.getRecipesForMachine(MachineRecipeRegistry.ENCHANTER).forEach((r, l) -> addBackup((EnchanterRecipe) l));
        ((SimpleRecipeGroupHolderAccessor) MachineRecipeRegistry.instance.getRecipeHolderssForMachine(MachineRecipeRegistry.ENCHANTER)).getRecipes().clear();
    }

    public static class RecipeBuilder implements IRecipeBuilder<EnchanterRecipe> {

        private Enchantment enchantment;
        private IIngredient input;
        private int amount = 0;
        private double costMultiplier = 1;
        private IIngredient lapis = new OreDictIngredient("gemLapis");
        private IIngredient book = IngredientHelper.toIIngredient(new ItemStack(Items.WRITABLE_BOOK));

        public RecipeBuilder enchantment(Enchantment enchantment) {
            this.enchantment = enchantment;
            return this;
        }

        public RecipeBuilder input(IIngredient ingredient) {
            this.input = ingredient;
            return this;
        }

        public RecipeBuilder amountPerLevel(int amount) {
            this.amount = amount;
            return this;
        }

        public RecipeBuilder xpCostMultiplier(double costMultiplier) {
            this.costMultiplier = costMultiplier;
            return this;
        }

        public RecipeBuilder customBook(IIngredient book) {
            this.book = book;
            return this;
        }

        public RecipeBuilder customLapis(IIngredient lapis) {
            this.lapis = lapis;
            return this;
        }

        @Override
        public boolean validate() {
            GroovyLog.Msg msg = GroovyLog.msg("Error adding EnderIO Enchanter recipe").error()
                    .add(enchantment == null, () -> "enchantment must not be null")
                    .add(IngredientHelper.isEmpty(input), () -> "input must not be empty")
                    .add(IngredientHelper.isEmpty(book), () -> "custom book must not be empty")
                    .add(IngredientHelper.isEmpty(lapis), () -> "custom lapis must not be empty");
            if (amount <= 0 && input != null) amount = input.getAmount();

            return !msg.postIfNotEmpty();
        }

        @Override
        public @Nullable EnchanterRecipe register() {
            if (!validate()) return null;
            EnchanterRecipe recipe = new CustomEnchanterRecipe(input, amount, enchantment, costMultiplier, lapis, book);
            ModSupport.ENDER_IO.get().enchanter.add(recipe);
            return recipe;
        }
    }
}
