package arekkuusu.betterhurttimer.mixin;

import arekkuusu.betterhurttimer.api.event.PreLivingKnockBackEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityLivingBase.class)
public abstract class KnockbackMixin {

    @Redirect(method = "attackEntityFrom(Lnet/minecraft/util/DamageSource;F)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;knockBack(Lnet/minecraft/entity/Entity;FDD)V"))
    public void knockBack(EntityLivingBase that, Entity entityIn, float strength, double xRatio, double zRatio, DamageSource source, float amount) {
        if(!MinecraftForge.EVENT_BUS.post(new PreLivingKnockBackEvent(that, source))) {
           this.knockBack(entityIn, strength, xRatio, zRatio);
        }
    }

    @Shadow
    public abstract void knockBack(Entity entityIn, float strength, double xRatio, double zRatio);
}
