package com.james.dot;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DOT implements ModInitializer {
	public static final String MOD_ID = "dot";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Main systems used by the mod.
	private ThreatManager threatManager;
	private BehaviorController behaviorController;

	@Override
	public void onInitialize() {
		// Loads the mod and it's systems on launch.
		threatManager = new ThreatManager();
		behaviorController = new BehaviorController(threatManager);

		LOGGER.info("DOT loaded successfully.");
		LOGGER.info("Starting threat level: " + threatManager.getThreatLevel());

		registerEvents();
		registerCommands();
	}

	private void registerEvents() {
		// Checks every living entity death for player kills.
		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			// Only count the kill if a player caused it.
			if (damageSource.getAttacker() instanceof ServerPlayerEntity player) {
				LOGGER.info("A mob was killed by the player.");

				// Increases threat level and updates nearby mobs.
				threatManager.addThreat(1);
				behaviorController.updateBehaviors();

				behaviorController.updateNearbyMobs(
						(ServerWorld) entity.getEntityWorld(),
						player.getX(),
						player.getY(),
						player.getZ()
				);

				LOGGER.info("New threat level: " + threatManager.getThreatLevel());
			}
		});
	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(CommandManager.literal("dot")

					// Shows the current threat level.
					.then(CommandManager.literal("status")
							.executes(context -> {
								int threat = threatManager.getThreatLevel();
								context.getSource().sendMessage(Text.literal("Threat level: " + threat));
								return 1;
							})
					)

					// Adds a chosen amount to threat.
					.then(CommandManager.literal("add")
							.then(CommandManager.argument("amount", IntegerArgumentType.integer())
									.executes(context -> {
										int amount = IntegerArgumentType.getInteger(context, "amount");

										threatManager.addThreat(amount);
										behaviorController.updateBehaviors();

										ServerWorld world = context.getSource().getWorld();
										ServerPlayerEntity player = context.getSource().getPlayer();

										behaviorController.updateNearbyMobs(
												world,
												player.getX(),
												player.getY(),
												player.getZ()
										);

										context.getSource().sendMessage(Text.literal(
												"Added " + amount + " threat. New level: "
														+ threatManager.getThreatLevel()
										));
										return 1;
									})
							)
					)

					// Sets threat to an exact value.
					.then(CommandManager.literal("set")
							.then(CommandManager.argument("amount", IntegerArgumentType.integer())
									.executes(context -> {
										int amount = IntegerArgumentType.getInteger(context, "amount");

										threatManager.setThreatLevel(amount);
										behaviorController.updateBehaviors();

										ServerWorld world = context.getSource().getWorld();
										ServerPlayerEntity player = context.getSource().getPlayer();

										behaviorController.updateNearbyMobs(
												world,
												player.getX(),
												player.getY(),
												player.getZ()
										);

										context.getSource().sendMessage(Text.literal(
												"Set threat to: " + threatManager.getThreatLevel()
										));
										return 1;
									})
							)
					)

					// Resets threat back to 0.
					.then(CommandManager.literal("reset")
							.executes(context -> {
								ThreatManager.resetThreat();
								behaviorController.updateBehaviors();

								ServerWorld world = context.getSource().getWorld();
								ServerPlayerEntity player = context.getSource().getPlayer();

								behaviorController.updateNearbyMobs(
										world,
										player.getX(),
										player.getY(),
										player.getZ()
								);

								context.getSource().sendMessage(Text.literal("Threat reset to 0"));
								return 1;
							})
					)
			);
		});
	}
}