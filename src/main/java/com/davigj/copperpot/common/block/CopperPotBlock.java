package com.davigj.copperpot.common.block;

import com.davigj.copperpot.common.block.entity.CopperPotBlockEntity;
import com.davigj.copperpot.core.registry.CPBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import vectorwing.farmersdelight.common.block.state.CookingPotSupport;
import vectorwing.farmersdelight.common.tag.ModTags;

import javax.annotation.Nullable;

public class CopperPotBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<CookingPotSupport> SUPPORT = EnumProperty.create("support", CookingPotSupport.class);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

   protected static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 5.0D, 14.0D);
   protected static final VoxelShape SHAPE_SUPPORTED = Shapes.or(SHAPE, Block.box(0.0D, -1.0D, 0.0D, 16.0D, 0.0D, 16.0D));
   protected static final VoxelShape SHAPE_HANDLE = Block.box(2.0D, 5.0D, 2.0D, 14.0D, 10.0D, 14.0D);

   public CopperPotBlock() {
      super(Properties.of()
              .mapColor(MapColor.METAL)
              .strength(2.0F, 6.0F)
              .sound(SoundType.LANTERN));
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SUPPORT, CookingPotSupport.NONE).setValue(WATERLOGGED, false).setValue(ENABLED,true));
   }

   @Override
   public ItemStack getCloneItemStack( BlockGetter level, BlockPos pos, BlockState state ) {
      ItemStack stack = super.getCloneItemStack(level, pos, state);
      CopperPotBlockEntity copperPotBE = (CopperPotBlockEntity) level.getBlockEntity(pos);
      if ( copperPotBE != null ) {
         CompoundTag nbt = copperPotBE.writeMeal(new CompoundTag());
         if ( !nbt.isEmpty() ) {
            stack.addTagElement("BlockEntityTag", nbt);
         }
         if ( copperPotBE.hasCustomName() ) {
            stack.setHoverName(copperPotBE.getCustomName());
         }
      }
      return stack;
   }

   @Override
   public RenderShape getRenderShape( BlockState pState ) {
      return RenderShape.MODEL;
   }

   @Override
   public VoxelShape getShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
      return switch(state.getValue(SUPPORT)) {
         case TRAY-> SHAPE_SUPPORTED;
         case HANDLE -> SHAPE_HANDLE;
         case NONE -> SHAPE;
      };   }

   @Override
   public VoxelShape getCollisionShape( BlockState state, BlockGetter level, BlockPos pos, CollisionContext context ) {
      return switch(state.getValue(SUPPORT)) {
         case TRAY-> SHAPE_SUPPORTED;
         case HANDLE-> SHAPE_HANDLE;
         case NONE-> SHAPE;
      };
   }

   @Override
   public BlockState getStateForPlacement( BlockPlaceContext context ) {
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      FluidState fluid = level.getFluidState(context.getClickedPos());

      BlockState state = this.defaultBlockState()
              .setValue(FACING, context.getHorizontalDirection().getOpposite())
              .setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);

      if ( context.getClickedFace().equals(Direction.DOWN) ) {
         return state.setValue(SUPPORT, CookingPotSupport.HANDLE);
      }
      return state.setValue(SUPPORT, getTrayState(level, pos)).setValue(ENABLED, true);
   }

   @Override
   public void onNeighborChange( BlockState state, LevelReader level, BlockPos pos, BlockPos neighbor ) {
      super.onNeighborChange(state, level, pos, neighbor);
      updateShape(state, level.getBlockState(pos).getValue(FACING), state, (LevelAccessor) level, pos, neighbor);
   }

   @Override
   public BlockState updateShape( BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos ) {
      if ( state.getValue(WATERLOGGED) ) {
         level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }
      if ( facing.getAxis().equals(Direction.Axis.Y) && !state.getValue(SUPPORT).equals(CookingPotSupport.HANDLE) ) {
         return state.setValue(SUPPORT, getTrayState(level, currentPos));
      }
      boolean flag = !level.hasNeighborSignal(currentPos);
      if (flag != state.getValue(ENABLED)) {
         return state.setValue(ENABLED, flag);
      }
      return state;
   }

   private CookingPotSupport getTrayState( LevelAccessor level, BlockPos pos ) {
      if ( level.getBlockState(pos.below()).is(ModTags.TRAY_HEAT_SOURCES) ) {
         return CookingPotSupport.TRAY;
      }
      return CookingPotSupport.NONE;
   }

   @Override
   public void onRemove( BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving ) {
      if ( state.getBlock() != newState.getBlock() ) {
         BlockEntity tileEntity = level.getBlockEntity(pos);
         if ( tileEntity instanceof CopperPotBlockEntity copperPotEntity ) {
            Containers.dropContents(level, pos, copperPotEntity.getDroppableInventory());
            copperPotEntity.getUsedRecipesAndPopExperience(level, Vec3.atCenterOf(pos));
            level.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   @Override
   protected void createBlockStateDefinition( StateDefinition.Builder<Block, BlockState> builder ) {
      super.createBlockStateDefinition(builder);
      builder.add(FACING, SUPPORT, WATERLOGGED, ENABLED);
   }

   @Override
   public void setPlacedBy( Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack ) {
      if ( stack.hasCustomHoverName() ) {
         BlockEntity tileEntity = level.getBlockEntity(pos);
         if ( tileEntity instanceof CopperPotBlockEntity copperPotBlock ) {
            copperPotBlock.setCustomName(stack.getHoverName());
         }
      }
   }


   @Override
   public FluidState getFluidState( BlockState state ) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity( BlockPos pos, BlockState state ) {
      return CPBlockEntityTypes.COPPER_POT.get().create(pos, state);
   }

   @Override
   public InteractionResult use( BlockState state, Level level, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult result) {
         BlockEntity tile = level.getBlockEntity(pos);
         if (tile instanceof CopperPotBlockEntity copperPotEntity) {
            ItemStack serving = (copperPotEntity).useHeldItemOnMeal(player.getItemInHand(handIn));
            if (serving != ItemStack.EMPTY) {
               if (!player.getInventory().add(serving)) {
                  player.drop(serving, false);
               }
               level.playSound(null, pos, SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.BLOCKS, 1.0F, 1.0F);
            } else {
               if (!level.isClientSide())
                  NetworkHooks.openScreen((ServerPlayer)player, copperPotEntity, pos);
            }
         }
         return InteractionResult.SUCCESS;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker( Level level, BlockState state, BlockEntityType<T> blockEntity ) {
      if ( level.isClientSide ) {
         return createTickerHelper(blockEntity, CPBlockEntityTypes.COPPER_POT.get(), CopperPotBlockEntity::animationTick);
      }
      return createTickerHelper(blockEntity, CPBlockEntityTypes.COPPER_POT.get(), CopperPotBlockEntity::cookingTick);
   }
}
