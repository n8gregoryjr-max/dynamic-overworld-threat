package com.james.dot;

import net.minecraft.entity.passive.PassiveEntity;

public class PanicSystem {

    // Settings for how far mobs notice the player and how far they flee.
    private int panicRadius = 30;
    private double speedBoost = 2.0;
    private double fleeDistance = 20.0;

    public void applyBehavior(PassiveEntity mob, double playerX, double playerZ, int threatLevel) {
        // Checks if it can even run.
        if (!shouldActivate(threatLevel)) {
            return;
        }

        // Finds the angle from the player to the entity.
        double x = mob.getX() - playerX;
        double z = mob.getZ() - playerZ;
        double distance = Math.sqrt(x * x + z * z);

        // Stop if the mob is too far away or directly on the player.
        if (distance == 0 || distance > panicRadius) {
            return;
        }

        // Convert the direction into a standard direction we can use for movement.
        double dirX = x / distance;
        double dirZ = z / distance;

        // Choose a point farther away from the player.
        double targetX = mob.getX() + dirX * fleeDistance;
        double targetY = mob.getY();
        double targetZ = mob.getZ() + dirZ * fleeDistance;

        // Tell the mob to move toward said point.
        mob.getNavigation().startMovingTo(targetX, targetY, targetZ, speedBoost);

        DOT.LOGGER.info("Passive mob is fleeing from the player.");
    }

    public boolean shouldActivate(int threatLevel) {
        return threatLevel >= 25;
    }
}