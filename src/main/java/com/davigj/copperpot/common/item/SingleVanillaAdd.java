package com.davigj.copperpot.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Iterator;

public class SingleVanillaAdd extends Item {

   String effectName;

   public SingleVanillaAdd(Item.Properties properties, String effect) {
      super(properties);
      this.effectName = effect;
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide()) {
         extendEffect(pLivingEntity);
      }
      return pStack;   }



   public void extendEffect(LivingEntity player) {
      Iterator<MobEffectInstance> effects = player.getActiveEffects().iterator();
      while(effects.hasNext()) {
         MobEffectInstance effect = effects.next();
         if (effect.getDuration() > 10 && effect.getDescriptionId().equals(effectName)) {
            player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() + 200,
                    effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
         }
      }
   }
}
