package com.davigj.copperpot.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import vectorwing.farmersdelight.common.utility.TextUtils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class SpicedAppleJam extends Item {
   String effectName;
   String effectName2;

   public SpicedAppleJam(Item.Properties properties, String effect1, String effect2) {
      super(properties);
      this.effectName = effect1;
      this.effectName2 = effect2;
   }


   @Override
   public ItemStack finishUsingItem( ItemStack pStack, Level pLevel, LivingEntity pLivingEntity ) {
      super.finishUsingItem(pStack, pLevel, pLivingEntity);
      if (pLivingEntity instanceof ServerPlayer serverPlayer) {
         CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, pStack);
         serverPlayer.awardStat(Stats.ITEM_USED.get(this));
      }

      if (pStack.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         double rand = Math.random();
         if (pLivingEntity instanceof Player player && !player.getAbilities().instabuild) {
            if(ModList.get().isLoaded("fruitful")) {
               if (rand < 0.7) {
                  pLivingEntity.addEffect(new MobEffectInstance(
                          getCompatEffect("fruitful", new ResourceLocation(
                                  "fruitful", "sustaining")).get(), 200, 0));
               }
               extendEffect(pLivingEntity);
            }
            if(ModList.get().isLoaded("abundance")) {
               if (rand > 0.3) {
                  pLivingEntity.addEffect(new MobEffectInstance(
                          getCompatEffect("abundance", new ResourceLocation(
                                  "abundance", "supportive")).get(), 200, 0));
               }
               if (rand > 0.3) {
                  extendEffect(pLivingEntity);
               }
            }
            ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
            if (!player.getInventory().add(itemstack)) {
               player.drop(itemstack, false);
            }
         }
         if(ModList.get().isLoaded("fruitful")) {
            if (rand < 0.7) {
               pLivingEntity.addEffect(new MobEffectInstance(
                       getCompatEffect("fruitful", new ResourceLocation(
                               "fruitful", "sustaining")).get(), 100, 0));
            }
            extendEffect(pLivingEntity);
         }
         if(ModList.get().isLoaded("abundance")) {
            if (rand > 0.3) {
               pLivingEntity.addEffect(new MobEffectInstance(
                       getCompatEffect("abundance", new ResourceLocation(
                               "abundance", "supportive")).get(), 100, 0));
            }
            if (rand > 0.3) {
               extendEffect(pLivingEntity);
            }
         }
         return pStack;
      }
   }

   public void extendEffect(LivingEntity player) {
      Iterator<MobEffectInstance> effects = player.getActiveEffects().iterator();
      while(effects.hasNext()) {
         MobEffectInstance effect = effects.next();
         if (effect.getDuration() > 10 && effect.getDescriptionId().equals(effectName) || effect.getDescriptionId().equals(effectName2)) {
            player.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration() + 60, effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
         }
      }
   }

   @Override
   public UseAnim getUseAnimation( ItemStack pStack ) {
      return UseAnim.DRINK;
   }


   @Override
   public SoundEvent getEatingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   @Override
   public int getUseDuration( ItemStack pStack ) {
      return 40;
   }

   @Override
   public SoundEvent getDrinkingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
      return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
   }

   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return ( ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }

   @Override
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText( ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced ) {
      MutableComponent tip = Component.translatable("copperpot.tooltip.spiced_apple_jam.tip");
      MutableComponent tip2 = Component.translatable("copperpot.tooltip.spiced_apple_jam.tip2");
      pTooltipComponents.add(tip.withStyle(ChatFormatting.BLUE));
      pTooltipComponents.add(tip2.withStyle(ChatFormatting.BLUE));
   }
}
