package net.hopelen.gitcraft.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.hopelen.gitcraft.GitCraft;
import net.hopelen.gitcraft.logic.Init;
import net.hopelen.gitcraft.logic.Place;
import net.hopelen.gitcraft.logic.Suggestions;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;


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

            // /gitcraft init <repoName> <x1,y1,z1> <x2,y2,z2>
            gitcraft.then(ClientCommands.literal("init")
                    .then(ClientCommands.argument("repoName", StringArgumentType.word())
                            .then(ClientCommands.argument("x1", IntegerArgumentType.integer())
                                    .then(ClientCommands.argument("y1", IntegerArgumentType.integer())
                                            .then(ClientCommands.argument("z1", IntegerArgumentType.integer())
                                                    .then(ClientCommands.argument("x2", IntegerArgumentType.integer())
                                                            .then(ClientCommands.argument("y2", IntegerArgumentType.integer())
                                                                    .then(ClientCommands.argument("z2", IntegerArgumentType.integer())
                                                                            .executes(context -> {
                                                                                String repoName = StringArgumentType.getString(context, "repoName");

                                                                                BlockPos pos1 = new BlockPos(
                                                                                        IntegerArgumentType.getInteger(context, "x1"),
                                                                                        IntegerArgumentType.getInteger(context, "y1"),
                                                                                        IntegerArgumentType.getInteger(context, "z1")
                                                                                );
                                                                                BlockPos pos2 = new BlockPos(
                                                                                        IntegerArgumentType.getInteger(context, "x2"),
                                                                                        IntegerArgumentType.getInteger(context, "y2"),
                                                                                        IntegerArgumentType.getInteger(context, "z2")
                                                                                );

                                                                                Init.execute(context.getSource(), repoName, pos1, pos2);
                                                                                return 1;
                                                                            })
                                                                    )))))))
            );

            //gitcraft place <repoName>
            gitcraft.then(ClientCommands.literal("place")
                    .then(ClientCommands.argument("repoName", StringArgumentType.word())
                            .suggests(Suggestions.REPO_NAMES)
                            .then(ClientCommands.argument("x", IntegerArgumentType.integer())
                                    .then(ClientCommands.argument("y", IntegerArgumentType.integer())
                                            .then(ClientCommands.argument("z", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        BlockPos origin = new BlockPos(
                                                                IntegerArgumentType.getInteger(context, "x"),
                                                                IntegerArgumentType.getInteger(context, "y"),
                                                                IntegerArgumentType.getInteger(context, "z")
                                                        );
                                                        String repoName = StringArgumentType.getString(context, "repoName");
                                                        Place.execute(context.getSource(), repoName, origin);

                                                        return 1;
                                                    }))))));

            // /gitcraft commit -m <message>
            gitcraft.then(ClientCommands.literal("commit")
                    .then(ClientCommands.literal("-m")
                            .then(ClientCommands.argument("message", StringArgumentType.greedyString())
                                    .executes(context -> {
                                        String message = StringArgumentType.getString(context, "message");
                                        context.getSource().sendFeedback(Component.literal("Committed: " + message));
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
}
