package arekkuusu.betterhurttimer.api;

import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class BHTAPI {

    public static final Function<LivingEntity, Function<String, HurtSourceInfo>> HURT_SOURCE_INFO_FUNCTION = e -> s -> new HurtSourceInfo(s, false, e.maxHurtResistantTime);
    public static final Function<HurtSourceInfo, Function<String, HurtSourceData>> HURT_SOURCE_DATA_FUNCTION = i -> s -> new HurtSourceData(i);
    public static final Map<ResourceLocation, Double> ATTACK_THRESHOLD_MAP = new LinkedHashMap<>();
    public static final Map<String, HurtSourceInfo> DAMAGE_SOURCE_INFO_MAP = new LinkedHashMap<>();

    public static void addSource(HurtSourceInfo info) {
        BHTAPI.DAMAGE_SOURCE_INFO_MAP.put(info.sourceName, info);
    }

    public static void addAttacker(ResourceLocation location, double threshold) {
        BHTAPI.ATTACK_THRESHOLD_MAP.put(location, threshold);
    }

    public static LazyOptional<HurtSourceData> get(LivingEntity entity, DamageSource source) {
        HurtSourceInfo info = BHTAPI.DAMAGE_SOURCE_INFO_MAP.computeIfAbsent(source.getDamageType(), HURT_SOURCE_INFO_FUNCTION.apply(entity));
        return Capabilities.hurt(entity).map(c ->
                c.hurtMap.computeIfAbsent(source.getDamageType(), HURT_SOURCE_DATA_FUNCTION.apply(info))
        );
    }
}
