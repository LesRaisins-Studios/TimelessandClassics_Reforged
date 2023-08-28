package com.tac.guns.effect;

import com.tac.guns.init.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber()
public class BurnedEffect extends Effect {
    public BurnedEffect(EffectType typeIn, int liquidColorIn)
    {
        super(typeIn, liquidColorIn);
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        return Collections.emptyList();
    }

    @Override
    public void performEffect(LivingEntity entityLivingBaseIn, int amplifier){
        if(!entityLivingBaseIn.isImmuneToFire()){
            entityLivingBaseIn.setFire(2);
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier){
        return duration % 20 == 0;
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event){
        if(event.getEntityLiving().isPotionActive(ModEffects.BURNED.get())){
            if(event.getSource().isFireDamage())
                event.setAmount(event.getAmount() * 2.0f);
        }
    }
}
