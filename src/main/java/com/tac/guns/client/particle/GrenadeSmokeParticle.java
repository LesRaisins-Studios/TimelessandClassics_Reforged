package com.tac.guns.client.particle;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;

public class GrenadeSmokeParticle extends SpriteTexturedParticle {
    private final IAnimatedSprite spriteSetWithAge;

    private GrenadeSmokeParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteSetWithAge) {
        super(world, x, y, z, 0.0D, 0.0D, 0.0D);
        this.spriteSetWithAge = spriteSetWithAge;
        this.motionX *= 0.1F;
        this.motionY *= 0.1F;
        this.motionZ *= 0.1F;
        this.motionX += motionX;
        this.motionY += motionY;
        this.motionZ += motionZ;
        float f1 = 1.0F - (float)(Math.random() * (double)0.3F);
        this.particleRed = f1;
        this.particleGreen = f1;
        this.particleBlue = f1;
        this.particleScale *= 1.875F;
        int i = (int)(8.0D / (Math.random() * 0.8D + 0.3D));
        this.maxAge = (int)Math.max((float)i * 2.5F, 1.0F);
        this.canCollide = false;
        this.selectSpriteWithAge(spriteSetWithAge);
    }

    public IParticleRenderType getRenderType() {
        return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public float getScale(float scaleFactor) {
        return this.particleScale * MathHelper.clamp(((float)this.age + scaleFactor) / (float)this.maxAge * 32.0F, 0.0F, 1.0F);
    }

    public void tick() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.age++ >= this.maxAge) {
            this.setExpired();
        } else {
            this.selectSpriteWithAge(this.spriteSetWithAge);
            this.move(this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.96F;
            this.motionY *= 0.96F;
            this.motionZ *= 0.96F;

            if (this.onGround) {
                this.motionX *= 0.7F;
                this.motionZ *= 0.7F;
            }

            BlockPos bp = new BlockPos(this.posX,this.posY,this.posZ);
            BlockState bs =world.getBlockState(bp);
            if(!bs.getBlock().matchesBlock(Blocks.AIR)){
                if(bs.getOpacity(world, bp) != 0 && bs.isSolid() || bs.isIn(Tags.Blocks.GLASS)){
                    this.setExpired();
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements IParticleFactory<BasicParticleType> {
        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GrenadeSmokeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

}
