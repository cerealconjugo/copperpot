package com.davigj.copperpot.common.item;

import com.davigj.copperpot.client.gui.CopperPotTooltip;
import com.davigj.copperpot.common.block.entity.CopperPotBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class CopperPotItem extends BlockItem
{
   private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

   public CopperPotItem( Block block, Properties properties) {
      super(block, properties);
   }

   @Override
   public boolean isBarVisible( ItemStack stack) {
      return getServingCount(stack) > 0;
   }

   @Override
   public int getBarWidth(ItemStack stack) {
      return Math.min(1 + 12 * getServingCount(stack) / 64, 13);
   }

   @Override
   public int getBarColor(ItemStack stack) {
      return BAR_COLOR;
   }

   @Override
   public Optional<TooltipComponent> getTooltipImage( ItemStack stack) {
      ItemStack mealStack = CopperPotBlockEntity.getMealFromItem(stack);
      return Optional.of(new CopperPotTooltip.CopperPotTooltipComponent(mealStack));
   }

   private static int getServingCount(ItemStack stack) {
      CompoundTag nbt = stack.getTagElement("BlockEntityTag");
      if (nbt == null) {
         return 0;
      } else {
         ItemStack mealStack = CopperPotBlockEntity.getMealFromItem(stack);
         return mealStack.getCount();
      }
   }
}