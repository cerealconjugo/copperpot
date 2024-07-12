package com.davigj.copperpot.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BakedAlaskaBlock extends Block {
   public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
   public static final VoxelShape SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 5.0D, 12.0D);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;


   public BakedAlaskaBlock(Block.Properties properties) {
      super(properties);
      this.registerDefaultState(this.stateDefinition.any().setValue(BITES, 0));
   }
   @Override
   public RenderShape getRenderShape( BlockState pState ) {
      return RenderShape.MODEL;
   }

   @Override
   public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
      return SHAPE;
   }

   @Override
   public InteractionResult use( BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (worldIn.isClientSide) {
         ItemStack itemstack = player.getItemInHand(handIn);
         if (this.eatSlice(worldIn, pos, state, player).consumesAction()) {
            return InteractionResult.SUCCESS;
         }
         if (itemstack.isEmpty()) {
            return InteractionResult.CONSUME;
         }
      }

      return this.eatSlice(worldIn, pos, state, player);
   }

   private InteractionResult eatSlice( LevelAccessor world, BlockPos pos, BlockState state, Player player) {
      if (!player.canEat(false)) {
         return InteractionResult.PASS;
      } else {
         player.awardStat(Stats.EAT_CAKE_SLICE);
         player.getFoodData().eat(3, 0.2F);
         int i = state.getValue(BITES);
         if (i < 3) {
            world.setBlock(pos, state.setValue(BITES, Integer.valueOf(i + 1)), 3);
         } else {
            world.removeBlock(pos, false);
         }
         if (!world.isClientSide()) {
            double random = Math.random();
            if (random < 0.33) {
               player.addEffect(new MobEffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "sugar_rush")).get(), 200, 2));
            } else if (random < 0.66 && random > 0.33) {
               player.addEffect(new MobEffectInstance(getCompatEffect("neapolitan", new ResourceLocation("neapolitan", "vanilla_scent")).get(), 100));
            } else if (random > 0.66 && random < 0.77) {
               player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1));
            } else {
               player.heal(4.0F);
            }
         }
         return InteractionResult.SUCCESS;
      }
   }

   private static Supplier<MobEffect> getCompatEffect( String modid, ResourceLocation effect) {
      return ( ModList.get().isLoaded(modid) ? () -> ForgeRegistries.MOB_EFFECTS.getValue(effect) : () -> null);
   }


   public BlockState updateShape( BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean canSurvive(LevelAccessor worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.below()).isSolid();
   }

   @Override
   protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
      super.createBlockStateDefinition(builder);
      builder.add(FACING, BITES);
   }


   @Override
   public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
      return (4 - blockState.getValue(BITES)) * 2;
   }

   @Override
   public boolean hasAnalogOutputSignal(BlockState state) {
      return true;
   }

   @Override
   public boolean isPathfindable( BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType ) {
      return false;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement( BlockPlaceContext pContext ) {
      return (BlockState)this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
   }

}
