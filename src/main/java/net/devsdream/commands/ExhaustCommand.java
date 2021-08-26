package net.devsdream.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;

import java.util.Collection;
import java.util.List;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.*;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class ExhaustCommand {
    private static final SimpleCommandExceptionType EXHAUST_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.exhaust.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("exhaust").requires((user) -> {
            return user.hasPermission(2);
        }).then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("amount", FloatArgumentType.floatArg(0)).executes((exhaust) -> {
                    return exhaustPlayer(exhaust.getSource(), EntityArgument.getPlayers(exhaust, "targets"),
                            FloatArgumentType.getFloat(exhaust, "amount"));
                }))));
    }

    private static int exhaustPlayer(CommandSourceStack source, Collection<? extends Player> targets, float amount)
            throws CommandSyntaxException {

        List<Player> list = Lists.newArrayListWithCapacity(targets.size());

        for (Player entity : targets) {
            if (entity instanceof Player) {
                ((Player) entity).getFoodData().addExhaustion(amount);
                list.add(entity);
            }
        }

        if (list.isEmpty()) {
            throw EXHAUST_FAILED_EXCEPTION.create();
        } else {
            if (list.size() == 1) {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.exhaust.success.single",
                        list.iterator().next().getDisplayName(), amount), true);
            } else {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.exhaust.success.multiple",
                        list.size(), amount), true);
            }

            return list.size();
        }
    }
}