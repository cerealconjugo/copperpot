package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.block.entity.container.CopperPotMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPMenuTypes
{
   public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CopperPot.MODID);

   public static final RegistryObject<MenuType<CopperPotMenu>> COPPER_POT = MENU_TYPES
           .register("copper_pot", () -> IForgeMenuType.create(CopperPotMenu::new));
}
