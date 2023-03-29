package arekkuusu.betterhurttimer.common.proxy;

import net.minecraft.world.entity.LivingEntity;

public interface IProxy {

    default void setPreHurtTime(LivingEntity entity) {
        //Nothing here
    }
}
