package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.block.entity.CopperPotBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPBlockEntityTypes
{
   public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CopperPot.MODID);

   public static final RegistryObject<BlockEntityType<CopperPotBlockEntity>> COPPER_POT = TILES.register("copper_pot",
           () -> BlockEntityType.Builder.of(CopperPotBlockEntity::new, CPBlocks.COPPER_POT.get()).build(null));

}