package com.davigj.copperpot.core.tags;

import com.davigj.copperpot.CopperPot;
import com.teamabnormals.blueprint.core.util.TagUtil;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class CPMobEffectTags {
    public static final TagKey<MobEffect> AUTUMNAL = mobEffectTag("autumnal");
    public static final TagKey<MobEffect> BRUMAL = mobEffectTag("brumal");
    public static final TagKey<MobEffect> VERNAL = mobEffectTag("vernal");
    public static final TagKey<MobEffect> AESTIVAL = mobEffectTag("aestival");

    private static TagKey<MobEffect> mobEffectTag(String name) {
        return TagUtil.mobEffectTag(CopperPot.MODID, name);
    }
}