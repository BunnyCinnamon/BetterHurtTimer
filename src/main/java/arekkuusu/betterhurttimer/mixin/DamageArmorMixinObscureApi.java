package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.HurtCapability;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(LivingEntity.class)
public abstract class DamageArmorMixinObscureApi {

    @Shadow
    public abstract boolean addEffect(MobEffectInstance arg);

    //Forge Compliant
    @Redirect(method = "hurt", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V", value = "INVOKE"), require = 0)
    public void damageShield(LivingEntity entity, float damage) {
        LazyOptional<HurtCapability> optional = Capabilities.hurt(entity);
        if (optional.isPresent()) {
            HurtCapability capability = optional.orElseThrow(UnsupportedOperationException::new);
            if (capability.ticksToShieldDamage > 0) {
                if (Double.compare(Math.max(0, capability.lastShieldDamage + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), damage) < 0) {
                    hurtCurrentlyUsedShield((float) (damage - capability.lastShieldDamage));
                    capability.lastShieldDamage = damage;
                }
            } else {
                hurtCurrentlyUsedShield(damage);
                capability.lastShieldDamage = damage;
                capability.ticksToShieldDamage = BHTConfig.Runtime.DamageFrames.shieldResistantTime;
            }
        } else {
            hurtCurrentlyUsedShield(damage);
        }
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true, require = 0)
    public void damageArmor(DamageSource source, float damage, CallbackInfoReturnable<Float> info) {
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            LazyOptional<HurtCapability> optional = Capabilities.hurt((LivingEntity) (Object) this);
            if (optional.isPresent()) {
                HurtCapability capability = optional.orElseThrow(UnsupportedOperationException::new);
                if (capability.ticksToArmorDamage > 0) {
                    if (Double.compare(Math.max(0, capability.lastArmorDamage + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), damage) < 0) {
                        hurtArmor(source, (float) (damage - capability.lastArmorDamage));
                        capability.lastArmorDamage = damage;
                    }
                } else {
                    hurtArmor(source, damage);
                    capability.lastArmorDamage = damage;
                    capability.ticksToArmorDamage = BHTConfig.Runtime.DamageFrames.armorResistantTime;
                }
            } else {
                hurtArmor(source, damage);
            }
            float penetration = 0F;
            if(source.getEntity() instanceof LivingEntity) {
                try {
                    Class<?> obscureAPIAttributes = Class.forName("com.obscuria.obscureapi.registry.ObscureAPIAttributes");
                    Method getPenetration = obscureAPIAttributes.getMethod("getPenetration", LivingEntity.class);
                    penetration = 1F - (float) getPenetration.invoke(null, (LivingEntity) source.getEntity());
                } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            float amount = CombatRules.getDamageAfterAbsorb(damage, (float) this.getArmorValue() * penetration, (float) this.getAttributeValue(Attributes.ARMOR_TOUGHNESS) * penetration);
            info.setReturnValue(amount);
            info.cancel();
        }
    }
    //Forge Compliant

    @Shadow
    protected abstract int getArmorValue();

    @Shadow
    protected abstract double getAttributeValue(Attribute attribute);

    @Shadow
    protected abstract void hurtArmor(DamageSource arg, float f);

    @Shadow
    protected abstract void hurtCurrentlyUsedShield(float f);
}
