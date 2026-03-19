# Dynamic Overworld Threat (DOT)

This mod introduces a dynamic threat system that changes mob behavior based on player actions.

As the player kills mobs, the world reacts and becomes more dangerous.

## Features
- Global threat system based on mob kills (+1 per kill).
- Passive mobs begin to flee from the player at threat level 25.
- Hostile mobs gain +1 attack damage for every 100 threat levels.
- Commands to control and test threat levels.

## Commands
- /dot status -> shows current threat level
- /dot add <amount> -> increases threat
- /dot set <amount> -> sets threat level
- /dot reset -> resets threat to 0

## How It Works
The mod tracks a global threat level using a ThreatManager class.
A BehaviorController checks nearby mobs and applies different systems based on the threat level:
- PanicSystem handles passive mob fleeing.
- AggressionSystem increases hostile mob damage.

## How to Run
1. Install Minecraft Fabric Loader
2. Place the provided .jar file into your Minecraft "mods" folder
3. Launch Minecraft with Fabric
