package com.davigj.copperpot.common.block.entity.container;

import com.davigj.copperpot.common.block.entity.CopperPotBlockEntity;
import com.davigj.copperpot.core.registry.CPBlocks;
import com.davigj.copperpot.core.registry.CPMenuTypes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.tag.ModTags;

import java.util.Objects;


public class CopperPotMenu extends RecipeBookMenu<RecipeWrapper> {
   public static final ResourceLocation EMPTY_CONTAINER_SLOT_BOWL = new ResourceLocation("farmersdelight", "item/empty_container_slot_bowl");
   public final CopperPotBlockEntity blockEntity;
   public final ItemStackHandler inventory;
   private final ContainerData copperPotData;
   private final ContainerLevelAccess canInteractWithCallable;
   protected final Level level;

   public CopperPotMenu( int windowId, Inventory playerInventory, FriendlyByteBuf data) {
      this(windowId, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(4));
   }

   public CopperPotMenu( int windowId, Inventory playerInventory, CopperPotBlockEntity blockEntity, ContainerData copperPotDataIn) {
      super(CPMenuTypes.COPPER_POT.get(), windowId);
      this.blockEntity = blockEntity;
      this.inventory = blockEntity.getInventory();
      this.copperPotData = copperPotDataIn;
      this.level = playerInventory.player.level();
      this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
      int startX = 8;
      int startY = 18;
      int inputStartX = 30;
      int borderSlotSize = 18;

         for(int column = 0; column < 3; ++column) {
            this.addSlot(new SlotItemHandler(this.inventory, column, inputStartX + column * borderSlotSize, 26 ));
         }


      this.addSlot(new CopperPotMealSlot(this.inventory, 3, 124, 26));
      this.addSlot(new SlotItemHandler(this.inventory, 4, 92, 55) {
         public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
            return Pair.of(InventoryMenu.BLOCK_ATLAS, vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu.EMPTY_CONTAINER_SLOT_BOWL);
         }
      });
      this.addSlot(new CopperPotResultSlot(playerInventory.player, blockEntity, this.inventory, 5, 124, 55));
      int startPlayerInvY = startY * 4 + 12;

      for (int row = 0; row < 3; ++row) {
         for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory,  9+(row * 9) + column, startX + (column * borderSlotSize),
                    startPlayerInvY + (row * borderSlotSize)));
         }
      }

      for(int column = 0; column < 9; ++column) {
         this.addSlot(new Slot(playerInventory, column, startX + column * borderSlotSize, 142));
      }

      this.addDataSlots(copperPotDataIn);
   }

   private static CopperPotBlockEntity getTileEntity(Inventory playerInventory, FriendlyByteBuf data) {
      Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
      Objects.requireNonNull(data, "data cannot be null");
      BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
      if (tileAtPos instanceof CopperPotBlockEntity copperPotBlock) {
         return copperPotBlock;
      } else {
         throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
      }
   }

   public boolean stillValid( Player playerIn) {
      return stillValid(this.canInteractWithCallable, playerIn, (Block) CPBlocks.COPPER_POT.get());
   }

   public ItemStack quickMoveStack( Player playerIn, int index) {
      int indexMealDisplay = 3;
      int indexContainerInput = 4;
      int indexOutput = 5;
      int startPlayerInv = indexOutput + 1;
      int endPlayerInv = startPlayerInv + 36;
      ItemStack slotStackCopy = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         slotStackCopy = slotStack.copy();
         if (index == indexOutput) {
            if (!this.moveItemStackTo(slotStack, startPlayerInv, endPlayerInv, true)) {
               return ItemStack.EMPTY;
            }
         } else if (index <= indexOutput) {
            if (!this.moveItemStackTo(slotStack, startPlayerInv, endPlayerInv, false)) {
               return ItemStack.EMPTY;
            }
         } else {
            boolean isValidContainer = slotStack.is(ModTags.SERVING_CONTAINERS) || slotStack.is(this.blockEntity.getContainer().getItem());
            if (isValidContainer && !this.moveItemStackTo(slotStack, indexContainerInput, indexContainerInput + 1, false)) {
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(slotStack, 0, indexMealDisplay, false)) {
               return ItemStack.EMPTY;
            }

            if (!this.moveItemStackTo(slotStack, indexContainerInput, indexOutput, false)) {
               return ItemStack.EMPTY;
            }
         }

         if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (slotStack.getCount() == slotStackCopy.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(playerIn, slotStack);
      }

      return slotStackCopy;
   }

   public int getCookProgressionScaled() {
      int i = this.copperPotData.get(0);
      int j = this.copperPotData.get(1);
      return j != 0 && i != 0 ? i * 24 / j : 0;
   }

   public boolean isHeated() {
      return this.blockEntity.isHeated();
   }

   public void fillCraftSlotsStackedContents( StackedContents helper) {
      for(int i = 0; i < this.inventory.getSlots(); ++i) {
         helper.accountSimpleStack(this.inventory.getStackInSlot(i));
      }

   }

   public void clearCraftingContent() {
      for(int i = 0; i < 3; ++i) {
         this.inventory.setStackInSlot(i, ItemStack.EMPTY);
      }

   }

   public boolean recipeMatches( Recipe<? super RecipeWrapper> recipe) {
      return recipe.matches(new RecipeWrapper(this.inventory), this.level);
   }

   public int getResultSlotIndex() {
      return 5;
   }

   public int getGridWidth() {
      return 3;
   }

   public int getGridHeight() {
      return 1;
   }

   public int getSize() {
      return 4;
   }

   public RecipeBookType getRecipeBookType() {
      return null;
   }

   public boolean shouldMoveToInventory(int slot) {
      return slot < this.getGridWidth() * this.getGridHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect() { return this.blockEntity.hasEffect(); }

}
