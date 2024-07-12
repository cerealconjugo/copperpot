package com.davigj.copperpot.client.gui;

import com.davigj.copperpot.common.block.entity.container.CopperPotMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import vectorwing.farmersdelight.client.gui.CookingPotRecipeBookComponent;
import vectorwing.farmersdelight.client.gui.CookingPotScreen;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@ParametersAreNonnullByDefault
public class CopperPotScreen extends AbstractContainerScreen<CopperPotMenu> {
   private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("copperpot", "textures/gui/copper_pot.png");
   private static final Rectangle HEAT_ICON = new Rectangle(47, 55, 17, 15);
   private static final Rectangle BUBBLES_ICON = new Rectangle(176, 0, 15, 18);

   private static final Rectangle PROGRESS_ARROW = new Rectangle(89, 25, 0, 17);

   public CopperPotScreen( CopperPotMenu screenContainer, Inventory inv, Component titleIn) {
      super(screenContainer, inv, titleIn);
      this.leftPos = 0;
      this.topPos = 0;
      this.imageWidth = 176;
      this.imageHeight = 166;
      this.titleLabelX = 28;
   }

   public void render( GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
      this.renderBackground(gui);
      super.render(gui, mouseX, mouseY, partialTicks);
      this.renderMealDisplayTooltip(gui, mouseX, mouseY);
      this.renderHeatIndicatorTooltip(gui, mouseX, mouseY);
   }

   private void renderHeatIndicatorTooltip(GuiGraphics gui, int mouseX, int mouseY) {
      if (this.isHovering(HEAT_ICON.x, HEAT_ICON.y, HEAT_ICON.width, HEAT_ICON.height, (double)mouseX, (double)mouseY)) {
         String key = "container.cooking_pot." + (((CopperPotMenu)this.menu).isHeated() ? "heated" : "not_heated");
         gui.renderTooltip(this.font, Component.translatable("farmersdelight."+key), mouseX, mouseY);
      }

   }

   protected void renderMealDisplayTooltip(GuiGraphics gui, int mouseX, int mouseY) {
      if (this.minecraft != null && this.minecraft.player != null && (this.menu).getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         if (this.hoveredSlot.index == 3) {
            List<Component> tooltip = new ArrayList();
            ItemStack mealStack = this.hoveredSlot.getItem();
            tooltip.add(((MutableComponent)mealStack.getItem().getDescription()).withStyle(mealStack.getRarity().color));
            ItemStack containerStack = (this.menu).blockEntity.getContainer();
            String container = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";
            tooltip.add(TextUtils.getTranslation("container.cooking_pot.served_on", new Object[]{container}).withStyle(ChatFormatting.GRAY));
            gui.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
         } else {
            gui.renderTooltip(this.font, this.hoveredSlot.getItem(), mouseX, mouseY);
         }
      }

   }

   protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
      super.renderLabels(gui, mouseX, mouseY);
      gui.drawString(this.font, this.playerInventoryTitle, 8, this.imageHeight - 96 + 2, 4210752, false);
   }

   protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.minecraft != null) {
         gui.blit(BACKGROUND_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
         if (this.menu.isHeated()) {
            gui.blit(BACKGROUND_TEXTURE, this.leftPos + HEAT_ICON.x+1, this.topPos + HEAT_ICON.y-7, 176, 0, HEAT_ICON.width, HEAT_ICON.height);
         }

         int l = (this.menu).getCookProgressionScaled();
         gui.blit(BACKGROUND_TEXTURE, this.leftPos + PROGRESS_ARROW.x, this.topPos + PROGRESS_ARROW.y, 176, 15, l + 1, PROGRESS_ARROW.height);
         if ((this.menu).hasEffect()) {
            gui.blit( BACKGROUND_TEXTURE, this.leftPos + 123, this.topPos+25-((int)(0.9*l)), 176, 55-((int)(0.9*l)), BUBBLES_ICON.width, (int)(0.9*l));
         }
      }
   }
}
