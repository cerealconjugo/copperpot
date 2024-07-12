package com.davigj.copperpot.common.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

import java.util.Iterator;

public class IncendiaryMeringue extends Item {

   public IncendiaryMeringue(Properties properties) {
      super(properties);
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if ( !pLevel.isClientSide() ) {
         double rand = Math.random();
         if ( ModList.get().isLoaded("atmospheric") && ModList.get().isLoaded("neapolitan") ) {
            if ( rand < 0.5 ) {
               pLivingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100));
            }
            if ( rand > 0.3 ) {
               setAblaze(pLivingEntity);
            }
         }
      }
      return pStack;
   }

      public void setAblaze( LivingEntity player) {
      player.setSecondsOnFire(10);
      Iterator<MobEffectInstance> effects = player.getActiveEffects().iterator();
      while (effects.hasNext()) {
         MobEffectInstance effect = effects.next();
         double rand = Math.random();
         if (effect != null && effect.getDuration() > 10 && effect.getDescriptionId().equals("effect.minecraft.strength")) {
            player.setHealth(player.getHealth() - (float)(effect.getAmplifier() * 2));
            if (rand < (double)((1 / (effect.getAmplifier() + 1)) + 0.2D)) {
               player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier() + 1, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
         }
      }
   }
}
