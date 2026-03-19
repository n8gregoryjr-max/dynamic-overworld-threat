package com.james.dot;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

import java.util.List;

public class BehaviorController {

    // References to the systems this class controls.
    private ThreatManager threatManager;
    private PanicSystem panicSystem;
    private AggressionSystem aggressionSystem;

    public BehaviorController(ThreatManager threatManager) {
        this.threatManager = threatManager;
        this.panicSystem = new PanicSystem();
        this.aggressionSystem = new AggressionSystem();
    }

    public void updateBehaviors() {
        int threat = threatManager.getThreatLevel();
        DOT.LOGGER.info("Checking behavior systems at threat level: " + threat);

        if (threat >= 25) {
            DOT.LOGGER.info("Passive mob panic is active.");
        }

        if (threat >= 100) {
            DOT.LOGGER.info("Hostile mob aggression is active.");
        }
    }

    public void updateNearbyMobs(ServerWorld world, double x, double y, double z) {
        int threat = threatManager.getThreatLevel();

        // Check a box around the player for nearby mobs.
        final int RANGE = 30;
        Box area = new Box(
                x - RANGE, y - RANGE, z - RANGE,
                x + RANGE, y + RANGE, z + RANGE
        );

        List<Entity> entities = world.getOtherEntities(null, area);

        DOT.LOGGER.info("Checking nearby mobs...");

        for (Entity entity : entities) {

            // Passive mobs run away once threat is high enough.
            if (entity instanceof PassiveEntity passiveMob) {
                panicSystem.applyBehavior(passiveMob, x, z, threat);
            }

            // Hostile mobs get stronger, but passive mobs should not be checked here.
            if (entity instanceof MobEntity mob && !(mob instanceof PassiveEntity)) {
                aggressionSystem.applyBehavior(mob, threat);
            }
        }
    }
}