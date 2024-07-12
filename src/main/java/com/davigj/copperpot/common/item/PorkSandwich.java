package com.davigj.copperpot.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.List;
import java.util.function.Supplier;

public class PorkSandwich extends Item {
   String effectName;

   public PorkSandwich(Item.Properties properties) {
      super(properties);
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide() && ModList.get().isLoaded("abundance")) {
         pLivingEntity.addEffect(new MobEffectInstance(
                 getCompatEffect("abundance", new ResourceLocation(
                         "abundance", "supportive")).get(), 400));
      }
      return pStack;
   }

   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return ( ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }

}
