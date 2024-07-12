package com.davigj.copperpot.common.block.entity.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMealSlot;

public class CopperPotMealSlot extends SlotItemHandler {
   public CopperPotMealSlot( IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
      super(inventoryIn, index, xPosition, yPosition);
   }

   public boolean mayPlace( ItemStack stack) {
      return false;
   }

   public boolean mayPickup( Player playerIn) {
      return false;
   }
}
