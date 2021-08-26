package net.devsdream.commands;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.TranslatableComponent;

public class FreezeCommand {
   private static SimpleCommandExceptionType FREEZE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
         new TranslatableComponent("commands.devsdream.freeze.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(Commands.literal("freeze").requires((user) -> {
         return user.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities())
            .then(Commands.argument("ticks", IntegerArgumentType.integer(0)).executes((context) -> {
               return freezeEntity(context.getSource(), EntityArgument.getEntities(context, "targets"),
                     IntegerArgumentType.getInteger(context, "ticks"));
            }))));
   }

   private static int freezeEntity(CommandSourceStack source, Collection<? extends Entity> targets, int freezeTime)
         throws CommandSyntaxException {
      List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

      for (Entity entity : targets) {
         ((Entity) entity).setTicksFrozen(freezeTime);
         list.add(entity);
      }

      if (list.isEmpty()) {
         throw FREEZE_FAILED_EXCEPTION.create();
      } else {
         if (list.size() == 1) {
            source.sendSuccess(new TranslatableComponent("commands.devsdream.freeze.success.single",
                  list.iterator().next().getDisplayName(), freezeTime), true);
         } else {
            source.sendSuccess(
                  new TranslatableComponent("commands.devsdream.freeze.success.multiple", list.size(), freezeTime),
                  true);
         }

         return (int) freezeTime;
      }
   }
}