package arekkuusu.balancedhurttimer.api;

import arekkuusu.balancedhurttimer.api.capability.Capabilities;
import arekkuusu.balancedhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.balancedhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class BHTAPI {

    public static final Function<EntityLivingBase, Function<String, HurtSourceInfo>> HURT_SOURCE_INFO_FUNCTION = e -> s -> new HurtSourceInfo(s, false, e.maxHurtResistantTime);
    public static final Function<HurtSourceInfo, Function<String, HurtSourceData>> HURT_SOURCE_DATA_FUNCTION = i -> s -> new HurtSourceData(i);
    public static final Map<String, HurtSourceInfo> HURT_SOURCE_INFO_MAP = new HashMap<>();

    public static void add(HurtSourceInfo info) {
        BHTAPI.HURT_SOURCE_INFO_MAP.put(info.sourceName, info);
    }

    public static HurtSourceData get(EntityLivingBase entity, DamageSource source) {
        HurtSourceInfo info = BHTAPI.HURT_SOURCE_INFO_MAP.computeIfAbsent(source.getDamageType(), HURT_SOURCE_INFO_FUNCTION.apply(entity));
        return Capabilities.hurt(entity).map(c ->
                c.hurtMap.computeIfAbsent(source.getDamageType(), HURT_SOURCE_DATA_FUNCTION.apply(info))
        ).orElseThrow(NotImplementedException::new);
    }
}
