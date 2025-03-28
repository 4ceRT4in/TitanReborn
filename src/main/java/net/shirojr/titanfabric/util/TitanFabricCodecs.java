package net.shirojr.titanfabric.util;

import com.mojang.serialization.Codec;
import net.shirojr.titanfabric.util.effects.WeaponEffectData;

import java.util.HashSet;
import java.util.List;

public class TitanFabricCodecs {
    public static final Codec<HashSet<WeaponEffectData>> WEAPON_EFFECTS = Codec.list(WeaponEffectData.CODEC).xmap(HashSet::new, List::copyOf);

}
