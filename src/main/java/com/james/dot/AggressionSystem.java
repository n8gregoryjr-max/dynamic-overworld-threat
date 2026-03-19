package com.james.dot;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;

public class AggressionSystem {

    public boolean shouldActivate(MobEntity mob, int threatLevel) {
        // Aggression only starts at 100 threat.
        if (threatLevel < 100) {
            return false;
        }

        EntityAttributeInstance attackDamage =
                mob.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);

        if (attackDamage == null) {
            return false;
        }

        double currentDamage = attackDamage.getBaseValue();

        // Only buff mobs if they are still at their normal damage.
        if (mob instanceof ZombieEntity) {
            return currentDamage == 3.0;
        }

        if (mob instanceof SkeletonEntity) {
            return currentDamage == 2.0;
        }

        return false;
    }

    public void applyBehavior(MobEntity mob, int threatLevel) {
        if (!shouldActivate(mob, threatLevel)) {
            return;
        }

        EntityAttributeInstance attackDamage =
                mob.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE);

        if (attackDamage == null) {
            return;
        }

        double currentDamage = attackDamage.getBaseValue();

        // Every 100 threat adds 1 extra damage.
        int bonusDamage = threatLevel / 100;
        double newDamage = currentDamage + bonusDamage;

        attackDamage.setBaseValue(newDamage);

        DOT.LOGGER.info("Buffed hostile mob damage to: " + newDamage);
    }
}