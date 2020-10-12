package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.HurtCapability;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class DamageArmorMixin {

    //Forge Compliant
    @Redirect(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V", value = "INVOKE"), require = 0)
    public void damageShield(LivingEntity entity, float damage) {
        LazyOptional<HurtCapability> optional = Capabilities.hurt(entity);
        if (optional.isPresent()) {
            HurtCapability capability = optional.orElseThrow(UnsupportedOperationException::new);
            if (capability.ticksToShieldDamage > 0) {
                if (Double.compare(Math.max(0, capability.lastShieldDamage + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), damage) < 0) {
                    damageShield((float) (damage - capability.lastShieldDamage));
                    capability.lastShieldDamage = damage;
                }
            } else {
                damageShield(damage);
                capability.lastShieldDamage = damage;
                capability.ticksToShieldDamage = BHTConfig.Runtime.DamageFrames.shieldResistantTime;
            }
        } else {
            damageShield(damage);
        }
    }

    @Redirect(method = "applyArmorCalculations(Lnet/minecraft/util/DamageSource;F)F", at = @At(target = "Lnet/minecraft/entity/LivingEntity;func_230294_b_(Lnet/minecraft/util/DamageSource;F)V", value = "INVOKE"), require = 0)
    public void damageArmor(LivingEntity entity, DamageSource source, float damage) {
        LazyOptional<HurtCapability> optional = Capabilities.hurt(entity);
        if (optional.isPresent()) {
            HurtCapability capability = optional.orElseThrow(UnsupportedOperationException::new);
            if (capability.ticksToArmorDamage > 0) {
                if (Double.compare(Math.max(0, capability.lastArmorDamage + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), damage) < 0) {
                    func_230294_b_(source, (float) (damage - capability.lastArmorDamage));
                    capability.lastArmorDamage = damage;
                }
            } else {
                func_230294_b_(source, damage);
                capability.lastArmorDamage = damage;
                capability.ticksToArmorDamage = BHTConfig.Runtime.DamageFrames.armorResistantTime;
            }
        } else {
            func_230294_b_(source, damage);
        }
    }
    //Forge Compliant

    //Bukkit Compliant
    @Redirect(method = "damageEntity_CB(Lnet/minecraft/util/DamageSource;F)Z", at = @At(target = "Lnet/minecraft/entity/LivingEntity;damageShield(F)V", value = "INVOKE"), require = 0)
    public void damageShieldS(LivingEntity entity, float damage) {
        LazyOptional<HurtCapability> optional = Capabilities.hurt(entity);
        if (optional.isPresent()) {
            HurtCapability capability = optional.orElseThrow(UnsupportedOperationException::new);
            if (capability.ticksToShieldDamage > 0) {
                if (Double.compare(Math.max(0, capability.lastShieldDamage + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), damage) < 0) {
                    damageShield((float) (damage - capability.lastShieldDamage));
                    capability.lastShieldDamage = damage;
                }
            } else {
                damageShield(damage);
                capability.lastShieldDamage = damage;
                capability.ticksToShieldDamage = BHTConfig.Runtime.DamageFrames.shieldResistantTime;
            }
        } else {
            damageShield(damage);
        }
    }

    @Redirect(method = "damageEntity_CB(Lnet/minecraft/util/DamageSource;F)F", at = @At(target = "Lnet/minecraft/entity/LivingEntity;func_230294_b_(Lnet/minecraft/util/DamageSource;F)V", value = "INVOKE"), require = 0)
    public void damageArmorS(LivingEntity entity, DamageSource source, float damage) {
        LazyOptional<HurtCapability> optional = Capabilities.hurt(entity);
        if (optional.isPresent()) {
            HurtCapability capability = optional.orElseThrow(UnsupportedOperationException::new);
            if (capability.ticksToArmorDamage > 0) {
                if (Double.compare(Math.max(0, capability.lastArmorDamage + BHTConfig.Runtime.DamageFrames.nextAttackDamageDifference), damage) < 0) {
                    func_230294_b_(source, (float) (damage - capability.lastArmorDamage));
                    capability.lastArmorDamage = damage;
                }
            } else {
                func_230294_b_(source, damage);
                capability.lastArmorDamage = damage;
                capability.ticksToArmorDamage = BHTConfig.Runtime.DamageFrames.armorResistantTime;
            }
        } else {
            func_230294_b_(source, damage);
        }
    }
    //Bukkit Compliant

    @Shadow
    protected abstract void func_230294_b_(DamageSource p_230294_1_, float p_230294_2_);

    @Shadow
    protected abstract void damageShield(float damage);
}
