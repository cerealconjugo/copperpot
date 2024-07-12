package com.davigj.copperpot.core.registry;


import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPRecipeTypes
{
   public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CopperPot.MODID);

   public static final RegistryObject<RecipeType<CopperPotRecipe>> COPPER_POT = RECIPE_TYPES.register("cooking", () -> registerRecipeType("cooking"));

   public static <T extends Recipe<?>> RecipeType<T> registerRecipeType( final String identifier) {
      return new RecipeType<>()
      {
         public String toString() {
            return CopperPot.MODID + ":" + identifier;
         }
      };
   }
}
