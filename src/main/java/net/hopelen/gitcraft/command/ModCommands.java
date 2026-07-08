package net.hopelen.gitcraft.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.hopelen.gitcraft.GitCraft;
import net.hopelen.gitcraft.logic.Commit;
import net.hopelen.gitcraft.logic.Init;
import net.hopelen.gitcraft.logic.Place;
import net.hopelen.gitcraft.logic.Suggestions;
import net.hopelen.gitcraft.logic.Unplace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.level.block.Rotation;


public class ModCommands {


    public static void registerCommands() {

        GitCraft.LOGGER.info("Command initialization begins");


        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            var gitcraft = ClientCommands.literal("gitcraft");

            // /gitcraft status
            gitcraft.then(ClientCommands.literal("status")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("gitcraft status"));
                        return 1;
                    })
            );

            // /gitcraft init <repoName> <pos1> <pos2>  (relative coords like ~ ~ ~ work)
            gitcraft.then(ClientCommands.literal("init")
                    .then(ClientCommands.argument("repoName", StringArgumentType.word())
                            .then(ClientCommands.argument("pos1", BlockPosArgument.blockPos())
                                    .then(ClientCommands.argument("pos2", BlockPosArgument.blockPos())
                                            .executes(context -> {
                                                String repoName = StringArgumentType.getString(context, "repoName");
                                                Init.execute(context.getSource(), repoName,
                                                        resolveBlockPos(context, "pos1"),
                                                        resolveBlockPos(context, "pos2"));
                                                return 1;
                                            })))));

            // /gitcraft place <repoName> <pos> [90|180|270]
            gitcraft.then(ClientCommands.literal("place")
                    .then(ClientCommands.argument("repoName", StringArgumentType.word())
                            .suggests(Suggestions.REPO_NAMES)
                            .then(ClientCommands.argument("pos", BlockPosArgument.blockPos())
                                    .executes(context -> runPlace(context, Rotation.NONE))
                                    .then(ClientCommands.literal("90")
                                            .executes(context -> runPlace(context, Rotation.CLOCKWISE_90)))
                                    .then(ClientCommands.literal("180")
                                            .executes(context -> runPlace(context, Rotation.CLOCKWISE_180)))
                                    .then(ClientCommands.literal("270")
                                            .executes(context -> runPlace(context, Rotation.COUNTERCLOCKWISE_90))))));

            // /gitcraft unplace [clear] — remove the placement you are standing in;
            // "clear" also removes the blocks in the region
            gitcraft.then(ClientCommands.literal("unplace")
                    .executes(context -> {
                        Unplace.execute(context.getSource(), false);
                        return 1;
                    })
                    .then(ClientCommands.literal("clear")
                            .executes(context -> {
                                Unplace.execute(context.getSource(), true);
                                return 1;
                            }))
            );

            // /gitcraft commit -m <message>
            gitcraft.then(ClientCommands.literal("commit")
                    .then(ClientCommands.literal("-m")
                            .then(ClientCommands.argument("message", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        String message = StringArgumentType.getString(context, "message");
                                        Commit.execute(context.getSource(), message);
                                        return 1;
                                    })
                            )
                    )
            );

            // /gitcraft branch — list branches
            // /gitcraft branch <name> — create branch
            gitcraft.then(ClientCommands.literal("branch")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Listing branches..."));
                        return 1;
                    })
                    .then(ClientCommands.argument("branchName", StringArgumentType.word())
                            .executes(context -> {
                                String branchName = StringArgumentType.getString(context, "branchName");
                                context.getSource().sendFeedback(Component.literal("Created branch: " + branchName));
                                return 1;
                            })
                    )
            );

            // /gitcraft checkout <branch>
            gitcraft.then(ClientCommands.literal("checkout")
                    .then(ClientCommands.argument("branchName", StringArgumentType.word())
                            .executes(context -> {
                                String branchName = StringArgumentType.getString(context, "branchName");
                                context.getSource().sendFeedback(Component.literal("Checked out: " + branchName));
                                return 1;
                            })
                    )
            );

            // /gitcraft add
            gitcraft.then(ClientCommands.literal("add")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Staged region."));
                        return 1;
                    })
            );

            // /gitcraft log
            gitcraft.then(ClientCommands.literal("log")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Commit log..."));
                        return 1;
                    })
            );

            // /gitcraft remote add <name> <url>
            gitcraft.then(ClientCommands.literal("remote")
                    .then(ClientCommands.literal("add")
                            .then(ClientCommands.argument("remoteName", StringArgumentType.word())
                                    .then(ClientCommands.argument("url", StringArgumentType.greedyString())
                                            .executes(context -> {
                                                String remoteName = StringArgumentType.getString(context, "remoteName");
                                                String url = StringArgumentType.getString(context, "url");
                                                context.getSource().sendFeedback(Component.literal("Added remote " + remoteName + " -> " + url));
                                                return 1;
                                            })
                                    )
                            )
                    )
            );

            dispatcher.register(gitcraft);
        });
        GitCraft.LOGGER.info("Command initialization ends");
    }

    private static int runPlace(CommandContext<FabricClientCommandSource> context, Rotation rotation) {
        String repoName = StringArgumentType.getString(context, "repoName");
        Place.execute(context.getSource(), repoName, resolveBlockPos(context, "pos"), rotation);
        return 1;
    }

    // vanilla Coordinates (supports ~ and ^) resolve against a CommandSourceStack, which
    // only exists server-side; build a throwaway one anchored at the client player
    private static BlockPos resolveBlockPos(CommandContext<FabricClientCommandSource> context, String name) {
        Coordinates coords = context.getArgument(name, Coordinates.class);
        LocalPlayer player = Minecraft.getInstance().player;
        CommandSourceStack anchor = new CommandSourceStack(CommandSource.NULL,
                player.position(), player.getRotationVector(), null, PermissionSet.ALL_PERMISSIONS,
                "gitcraft", CommonComponents.EMPTY, null, player);
        return coords.getBlockPos(anchor);
    }
}
