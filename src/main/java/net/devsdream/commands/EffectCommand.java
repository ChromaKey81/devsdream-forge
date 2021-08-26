package net.devsdream.commands;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import javax.annotation.Nullable;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.TranslatableComponent;

public class EffectCommand {
   private static final SimpleCommandExceptionType GIVE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
         new TranslatableComponent("commands.effect.give.failed"));
   private static final SimpleCommandExceptionType CLEAR_EVERYTHING_FAILED_EXCEPTION = new SimpleCommandExceptionType(
         new TranslatableComponent("commands.effect.clear.everything.failed"));
   private static final SimpleCommandExceptionType CLEAR_SPECIFIC_FAILED_EXCEPTION = new SimpleCommandExceptionType(
         new TranslatableComponent("commands.effect.clear.specific.failed"));

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      dispatcher
            .register(Commands.literal("dreameffect").requires((user) -> {
               return user.hasPermission(2);
            }).then(Commands.literal("clear").executes((clearAllUser) -> {
               return clearAllEffects(clearAllUser.getSource(),
                     ImmutableList.of(clearAllUser.getSource().getEntityOrException()));
            }).then(Commands.argument("targets", EntityArgument.entities()).executes((clearAllTargets) -> {
               return clearAllEffects(clearAllTargets.getSource(),
                     EntityArgument.getEntities(clearAllTargets, "targets"));
            }).then(Commands.argument("effect", MobEffectArgument.effect()).executes((clearEffect) -> {
               return clearEffect(clearEffect.getSource(), EntityArgument.getEntities(clearEffect, "targets"),
                     MobEffectArgument.getEffect(clearEffect, "effect"));
            })))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities())
                  .then(Commands.argument("effect", MobEffectArgument.effect()).executes((context) -> {
                     return addEffect(context.getSource(), EntityArgument.getEntities(context, "targets"),
                           MobEffectArgument.getEffect(context, "effect"), (Integer) null, 0, true, true, false);
                  }).then(Commands.argument("seconds", IntegerArgumentType.integer(1, 1000000))
                        .executes((context) -> {
                           return addEffect(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                 MobEffectArgument.getEffect(context, "effect"),
                                 IntegerArgumentType.getInteger(context, "seconds"), 0, true, true, false);
                        }).then(Commands.argument("amplifier", IntegerArgumentType.integer(0, 255))
                              .executes((context) -> {
                                 return addEffect(context.getSource(),
                                       EntityArgument.getEntities(context, "targets"),
                                       MobEffectArgument.getEffect(context, "effect"),
                                       IntegerArgumentType.getInteger(context, "seconds"),
                                       IntegerArgumentType.getInteger(context, "amplifier"), true, true, false);
                              }).then(Commands.argument("hideParticles", BoolArgumentType.bool())
                                    .executes((context) -> {
                                       return addEffect(context.getSource(),
                                             EntityArgument.getEntities(context, "targets"),
                                             MobEffectArgument.getEffect(context, "effect"),
                                             IntegerArgumentType.getInteger(context, "seconds"),
                                             IntegerArgumentType.getInteger(context, "amplifier"),
                                             !BoolArgumentType.getBool(context, "hideParticles"), false, false);
                                    }).then(Commands.argument("hideIcon", BoolArgumentType.bool())
                                          .executes((context) -> {
                                             return addEffect(context.getSource(),
                                                   EntityArgument.getEntities(context, "targets"),
                                                   MobEffectArgument.getEffect(context, "effect"),
                                                   IntegerArgumentType.getInteger(context, "seconds"),
                                                   IntegerArgumentType.getInteger(context, "amplifier"),
                                                   !BoolArgumentType.getBool(context, "hideParticles"),
                                                   !BoolArgumentType.getBool(context, "hideIcon"), false);
                                          }).then(Commands.argument("isAmbient", BoolArgumentType.bool())
                                                .executes((context) -> {
                                                   return addEffect(context.getSource(),
                                                         EntityArgument.getEntities(context, "targets"),
                                                         MobEffectArgument.getEffect(context, "effect"),
                                                         IntegerArgumentType.getInteger(context, "seconds"),
                                                         IntegerArgumentType.getInteger(context, "amplifier"),
                                                         !BoolArgumentType.getBool(context, "hideParticles"),
                                                         !BoolArgumentType.getBool(context, "hideIcon"),
                                                         BoolArgumentType.getBool(context, "isAmbient"));
                                                }))))))))));
   }

   private static int addEffect(CommandSourceStack source, Collection<? extends Entity> targets, MobEffect effect,
         @Nullable Integer seconds, int amplifier, boolean showParticles, boolean showIcon, boolean ambient)
         throws CommandSyntaxException {
      List<Entity> list = Lists.newArrayListWithCapacity(targets.size());
      int j;
      if (seconds != null) {
         if (effect.isInstantenous()) {
            j = seconds;
         } else {
            j = seconds * 20;
         }
      } else if (effect.isInstantenous()) {
         j = 1;
      } else {
         j = 600;
      }

      for (Entity entity : targets) {
         if (entity instanceof LivingEntity) {
            MobEffectInstance effectinstance = new MobEffectInstance(effect, j, amplifier, ambient, showParticles, showIcon);
            if (((LivingEntity) entity).addEffect(effectinstance)) {
               list.add(entity);
            }
         }
      }

      if (list.isEmpty()) {
         throw GIVE_FAILED_EXCEPTION.create();
      } else {
         if (list.size() == 1) {
            source.sendSuccess(new TranslatableComponent("commands.effect.give.success.single",
                  effect.getDisplayName(), targets.iterator().next().getDisplayName(), j / 20), true);
         } else {
            source.sendSuccess(new TranslatableComponent("commands.effect.give.success.multiple",
                  effect.getDisplayName(), targets.size(), j / 20), true);
         }

         return list.size();
      }
   }

   private static int clearAllEffects(CommandSourceStack source, Collection<? extends Entity> targets)
         throws CommandSyntaxException {
      List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

      for (Entity entity : targets) {
         if (entity instanceof LivingEntity && ((LivingEntity) entity).removeAllEffects()) {
            list.add(entity);
         }
      }

      if (list.isEmpty()) {
         throw CLEAR_EVERYTHING_FAILED_EXCEPTION.create();
      } else {
         if (list.size() == 1) {
            source.sendSuccess(new TranslatableComponent("commands.effect.clear.everything.success.single",
                  targets.iterator().next().getDisplayName()), true);
         } else {
            source.sendSuccess(
                  new TranslatableComponent("commands.effect.clear.everything.success.multiple", targets.size()),
                  true);
         }

         return list.size();
      }
   }

   private static int clearEffect(CommandSourceStack source, Collection<? extends Entity> targets, MobEffect effect)
         throws CommandSyntaxException {
      List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

      for (Entity entity : targets) {
         if (entity instanceof LivingEntity && ((LivingEntity) entity).removeEffect(effect)) {
            list.add(entity);
         }
      }

      if (list.isEmpty()) {
         throw CLEAR_SPECIFIC_FAILED_EXCEPTION.create();
      } else {
         if (list.size() == 1) {
            source.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.single",
                  effect.getDisplayName(), list.iterator().next().getDisplayName()), true);
         } else {
            source.sendSuccess(new TranslatableComponent("commands.effect.clear.specific.success.multiple",
                  effect.getDisplayName(), list.size()), true);
         }

         return list.size();
      }
   }
}