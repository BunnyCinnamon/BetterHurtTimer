package arekkuusu.betterhurttimer.client;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.client.render.effect.DamageParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID, value = Dist.CLIENT)
public class Events {

    @SubscribeEvent
    public static void displayDamage(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.level.isClientSide() || !BHTConfig.Runtime.Rendering.showDamageParticles) return;

        Capabilities.health(entity).ifPresent(cap -> {
        int currentHealth = (int) Math.ceil(entity.getHealth());
            if (cap.health != -1 && cap.health != currentHealth) {
                displayParticle(entity, cap.health - currentHealth);
            }
            cap.health = currentHealth;
        });
    }

    public static void displayParticle(Entity entity, int damage) {
        if (damage == 0) return;

        ClientLevel world = (ClientLevel) entity.level;
        double motionX = world.random.nextGaussian() * 0.02;
        double motionY = 0.5f;
        double motionZ = world.random.nextGaussian() * 0.02;
        Particle damageIndicator = new DamageParticle(damage, world, entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ(), motionX, motionY, motionZ);
        Minecraft.getInstance().particleEngine.add(damageIndicator);
    }
}
