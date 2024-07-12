package com.davigj.copperpot.core.tags;

import com.davigj.copperpot.CopperPot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CPBlockTags {
   public static final TagKey<Block> FUME_INHIBITORS = modBlockTag("fume_inhibitors");
   public static final TagKey<Block> PARTIAL_FUME_INHIBITORS = modBlockTag("partial_fume_inhibitors");


   private static TagKey<Block> modBlockTag(String path) {
      return TagKey.create(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(CopperPot.MODID + ":" + path));
   }
}

