package com.tac.guns.item.TransitionalTypes.grenades;

import com.google.common.collect.Lists;
import com.tac.guns.entity.EffectCloudGrenadeEntity;
import com.tac.guns.entity.ThrowableGrenadeEntity;
import com.tac.guns.init.ModEffects;
import com.tac.guns.item.GrenadeItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class EffectGrenadeItem extends GrenadeItem {
    public static Function<EffectGrenadeItem,ItemStack> MOLOTOV = (item)->{
        ItemStack stack = new ItemStack(item);

        List<EffectInstance> effects = new ArrayList<>();
        effects.add(new EffectInstance(ModEffects.BURNED.get(),60,0));

        PotionUtils.appendEffects(stack,effects);
        return stack;
    };
    public EffectGrenadeItem(Properties properties, float speed,Function<EffectGrenadeItem,ItemStack> createItem) {
        super(properties,999, 0, speed);
        this.createItem = createItem;
    }

    public EffectGrenadeItem(Properties properties, float speed) {
        super(properties,999, 0, speed);
    }
    //default attributes
    private float spreadPreTick = 0.01f;
    private float minRadius = 4.0f;
    private float maxRadius = 8.0f;
    private int areaDuration = 300;
    private float areaHeight = 0.5f;
    private boolean extinguishByFire = false;
    private Function<EffectGrenadeItem,ItemStack> createItem = (item)-> new ItemStack(this);

    private Supplier<IParticleData> particle = ()->ParticleTypes.ENTITY_EFFECT;
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
    public void setExtinguishByFire(boolean extinguishByFire) {
        this.extinguishByFire = extinguishByFire;
    }
    public void setParticle(Supplier<IParticleData> particle) {
        this.particle = particle;
    }
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items){
        if(this.isInGroup(group)){
            items.add(createItem.apply(this));
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        PotionUtils.addPotionTooltip(stack,tooltip,1.0F);
    }


    public ThrowableGrenadeEntity create(World world, LivingEntity entity, ItemStack stack) {
        EffectCloudGrenadeEntity grenade = new EffectCloudGrenadeEntity(world,entity,stack);

        grenade.addEffects(getEffects(stack));
        grenade.setColor(PotionUtils.getPotionColorFromEffectList(getEffects(stack)));

        grenade.setAreaHeight(areaHeight);
        grenade.setMaxRadius(maxRadius);
        grenade.setMinRadius(minRadius);
        grenade.setAreaDuration(areaDuration);
        grenade.setExtinguishByFire(extinguishByFire);
        grenade.setSpreadPreTick(spreadPreTick);
        grenade.setParticle(particle.get());

        return grenade;
    }
    public List<EffectInstance> getEffects(ItemStack stack){
        List<EffectInstance> effects = Lists.newArrayList();
        if(stack.getTag()!=null){
            PotionUtils.addCustomPotionEffectToList(stack.getTag(),effects);
        }
        return effects;
    }


    public boolean canCook() {
        return false;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        return stack;
    }

    @Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft)
    {
        if(!worldIn.isRemote())
        {
            int duration = this.getUseDuration(stack) - timeLeft;
            if(duration >= 5)
            {
                if(!(entityLiving instanceof PlayerEntity) || !((PlayerEntity) entityLiving).isCreative())
                    stack.shrink(1);
                ThrowableGrenadeEntity grenade = this.create(worldIn, entityLiving, stack);
                grenade.func_234612_a_(entityLiving, entityLiving.rotationPitch, entityLiving.rotationYaw, 0.0F, Math.min(1.0F, duration / 20F)*this.speed, 1.5F);
                worldIn.addEntity(grenade);
                this.onThrown(worldIn, grenade);
            }
        }
    }

}
