package com.davigj.copperpot.integration.jei;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.client.gui.CopperPotScreen;
import com.davigj.copperpot.common.block.entity.container.CopperPotMenu;
import com.davigj.copperpot.common.registry.CPItems;
import com.davigj.copperpot.common.registry.CPMenuTypes;
import com.davigj.copperpot.integration.jei.category.CopperPotRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public class JEICPPlugin implements IModPlugin {

   private static final ResourceLocation ID = new ResourceLocation(CopperPot.MODID, "jei_plugin");

   @Override
   public void registerCategories( IRecipeCategoryRegistration registry) {
      registry.addRecipeCategories(new CopperPotRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
   }

   @Override
   public void registerRecipes( IRecipeRegistration registration) {
      JEICPRecipes modRecipes = new JEICPRecipes();
      registration.addRecipes(JEICPRecipeTypes.COPPER_POT, modRecipes.getCopperPotRecipes());
   }

   @Override
   public void registerRecipeCatalysts( IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(new ItemStack(CPItems.COPPER_POT.get()), JEICPRecipeTypes.COPPER_POT);
   }

   @Override
   public void registerGuiHandlers( IGuiHandlerRegistration registration) {
      registration.addRecipeClickArea(CopperPotScreen.class, 90, 26, 22, 15, JEICPRecipeTypes.COPPER_POT);
   }

   @Override
   public void registerRecipeTransferHandlers( IRecipeTransferRegistration registration) {
      registration.addRecipeTransferHandler(CopperPotMenu.class, CPMenuTypes.COPPER_POT.get(), JEICPRecipeTypes.COPPER_POT, 0, 3, 6, 36);
   }

   @Override
   public ResourceLocation getPluginUid() {
      return ID;
   }
}
