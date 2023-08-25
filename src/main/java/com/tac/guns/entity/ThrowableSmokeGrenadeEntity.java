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
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

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
                    double phi = Math.acos(2 * this.rand.nextDouble() - 1);

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
            if(ticksExisted%20==0){
                int amount = Math.min( (ticksExisted-40) /2, 40);
                int radius = amount/8;
                int minX = MathHelper.floor(this.getPosX() - radius);
                int maxX = MathHelper.floor(this.getPosX() + radius);
                int minY = MathHelper.floor(y - radius);
                int maxY = MathHelper.floor(y + radius);
                int minZ = MathHelper.floor(this.getPosZ() - radius);
                int maxZ = MathHelper.floor(this.getPosZ() + radius);
                for(LivingEntity entity : this.world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ))) {
                    entity.addPotionEffect(new EffectInstance(Effects.BLINDNESS,60,0,false,false));
                }
            }
        }
    }

    @Override
    protected void onImpact(RayTraceResult result)
    {
        switch(result.getType())
        {
            case BLOCK:
                BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
                if(this.shouldBounce) {
                    double speed = this.getMotion().length();
                    if(speed > 0.1) {
                        this.world.playSound(null, result.getHitVec().x, result.getHitVec().y, result.getHitVec().z,
                                ModSounds.ENTITY_SMOKE_GRENADE_HIT.get(), SoundCategory.AMBIENT, 1.0F, 1.0F);
                    }
                    Direction direction = blockResult.getFace();
                    switch(direction.getAxis())
                    {
                        case X:
                            this.setMotion(this.getMotion().mul(-0.5, 0.75, 0.75));
                            break;
                        case Y:
                            this.setMotion(this.getMotion().mul(0.75, -0.25, 0.75));
                            if(this.getMotion().getY() < this.getGravityVelocity())
                            {
                                this.setMotion(this.getMotion().mul(1, 0, 1));
                            }
                            break;
                        case Z:
                            this.setMotion(this.getMotion().mul(0.75, 0.75, -0.5));
                            break;
                    }
                }
            default:
                break;
        }
    }

    @Override
    public void onDeath(){

    }
}
