package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.api.capability.HurtCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ISpecialArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ISpecialArmor.ArmorProperties.class)
public abstract class DamageSpecialArmorMixin {

    private static double damageAlt;

    @Inject(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Ljava/util/ArrayList;<init>()V", value = "INVOKE", shift = At.Shift.BEFORE), remap = false)
    private static void applyArmorPre(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> info) {
        HurtCapability capability = Capabilities.hurt(entity).orElse(null);
        if (capability != null) {
            if (capability.ticksToArmorDamage > 0) {
                if (Double.compare(Math.max(0, capability.lastArmorDamage + BHTConfig.CONFIG.damageFrames.nextAttackDamageDifference), damage) < 0) {
                    damageAlt = damage - capability.lastArmorDamage;
                } else {
                    damageAlt = damage;
                }
                capability.lastArmorDamage = damage;
            } else {
                damageAlt = damage;
                capability.lastArmorDamage = damage;
            }
        } else {
            damageAlt = damage;
        }
    }

    @ModifyVariable(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Ljava/util/ArrayList;<init>()V", value = "INVOKE", shift = At.Shift.AFTER), remap = false, argsOnly = true)
    private static double applyArmorPost(double damage) {
        return damageAlt;
    }
}
