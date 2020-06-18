package arekkuusu.balancedhurttimer.api.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class PreLivingKnockBackEvent extends Event {

    private final EntityLivingBase entityLiving;
    private final DamageSource source;

    public PreLivingKnockBackEvent(EntityLivingBase entityLiving, DamageSource source) {
        this.entityLiving = entityLiving;
        this.source = source;
    }

    public EntityLivingBase getEntityLiving() {
        return entityLiving;
    }

    public DamageSource getSource() {
        return source;
    }
}
