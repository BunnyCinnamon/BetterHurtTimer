package arekkuusu.betterhurttimer.api;

import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public final class BHTAPI {

    public static final Function<EntityLivingBase, Function<CharSequence, HurtSourceInfo>> HURT_SOURCE_INFO_FUNCTION = e -> s -> new HurtSourceInfo(s, false, e.maxHurtResistantTime);
    public static final Function<HurtSourceInfo, Function<CharSequence, HurtSourceData>> HURT_SOURCE_DATA_FUNCTION = i -> s -> new HurtSourceData(i);
    public static final Object2ObjectMap<CharSequence, HurtSourceInfo> DAMAGE_SOURCE_INFO_MAP = new Object2ObjectArrayMap<>();
    public static final Map<ResourceLocation, Double> ATTACK_THRESHOLD_MAP = new LinkedHashMap<>();
    public static final Map<ResourceLocation, Double> ATTACK_ITEM_THRESHOLD_MAP = new LinkedHashMap<>();
    public static final Function<Entity, AttackInfo> INFO_FUNCTION = u -> new AttackInfo();

    public static void addSource(HurtSourceInfo info) {
        BHTAPI.DAMAGE_SOURCE_INFO_MAP.put(new HurtSourceInfo.HurtType(info.sourceName), info);
    }

    public static void addAttacker(ResourceLocation location, double threshold) {
        BHTAPI.ATTACK_THRESHOLD_MAP.put(location, threshold);
    }

    public static void addItem(ResourceLocation location, double threshold) {
        BHTAPI.ATTACK_ITEM_THRESHOLD_MAP.put(location, threshold);
    }

    public static HurtSourceData get(EntityLivingBase entity, DamageSource source) {
        HurtSourceInfo info = BHTAPI.DAMAGE_SOURCE_INFO_MAP.computeIfAbsent(source.getDamageType(), BHTAPI.HURT_SOURCE_INFO_FUNCTION.apply(entity));
        return Capabilities.hurt(entity).map(c ->
                c.hurtMap.computeIfAbsent(info.sourceName, BHTAPI.HURT_SOURCE_DATA_FUNCTION.apply(info))
        ).orElseThrow(UnsupportedOperationException::new);
    }
}
