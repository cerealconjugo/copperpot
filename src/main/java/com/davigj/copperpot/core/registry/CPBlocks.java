package com.davigj.copperpot.core.registry;

import com.davigj.copperpot.CopperPot;
import com.davigj.copperpot.common.block.BakedAlaskaBlock;
import com.davigj.copperpot.common.block.CopperPotBlock;
import com.davigj.copperpot.common.block.MeringueBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CPBlocks {
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CopperPot.MODID);

   public static final RegistryObject<Block> COPPER_POT = BLOCKS.register("copper_pot", CopperPotBlock::new);

   public static final RegistryObject<Block> BAKED_ALASKA_BLOCK = BLOCKS.register("baked_alaska_block", () -> new BakedAlaskaBlock(
           BlockBehaviour.Properties.copy(Blocks.CAKE).strength(0.5F).sound(SoundType.WOOL)));

   public static final RegistryObject<Block> MERINGUE_BLOCK = BLOCKS.register("meringue_block", () -> new MeringueBlock(
           BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).strength(0.1F).sound(SoundType.SLIME_BLOCK)));

}
