package com.davigj.copperpot.common.registry;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPRecipeSerializers
{
   public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CopperPot.MODID);

   public static final RegistryObject<RecipeSerializer<?>> COPPER_POT = RECIPE_SERIALIZERS.register(
           "cooking", CopperPotRecipe.Serializer::new
   );
}