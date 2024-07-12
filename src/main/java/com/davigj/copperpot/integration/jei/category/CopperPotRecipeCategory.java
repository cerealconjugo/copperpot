package com.davigj.copperpot.integration.jei.category;

import com.davigj.copperpot.common.crafting.CopperPotRecipe;
import com.davigj.copperpot.common.registry.CPItems;
import com.davigj.copperpot.integration.jei.JEICPRecipeTypes;
import com.google.common.collect.ImmutableList;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vectorwing.farmersdelight.common.utility.ClientRenderUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CopperPotRecipeCategory implements IRecipeCategory<CopperPotRecipe> {
   public static final ResourceLocation UID = new ResourceLocation("copperpot", "cooking");
   protected final IDrawable heatIndicator;
   protected final IDrawableAnimated arrow;
   protected final IDrawableAnimated bubbles;
   private final Component title = Component.translatable("copperpot.jei.cooking", new Object[0]);
   private final IDrawable background;
   private final IDrawable icon;

   protected final IDrawable timeIcon;
   protected final IDrawable expIcon;

   public CopperPotRecipeCategory( IGuiHelper helper) {
      ResourceLocation backgroundImage = new ResourceLocation("copperpot", "textures/gui/jei/copper_pot_jei.png");
      this.background = helper.createDrawable(backgroundImage, 29, 6, 117, 66);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(CPItems.COPPER_POT.get()));
      this.heatIndicator = helper.createDrawable(backgroundImage, 176, 0, 17, 15);
      this.arrow = helper.drawableBuilder(backgroundImage, 176, 15, 24, 17).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
      this.bubbles = helper.drawableBuilder(backgroundImage, 176, 31, 17, 19).buildAnimated(20, IDrawableAnimated.StartDirection.BOTTOM, false);
      this.expIcon = helper.createDrawable(backgroundImage, 176, 61, 9, 9);
      this.timeIcon = helper.createDrawable(backgroundImage, 176, 50, 8, 11);
   }

   public Class<? extends CopperPotRecipe> getRecipeClass() {
      return CopperPotRecipe.class;
   }

   @Override
   public RecipeType<CopperPotRecipe> getRecipeType() {
      return JEICPRecipeTypes.COPPER_POT;
   }

   @Override
   public Component getTitle() {
      return this.title;
   }

   @Override
   public IDrawable getBackground() {
      return this.background;
   }

   @Override
   public IDrawable getIcon() {
      return this.icon;
   }

//   public void setIngredients(CopperPotRecipe copperPotRecipe, IIngredients ingredients) {
//      List<Ingredient> inputAndContainer = new ArrayList(copperPotRecipe.getIngredients());
//      inputAndContainer.add(Ingredient.of(new ItemStack[]{copperPotRecipe.getOutputContainer()}));
//      ingredients.setInputIngredients(inputAndContainer);
//      ingredients.setOutput(VanillaTypes.ITEM, copperPotRecipe.getResultItem());
//   }

   @Override
   public void setRecipe(IRecipeLayoutBuilder builder, CopperPotRecipe recipe, IFocusGroup focusGroup) {

      NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
      int borderSlotSize = 18;

      for(int row = 0; row < 1; ++row) {
         for(int column = 0; column < 3; ++column) {
            int inputIndex = (row * 3) + column;
            if (inputIndex < recipeIngredients.size()) {
               builder.addSlot(RecipeIngredientRole.INPUT, (column * borderSlotSize) + 1, (row * borderSlotSize) + 20)
                       .addItemStacks(Arrays.asList(recipeIngredients.get(inputIndex).getItems()));
            }
         }
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 20)
              .addItemStack(recipe.getResultItem(null));

      if (!recipe.getOutputContainer().isEmpty()) {
         builder.addSlot(RecipeIngredientRole.CATALYST, 63, 49)
                 .addItemStack(recipe.getOutputContainer());
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 49)
              .addItemStack(recipe.getResultItem(null));

      builder.moveRecipeTransferButton(123, 53);

   }

   public void draw( CopperPotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics matrixStack, double mouseX, double mouseY) {
      this.arrow.draw(matrixStack, 60, 19);
      if (recipe.hasEffect()) {
         this.bubbles.draw(matrixStack, 94, -1);
      }
      this.heatIndicator.draw(matrixStack, 19, 41);


      timeIcon.draw(matrixStack, 64, 12);
      if ( recipe.getExperience() > 0 ) {
         expIcon.draw(matrixStack, 63, 31);
      }

   }

   public List<Component> getTooltipStrings( CopperPotRecipe recipe, IRecipeSlotsView recipeSlotsView, double mouseX, double mouseY) {
      if ( ClientRenderUtils.isCursorInsideBounds(89, 0, 30, 19, mouseX, mouseY) && recipe.hasEffect()) {
         // need to convert one number to mins:seconds
         return ImmutableList.of(Component.translatable(convertEffectName(recipe.getEffect())));
      }
      else if ( ClientRenderUtils.isCursorInsideBounds(61, 12, 22, 28, mouseX, mouseY) ) {
         List<Component> tooltipStrings = new ArrayList<>();
         int cookTime = recipe.getCookTime();
         if ( cookTime > 0 ) {
            int cookTimeSeconds = cookTime / 20;
            tooltipStrings.add(Component.translatable("gui.jei.category.smelting.time.seconds", cookTimeSeconds));
         }
         float experience = recipe.getExperience();
         if ( experience > 0 ) {
            tooltipStrings.add(Component.translatable("gui.jei.category.smelting.experience", experience));
         }

         return tooltipStrings;
      } else {
         return Collections.emptyList();
      }
   }

   private String convertEffectName(String effect) {
      // I cordially invite God to smite me down.
      String[] effectName = effect.split(":", 2);
      return ("effect." + effectName[0] + "." + effectName[1]);
   }

   private String convertEffectDuration(int duration) {
      int seconds = duration / 20;
      return Integer.toString(Math.floorDiv(seconds, 60)) + ":" + Integer.toString(seconds % 60);
   }
}
