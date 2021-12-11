package arekkuusu.betterhurttimer.common.proxy;

import net.minecraft.entity.EntityLivingBase;

public interface IProxy {

     default void setPreHurtTime(EntityLivingBase entity) {
         //Nothing here
     }
}
