package com.davigj.copperpot.common;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.loot.CPCopyMealFunction;
import com.davigj.copperpot.common.registry.CPItems;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;

@Mod.EventBusSubscriber(modid = CopperPot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CPCommonSetup {
   @SubscribeEvent
   public static void init(final FMLCommonSetupEvent event) {
      event.enqueueWork(CPCommonSetup::registerCompostables);
      event.enqueueWork(CPCommonSetup::registerLootItemFunctions);

   }

   public static void registerCompostables() {
      // 30% chance
      ComposterBlock.COMPOSTABLES.put(CPItems.AUTUMNAL_AGAR.get(), 0.65F);
      ComposterBlock.COMPOSTABLES.put(CPItems.AESTIVAL_AGAR.get(), 0.65F);
      ComposterBlock.COMPOSTABLES.put(CPItems.VERNAL_AGAR.get(), 0.65F);
      ComposterBlock.COMPOSTABLES.put(CPItems.BRUMAL_AGAR.get(), 0.65F);
   }

   public static void registerLootItemFunctions() {
      LootItemFunctions.register(CPCopyMealFunction.ID.toString(), new CPCopyMealFunction.Serializer());
   }
}
