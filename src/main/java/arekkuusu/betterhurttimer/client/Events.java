package arekkuusu.betterhurttimer.client;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import arekkuusu.betterhurttimer.api.capability.Capabilities;
import arekkuusu.betterhurttimer.client.render.effect.DamageParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BHT.MOD_ID, value = Dist.CLIENT)
public class Events {

    @SubscribeEvent
    public static void displayDamage(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!entity.world.isRemote || !BHTConfig.Runtime.Rendering.showDamageParticles) return;

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

        ClientWorld world = (ClientWorld) entity.world;
        double motionX = world.rand.nextGaussian() * 0.02;
        double motionY = 0.5f;
        double motionZ = world.rand.nextGaussian() * 0.02;
        Particle damageIndicator = new DamageParticle(damage, world, entity.getPosX(), entity.getPosY() + entity.getHeight(), entity.getPosZ(), motionX, motionY, motionZ);
        Minecraft.getInstance().particles.addEffect(damageIndicator);
    }
}
