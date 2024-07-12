package com.davigj.copperpot.client;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.client.gui.CopperPotScreen;
import com.davigj.copperpot.client.gui.CopperPotTooltip;
import com.davigj.copperpot.core.registry.CPMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CopperPot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CPClientSetup {
   @SubscribeEvent
   public static void init(final FMLClientSetupEvent event) {
      event.enqueueWork(() -> MenuScreens.register(CPMenuTypes.COPPER_POT.get(), CopperPotScreen::new));
   }

   @SubscribeEvent
   public static void registerCustomTooltipRenderers( RegisterClientTooltipComponentFactoriesEvent event) {
      event.register(CopperPotTooltip.CopperPotTooltipComponent.class, CopperPotTooltip::new);
   }
}
