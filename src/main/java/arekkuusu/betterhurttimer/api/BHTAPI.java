package arekkuusu.betterhurttimer.api;

import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class BHTAPI {

    public static final Function<LivingEntity, Function<CharSequence, HurtSourceInfo>> HURT_SOURCE_INFO_FUNCTION = e -> s -> new HurtSourceInfo(s, false, e.maxHurtResistantTime);
    public static final Function<HurtSourceInfo, Function<CharSequence, HurtSourceData>> HURT_SOURCE_DATA_FUNCTION = i -> s -> new HurtSourceData(i);
    public static final Map<CharSequence, HurtSourceInfo> DAMAGE_SOURCE_INFO_MAP = new LinkedHashMap<>();
    public static final Map<ResourceLocation, Double> ATTACK_THRESHOLD_MAP = new LinkedHashMap<>();
    public static final Function<Entity, AttackInfo> INFO_FUNCTION = u -> new AttackInfo();

    public static synchronized void addSource(HurtSourceInfo info) {
        BHTAPI.DAMAGE_SOURCE_INFO_MAP.put(new HurtSourceInfo.HurtType(info.sourceName), info);
    }

    public static synchronized void addAttacker(ResourceLocation location, double threshold) {
        BHTAPI.ATTACK_THRESHOLD_MAP.put(location, threshold);
    }

    public static LazyOptional<HurtSourceData> get(LivingEntity entity, DamageSource source) {
        HurtSourceInfo info = BHTAPI.DAMAGE_SOURCE_INFO_MAP.computeIfAbsent(source.getDamageType(), BHTAPI.HURT_SOURCE_INFO_FUNCTION.apply(entity));
        return Capabilities.hurt(entity).lazyMap(c ->
                c.hurtMap.computeIfAbsent(info.sourceName, BHTAPI.HURT_SOURCE_DATA_FUNCTION.apply(info))
        );
    }
}
