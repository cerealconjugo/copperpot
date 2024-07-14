package com.davigj.copperpot.common.crafting;

import com.davigj.copperpot.core.registry.CPItems;
import com.davigj.copperpot.core.registry.CPRecipeSerializers;
import com.davigj.copperpot.core.registry.CPRecipeTypes;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.util.RecipeMatcher;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

import javax.annotation.Nullable;
import java.util.*;

public class CopperPotRecipe implements Recipe<RecipeWrapper> {
   public static final int INPUT_SLOTS = 3;
   private final ResourceLocation id;
   private final String group;
   private final NonNullList<Ingredient> inputItems;
   private final ItemStack output;
   private final ItemStack container;
   private final float experience;
   private final int cookTime;

   private final String effect;
   private final int effectDuration;
   private final int effectAmplifier;

   public CopperPotRecipe( ResourceLocation id, String group, NonNullList<Ingredient> inputItems, ItemStack output, ItemStack container, float experience, int cookTime, @Nullable String effect, int effectDuration, int effectAmplifier ) {
      this.id = id;
      this.group = group;
      this.inputItems = inputItems;
      this.output = output;
      if ( !container.isEmpty() ) {
         this.container = container;
      }
      else if ( !output.getCraftingRemainingItem().isEmpty() ) {
         this.container = output.getCraftingRemainingItem();
      }
      else {
         this.container = ItemStack.EMPTY;
      }

      this.experience = experience;
      this.cookTime = cookTime;

      this.effect = effect;
      this.effectDuration = effectDuration;
      this.effectAmplifier = effectAmplifier;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public String getGroup() {
      return this.group;
   }

   public NonNullList<Ingredient> getIngredients() {
      return this.inputItems;
   }

   public ItemStack getResultItem( RegistryAccess access ) {
      return this.output;
   }

   public boolean hasEffect() {
      return this.effect != null;
   }
   public String getEffect() {
      return this.effect;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }

   public int getEffectAmplifier() {
      return this.effectAmplifier;
   }

   public ItemStack getOutputContainer() {
      return this.container;
   }

   public ItemStack assemble( RecipeWrapper inv, RegistryAccess access ) {
      return this.output.copy();
   }

   public float getExperience() {
      return this.experience;
   }

   public int getCookTime() {
      return this.cookTime;
   }

   public boolean matches( RecipeWrapper inv, Level level ) {
      List<ItemStack> inputs = new ArrayList();
      int i = 0;

      for ( int j = 0; j < 3; ++j ) {
         ItemStack itemstack = inv.getItem(j);
         if ( !itemstack.isEmpty() ) {
            ++i;
            inputs.add(itemstack);
         }
      }

      return i == this.inputItems.size() && RecipeMatcher.findMatches(inputs, this.inputItems) != null;
   }

   public boolean canCraftInDimensions( int width, int height ) {
      return width * height >= this.inputItems.size();
   }

   public RecipeSerializer<?> getSerializer() {
      return (RecipeSerializer) CPRecipeSerializers.COPPER_POT.get();
   }

   public RecipeType<?> getType() {
      return (RecipeType) CPRecipeTypes.COPPER_POT.get();
   }

   public ItemStack getToastSymbol() {
      return new ItemStack((ItemLike) CPItems.COPPER_POT.get());
   }

   public boolean equals( Object o ) {
      if ( this == o ) {
         return true;
      }
      else if ( o != null && this.getClass() == o.getClass() ) {
         CopperPotRecipe that = (CopperPotRecipe) o;
         if ( Float.compare(that.getExperience(), this.getExperience()) != 0 ) {
            return false;
         }
         else if ( this.getCookTime() != that.getCookTime() ) {
            return false;
         }
         else if ( !Objects.equals(this.getEffect(), that.getEffect()) ) {
            return false;
         }
         else if ( this.getEffectAmplifier() != that.getEffectAmplifier() ) {
            return false;
         }
         else if ( this.getEffectDuration() != that.getEffectDuration() ) {
            return false;
         }
         else if ( !this.getId().equals(that.getId()) ) {
            return false;
         }
         else if ( !this.getGroup().equals(that.getGroup()) ) {
            return false;
         }
         else if ( this.inputItems.equals(that.inputItems) ) {
            return false;
         }
         else {
            return this.output.equals(that.output) && this.container.equals(that.container);
         }
      }
      else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.getId().hashCode();
      result = 31 * result + this.getGroup().hashCode();
      result = 31 * result + this.inputItems.hashCode();
      result = 31 * result + this.output.hashCode();
      result = 31 * result + this.container.hashCode();
      if ( this.getEffect() != null ) {
         result = 31 * result + this.effect.hashCode();
         result = 31 * result + this.effectAmplifier;
         result = 31 * result + this.effectDuration;
      }
      result = 31 * result + ( this.getExperience() != 0.0F ? Float.floatToIntBits(this.getExperience()) : 0 );
      result = 31 * result + this.getCookTime();
      return result;
   }

   public static boolean hasEffectInRecipe( Recipe<RecipeWrapper> recipeWrapperRecipe ) {
        return recipeWrapperRecipe instanceof CopperPotRecipe && ((CopperPotRecipe) recipeWrapperRecipe).getEffect() != null;
   }

   public static class Serializer implements RecipeSerializer<CopperPotRecipe> {
      public Serializer() {
      }

      public CopperPotRecipe fromJson( ResourceLocation recipeId, JsonObject json ) {
         String groupIn = GsonHelper.getAsString(json, "group", "");
         NonNullList<Ingredient> inputItemsIn = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
         if ( inputItemsIn.isEmpty() ) {
            throw new JsonParseException("No ingredients for cooking recipe");
         }
         else if ( inputItemsIn.size() > INPUT_SLOTS ) {
            throw new JsonParseException("Too many ingredients for cooking recipe! The max is 3");
         }
         else {

            ItemStack outputIn = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            ItemStack container = GsonHelper.isValidNode(json, "container") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "container"), true) : ItemStack.EMPTY;
            float experienceIn = GsonHelper.getAsFloat(json, "experience", 0.0F);
            int cookTimeIn = GsonHelper.getAsInt(json, "cookingtime", 200);

            if ( GsonHelper.isValidNode(json, "effect") ) {
               JsonObject effectJson = GsonHelper.getAsJsonObject(json, "effect");
               String effect = GsonHelper.getAsString(effectJson, "type");
               int effectDuration = GsonHelper.getAsInt(effectJson, "duration",40);
               int effectAmplifier = GsonHelper.getAsInt(effectJson, "amplifier",0);
               return new CopperPotRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, effect, effectDuration, effectAmplifier);
            }
            else
               return new CopperPotRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, null, -1, -1);
         }
      }

      private static NonNullList<Ingredient> readIngredients( JsonArray ingredientArray ) {
         NonNullList<Ingredient> nonnulllist = NonNullList.create();

         for ( int i = 0; i < ingredientArray.size(); ++i ) {
            Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
            if ( !ingredient.isEmpty() ) {
               nonnulllist.add(ingredient);
            }
         }

         return nonnulllist;
      }

      @Nullable
      public CopperPotRecipe fromNetwork( ResourceLocation recipeId, FriendlyByteBuf buffer ) {
         String groupIn = buffer.readUtf();

         int i = buffer.readVarInt();
         NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);

         for ( int j = 0; j < inputItemsIn.size(); ++j ) {
            inputItemsIn.set(j, Ingredient.fromNetwork(buffer));
         }

         ItemStack outputIn = buffer.readItem();
         ItemStack container = buffer.readItem();
         float experienceIn = buffer.readFloat();
         int cookTimeIn = buffer.readVarInt();

         if ( buffer.readableBytes() > 0 ) {
            String effect = buffer.readUtf();
            int effectDuration = buffer.readVarInt();
            int effectAmplifier = buffer.readVarInt();
            return new CopperPotRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, effect, effectDuration, effectAmplifier);
         }
         return new CopperPotRecipe(recipeId, groupIn, inputItemsIn, outputIn, container, experienceIn, cookTimeIn, null, -1, -1);
      }

      public void toNetwork( FriendlyByteBuf buffer, CopperPotRecipe recipe ) {
         buffer.writeUtf(recipe.group);

         buffer.writeVarInt(recipe.inputItems.size());
         Iterator var3 = recipe.inputItems.iterator();

         while ( var3.hasNext() ) {
            Ingredient ingredient = (Ingredient) var3.next();
            ingredient.toNetwork(buffer);
         }

         buffer.writeItem(recipe.output);
         buffer.writeItem(recipe.container);
         buffer.writeFloat(recipe.experience);
         buffer.writeVarInt(recipe.cookTime);

         if ( recipe.effect != null ) {
            buffer.writeUtf(recipe.effect);
            buffer.writeVarInt(recipe.effectDuration);
            buffer.writeVarInt(recipe.effectAmplifier);
         }
      }

   }
   public static CopperPotRecipe fromRecipe( Recipe<RecipeWrapper> recipe ) {
      if ( recipe instanceof CookingPotRecipe cook) {
         return new CopperPotRecipe(cook.getId(), cook.getGroup(), cook.getIngredients(), cook.getResultItem(null), cook.getOutputContainer(), cook.getExperience(), cook.getCookTime()/2, null,-1,-1);
      }
      else {
         throw new IllegalArgumentException("Can only serialize CookingPotRecipe");
      }
   }
}
