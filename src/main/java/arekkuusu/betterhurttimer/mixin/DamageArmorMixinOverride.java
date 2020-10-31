package arekkuusu.betterhurttimer.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class DamageArmorMixinOverride {

    boolean executing;

    @Inject(method = "applyArmorCalculations(Lnet/minecraft/util/DamageSource;F)F", at = @At("HEAD"), cancellable = true)
    @Final //Take that!
    public void onArmorReduction(DamageSource source, float damage, CallbackInfoReturnable<Float> info) {
        if (!executing) {
            executing = true;
            DamageSource newSource = new DamageSource(source.getDamageType());
            if (source.isUnblockable()) {
                newSource.setDamageBypassesArmor();
            }
            info.setReturnValue(applyArmorCalculations(newSource, damage));
            info.cancel();
            executing = false;
        }
    }

    @Shadow
    protected abstract float applyArmorCalculations(DamageSource source, float damage);
}
