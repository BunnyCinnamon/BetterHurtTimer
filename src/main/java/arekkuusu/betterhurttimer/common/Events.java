package arekkuusu.betterhurttimer.common;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceInfo.HurtSourceData;
import arekkuusu.betterhurttimer.api.event.PreLivingAttackEvent;
import arekkuusu.betterhurttimer.api.event.PreLivingKnockBackEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID)
public class Events {

    public static boolean onAttackEntityOverride = true;
    public static int maxHurtResistantTime = 20;
    public static boolean onAttackPreFinished = false;

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
                        try {
                            data.apply(event.getEntity());
                        } catch (Exception e) {
                            Events.onAttackEntityOverride = true;
                            throw e;
                        }
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onNonLivingEntityUpdate(TickEvent.WorldTickEvent event) {
        if (event.world.isClientSide()) return;
        for (Entity entity : ((ServerLevel) event.world).getEntities().getAll()) {
            if(!(entity instanceof LivingEntity) && BHTAPI.isCustom(entity)) {
                Capabilities.hurt(entity).ifPresent(capability -> {
                    //Source Damage i-Frames
                    if (!capability.hurtMap.isEmpty()) {
                        capability.hurtMap.forEach((s, data) -> {
                            ++data.lastHurtTick;
                            if (data.tick > 0) {
                                --data.tick;
                            }
                            if (data.info.doFrames && data.tick == 0 && !data.canApply) {
                                Events.onAttackEntityOverride = false;
                                try {
                                    data.apply(entity);
                                } catch (Exception e) {
                                    Events.onAttackEntityOverride = true;
                                    throw e;
                                }
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
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntityFromPre(PreLivingAttackEvent event) {
        if(Events.onAttackPreFinished) return;
        if (isClientWorld(event.getEntityLiving())) return;
        if (!Events.onAttackEntityOverride) return;
        DamageSource source = event.getSource();
        if (Events.isAttack(source) && !(source instanceof IndirectEntityDamageSource)) return;

        LivingEntity entity = event.getEntityLiving();
        LazyOptional<HurtSourceData> optional = BHTAPI.get(entity, source);
        if (!optional.isPresent()) return;
        HurtSourceData data = optional.orElseThrow(UnsupportedOperationException::new);
        data.damageSource = source;
        if (data.tick == 0 && data.canApply) {
            data.trigger();
        }

        if (data.info.doFrames) {
            if (data.lastHurtTick < data.info.waitTime) {
                data.amount += event.getAmount();
                event.setCanceled(true);
            }
        } else if (data.tick != 0) {
            float lastAmount = event.getAmount();
            if (data.lastHurtTick < data.info.waitTime) {
                if (Double.compare(Math.max(0, data.lastHurtAmount + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), event.getAmount()) < 0) {
                    if (BHTConfig.Runtime.DamageFrames.nextAttackDamageDifferenceApply) {
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
        Events.onAttackPreFinished = true;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void onEntityAttackPreFinished(PreLivingAttackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        if (!Events.onAttackEntityOverride) return;
        DamageSource source = event.getSource();
        if (Events.isAttack(source) && !(source instanceof IndirectEntityDamageSource)) return;

        Events.onAttackPreFinished = false;
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (isClientWorld(event.getEntity())) return;
        if(!event.getEntity().level.isClientSide() && event.getEntity() instanceof FakePlayer) return;
        Capabilities.hurt(event.getPlayer()).ifPresent(capability -> {
            final AttackInfo attackInfo = capability.meleeMap.computeIfAbsent(event.getTarget(), BHTAPI.INFO_FUNCTION);
            Entity target = event.getTarget();
            Entity attacker = event.getPlayer();
            int ticksSinceLastHurt = Events.getHurtTime(target, attacker);
            int ticksSinceLastMelee = event.getPlayer().attackStrengthTicker;
            if (ticksSinceLastMelee > ticksSinceLastHurt) {
                attackInfo.ticksSinceLastMelee = ticksSinceLastMelee;
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityAttack(LivingAttackEvent event) {
        if (isClientWorld(event.getEntity())) return;
        DamageSource source = event.getSource();
        if (event.getAmount() <= 0) return;
        if (!Events.isAttack(source) && !BHTAPI.isCustom(source.getDirectEntity())) return;
        if (source instanceof IndirectEntityDamageSource && !BHTAPI.isCustom(source.getDirectEntity())) return;
        if (!(source.getDirectEntity() instanceof LivingEntity) && !BHTAPI.isCustom(source.getDirectEntity())) return;

        Entity target = event.getEntity();
        Entity attacker = source.getDirectEntity();
        Capabilities.hurt(attacker).ifPresent(capability -> {

            //Calculate last hurt time required
            final AttackInfo attackInfo = capability.meleeMap.computeIfAbsent(target, BHTAPI.INFO_FUNCTION);
            int ticksSinceLastHurt = Events.getHurtTime(target, attacker);
            int ticksSinceLastMelee = attackInfo.ticksSinceLastMelee;
            if (ticksSinceLastMelee < ticksSinceLastHurt) {
                // What needs to be done to fix other peoples shit.
                if (attackInfo.ticksSinceLastMelee == 0 && (!(attacker instanceof Player) || ((Player) attacker).getAttackStrengthScale(0) == 0)) {
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
        ItemStack stack = entity.getItemInHand(InteractionHand.MAIN_HAND);
        Item item = stack.getItem();
        return entity.attackStrengthTicker >= 0 && item.getAttributeModifiers(
                EquipmentSlot.MAINHAND,
                stack
        ).containsKey(Attributes.ATTACK_SPEED);
    }

    public static double getCoolPeriod(LivingEntity entity) {
        return (1D / entity.getAttribute(Attributes.ATTACK_SPEED).getValue() * Events.maxHurtResistantTime);
    }

    public static double getHurtResistantTime(Entity entity) {
        return entity instanceof LivingEntity ?
                ((LivingEntity) entity).invulnerableDuration
                : Events.maxHurtResistantTime;
    }

    public static double getAttackSpeed(Entity entity) {
        double attackSpeed = Attributes.ATTACK_SPEED.getDefaultValue();
        AttributeInstance attribute = null;
        if (entity instanceof LivingEntity) {
            attribute = ((LivingEntity) entity).getAttribute(Attributes.ATTACK_SPEED);
        }
        if (attribute != null) {
            attackSpeed = attribute.getValue();
        }
        return 1.2D - (1.2D / (1.2D / (attackSpeed * 1.2) * 20D));
    }

    public static double getThreshold(Entity entity) {
        ResourceLocation location = EntityType.getKey(entity.getType());
        double threshold = BHTConfig.Runtime.AttackFrames.attackThresholdDefault;
        if (entity instanceof Player)
            threshold = BHTConfig.Runtime.AttackFrames.attackThresholdPlayer;
        if (BHTAPI.ATTACK_THRESHOLD_MAP.containsKey(location))
            threshold = BHTAPI.ATTACK_THRESHOLD_MAP.get(location);
        return threshold;
    }

    public static boolean isAttack(DamageSource source) {
        return BHTConfig.Runtime.AttackFrames.attackSources.contains(source.getMsgId());
    }

    @SubscribeEvent()
    public static void onKnockback(PreLivingKnockBackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        if (BHTConfig.Runtime.KnockbackFrames.knockbackExemptSource.contains(event.getSource().getMsgId())) {
            event.setCanceled(true);
        }
    }

    public static boolean isClientWorld(Entity entity) {
        return entity.getLevel().isClientSide();
    }
}
