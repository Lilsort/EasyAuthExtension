package com.lilsort.easyauthextension.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import com.lilsort.easyauthextension.config.ExtensionConfig;
import com.lilsort.easyauthextension.utils.FailedLoginIpManager;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ExtensionCommands {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, net.minecraft.command.CommandRegistryAccess registryAccess, net.minecraft.server.command.CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(literal("eaext")
            .requires(source -> source.hasPermissionLevel(3))
            .then(literal("bantime")
                .then(argument("minutes", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        int minutes = IntegerArgumentType.getInteger(ctx, "minutes");
                        ExtensionConfig.INSTANCE.banDurationMinutes = minutes;
                        ExtensionConfig.save();
                        ctx.getSource().sendFeedback(() -> Text.literal("§aBan duration set to " + minutes + " minutes."), true);
                        return 1;
                    })
                )
            )
            .then(literal("maxattempts")
                .then(argument("attempts", IntegerArgumentType.integer(1))
                    .executes(ctx -> {
                        int attempts = IntegerArgumentType.getInteger(ctx, "attempts");
                        ExtensionConfig.INSTANCE.maxFailures = attempts;
                        ExtensionConfig.save();
                        ctx.getSource().sendFeedback(() -> Text.literal("§aMax failed attempts set to " + attempts + "."), true);
                        return 1;
                    })
                )
            )
            .then(literal("unban")
                .then(argument("ip", StringArgumentType.word())
                    .suggests((ctx, builder) -> suggestIps(builder))
                    .executes(ctx -> {
                        String ip = StringArgumentType.getString(ctx, "ip");
                        if (FailedLoginIpManager.unbanIp(ip)) {
                            ctx.getSource().sendFeedback(() -> Text.literal("§aSuccessfully unbanned IP: " + ip), true);
                        } else {
                            ctx.getSource().sendFeedback(() -> Text.literal("§cIP is not banned: " + ip), false);
                        }
                        return 1;
                    })
                )
            )
        );
    }
    
    private static CompletableFuture<Suggestions> suggestIps(SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        Set<String> bannedIps = FailedLoginIpManager.getBannedIps();
        for (String ip : bannedIps) {
            if (ip.toLowerCase().startsWith(remaining)) {
                builder.suggest(ip);
            }
        }
        return builder.buildFuture();
    }
}
