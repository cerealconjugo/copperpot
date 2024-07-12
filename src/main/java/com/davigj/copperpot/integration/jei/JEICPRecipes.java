package com.davigj.copperpot.integration.jei;

import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import com.davigj.copperpot.common.registry.CPRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.common.registry.ModRecipeTypes;

import java.util.List;
import java.util.stream.Stream;

public class JEICPRecipes {

   private final RecipeManager recipeManager;

   public JEICPRecipes() {
      Minecraft minecraft = Minecraft.getInstance();
      ClientLevel level = minecraft.level;

      if (level != null) {
         this.recipeManager = level.getRecipeManager();
      } else {
         throw new NullPointerException("minecraft world must not be null.");
      }
   }

   public List<CopperPotRecipe> getCopperPotRecipes() {

      List<CopperPotRecipe> copperPot = recipeManager.getAllRecipesFor(CPRecipeTypes.COPPER_POT.get()).stream().toList();
      List<ItemStack> results = copperPot.stream().map(( e)->e.getResultItem(null)).toList();

      List<CopperPotRecipe> cookingPot = recipeManager.getAllRecipesFor(ModRecipeTypes.COOKING.get()).stream()
              .filter(recipe->recipe.getIngredients().size()<4)
              .filter(recipe-> results.stream().noneMatch(( e ) -> e.is(recipe.getResultItem(null).getItem())))
              .map(CopperPotRecipe::fromRecipe)
              .toList();


      return List.of(copperPot, cookingPot).stream().flatMap(List::stream).toList();
   }

}
