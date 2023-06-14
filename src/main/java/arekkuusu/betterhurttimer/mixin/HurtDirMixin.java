package arekkuusu.betterhurttimer.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class HurtDirMixin extends Entity {

    @Shadow
    public float hurtDir;
    public float preAttackedAtYaw;

    public HurtDirMixin(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void attackEntityFromBefore(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (this.hurtDir > 0) {
            this.preAttackedAtYaw = this.hurtDir;
        } else {
            this.preAttackedAtYaw = 0;
        }
    }

    @Inject(method = "hurt", at = @At("TAIL"))
    public void attackEntityFromAfter(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (this.preAttackedAtYaw > 0) {
            this.hurtDir = this.preAttackedAtYaw;
        }
    }
}
