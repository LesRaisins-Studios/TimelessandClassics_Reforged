package com.tac.guns.entity;

import com.google.common.collect.Lists;
import com.tac.guns.init.ModEntities;
import com.tac.guns.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import java.util.List;

public class EffectCloudGrenadeEntity extends ThrowableGrenadeEntity{
    //effect of the cloud
    protected List<EffectInstance> effects = Lists.newArrayList();

    protected IParticleData particle = ParticleTypes.ENTITY_EFFECT;
    protected int color = 0;
    private float spreadPreTick = 0.01f;
    private float minRadius = 4.0f;
    private float maxRadius = 8.0f;
    private int areaDuration = 300;
    private float areaHeight = 0.5f;
    private boolean extinguishBySmoke = false;

    public EffectCloudGrenadeEntity(World world, LivingEntity player,ItemStack stack) {
        super(ModEntities.THROWABLE_EFFECT_GRENADE.get(), world, player);
        this.setItem(stack);
        init();
    }

    public EffectCloudGrenadeEntity(EntityType<? extends ThrowableItemEntity> entityType, World worldIn) {
        super(entityType, worldIn);
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        super.writeSpawnData(buffer);
        buffer.writeVarInt(this.color);
        DataSerializers.PARTICLE_DATA.write(buffer,particle);
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        super.readSpawnData(buffer);
        this.color=buffer.readVarInt();
        this.particle = DataSerializers.PARTICLE_DATA.read(buffer);
    }

    public void init(){
        this.setMaxLife(400);
        this.setShouldBounce(true);
        this.setBrokeOnGround(true);
        this.setGravityVelocity(0.055F);
    }
    @Override
    public void onDeath(){
        if(!world.isRemote()){
            releaseEffectCloud();
            this.world.playSound(null,this.getPosX(),this.getPosY(),this.getPosZ(), ModSounds.ENTITY_MOLOTOV_EXPLOSION.get(), SoundCategory.AMBIENT, 6, 1);
        }
    }

    @Override
    public void playImpactSound(BlockRayTraceResult result){
        SoundEvent sound = ModSounds.ENTITY_SMOKE_GRENADE_HIT.get();
        this.world.playSound(null, result.getHitVec().x, result.getHitVec().y, result.getHitVec().z, sound, SoundCategory.AMBIENT, 1.0F, 1.0F);
    }

    @Override
    public void renderTrailing(){
        int r = this.color >> 16 & 255;
        int g = this.color >> 8 & 255;
        int b = this.color & 255;
        this.world.addOptionalParticle(this.particle, this.getPosX()+rand.nextDouble()/3.0f, this.getPosY()+rand.nextDouble()/3.0f+0.25, this.getPosZ()+rand.nextDouble()/3.0f,
                (float)r / 255.0F, (float)g / 255.0F, (float)b / 255.0F);
    }

    public void releaseEffectCloud(){
        ModifiedAreaEffectCloud effectCloud = new ModifiedAreaEffectCloud(this.world,this.getPosX(),this.getPosY()+0.15,this.getPosZ());
        effectCloud.setParticleData(this.particle);
        for(EffectInstance effect : effects){
            effectCloud.addEffect(new EffectInstance(effect));
        }

        effectCloud.setRadiusPerTick(this.spreadPreTick);
        effectCloud.setColor(this.color);
        effectCloud.setHeight(this.areaHeight);
        effectCloud.setRadius(this.minRadius);
        effectCloud.setExtinguishBySmoke(this.extinguishBySmoke);
        effectCloud.setMaxRadius(this.maxRadius);
        effectCloud.setDuration(this.areaDuration);
        effectCloud.setParticleData(this.particle);

        this.world.addEntity(effectCloud);
    }

    public void addEffects(List<EffectInstance> effects){
        this.effects.addAll(effects);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setParticle(IParticleData particle) {
        this.particle = particle;
    }

    public void setSpreadPreTick(float spreadPreTick) {
        this.spreadPreTick = spreadPreTick;
    }

    public void setMinRadius(float minRadius) {
        this.minRadius = minRadius;
    }

    public void setMaxRadius(float maxRadius) {
        this.maxRadius = maxRadius;
    }

    public void setAreaDuration(int areaDuration) {
        this.areaDuration = areaDuration;
    }

    public void setAreaHeight(float areaHeight) {
        this.areaHeight = areaHeight;
    }

    public void setExtinguishBySmoke(boolean extinguishBySmoke) {
        this.extinguishBySmoke = extinguishBySmoke;
    }


}
