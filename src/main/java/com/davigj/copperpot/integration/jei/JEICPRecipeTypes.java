package com.davigj.copperpot.integration.jei;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import mezz.jei.api.recipe.RecipeType;

public class JEICPRecipeTypes {
   public static final RecipeType<CopperPotRecipe> COPPER_POT = RecipeType.create(CopperPot.MODID, "copper_pot", CopperPotRecipe.class);

}
