package net.devsdream.commands;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.*;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.EquipmentSlot;

public class DamageItemCommand {
    private static final SimpleCommandExceptionType DAMAGEITEM_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.damageitem.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("damageitem").requires((user) -> {
            return user.hasPermission(2);
        }).then(Commands.argument("targets", EntityArgument.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .then(Commands.literal("mainhand").executes((damage) -> {
                            return damageItem(damage.getSource(), EntityArgument.getPlayers(damage, "targets"),
                                    EquipmentSlot.MAINHAND, IntegerArgumentType.getInteger(damage, "amount"));
                        })).then(Commands.literal("offhand").executes((damage) -> {
                            return damageItem(damage.getSource(), EntityArgument.getPlayers(damage, "targets"),
                                    EquipmentSlot.OFFHAND, IntegerArgumentType.getInteger(damage, "amount"));
                        })).then(Commands.literal("head").executes((damage) -> {
                            return damageItem(damage.getSource(), EntityArgument.getPlayers(damage, "targets"),
                                    EquipmentSlot.HEAD, IntegerArgumentType.getInteger(damage, "amount"));
                        })).then(Commands.literal("chest").executes((damage) -> {
                            return damageItem(damage.getSource(), EntityArgument.getPlayers(damage, "targets"),
                                    EquipmentSlot.CHEST, IntegerArgumentType.getInteger(damage, "amount"));
                        })).then(Commands.literal("legs").executes((damage) -> {
                            return damageItem(damage.getSource(), EntityArgument.getPlayers(damage, "targets"),
                                    EquipmentSlot.LEGS, IntegerArgumentType.getInteger(damage, "amount"));
                        })).then(Commands.literal("feet").executes((damage) -> {
                            return damageItem(damage.getSource(), EntityArgument.getPlayers(damage, "targets"),
                                    EquipmentSlot.FEET, IntegerArgumentType.getInteger(damage, "amount"));
                        })))));
    }

    private static int damageItem(CommandSourceStack source, Collection<? extends ServerPlayer> targets,
            EquipmentSlot slot, int amount) throws CommandSyntaxException {
        Map<ServerPlayer, ItemStack> map = Maps.newHashMapWithExpectedSize(targets.size());

        for (ServerPlayer player : targets) {
            ItemStack targetItem = player.getItemBySlot(slot);
            targetItem.hurtAndBreak(amount, player, (p) -> {
                p.broadcastBreakEvent(slot);
            });
            map.put(player, targetItem);
        }

        if (map.isEmpty()) {
            throw DAMAGEITEM_FAILED_EXCEPTION.create();
        } else {
            if (map.size() == 1) {
                map.forEach((player, itemStack) -> {
                    source.sendSuccess(new TranslatableComponent("commands.devsdream.damageitem.success.single",
                            player.getDisplayName(), itemStack.getDisplayName(), amount), true);
                });
            } else {
                source.sendSuccess(new TranslatableComponent("commands.devsdream.damageitem.success.multiple",
                        map.size(), amount), true);
            }

            return map.size();
        }
    }
}