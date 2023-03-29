package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.api.event.PreLivingKnockBackEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class KnockbackMixin {

    @Redirect(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;knockback(DDD)V"))
    public void knockBack(LivingEntity that, double strength, double xRatio, double zRatio, DamageSource source, float amount) {
        if(!MinecraftForge.EVENT_BUS.post(new PreLivingKnockBackEvent(that, source))) {
           this.knockback(strength, xRatio, zRatio);
        }
    }

    @Shadow
    public abstract void knockback(double d, double e, double f);
}
