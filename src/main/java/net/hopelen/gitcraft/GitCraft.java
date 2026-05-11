package net.hopelen.gitcraft;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.hopelen.gitcraft.command.ModCommands;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitCraft implements ModInitializer {
    public static final String MOD_ID = "gitcraft";

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing GitCraft");

        ModCommands.registerCommands();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommands.literal("dimension").executes(context -> {
                context.getSource().sendFeedback(Component.literal("The dimension is: " + Minecraft.getInstance().level.dimension()));
                return 1;
            }));
        });
    }
}