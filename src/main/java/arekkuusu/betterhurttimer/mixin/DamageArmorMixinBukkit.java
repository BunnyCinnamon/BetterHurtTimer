package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.HurtCapability;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class DamageArmorMixinBukkit {

    //Bukkit Compliant
    @Redirect(method = "damageEntity_CB(Lnet/minecraft/util/DamageSource;F)Z", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;hurtCurrentlyUsedShield(F)V", value = "INVOKE"), require = 0)
    public void damageShieldS(LivingEntity entity, float damage) {
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

    @Redirect(method = "damageEntity_CB(Lnet/minecraft/util/DamageSource;F)F", at = @At(target = "Lnet/minecraft/world/entity/LivingEntity;hurtArmor(Lnet/minecraft/world/damagesource/DamageSource;F)V", value = "INVOKE"), require = 0)
    public void damageArmorS(LivingEntity entity, DamageSource source, float damage) {
        LazyOptional<HurtCapability> optional = Capabilities.hurt(entity);
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
    }
    //Bukkit Compliant

    @Shadow
    protected abstract void hurtArmor(DamageSource arg, float f);

    @Shadow
    protected abstract void hurtCurrentlyUsedShield(float f);
}
