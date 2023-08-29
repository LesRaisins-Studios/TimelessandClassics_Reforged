package com.tac.guns.entity;

import com.tac.guns.init.ModEntities;
import com.tac.guns.init.ModItems;
import com.tac.guns.init.ModParticleTypes;
import com.tac.guns.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class ThrowableSmokeGrenadeEntity extends ThrowableGrenadeEntity{
    public ThrowableSmokeGrenadeEntity(EntityType<? extends ThrowableItemEntity> entityType, World worldIn) {
        super(entityType, worldIn);
        this.setItem(new ItemStack(ModItems.SMOKE_GRENADE.get()));
        this.setMaxLife(640);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.055F);
    }
    public ThrowableSmokeGrenadeEntity(World world, LivingEntity player) {
        super(ModEntities.THROWABLE_SMOKE_GRENADE.get(), world, player);
        this.setItem(new ItemStack(ModItems.SMOKE_GRENADE.get()));
        this.setMaxLife(640);
        this.setShouldBounce(true);
        this.setGravityVelocity(0.055F);
    }

    @Override
    public void tick(){
        super.tick();
        double y = this.getPosY() + 0.15;
        if(ticksExisted==45){
            this.world.playSound(null, this.getPosX(), y, this.getPosZ(), ModSounds.ENTITY_SMOKE_GRENADE_EXPLOSION.get(), SoundCategory.BLOCKS, 3, (1 + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
        }
        if (this.world.isRemote) {
            Minecraft mc = Minecraft.getInstance();
            ParticleManager particleManager = mc.particles;

            if(ticksExisted<40){
                world.addParticle(ModParticleTypes.GRENADE_SMOKE.get(),this.getPosX(),
                        y, this.getPosZ(), 0, 0.05, 0);
            }else {
                int amount = Math.min( (ticksExisted-40) /3, 30);
                int radius = amount/5;
                for (int i = 0; i < amount; i++) {
                    double theta = this.rand.nextDouble() * 2 * Math.PI;
                    double phi = Math.acos(2*this.rand.nextDouble()-1);

                    double xs = radius * Math.sin(phi) * Math.cos(theta) * 0.05;
                    double ys =
                            radius * Math.abs(Math.sin(phi) * Math.sin(theta)) * this.rand.nextDouble() * 0.05;
                    double zs = radius * Math.cos(phi) * 0.05;

                    Particle smoke = particleManager.addParticle(ModParticleTypes.GRENADE_SMOKE.get(),this.getPosX(),
                            y, this.getPosZ(), xs, ys, zs);
                    if (smoke != null) {
                        smoke.setMaxAge(150);
                    }
                }
            }
        }else{
            if(ticksExisted<120 || ticksExisted%20!=0)return;
            int amount = Math.min( (ticksExisted-40) /3, 30);
            double radius = amount/5.5f;
            double x1 = this.getPosX()-radius;
            double x2 = this.getPosX()+radius;
            double y1 = this.getPosY()-radius;
            double y2 = this.getPosY()+radius;
            double z1 = this.getPosZ()-radius;
            double z2 = this.getPosZ()+radius;

            AxisAlignedBB bb = new AxisAlignedBB(x1,y1,z1,x2,y2,z2);
            List<ModifiedAreaEffectCloud> list1 = this.world.getEntitiesWithinAABB(ModifiedAreaEffectCloud.class, bb);
            for (ModifiedAreaEffectCloud cloud : list1) {
                double d0 = cloud.getPosX() - this.getPosX();
                double d1 = cloud.getPosZ() - this.getPosZ();
                double d2 = d0 * d0 + d1 * d1;
                if (d2 <= (radius * radius) && cloud.extinguishBySmoke()) {
                    cloud.remove();
                }
            }
        }
    }

    @Override
    public void playImpactSound(BlockRayTraceResult result){
        SoundEvent sound = ModSounds.ENTITY_SMOKE_GRENADE_HIT.get();
        this.world.playSound(null, result.getHitVec().x, result.getHitVec().y, result.getHitVec().z, sound, SoundCategory.AMBIENT, 1.0F, 1.0F);
    }

    @Override
    public void onDeath(){

    }
}
