package arekkuusu.betterhurttimer.client.render.effect;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class DamageParticle extends Particle {

    public boolean grow = true;
    public float scale;
    public Component text;
    public int color;

    public DamageParticle(int damage, ClientLevel world, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ) {
        super(world, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
        this.gravity = 0.1F;
        this.scale = 1F;
        this.lifetime = 12;
        this.color = damage > 0 ? BHTConfig.Runtime.Rendering.damageColor : BHTConfig.Runtime.Rendering.healColor;
        this.text = Component.translatable(BHT.MOD_ID + ".particle", Math.abs(damage));
    }

    @Override
    public void render(VertexConsumer arg, Camera renderInfo, float partialTicks) {
        Vec3 vec3d = renderInfo.getPosition();
        float locX = (float)(Mth.lerp((double)partialTicks, this.xo, this.x) - vec3d.x());
        float locY = (float)(Mth.lerp((double)partialTicks, this.yo, this.y) - vec3d.y());
        float locZ = (float)(Mth.lerp((double)partialTicks, this.zo, this.z) - vec3d.z());

        MultiBufferSource.BufferSource bufferIn = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        PoseStack stack = new PoseStack();
        stack.translate(locX, locY, locZ);
        stack.mulPose(renderInfo.rotation());
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.scale(this.scale * 0.008F, this.scale * 0.008F, this.scale * 0.008F);
        stack.scale(this.scale, this.scale, this.scale);
        Matrix4f matrix4f = stack.last().pose();
        Font fontRenderer = Minecraft.getInstance().font;
        float f2 = (float) (-fontRenderer.width(this.text) / 2);
        fontRenderer.drawInBatch(this.text, f2, 0F, color, false, matrix4f, bufferIn, true, 0, 15728880);
        bufferIn.endBatch();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.grow) {
            this.scale *= 1.08F;
            if (this.scale > 2F * 2.0D) {
                this.grow = false;
            }
        } else {
            this.scale *= 0.96F;
        }
    }

    @Override
    @Nonnull
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}
