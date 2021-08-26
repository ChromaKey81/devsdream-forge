package net.devsdream.commands;

import java.util.Random;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class RandomNumberCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> literalCommandNode = dispatcher
                .register(Commands.literal("randomnumber").requires((user) -> {
                    return user.hasPermission(2);
                }).then(Commands.argument("maximum", IntegerArgumentType.integer(0, 2147483647)).executes((rng) -> {
                    return rng(rng.getSource(), IntegerArgumentType.getInteger(rng, "maximum"));
                })));

        dispatcher.register(Commands.literal("rng").requires((user) -> {
            return user.hasPermission(2);
        }).redirect(literalCommandNode));
    }

    private static int rng(CommandSourceStack source, int maximum) {
        Random rand = new Random();
        int result = rand.nextInt(maximum);
        source.sendSuccess(new TranslatableComponent("commands.devsdream.rng.result", result), true);
        return result;
    }
}