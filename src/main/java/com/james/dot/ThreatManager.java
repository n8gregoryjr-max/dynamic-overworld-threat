package com.james.dot;

public class ThreatManager {

    // Shared threat level for the world.
    private static int threatLevel;

    // Starts counting at Zero.
    public ThreatManager() {
        threatLevel = 0;
    }

    // Returns the current threat level.
    public int getThreatLevel() {
        return threatLevel;
    }

    // Sets threat to a chosen value, or 0 if negative.
    public void setThreatLevel(int value) {
        threatLevel = Math.max(value, 0);
    }

    // Adds threat, but keeps it from going negative.
    public void addThreat(int amount) {
        threatLevel += amount;

        if (threatLevel < 0) {
            threatLevel = 0;
        }
    }

    // Resets threat to 0.
    public static void resetThreat() {
        threatLevel = 0;
    }
}