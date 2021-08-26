package net.devsdream.commands;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.arguments.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class AirCommand {
    private static final SimpleCommandExceptionType AIR_SET_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.air.set.failed"));
    private static final SimpleCommandExceptionType AIR_ADD_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.air.add.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("air").requires((user) -> {
            return user.hasPermission(2);
        }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.literal("set")
                .then(Commands.argument("amount", IntegerArgumentType.integer(0)).executes((run) -> {
                    return setAir(run.getSource(), EntityArgument.getEntities(run, "targets"),
                            IntegerArgumentType.getInteger(run, "amount"));
                }))).then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer()).executes((run) -> {
                            return increaseAir(run.getSource(), EntityArgument.getEntities(run, "targets"),
                                    IntegerArgumentType.getInteger(run, "amount"));
                        })))));
    }

    private static int setAir(CommandSourceStack source, Collection<? extends Entity> targets, int amount)
            throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                if (amount <= ((LivingEntity) entity).getMaxAirSupply()) {
                    ((LivingEntity) entity).setAirSupply(amount);;
                    i++;
                } else {
                    ((LivingEntity) entity).setAirSupply(((LivingEntity) entity).getMaxAirSupply());
                }
            }
        }

        if (i == 0) {
            throw AIR_SET_FAILED_EXCEPTION.create();
        } else {
            if (targets.size() == 1) {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.air.set.success.single",
                        targets.iterator().next().getDisplayName(), amount), true);
            } else {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.air.set.success.multiple",
                        targets.size(), amount), true);
            }

            return i;
        }
    }

    private static int increaseAir(CommandSourceStack source, Collection<? extends Entity> targets, int amount)
            throws CommandSyntaxException {
        List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                if (((LivingEntity) entity).getAirSupply() + amount <= ((LivingEntity) entity).getMaxAirSupply()) {
                    ((LivingEntity) entity).setAirSupply(((LivingEntity) entity).getAirSupply() + amount);
                    list.add(entity);
                } else {
                    ((LivingEntity) entity).setAirSupply(((LivingEntity) entity).getMaxAirSupply());
                    list.add(entity);
                }
            }
        }

        if (list.isEmpty()) {
            throw AIR_ADD_FAILED_EXCEPTION.create();
        } else {
            if (list.size() == 1) {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.air.add.success.single",
                        list.iterator().next().getDisplayName(), amount), true);
            } else {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.air.add.success.multiple",
                        list.size(), amount), true);
            }

            return amount;
        }
    }
}