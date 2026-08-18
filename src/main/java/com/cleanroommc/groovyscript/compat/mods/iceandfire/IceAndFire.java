package com.cleanroommc.groovyscript.compat.mods.iceandfire;

import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import com.github.alexthe666.iceandfire.item.IafDragonForgeRecipeRegistry;
import com.github.alexthe666.iceandfire.recipe.DragonForgeRecipe;
import com.github.alexthe666.iceandfire.recipe.IafRecipeRegistry;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class IceAndFire extends GroovyPropertyContainer {

    public final FireForge fireForge;
    public final IceForge iceForge;
    public final LightningForge lightningForge;

    public IceAndFire() {
        Version version = version();
        System.out.println("Found version " + version.name() + " " + version.getLightningForgeRecipes());
        fireForge = new FireForge(version.getFireForgeRecipes());
        iceForge = new IceForge(version.getIceForgeRecipes());
        lightningForge = version.getLightningForgeRecipes() != null ? new LightningForge(version.getLightningForgeRecipes()) : null;
    }

    private static Version version() {
        var entry = Loader.instance().getIndexedModList().get("iceandfire");
        if (entry == null) return Version.ORIGINAL;
        // Name should be "Ice And Fire: RotN Edition"
        if (entry.getName().contains("RotN")) return Version.ROTN;
        // Ice And Fire 2.x most likely means RLCraft edition. No name change to differentiate whatsoever.
        else if (entry.getVersion().startsWith("2")) return Version.RLCRAFT;
        else return Version.ORIGINAL;
    }

    @Deprecated
    public static boolean isRotN() {
        var entry = Loader.instance().getIndexedModList().get("iceandfire");
        if (entry == null) return false;
        // Name should be "Ice And Fire: RotN Edition"
        return entry.getName().contains("RotN");
    }

    enum Version {
        ORIGINAL() {
            @Override
            public Collection<DragonForgeRecipe> getFireForgeRecipes() {
                return IafRecipeRegistry.FIRE_FORGE_RECIPES;
            }

            @Override
            public Collection<DragonForgeRecipe> getIceForgeRecipes() {
                return IafRecipeRegistry.ICE_FORGE_RECIPES;
            }

            @Override
            public @Nullable Collection<DragonForgeRecipe> getLightningForgeRecipes() {
                return null;
            }
        },
        ROTN() {
            @Override
            public Collection<DragonForgeRecipe> getFireForgeRecipes() {
                return IafRecipeRegistry.FIRE_FORGE_RECIPES;
            }

            @Override
            public Collection<DragonForgeRecipe> getIceForgeRecipes() {
                return IafRecipeRegistry.ICE_FORGE_RECIPES;
            }

            @Override
            public @Nullable Collection<DragonForgeRecipe> getLightningForgeRecipes() {
                return IafRecipeRegistry.LIGHTNING_FORGE_RECIPES;
            }
        },
        RLCRAFT() {
            @Override
            public Collection<DragonForgeRecipe> getFireForgeRecipes() {
                return (Collection<DragonForgeRecipe>) IafDragonForgeRecipeRegistry.FIRE_FORGE_RECIPES;
            }

            @Override
            public Collection<DragonForgeRecipe> getIceForgeRecipes() {
                return (Collection<DragonForgeRecipe>) IafDragonForgeRecipeRegistry.ICE_FORGE_RECIPES;
            }

            @Override
            public @Nullable Collection<DragonForgeRecipe> getLightningForgeRecipes() {
                return (Collection<DragonForgeRecipe>) IafDragonForgeRecipeRegistry.LIGHTNING_FORGE_RECIPES;
            }
        };

        public abstract Collection<DragonForgeRecipe> getFireForgeRecipes();
        public abstract Collection<DragonForgeRecipe> getIceForgeRecipes();
        public abstract @Nullable Collection<DragonForgeRecipe> getLightningForgeRecipes();
    }
}