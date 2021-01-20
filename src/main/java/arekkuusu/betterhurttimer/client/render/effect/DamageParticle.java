package arekkuusu.betterhurttimer.client.render.effect;

import arekkuusu.betterhurttimer.BHT;
import arekkuusu.betterhurttimer.BHTConfig;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class DamageParticle extends Particle {

    public boolean grow = true;
    public float scale;
    public TranslationTextComponent text;
    public int color;

    public DamageParticle(int damage, ClientWorld world, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ) {
        super(world, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
        this.particleGravity = 0.1F;
        this.scale = 1F;
        this.maxAge = 12;
        this.color = damage > 0 ? BHTConfig.Runtime.Rendering.damageColor : BHTConfig.Runtime.Rendering.healColor;
        this.text = new TranslationTextComponent(BHT.MOD_ID + ".particle", Math.abs(damage));
    }

    @Override
    public void renderParticle(@Nonnull IVertexBuilder buffer, @Nonnull ActiveRenderInfo renderInfo, float partialTicks) {
        Vector3d vec3d = renderInfo.getProjectedView();
        float locX = (float) (MathHelper.lerp(partialTicks, this.prevPosX, this.posX) - vec3d.getX());
        float locY = (float) (MathHelper.lerp(partialTicks, this.prevPosY, this.posY) - vec3d.getY());
        float locZ = (float) (MathHelper.lerp(partialTicks, this.prevPosZ, this.posZ) - vec3d.getZ());

        IRenderTypeBuffer.Impl bufferIn = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        MatrixStack stack = new MatrixStack();
        stack.translate(locX, locY, locZ);
        stack.rotate(renderInfo.getRotation());
        stack.scale(-1.0F, -1.0F, 1.0F);
        stack.scale(this.scale * 0.008F, this.scale * 0.008F, this.scale * 0.008F);
        stack.scale(this.scale, this.scale, this.scale);
        Matrix4f matrix4f = stack.getLast().getMatrix();
        FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
        float f2 = (float) (-fontRenderer.func_238414_a_(text) / 2);
        fontRenderer.func_238416_a_(text.func_241878_f(), f2, 0, color, false, matrix4f, bufferIn, true, 0, 15728880);
        bufferIn.finish();
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
    public IParticleRenderType getRenderType() {
        return IParticleRenderType.CUSTOM;
    }
}
