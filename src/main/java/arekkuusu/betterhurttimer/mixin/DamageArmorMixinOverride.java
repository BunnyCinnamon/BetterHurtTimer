package arekkuusu.betterhurttimer.mixin;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class DamageArmorMixinOverride {

    boolean executing;

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    @Final //Take that!
    public void onArmorReduction(DamageSource source, float damage, CallbackInfoReturnable<Float> info) {
        if (!executing) {
            executing = true;
            DamageSource newSource = new DamageSource(source.typeHolder());
            info.setReturnValue(applyArmorCalculations(newSource, damage));
            info.cancel();
            executing = false;
        }
    }

    @Shadow
    protected abstract float applyArmorCalculations(DamageSource source, float damage);
}
