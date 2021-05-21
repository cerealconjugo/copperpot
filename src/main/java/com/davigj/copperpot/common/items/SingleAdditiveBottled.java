package com.davigj.copperpot.common.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;

public class SingleAdditiveBottled extends Item {
    public static final Logger LOGGER = LogManager.getLogger();
    String effectName;

    public SingleAdditiveBottled(Item.Properties properties, String effect) {
        super(properties);
        this.effectName = effect;
    }

    @Override
    public ItemStack onItemUseFinish (ItemStack stack, World worldIn, LivingEntity entityLiving) {
//        PlayerEntity playerentity = entityLiving instanceof PlayerEntity ? (PlayerEntity)entityLiving : null;
//        ItemStack container = stack.getContainerItem();
//        ItemStack itemstack = super.onItemUseFinish(stack, worldIn, entityLiving);
//        if (entityLiving instanceof PlayerEntity && ((PlayerEntity)entityLiving).abilities.isCreativeMode) {
//            extendEffect(entityLiving);
//            return itemstack;
//        } else {
//            if (playerentity == null || !playerentity.abilities.isCreativeMode) {
//                if (itemstack.isEmpty()) {
//                    return container;
//                }
//                if (playerentity != null) {
//                    playerentity.inventory.addItemStackToInventory(container);
//                }
//            }
//            extendEffect(entityLiving);
//            return stack;
//        }
        super.onItemUseFinish(stack, worldIn, entityLiving);
        if (entityLiving instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entityLiving;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
            serverplayerentity.addStat(Stats.ITEM_USED.get(this));
        }

        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (entityLiving instanceof PlayerEntity && !((PlayerEntity)entityLiving).abilities.isCreativeMode) {
                extendEffect(entityLiving);
                ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
                PlayerEntity playerentity = (PlayerEntity)entityLiving;
                if (!playerentity.inventory.addItemStackToInventory(itemstack)) {
                    playerentity.dropItem(itemstack, false);
                }
            }
            extendEffect(entityLiving);
            return stack;
        }
    }

    public void extendEffect(LivingEntity player) {
        Iterator effects = player.getActivePotionEffects().iterator();
        while(effects.hasNext()) {
            EffectInstance effect = (EffectInstance)effects.next();
            LOGGER.debug(effectName);
            if (effect.getDuration() > 10 && effect.getEffectName().equals(effectName)) {
                LOGGER.debug(effect.getEffectName());
                player.addPotionEffect(new EffectInstance(effect.getPotion(), effect.getDuration() + 50, effect.getAmplifier(), effect.isAmbient(), effect.doesShowParticles(), effect.isShowIcon()));
            }
        }
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    public SoundEvent getEatSound() {
        return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
    }

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        return DrinkHelper.startDrinking(worldIn, playerIn, handIn);
    }
}