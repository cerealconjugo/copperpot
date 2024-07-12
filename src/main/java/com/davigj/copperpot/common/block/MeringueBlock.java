package com.davigj.copperpot.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

public class MeringueBlock extends Block {
   protected static final VoxelShape SHAPES = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

   public MeringueBlock(Properties properties) {
      super(properties);
   }

   @Override
   public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
      return SHAPES;
   }

   @Override
   public void entityInside( BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity ) {
      if (this.isRiding(pEntity)) {
         this.ride(pEntity);
      }
      super.entityInside(pState, pLevel, pPos, pEntity);
   }

   private boolean isRiding(Entity entity) {
      Vec3 vector3d = entity.getDeltaMovement();
      return Math.abs(vector3d.y) > 0.1;
   }

   private void ride(Entity entity) {
      Vec3 vector3d = entity.getDeltaMovement();
      double amp = 0;
      if (entity instanceof LivingEntity living) {
         if (entity instanceof Player player ) {
            MobEffectInstance[] effects = {player.getEffect(MobEffects.MOVEMENT_SPEED),
                    player.getEffect(MobEffects.JUMP),
                    player.getEffect(MobEffects.SLOW_FALLING),
                    player.getEffect(MobEffects.MOVEMENT_SLOWDOWN)};
            for (int currentEffect = 0; currentEffect < 4; currentEffect++) {
               if (effects[currentEffect] != null) {
                  if (currentEffect < 2) {
                     amp = effects[currentEffect].getAmplifier() + 1;
                  } else {
                     amp = amp - (effects[currentEffect].getAmplifier() + 1);
                  }
               }
            }
         }
         if (living.jumping && vector3d.y > 0) {
//                // we know you're ascending. you're ascending at a speed bounded by
//                // 0.3 + (amp*0.1) or 0
////                entity.setMotion(vector3d.x, Math.max(0.3 + (amp * 0.1D), vector3d.y+ (amp * 0.1D)), vector3d.z);
//
//                if (((LivingEntity) entity).isJumping && vector3d.y > 0) {
//                    // we know you're ascending. you're ascending at a speed bounded by
//                    // 0.3 + (amp*0.1) or 0. If amp is positive, 0.3 + (amp*0.1) is the top bound.
//                    // If amp is negative, 0 is the bottom bound. You maintain your normal v3y speed
//                    // if your calculation exceeds the top bound, and stop if it looks to exceed the bottom bound.
//                    // In other words, amp * 0.1 cannot go below -0.3.
//                    if (vector3d.y < 0.3 + (amp * 0.1D) && amp > 0) {
//                        entity.setMotion(new Vector3d(vector3d.x, vector3d.y + (amp * 0.1D), vector3d.z));
//                    } else if (vector3d.y < 0.3 + (amp * 0.1D) && amp <= 0) {
//                        entity.setMotion(new Vector3d(vector3d.x, Math.max(0.03, vector3d.y + (amp * 0.1D)), vector3d.z));
//                    }
//                    if (rand > 0.99) {
//                        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
//                    }
//                } else if (vector3d.y < 0D) {
//                    if(amp < 0) {
//                        entity.setMotion(new Vector3d(vector3d.x, Math.min(-0.03, Math.max(-0.1D, -0.1D - (amp * 0.05D))), vector3d.z));
//                    } else {
//                        entity.setMotion(new Vector3d(vector3d.x, -0.1D, vector3d.z));
//                    }
//                    if (rand > 0.99) {
//                        entity.playSound(SoundEvents.BLOCK_HONEY_BLOCK_STEP, 0.4F, 1.1F);
//                    }
//                }
            if (Math.abs(vector3d.y) < 0.3 + (amp * 0.1D)) {
               entity.setDeltaMovement(new Vec3(vector3d.x, Math.max(vector3d.y, vector3d.y + ((amp + 1) * 0.1D)), vector3d.z));
            } else {
               entity.setDeltaMovement(new Vec3(vector3d.x, vector3d.y, vector3d.z));
            }
            if (Math.random() > 0.99) {
               entity.playSound(SoundEvents.HONEY_BLOCK_STEP, 0.4F, 1.1F);
            }
         } else if (vector3d.y < 0D) {
            if(amp>=0) {
               entity.setDeltaMovement(new Vec3(vector3d.x, -0.1D, vector3d.z));
            }
            if (amp < 0) {
               // Magic number is -0.03 so far
               entity.setDeltaMovement(new Vec3(vector3d.x, Math.min(0.03D, -0.03D - (amp * 0.1)), vector3d.z));
            }
            if (Math.random() > 0.99) {
               entity.playSound(SoundEvents.HONEY_BLOCK_STEP, 0.4F, 1.1F);
            }
         }
         entity.fallDistance = 0.0F;
      }
   }

   @Override
   public void fallOn( Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float pFallDistance ) {
      pEntity.playSound(SoundEvents.HONEY_BLOCK_FALL, 0.6F, 1.0F);
      if (pEntity.causeFallDamage(pFallDistance, 0.3F, pEntity.damageSources().fall())) {
         pEntity.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.6F, this.soundType.getPitch() * 0.75F);
      }   }
}
