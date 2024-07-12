package com.davigj.copperpot.common.loot;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.block.entity.CopperPotBlockEntity;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer.simpleBuilder;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CPCopyMealFunction extends LootItemConditionalFunction
{
   public static final ResourceLocation ID = new ResourceLocation(CopperPot.MODID, "copy_meal");

   private CPCopyMealFunction( LootItemCondition[] conditions) {
      super(conditions);
   }

   public static LootItemConditionalFunction.Builder<?> builder() {
      return simpleBuilder(CPCopyMealFunction::new);
   }

   @Override
   protected ItemStack run( ItemStack stack, LootContext context) {
      BlockEntity tile = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
      if (tile instanceof CopperPotBlockEntity copPot) {
         CompoundTag tag = copPot.writeMeal(new CompoundTag());
         if (!tag.isEmpty()) {
            stack.addTagElement("BlockEntityTag", tag);
         }
      }
      return stack;
   }

   @Override
   @Nullable
   public LootItemFunctionType getType() {
      return null;
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<CPCopyMealFunction>
   {
      @Override
      public CPCopyMealFunction deserialize( JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
         return new CPCopyMealFunction(conditions);
      }
   }
}
