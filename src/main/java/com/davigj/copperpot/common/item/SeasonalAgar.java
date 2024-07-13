package com.davigj.copperpot.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SeasonalAgar extends Item {


   ForgeConfigSpec.ConfigValue<List<String>> effectConfig;
   public SeasonalAgar( Properties properties, ForgeConfigSpec.ConfigValue<List<String>> effects) {
      super(properties);
      this.effectConfig = effects;
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide()) {
         extendEffect(pLivingEntity);
      }
      return pStack;
   }

   public void extendEffect(LivingEntity player) {
      Iterator<MobEffectInstance> effects = player.getActiveEffects().iterator();
      while(effects.hasNext()) {
         MobEffectInstance effect = effects.next();
         double rand = Math.random();
         int durationBonus;
         if (rand < 0.7) {
            durationBonus = 200;
         } else { durationBonus = 400; }
         for(String i : effectConfig.get()) {
            if (effect.getDuration() > 10 && effect.getDescriptionId().equals(i)) {
               player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() +
                       durationBonus, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }
         }
      }
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      MutableComponent prefix = Component.translatable("copperpot.tooltip.seasonal_agar.prefix");
      pTooltipComponents.add(prefix.withStyle(ChatFormatting.BLUE));
      MutableComponent effectDescription;
      for( Iterator<String> var6 = effectConfig.get().iterator(); var6.hasNext(); pTooltipComponents.add(effectDescription.withStyle(ChatFormatting.AQUA))) {
         effectDescription = Component.literal("");
         MutableComponent effectName = Component.translatable( var6.next());
         effectDescription.append(effectName);
      }
   }
}
