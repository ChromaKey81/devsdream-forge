package net.devsdream.commands;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import net.minecraft.commands.arguments.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.commands.Commands;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class MotionCommand {
    private static SimpleCommandExceptionType MOTION_FAILED_EXCEPTION = new SimpleCommandExceptionType(
         new TranslatableComponent("commands.devsdream.motion.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher.register(Commands.literal("motion").requires((user) -> {
         return user.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities())
            .then(Commands.argument("value", DoubleArgumentType.doubleArg()).then(Commands.literal("x").executes((context) -> {
                return setMotion(context.getSource(), EntityArgument.getEntities(context, "targets"), "x", DoubleArgumentType.getDouble(context, "value"));
            }))).then(Commands.literal("y").executes((context) -> {
                return setMotion(context.getSource(), EntityArgument.getEntities(context, "targets"), "y", DoubleArgumentType.getDouble(context, "value"));
            })).then(Commands.literal("z").executes((context) -> {
                return setMotion(context.getSource(), EntityArgument.getEntities(context, "targets"), "z", DoubleArgumentType.getDouble(context, "value"));
            }))));
   }

   private static int setMotion(CommandSourceStack source, Collection<? extends Entity> targets, String which, double motion)
         throws CommandSyntaxException {
      List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

      for (Entity entity : targets) {
         Vec3 current = entity.getDeltaMovement();
         switch (which) {
             case "x": {
                entity.setDeltaMovement(motion, current.y, current.z);
                break;
             }
             case "y": {
                 entity.setDeltaMovement(current.x, motion, current.z);
                 break;
             }
             case "z": {
                 entity.setDeltaMovement(current.x, current.y, motion);
                 break;
             }
         }
         entity.hurtMarked = true;
         list.add(entity);
      }

      if (list.isEmpty()) {
         throw MOTION_FAILED_EXCEPTION.create();
      } else {
         if (list.size() == 1) {
            source.sendSuccess(new TranslatableComponent("commands.devsdream.motion.success.single",
                  list.iterator().next().getDisplayName(), motion), true);
         } else {
            source.sendSuccess(
                  new TranslatableComponent("commands.devsdream.motion.success.multiple", list.size(), motion),
                  true);
         }

         return (int) motion;
      }
   }
}
