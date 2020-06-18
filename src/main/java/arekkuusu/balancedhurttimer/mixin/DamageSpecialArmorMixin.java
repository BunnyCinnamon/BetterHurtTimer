package arekkuusu.balancedhurttimer.mixin;

import arekkuusu.balancedhurttimer.api.capability.Capabilities;
import arekkuusu.balancedhurttimer.api.capability.HurtCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ISpecialArmor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nonnull;
import java.util.ArrayList;

@Mixin(ISpecialArmor.ArmorProperties.class)
public abstract class DamageSpecialArmorMixin {

    private static double absorbTemp;
    private static double damageTemp;
    private static double ratioTemp;
    private static double damageAlt;

    @Inject(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Ljava/util/ArrayList;<init>()V", value = "INVOKE", shift = At.Shift.BEFORE), remap = false)
    private static void applyArmorPre(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> info) {
        HurtCapability capability = Capabilities.hurt(entity).orElse(null);
        if (capability != null) {
            if (capability.ticksToArmorDamage > 0) {
                if (capability.lastArmorDamage < damage) {
                    damageAlt = damage - capability.lastArmorDamage;
                    capability.lastArmorDamage = damage;
                }
            } else {
                damageAlt = damage;
            }
        } else {
            damageAlt = damage;
        }
    }

    @Inject(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Lnet/minecraftforge/common/ISpecialArmor$ArmorProperties;Priority:I", value = "FIELD", shift = At.Shift.BEFORE, ordinal = 1), locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
    private static void storeRatio(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> info, double ratio) {
        ratioTemp = ratio;
    }

    @Inject(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Lnet/minecraftforge/common/ISpecialArmor$ArmorProperties;Priority:I", value = "FIELD", shift = At.Shift.AFTER, ordinal = 2), locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
    private static void applyRatio(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> info) {
        damageAlt -= damageAlt * ratioTemp;
    }

    @Inject(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Lnet/minecraftforge/common/ISpecialArmor$ArmorProperties;AbsorbRatio:D", value = "FIELD", ordinal = 0), locals = LocalCapture.CAPTURE_FAILEXCEPTION, remap = false)
    private static void storeAbsorb(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> info, double totalArmor, double totalToughness, ArrayList<ISpecialArmor.ArmorProperties> dmgVals, ISpecialArmor.ArmorProperties props[], int level, double ratio, ISpecialArmor.ArmorProperties var14[], int var15, int var16, ISpecialArmor.ArmorProperties prop) {
        absorbTemp = prop.AbsorbRatio;
    }

    @Redirect(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Lnet/minecraftforge/common/ISpecialArmor;damageArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/DamageSource;II)V", value = "INVOKE", ordinal = 0), remap = false)
    private static void modifyAbsorbArmor(ISpecialArmor armor, EntityLivingBase entity, @Nonnull ItemStack stack, DamageSource source, int damage, int slot) {
        double damageTotal = damageAlt * absorbTemp;
        int dmg = (int) (damageTotal > 0 ? Math.max(1D, damageTotal) : 0D);
        if (dmg > 0) {
            armor.damageArmor(entity, stack, source, dmg, slot);
        }
    }

    @Redirect(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/EntityLivingBase;)V", value = "INVOKE", ordinal = 0))
    private static void modifyAbsorbItem(ItemStack stack, int amount, EntityLivingBase entityIn) {
        double damageTotal = damageAlt * absorbTemp;
        int dmg = (int) (damageTotal > 0 ? Math.max(1D, damageTotal) : 0D);
        if (dmg > 0) {
            stack.damageItem(dmg, entityIn);
        }
    }

    @Inject(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Lnet/minecraftforge/common/ISpecialArmor$ArmorProperties;AbsorbRatio:D", value = "FIELD", shift = At.Shift.BEFORE, ordinal = 1), remap = false)
    private static void storeLeftoverDamage(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> info) {
        damageTemp = damage;
    }

    @Redirect(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Ljava/lang/Math;max(DD)D", value = "INVOKE", ordinal = 1), remap = false)
    private static double modifyLeftoverDamage(double a, double b, EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage) {
        if (damageTemp > 0 && damageTemp > damage) {
            double subtracted = damageTemp - damage;
            double original = damage + subtracted;
            double ratio = subtracted / original;
            damageAlt -= (damageAlt * ratio);
        }
        double damageTotal = damageAlt / 4D;
        return damageTotal > 0 ? Math.max(1D, damageTotal) : 0D;
    }

    @Redirect(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At(target = "Lnet/minecraft/item/ItemStack;damageItem(ILnet/minecraft/entity/EntityLivingBase;)V", value = "INVOKE", ordinal = 1))
    private static void applyLeftoverDamage(ItemStack stack, int amount, EntityLivingBase entityIn) {
        if (amount > 0) {
            stack.damageItem(amount, entityIn);
        }
    }

    @Inject(method = "applyArmor(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/util/NonNullList;Lnet/minecraft/util/DamageSource;D)F", at = @At("TAIL"), remap = false)
    private static void applyArmorPost(EntityLivingBase entity, NonNullList<ItemStack> inventory, DamageSource source, double damage, CallbackInfoReturnable<Float> info) {
        damageAlt = 0;
        ratioTemp = 0;
        damageTemp = 0;
        absorbTemp = 0;
    }
}
