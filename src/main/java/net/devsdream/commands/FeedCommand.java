package net.devsdream.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import java.util.Collection;
import java.util.List;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class FeedCommand {
    private static final SimpleCommandExceptionType FEED_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.feed.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("feed").requires((user) -> {
            return user.hasPermission(2);
        }).then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("foodLevel", IntegerArgumentType.integer()).executes((feed) -> {
                    return feedPlayer(feed.getSource(), EntityArgument.getPlayers(feed, "targets"),
                            IntegerArgumentType.getInteger(feed, "foodLevel"), 0);
                }).then(Commands.argument("saturation", FloatArgumentType.floatArg()).executes((feedWithSaturation) -> {
                    return feedPlayer(feedWithSaturation.getSource(),
                            EntityArgument.getPlayers(feedWithSaturation, "targets"),
                            IntegerArgumentType.getInteger(feedWithSaturation, "foodLevel"),
                            FloatArgumentType.getFloat(feedWithSaturation, "saturation"));
                })))));
    }

    private static int feedPlayer(CommandSourceStack source, Collection<? extends Player> targets, int foodLevel,
            float saturation) throws CommandSyntaxException {
        List<Player> list = Lists.newArrayListWithCapacity(targets.size());

        for (Player player : targets) {
            if (player instanceof Player) {
                ((Player) player).getFoodData().eat(foodLevel, saturation);
                list.add(player);
            }
        }

        if (list.isEmpty()) {
            throw FEED_FAILED_EXCEPTION.create();
        } else {
            if (list.size() == 1) {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.feed.success.single",
                        list.iterator().next().getDisplayName(), foodLevel, saturation), true);
            } else {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.feed.success.multiple",
                        list.size(), foodLevel, saturation), true);
            }

            return list.size();
        }
    }
}