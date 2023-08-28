package com.tac.guns.item.TransitionalTypes.grenades;

import com.tac.guns.entity.ThrowableGrenadeEntity;
import com.tac.guns.entity.ThrowableSmokeGrenadeEntity;
import com.tac.guns.item.GrenadeItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class SmokeGrenadeItem extends GrenadeItem
{
    public SmokeGrenadeItem(Properties properties, int maxCookTime, float power, float speed)
    {
        super(properties, maxCookTime, power, speed);
    }

    public ThrowableGrenadeEntity create(World world, LivingEntity entity, int timeLeft)
    {
        return new ThrowableSmokeGrenadeEntity(world, entity); // Current ThrowableGrenadeEntity is perfect for impact 1/31/2022
    }

    public boolean canCook()
    {
        return false;
    }

    protected void onThrown(World world, ThrowableGrenadeEntity entity) {
    }
}
