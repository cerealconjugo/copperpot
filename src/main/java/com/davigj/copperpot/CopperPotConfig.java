package com.davigj.copperpot;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class CopperPotConfig {

   public static final ForgeConfigSpec COMMON_SPEC;
   public static final CopperPotConfig.Common COMMON;

   static {
      Pair<Common, ForgeConfigSpec> commonSpecPair = (new ForgeConfigSpec.Builder()).configure(CopperPotConfig.Common::new);
      COMMON_SPEC = (ForgeConfigSpec) commonSpecPair.getRight();
      COMMON = (CopperPotConfig.Common) commonSpecPair.getLeft();
   }

   public static class Common {
      // initialize
      public final ForgeConfigSpec.ConfigValue<List<String>> mooncakeBadReactDims;
      public final ForgeConfigSpec.DoubleValue copperFumeRadius;

      // define
      Common(ForgeConfigSpec.Builder builder) {
         mooncakeBadReactDims = builder.comment("A list of dimensions in which mooncakes apply adverse effects " +
                 "to the player when consumed, usually due to the lack of a moon.").define("mooncakeBadReactDims",
                 Lists.newArrayList("minecraft:the_nether", "minecraft:the_end"));
         copperFumeRadius = builder.comment("The horizontal radius for which copper pots will grant effects with no fume inhibitors involved.")
                 .defineInRange("copperFumeRadius", 3.0D, 1.0D, Double.MAX_VALUE);
      }
   }
}
