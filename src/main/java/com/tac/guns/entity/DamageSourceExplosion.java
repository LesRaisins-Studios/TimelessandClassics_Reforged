package com.tac.guns.entity;

import com.tac.guns.Config;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class DamageSourceExplosion extends EntityDamageSource {
    private final ResourceLocation item;
    public DamageSourceExplosion(@Nullable Entity damageSourceEntityIn,ResourceLocation item) {
        super("explosion.player", damageSourceEntityIn);
        this.item = item;
        if(Config.COMMON.grenades.ignoreArmor.get()) {
            this.setDamageBypassesArmor();
        }
    }
    public ResourceLocation getItem() {
        return item;
    }
}
