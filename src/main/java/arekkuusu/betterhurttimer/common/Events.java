package arekkuusu.betterhurttimer.common;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.BHTAPI;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.data.AttackInfo;
import arekkuusu.betterhurttimer.api.capability.data.HurtSourceData;
import arekkuusu.betterhurttimer.api.event.PreLivingAttackEvent;
import arekkuusu.betterhurttimer.api.event.PreLivingKnockBackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;
import java.util.Optional;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID)
public class Events {

    public static int maxHurtResistantTime = 20;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onNonLivingEntityUpdate(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote) return;

        for (Entity entity : event.world.loadedEntityList) {
            Capabilities.hurt(entity).ifPresent(capability -> {
                //Source Damage i-Frames
                if (!capability.hurtMap.isEmpty()) {
                    capability.hurtMap.forEach((s, data) -> {
                        data.update();
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

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackEntityFromPre(PreLivingAttackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;

        DamageSource source = event.getSource();
        EntityLivingBase entity = event.getEntityLiving();
        Optional<HurtSourceData> optional = BHTAPI.get(entity, source);

        if (Events.isAttack(source) && !(source instanceof EntityDamageSourceIndirect)) return;
        if (!optional.isPresent()) return;

        HurtSourceData data = optional.orElseThrow(UnsupportedOperationException::new);

        if (!data.canApply()) {
            float lastAmount = event.getAmount();
            if (Double.compare(data.lastHurtAmount + BHTConfig.CONFIG.damageFrames.nextAttackDamageDifference, event.getAmount()) > 0) {
                if (BHTConfig.CONFIG.damageFrames.nextAttackDamageDifferenceApply) {
                    event.setAmount(lastAmount - Math.max(0, (float) data.lastHurtAmount));
                }
                data.lastHurtAmount = lastAmount;
            } else {
                event.setCanceled(true);
            }
        } else {
            data.trigger();
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        if (isClientWorld(event.getEntity())) return;
        if (!event.getEntity().world.isRemote && event.getEntity() instanceof FakePlayer) return;
        Capabilities.hurt(event.getEntityPlayer()).ifPresent(capability -> {
            final AttackInfo attackInfo = capability.meleeMap.computeIfAbsent(event.getTarget(), BHTAPI.INFO_FUNCTION);
            Entity target = event.getTarget();
            Entity attacker = event.getEntityPlayer();
            int ticksSinceLastHurt = Events.getHurtTime(target, attacker);
            try {
                int ticksSinceLastMelee = BHTAPI.field.getInt(event.getEntityPlayer());
                if (ticksSinceLastMelee > ticksSinceLastHurt) {
                    attackInfo.ticksSinceLastMelee = ticksSinceLastMelee;
                }
            } catch (Exception ignored) {
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityAttack(LivingAttackEvent event) {
        if (isClientWorld(event.getEntity())) return;
        if (event.getAmount() <= 0) return;

        DamageSource source = event.getSource();

        if (!Events.isAttack(source) && !BHTAPI.isCustom(source.getImmediateSource())) return;
        if (source instanceof EntityDamageSourceIndirect && !BHTAPI.isCustom(source.getImmediateSource())) return;
        if (!(source.getImmediateSource() instanceof EntityLivingBase) && !BHTAPI.isCustom(source.getImmediateSource())) return;

        Entity target = event.getEntity();
        Entity attacker = source.getImmediateSource();
        Capabilities.hurt(attacker).ifPresent(capability -> {

            //Calculate last hurt time required
            final AttackInfo attackInfo = capability.meleeMap.computeIfAbsent(target, BHTAPI.INFO_FUNCTION);
            int ticksSinceLastHurt = Events.getHurtTime(target, attacker);
            int ticksSinceLastMelee = attackInfo.ticksSinceLastMelee;
            if (ticksSinceLastMelee < ticksSinceLastHurt) {
                // What needs to be done to fix other peoples shit.
                if (attackInfo.ticksSinceLastMelee == 0 && (!(attacker instanceof EntityPlayer) || ((EntityPlayer) attacker).getCooledAttackStrength(0) == 0)) {
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

        if (attacker instanceof EntityLivingBase && Events.canSwing((EntityLivingBase) attacker)) {
            return (int) (Events.getCoolPeriod((EntityLivingBase) attacker) * threshold);
        } else {
            double maxHurtResistantTime = Events.getHurtResistantTime(target);
            double attackerAttackSpeed = Events.getAttackSpeed(attacker);
            return (int) (maxHurtResistantTime * (attackerAttackSpeed * threshold));
        }
    }

    public static boolean canSwing(EntityLivingBase entity) {
        ItemStack stack = entity.getHeldItem(EnumHand.MAIN_HAND);
        Item item = stack.getItem();
        boolean canSwing = false;
        try {
            canSwing = BHTAPI.field.getInt(entity) >= 0 && item.getAttributeModifiers(
                    EntityEquipmentSlot.MAINHAND,
                    stack
            ).containsKey(SharedMonsterAttributes.ATTACK_SPEED.getName());
        } catch(Exception ignored) {
        }
        return canSwing;
    }

    public static double getCoolPeriod(EntityLivingBase entity) {
        return (1D / entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getAttributeValue() * Events.maxHurtResistantTime);
    }

    public static double getHurtResistantTime(Entity entity) {
        return entity instanceof EntityLivingBase ?
                ((EntityLivingBase) entity).maxHurtResistantTime
                : Events.maxHurtResistantTime;
    }

    public static double getAttackSpeed(Entity entity) {
        double attackSpeed = SharedMonsterAttributes.ATTACK_SPEED.getDefaultValue();
        IAttributeInstance attribute = null;
        if (entity instanceof EntityLivingBase) {
            attribute = ((EntityLivingBase) entity).getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        }
        if (attribute != null) {
            attackSpeed = attribute.getAttributeValue();
        }
        return 1.2D - (1.2D / (1.2D / (attackSpeed * 1.2) * 20D));
    }

    public static double getThreshold(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            ResourceLocation itemLocation = ((EntityLivingBase) entity).getHeldItemMainhand().getItem().getRegistryName();
            if (BHTAPI.ATTACK_ITEM_THRESHOLD_MAP.containsKey(itemLocation)) {
                return BHTAPI.ATTACK_ITEM_THRESHOLD_MAP.get(itemLocation);
            }
        }
        ResourceLocation location = EntityList.getKey(entity.getClass());
        double threshold = BHTConfig.CONFIG.attackFrames.attackThresholdDefault;
        if (entity instanceof EntityPlayer)
            threshold = BHTConfig.CONFIG.attackFrames.attackThresholdPlayer;
        if (location != null && BHTAPI.ATTACK_THRESHOLD_MAP.containsKey(location))
            threshold = BHTAPI.ATTACK_THRESHOLD_MAP.get(location);
        return threshold;
    }

    public static boolean isAttack(DamageSource source) {
        return Arrays.asList(BHTConfig.CONFIG.attackFrames.attackSources).contains(source.getDamageType());
    }

    @SubscribeEvent()
    public static void onKnockback(PreLivingKnockBackEvent event) {
        if (isClientWorld(event.getEntityLiving())) return;
        if (Arrays.asList(BHTConfig.CONFIG.knockbackFrames.knockbackExemptSource).contains(event.getSource().getDamageType())) {
            event.setCanceled(true);
        }
    }

    public static boolean isClientWorld(Entity entity) {
        return entity.getEntityWorld().isRemote;
    }
}
