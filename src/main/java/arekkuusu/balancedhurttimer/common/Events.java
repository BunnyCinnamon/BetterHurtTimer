package arekkuusu.balancedhurttimer.common;

import arekkuusu.balancedhurttimer.BHT;
import arekkuusu.balancedhurttimer.api.BHTAPI;
import arekkuusu.balancedhurttimer.api.capability.Capabilities;
import arekkuusu.balancedhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import arekkuusu.balancedhurttimer.api.event.PreLivingAttackEvent;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID)
public class Events {

    public static boolean onAttackEntityOverride = true;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntityFromPre(PreLivingAttackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        if (!Events.onAttackEntityOverride) return;
        DamageSource source = event.getSource();
        if (source.getDamageType().matches("player|mob")) return; //If my source is melee, return

        EntityLivingBase entity = event.getEntityLiving();
        HurtSourceData data = BHTAPI.get(entity, source);
        //if (data.damageSource == null)
        data.damageSource = source; //Last source to do the damage gets the kill
        if (data.tick == 0 && data.canApply)
            data.trigger();

        if (data.info.doFrames) {
            data.accumulate(event.getAmount());
            event.setCanceled(true);
        } else if (data.tick != 0) {
            event.setCanceled(true);
        } else {
            data.canApply = true;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        Capabilities.hurt(event.getEntityLiving()).ifPresent(capability -> {
            capability.hurtMap.forEach((s, data) -> {
                if (data.tick > 0) {
                    --data.tick;
                }
                if (data.info.doFrames && data.tick == 0 && !data.canApply) {
                    Events.onAttackEntityOverride = false;
                    data.apply(event.getEntityLiving());
                    Events.onAttackEntityOverride = true;
                }
            });
            ++capability.ticksSinceLastMelee;
        });
        if (event.getEntityLiving().hurtResistantTime > 0) {
            event.getEntityLiving().hurtResistantTime = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntityAttack(LivingAttackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        DamageSource source = event.getSource();
        if (!(source.getImmediateSource() instanceof EntityLivingBase) || event.getAmount() <= 0) return;
        if(!source.getDamageType().matches("player|mob")) return;
        EntityLivingBase target = event.getEntityLiving();
        EntityLivingBase attacker = (EntityLivingBase) source.getImmediateSource();
        Capabilities.hurt(attacker).ifPresent(capability -> {
            IAttributeInstance attribute = attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
            double attackSpeed = SharedMonsterAttributes.ATTACK_SPEED.getDefaultValue();
            //noinspection ConstantConditions
            if (attribute != null) attackSpeed = attribute.getAttributeValue();
            double attackerAttackSpeed = 1 - (1 / (1D / attackSpeed * 20D));
            int ticksSinceLastHurt = (int) ((float) target.maxHurtResistantTime * attackerAttackSpeed / 2);
            if (capability.ticksSinceLastMelee <= ticksSinceLastHurt) {
                event.setCanceled(true);
            } else {
                capability.ticksSinceLastMelee = 0;
            }
        });
    }

    public static boolean isClientWorld(EntityLivingBase entity) {
        return entity.getEntityWorld().isRemote;
    }
}
