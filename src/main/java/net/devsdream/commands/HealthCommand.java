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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class HealthCommand {
    private static final SimpleCommandExceptionType HEALTH_ADJUST_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.health.add.failed"));
    private static final SimpleCommandExceptionType HEALTH_SET_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.health.set.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("health").requires((user) -> {
            return user.hasPermission(2);
        }).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("amount", FloatArgumentType.floatArg(0)).executes((heal) -> {
                    return healEntity(heal.getSource(), EntityArgument.getEntities(heal, "targets"),
                            FloatArgumentType.getFloat(heal, "amount"));
                })))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.entities())
                        .then(Commands.argument("amount", FloatArgumentType.floatArg(0)).executes((set) -> {
                            return setEntityHealth(set.getSource(), EntityArgument.getEntities(set, "targets"),
                                    FloatArgumentType.getFloat(set, "amount"));
                        })))));
    }

    private static int healEntity(CommandSourceStack source, Collection<? extends Entity> targets, float amount)
            throws CommandSyntaxException {
        List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).heal(amount);
                list.add(entity);
            }
        }

        if (list.isEmpty()) {
            throw HEALTH_ADJUST_FAILED_EXCEPTION.create();
        } else {
            if (list.size() == 1) {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.health.add.success.single",
                        list.iterator().next().getDisplayName(), amount), true);
            } else {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.health.add.success.multiple",
                        list.size(), amount), true);
            }

            return list.size();
        }
    }

    private static int setEntityHealth(CommandSourceStack source, Collection<? extends Entity> targets, float amount)
            throws CommandSyntaxException {
        List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                ((LivingEntity) entity).setHealth(amount);
                list.add(entity);
            }
        }

        if (list.isEmpty()) {
            throw HEALTH_SET_FAILED_EXCEPTION.create();
        } else {
            if (list.size() == 1) {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.health.set.success.single",
                        list.iterator().next().getDisplayName(), amount), true);
            } else {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.health.set.success.multiple",
                        list.size(), amount), true);
            }

            return (int) amount;
        }
    }
}