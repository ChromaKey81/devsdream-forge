package net.devsdream.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class CalculateCommand {

    private static final SimpleCommandExceptionType DIVIDE_BY_ZERO_EXCEPTION = new SimpleCommandExceptionType(
                        new TranslatableComponent("commands.devsdream.calculate.divide_by_zero"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("calculate").requires((user) -> {
            return user.hasPermission(2);
        }).then(Commands.literal("value")
                        .then(Commands.argument("value", IntegerArgumentType.integer()).then(add("value"))
                                .then(subtract("value")).then(multiply("value")).then(divide("value"))
                                .then(modulo("value")).then(equals("value")))).then(Commands.literal("command").then(Commands.argument("command", StringArgumentType.string()).then(add("command")).then(subtract("command")).then(multiply("command")).then(divide("command")).then(modulo("command")))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> add(String firstArgumentType) {
        return Commands
                .literal(
                        "+")
                .then(Commands.literal("value")
                        .then(Commands.argument("value2", IntegerArgumentType.integer()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType),
                                    IntegerArgumentType.getInteger(context, "value2"), "add");
                        }))).then(Commands.literal("command").then(Commands.argument("command2", StringArgumentType.string()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType), getValueFromCommand(context, "command2"), "add");
                        })));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> subtract(String firstArgumentType) {
        return Commands
                .literal(
                        "-")
                .then(Commands.literal("value")
                        .then(Commands.argument("value2", IntegerArgumentType.integer()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType),
                                    IntegerArgumentType.getInteger(context, "value2"), "subtract");
                        }))).then(Commands.literal("command").then(Commands.argument("command2", StringArgumentType.string()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType), getValueFromCommand(context, "command2"), "subtract");
                        })));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> multiply(String firstArgumentType) {
        return Commands
                .literal(
                        "*")
                .then(Commands.literal("value")
                        .then(Commands.argument("value2", IntegerArgumentType.integer()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType),
                                    IntegerArgumentType.getInteger(context, "value2"), "multiply");
                        }))).then(Commands.literal("command").then(Commands.argument("command2", StringArgumentType.string()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType), getValueFromCommand(context, "command2"), "multiply");
                        })));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> divide(String firstArgumentType) {
        return Commands
                .literal(
                        "/")
                .then(Commands.literal("value")
                        .then(Commands.argument("value2", IntegerArgumentType.integer()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType),
                                    IntegerArgumentType.getInteger(context, "value2"), "divide");
                        }))).then(Commands.literal("command").then(Commands.argument("command2", StringArgumentType.string()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType), getValueFromCommand(context, "command2"), "divide");
                        })));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> modulo(String firstArgumentType) {
        return Commands
                .literal(
                        "%")
                .then(Commands.literal("value")
                        .then(Commands.argument("value2", IntegerArgumentType.integer()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType),
                                    IntegerArgumentType.getInteger(context, "value2"), "modulo");
                        }))).then(Commands.literal("command").then(Commands.argument("command2", StringArgumentType.string()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType), getValueFromCommand(context, "command2"), "modulo");
                        })));
    }
    private static LiteralArgumentBuilder<CommandSourceStack> equals(String firstArgumentType) {
        return Commands
                .literal(
                        "=")
                .then(Commands.literal("value")
                        .then(Commands.argument("value2", IntegerArgumentType.integer()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType),
                                    IntegerArgumentType.getInteger(context, "value2"), "equals");
                        }))).then(Commands.literal("command").then(Commands.argument("command2", StringArgumentType.string()).executes((context) -> {
                            return performOperation(context.getSource(), findFirstArgument(context, firstArgumentType), getValueFromCommand(context, "command2"), "equals");
                        })));
    }

    private static int findFirstArgument(CommandContext<CommandSourceStack> context, String firstArgumentType)
            throws CommandSyntaxException {
        switch (firstArgumentType) {
            case "value": {
                return IntegerArgumentType.getInteger(context, "value");
            }
            case "command": {
                return getValueFromCommand(context, "command");
            }
            default: {
                return 69;
            }
        }
    }

    private static int getValueFromCommand(CommandContext<CommandSourceStack> context, String argument) throws CommandSyntaxException {
        return context.getSource().getServer().getCommands().getDispatcher().execute(StringArgumentType.getString(context, argument), context.getSource());
    }

    private static int performOperation(CommandSourceStack source, int valueSoFar, int input, String type) throws CommandSyntaxException {
        int newVal = 0;
        switch (type) {
            case "add": {
                newVal = valueSoFar + input;
                break;
            }
            case "subtract": {
                newVal = valueSoFar - input;
                break;
            }
            case "multiply": {
                newVal = valueSoFar * input;
                break;
            }
            case "divide": {
                if (input == 0) {
                    if (valueSoFar == 0) {
                        throw new SimpleCommandExceptionType(new TextComponent("Imagine that you have zero cookies and you split them evenly among zero friends. How many cookies does each person get? See? It doesn’t make sense. And Cookie Monster is sad that there are no cookies, and you are sad that you have no friends.")).create();
                    } else throw DIVIDE_BY_ZERO_EXCEPTION.create();
                } else newVal = valueSoFar / input;
                break;
            }
            case "modulo": {
                if (input == 0) {
                    throw DIVIDE_BY_ZERO_EXCEPTION.create();
                } else newVal = valueSoFar % input;
                break;
            }
            case "equals": {
                newVal = valueSoFar == input ? 1 : 0;
                break;
            }
        }
        source.sendSuccess(new TranslatableComponent("commands.devsdream.calculate.success", newVal), true);
        return newVal;
    }
}