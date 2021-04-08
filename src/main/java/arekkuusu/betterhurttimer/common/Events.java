package arekkuusu.betterhurttimer.common;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import arekkuusu.betterhurttimer.api.event.PreLivingAttackEvent;
import arekkuusu.betterhurttimer.api.event.PreLivingKnockBackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Function;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID)
public class Events {

    public static boolean onAttackEntityOverride = true;
    public static int maxHurtResistantTime = 20;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (isClientWorld(event.getEntity())) return;
        Capabilities.hurt(event.getEntity()).ifPresent(capability -> {
            //Source Damage i-Frames
            if (!capability.hurtMap.isEmpty()) {
                capability.hurtMap.forEach((s, data) -> {
                    ++data.lastHurtTick;
                    if (data.tick > 0) {
                        --data.tick;
                    }
                    if (data.info.doFrames && data.tick == 0 && !data.canApply) {
                        Events.onAttackEntityOverride = false;
                        data.apply(event.getEntity());
                        Events.onAttackEntityOverride = true;
                    }
                });
            }
            //Melee i-Frames
            if (!capability.meleeMap.isEmpty()) {
                capability.meleeMap.forEach((e, a) -> a.ticksSinceLastMelee++);
            }
            //Armor i-Frames
            if (capability.ticksToArmorDamage > 0) {
                --capability.ticksToArmorDamage;
            } else {
                capability.lastArmorDamage = 0;
            }
            //Shield i-Frames
            if (capability.ticksToShieldDamage > 0) {
                --capability.ticksToShieldDamage;
            } else {
                capability.lastShieldDamage = 0;
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntityFromPre(PreLivingAttackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        if (!Events.onAttackEntityOverride) return;
        DamageSource source = event.getSource();
        if (Events.isAttack(source) && !(source instanceof IndirectEntityDamageSource)) return;

        LivingEntity entity = event.getEntityLiving();
        LazyOptional<HurtSourceData> optional = BHTAPI.get(entity, source);
        if(!optional.isPresent()) return;
        HurtSourceData data = optional.orElseThrow(UnsupportedOperationException::new);
        data.damageSource = source; //Last source to do the damage gets the kill
        if (data.tick == 0 && data.canApply) {
            data.trigger();
        }

        if (data.info.doFrames) {
            if (data.lastHurtTick < data.info.waitTime) {
                data.accumulate(event.getAmount());
                event.setCanceled(true);
            }
        } else if (data.tick != 0) {
            float lastAmount = event.getAmount();
            if (data.lastHurtTick < data.info.waitTime) {
                if (Double.compare(Math.max(0, data.lastHurtAmount + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), event.getAmount()) < 0) {
                    if(BHTConfig.Runtime.DamageFrames.nextAttackDamageDifferenceApply) {
                        event.setAmount(lastAmount - Math.max(0, data.lastHurtAmount));
                    }
                    data.lastHurtAmount = lastAmount;
                } else {
                    event.setCanceled(true);
                }
            } else {
                data.lastHurtAmount = lastAmount;
            }
        } else {
            data.canApply = true;
        }
        data.lastHurtTick = 0;
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (isClientWorld(event.getEntity())) return;
        Capabilities.hurt(event.getPlayer()).ifPresent(capability -> {
            final AttackInfo attackInfo = capability.meleeMap.computeIfAbsent(event.getTarget(), BHTAPI.INFO_FUNCTION);
            Entity target = event.getTarget();
            Entity attacker = event.getPlayer();
            int ticksSinceLastHurt = Events.getHurtTime(target, attacker);
            int ticksSinceLastMelee = event.getPlayer().ticksSinceLastSwing;
            if(ticksSinceLastMelee > ticksSinceLastHurt) {
                attackInfo.ticksSinceLastMelee = ticksSinceLastMelee;
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityAttack(LivingAttackEvent event) {
        if (isClientWorld(event.getEntity())) return;
        DamageSource source = event.getSource();
        if (!(source.getImmediateSource() instanceof LivingEntity) || event.getAmount() <= 0) return;
        if (!Events.isAttack(source) || (source instanceof IndirectEntityDamageSource)) return;

        Entity target = event.getEntity();
        Entity attacker = source.getImmediateSource();
        Capabilities.hurt(attacker).ifPresent(capability -> {

            //Calculate last hurt time required
            final AttackInfo attackInfo = capability.meleeMap.computeIfAbsent(target, BHTAPI.INFO_FUNCTION);
            int ticksSinceLastHurt = Events.getHurtTime(target, attacker);
            int ticksSinceLastMelee = attackInfo.ticksSinceLastMelee;
            if (ticksSinceLastMelee < ticksSinceLastHurt) {
                // What needs to be done to fix other peoples shit.
                if (attackInfo.ticksSinceLastMelee == 0 && (!(attacker instanceof PlayerEntity) || ((PlayerEntity) attacker).getCooledAttackStrength(0) == 0)) {
                    attackInfo.override = true;
                } else {
                    event.setCanceled(true);
                }
            } else {
                attackInfo.ticksSinceLastMelee = 0;
            }
        });
    }

    public static int getHurtTime(Entity target, Entity attacker) {
        double threshold = Events.getThreshold(attacker);

        if (attacker instanceof LivingEntity && Events.canSwing((LivingEntity) attacker)) {
            return (int) (Events.getCoolPeriod((LivingEntity) attacker) * threshold);
        } else {
            double maxHurtResistantTime = Events.getHurtResistantTime(target);
            double attackerAttackSpeed = Events.getAttackSpeed(attacker);
            return (int) (maxHurtResistantTime * (attackerAttackSpeed * threshold));
        }
    }

    public static boolean canSwing(LivingEntity entity) {
        ItemStack stack = entity.getHeldItem(Hand.MAIN_HAND);
        Item item = stack.getItem();
        return entity.ticksSinceLastSwing >= 0 && item.getAttributeModifiers(
                EquipmentSlotType.MAINHAND,
                stack
        ).containsKey(Attributes.field_233825_h_);
    }

    public static double getCoolPeriod(LivingEntity entity) {
        return (1D / entity.getAttribute(Attributes.field_233825_h_).getValue() * Events.maxHurtResistantTime);
    }

    public static double getHurtResistantTime(Entity entity) {
        return entity instanceof LivingEntity ?
                ((LivingEntity) entity).maxHurtResistantTime
                : Events.maxHurtResistantTime;
    }

    public static double getAttackSpeed(Entity entity) {
        double attackSpeed = Attributes.field_233825_h_.getDefaultValue();
        ModifiableAttributeInstance attribute = null;
        if (entity instanceof LivingEntity) {
            attribute = ((LivingEntity) entity).getAttribute(Attributes.field_233825_h_);
        }
        if (attribute != null) {
            attackSpeed = attribute.getValue();
        }
        return 1.2D - (1.2D / (1.2D / (attackSpeed * 1.2) * 20D));
    }

    public static double getThreshold(Entity entity) {
        ResourceLocation location = EntityType.getKey(entity.getType());
        double threshold = BHTConfig.Runtime.AttackFrames.attackThresholdDefault;
        if (entity instanceof PlayerEntity)
            threshold = BHTConfig.Runtime.AttackFrames.attackThresholdPlayer;
        if (BHTAPI.ATTACK_THRESHOLD_MAP.containsKey(location))
            threshold = BHTAPI.ATTACK_THRESHOLD_MAP.get(location);
        return threshold;
    }

    public static boolean isAttack(DamageSource source) {
        return BHTConfig.Runtime.AttackFrames.attackSources.contains(source.getDamageType());
    }

    @SubscribeEvent()
    public static void onKnockback(PreLivingKnockBackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        if (BHTConfig.Runtime.KnockbackFrames.knockbackExemptSource.contains(event.getSource().getDamageType())) {
            event.setCanceled(true);
        }
    }

    public static boolean isClientWorld(Entity entity) {
        return entity.getEntityWorld().isRemote;
    }
}
