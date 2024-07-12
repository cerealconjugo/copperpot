package com.davigj.copperpot.common.item;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class BakedAlaskaSlice extends Item {
   public BakedAlaskaSlice( Item.Properties properties) {
      super(properties);
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      if (!pLevel.isClientSide() && ModList.get().isLoaded("neapolitan")) {
         double random = Math.random();
         if (random < 0.25) {
            pLivingEntity.addEffect(new MobEffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "sugar_rush")).get(), 200, 2));
         } else if (random < 0.5 && random > 0.25) {
            pLivingEntity.addEffect(new MobEffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "vanilla_scent")).get(), 100));
         } else if (random > 0.5 && random < 0.8) {
            pLivingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
         } else {
            pLivingEntity.heal(4.0F);
         }
      }
      return super.finishUsingItem(pStack, pLevel, pLivingEntity);
   }


   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return (ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }
}
