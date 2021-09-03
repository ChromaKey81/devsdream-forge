package net.devsdream.commands;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public class DamageItemCommand {
    private static final SimpleCommandExceptionType DAMAGEITEM_FAILED_EXCEPTION = new SimpleCommandExceptionType(
            new TranslatableComponent("commands.devsdream.damageitem.failed"));
            private static final SimpleCommandExceptionType NO_ITEM_EXCEPTION = new SimpleCommandExceptionType(
                new TranslatableComponent("commands.devsdream.damageitem.failed.no_item"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("damageitem").requires((user) -> {
            return user.hasPermission(2);
        }).then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("amount", IntegerArgumentType.integer()).then(Commands.argument("slot", SlotArgument.slot()).executes((context) -> {
                    return damageItem(context.getSource(), EntityArgument.getEntities(context, "targets"), SlotArgument.getSlot(context, "slot"), IntegerArgumentType.getInteger(context, "amount"));
                })))));
    }

    private static int damageItem(CommandSourceStack source, Collection<? extends Entity> targets,
            int slot, int amount) throws CommandSyntaxException {
        Map<LivingEntity, ItemStack> map = Maps.newHashMapWithExpectedSize(targets.size());

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                LivingEntity target = (LivingEntity)entity;
                    SlotAccess stackReference = target.getSlot(slot);
                    if (stackReference == SlotAccess.NULL) {
                        throw NO_ITEM_EXCEPTION.create();
                    } else {
                        stackReference.get().hurtAndBreak(amount, target, (p) -> {
                            if (slot == 98) {
                                p.broadcastBreakEvent(InteractionHand.MAIN_HAND);
                            } else if (slot == 99) {
                                p.broadcastBreakEvent(InteractionHand.OFF_HAND);
                            } else if (slot >= 100 && slot <= 103) {
                                p.broadcastBreakEvent(Stream.of(EquipmentSlot.values()).filter(s -> s.getIndex() == (slot - 100)).findFirst().get());
                            }
                        });
                        if (target instanceof ServerPlayer) {
                            ((ServerPlayer)target).containerMenu.broadcastChanges();
                        }
                        map.put(target, stackReference.get());
                    }
            }
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