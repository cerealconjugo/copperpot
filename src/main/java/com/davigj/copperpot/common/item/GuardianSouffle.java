package com.davigj.copperpot.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.List;

public class GuardianSouffle extends Item {


   public GuardianSouffle(Properties properties) {
      super(properties);
   }

   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (!pLevel.isClientSide && pLivingEntity.isInWaterRainOrBubble()) {
         float negatives = 0.0F;
         for ( MobEffectInstance effect : pLivingEntity.getActiveEffects()) {
            if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
               negatives++;
            }
         }
         pLivingEntity.heal(negatives * 2F);
      }
      return pStack;
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      MutableComponent tip = Component.translatable("copperpot.tooltip.guardian_souffle.tip");
      MutableComponent tip2 = Component.translatable("copperpot.tooltip.guardian_souffle.tip2");
      pTooltipComponents.add(tip.withStyle(ChatFormatting.BLUE));
      pTooltipComponents.add(tip2.withStyle(ChatFormatting.BLUE));
   }
}
