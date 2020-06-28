package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.event.PreLivingKnockBackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class KnockbackMixin {

    @Redirect(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;knockBack(Lnet/minecraft/entity/Entity;FDD)V"))
    public void knockBack(LivingEntity that, Entity entityIn, float strength, double xRatio, double zRatio, DamageSource source, float amount) {
        if(!MinecraftForge.EVENT_BUS.post(new PreLivingKnockBackEvent(that, source))) {
           this.knockBack(entityIn, strength, xRatio, zRatio);
        }
    }

    @ModifyVariable(method = "knockBack(Lnet/minecraft/entity/Entity;FDD)V", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraftforge/event/entity/living/LivingKnockBackEvent;getStrength()F", shift = At.Shift.AFTER), remap = false)
    public float knockBackScale(float strength) {
        if(BHTConfig.Runtime.KnockbackFrames.knockbackAsAChance) {
            return strength;
        }
        return (float) ((double) strength * (1.0D - getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue()));
    }

    @Redirect(method = "knockBack(Lnet/minecraft/entity/Entity;FDD)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/attributes/IAttributeInstance;getValue()D"))
    public double knockBackCondition(IAttributeInstance instance, Entity entityIn, float strength, double xRatio, double zRatio) {
        if(BHTConfig.Runtime.KnockbackFrames.knockbackAsAChance) {
            return instance.getValue();
        }
        return !(strength <= 0.0F) ? -1 : Integer.MAX_VALUE;
    }

    @Shadow
    public abstract void knockBack(Entity entityIn, float strength, double xRatio, double zRatio);

    @Shadow
    public abstract IAttributeInstance getAttribute(IAttribute attribute);
}
