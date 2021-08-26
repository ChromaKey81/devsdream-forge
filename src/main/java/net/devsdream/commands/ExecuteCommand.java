package net.devsdream.commands;

import net.minecraft.commands.arguments.*;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.NbtPathArgument.NbtPath;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ExecuteCommand extends net.minecraft.server.commands.ExecuteCommand {

   public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralCommandNode<CommandSourceStack> literalcommandnode = dispatcher
            .register((LiteralArgumentBuilder) Commands.literal("dreamexecute").requires((source) -> {
               return source.hasPermission(2);
            }));
      dispatcher.register(Commands.literal("execute").requires((source) -> {
            return source.hasPermission(2);
         }).then(Commands.literal("run").redirect(dispatcher.getRoot())).then(addConditionals(literalcommandnode, Commands.literal("if"), true)).then(addConditionals(literalcommandnode, Commands.literal("unless"), false)).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (context) -> {
            List<CommandSourceStack> list = Lists.newArrayList();
   
            for(Entity entity : EntityArgument.getOptionalEntities(context, "targets")) {
               list.add(context.getSource().withEntity(entity));
            }
   
            return list;
         }))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (context) -> {
            List<CommandSourceStack> list = Lists.newArrayList();
   
            for(Entity entity : EntityArgument.getOptionalEntities(context, "targets")) {
               list.add(context.getSource().withLevel((ServerLevel)entity.level).withPosition(entity.position()).withRotation(entity.getRotationVector()));
            }
   
            return list;
         }))).then(Commands.literal("store").then(wrapStores(literalcommandnode, Commands.literal("result"), true)).then(wrapStores(literalcommandnode, Commands.literal("success"), false))).then(Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (context) -> {
            return context.getSource().withPosition(Vec3Argument.getVec3(context, "pos")).withAnchor(EntityAnchorArgument.Anchor.FEET);
         })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (context) -> {
            List<CommandSourceStack> list = Lists.newArrayList();
   
            for(Entity entity : EntityArgument.getOptionalEntities(context, "targets")) {
               list.add(context.getSource().withPosition(entity.position()));
            }
   
            return list;
         })))).then(Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect(literalcommandnode, (context) -> {
            return context.getSource().withRotation(RotationArgument.getRotation(context, "rot").getRotation(context.getSource()));
         })).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork(literalcommandnode, (context) -> {
            List<CommandSourceStack> list = Lists.newArrayList();
   
            for(Entity entity : EntityArgument.getOptionalEntities(context, "targets")) {
               list.add(context.getSource().withRotation(entity.getRotationVector()));
            }
   
            return list;
         })))).then(Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork(literalcommandnode, (context) -> {
            List<CommandSourceStack> list = Lists.newArrayList();
            EntityAnchorArgument.Anchor entityanchorargument$anchor = EntityAnchorArgument.getAnchor(context, "anchor");
   
            for(Entity entity : EntityArgument.getOptionalEntities(context, "targets")) {
               list.add(context.getSource().facing(entity, entityanchorargument$anchor));
            }
   
            return list;
         })))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect(literalcommandnode, (context) -> {
            return context.getSource().facing(Vec3Argument.getVec3(context, "pos"));
         }))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect(literalcommandnode, (context) -> {
            return context.getSource().withPosition(context.getSource().getPosition().align(SwizzleArgument.getSwizzle(context, "axes")));
         }))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect(literalcommandnode, (context) -> {
            return context.getSource().withAnchor(EntityAnchorArgument.getAnchor(context, "anchor"));
         }))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.dimension()).redirect(literalcommandnode, (context) -> {
            return context.getSource().withLevel(DimensionArgument.getDimension(context, "dimension"));
         }))));
      }

   protected static ArgumentBuilder<CommandSourceStack, ?> wrapStores(
         LiteralCommandNode<CommandSourceStack> node, LiteralArgumentBuilder<CommandSourceStack> builder,
         boolean requestResult) {
      ArgumentBuilder<CommandSourceStack, ?> newBuilder = net.minecraft.server.commands.ExecuteCommand
            .wrapStores(node, builder, requestResult);
      newBuilder
            .then(Commands
                  .literal(
                        "player")
                  .then(Commands
                        .argument("player",
                              EntityArgument.player())
                        .then(Commands
                              .literal(
                                    "item")
                              .then(Commands
                                    .argument("slot",
                                          SlotArgument.slot())
                                    .then(Commands.argument("path", NbtPathArgument.nbtPath())
                                          .then(Commands.literal("int")
                                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                      .redirect(node, (context) -> {
                                                         return storeIntoItem(context.getSource(),
                                                               EntityArgument.getPlayer(context, "player"),
                                                               SlotArgument.getSlot(context, "slot"),
                                                               (value) -> {
                                                                  return IntTag
                                                                        .valueOf((int) ((double) value * DoubleArgumentType
                                                                              .getDouble(context, "scale")));
                                                               }, NbtPathArgument.getPath(context, "path"),
                                                               requestResult);
                                                      })))
                                          .then(Commands.literal("float")
                                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                      .redirect(node, (context) -> {
                                                         return storeIntoItem(context.getSource(),
                                                               EntityArgument.getPlayer(context, "player"),
                                                               SlotArgument.getSlot(context, "slot"),
                                                               (value) -> {
                                                                  return FloatTag
                                                                        .valueOf((float) ((double) value * DoubleArgumentType
                                                                              .getDouble(context, "scale")));
                                                               }, NbtPathArgument.getPath(context, "path"),
                                                               requestResult);
                                                      })))
                                          .then(Commands.literal("short")
                                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                      .redirect(node, (context) -> {
                                                         return storeIntoItem(context.getSource(),
                                                               EntityArgument.getPlayer(context, "player"),
                                                               SlotArgument.getSlot(context, "slot"),
                                                               (value) -> {
                                                                  return ShortTag.valueOf((short) ((int) ((double) value
                                                                        * DoubleArgumentType.getDouble(context,
                                                                              "scale"))));
                                                               }, NbtPathArgument.getPath(context, "path"),
                                                               requestResult);
                                                      })))
                                          .then(Commands.literal("long")
                                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                      .redirect(node, (context) -> {
                                                         return storeIntoItem(context.getSource(),
                                                               EntityArgument.getPlayer(context, "player"),
                                                               SlotArgument.getSlot(context, "slot"),
                                                               (value) -> {
                                                                  return LongTag.valueOf((long) ((double) value * DoubleArgumentType
                                                                              .getDouble(context, "scale")));
                                                               }, NbtPathArgument.getPath(context, "path"),
                                                               requestResult);
                                                      })))
                                          .then(Commands.literal("double")
                                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                      .redirect(node, (context) -> {
                                                         return storeIntoItem(context.getSource(),
                                                               EntityArgument.getPlayer(context, "player"),
                                                               SlotArgument.getSlot(context, "slot"),
                                                               (value) -> {
                                                                  return DoubleTag
                                                                        .valueOf((double) value * DoubleArgumentType
                                                                              .getDouble(context, "scale"));
                                                               }, NbtPathArgument.getPath(context, "path"),
                                                               requestResult);
                                                      })))
                                          .then(Commands.literal("byte")
                                                .then(Commands.argument("scale", DoubleArgumentType.doubleArg())
                                                      .redirect(node, (context) -> {
                                                         return storeIntoItem(context.getSource(),
                                                               EntityArgument.getPlayer(context, "player"),
                                                               SlotArgument.getSlot(context, "slot"),
                                                               (value) -> {
                                                                  return ByteTag.valueOf((byte) ((int) ((double) value
                                                                        * DoubleArgumentType.getDouble(context,
                                                                              "scale"))));
                                                               }, NbtPathArgument.getPath(context, "path"),
                                                               requestResult);
                                                      }))))))
                        .then(Commands.literal("motion")
                              .then(Commands.literal("x").redirect(node, (context) -> {
                                 return storeIntoXMotion(context.getSource(),
                                       EntityArgument.getPlayer(context, "player"), requestResult);
                              })).then(Commands.literal("y").redirect(node, (context) -> {
                                 return storeIntoYMotion(context.getSource(),
                                       EntityArgument.getPlayer(context, "player"), requestResult);
                              })).then(Commands.literal("z").redirect(node, (context) -> {
                                 return storeIntoZMotion(context.getSource(),
                                       EntityArgument.getPlayer(context, "player"), requestResult);
                              })))
                        .then(Commands.literal("health").redirect(node, (context) -> {
                           return storeIntoHealth(context.getSource(), EntityArgument.getPlayer(context, "player"),
                                 requestResult);
                        })).then(Commands.literal("food").redirect(node, (context) -> {
                           return storeIntoFood(context.getSource(), EntityArgument.getPlayer(context, "player"),
                                 requestResult);
                        })).then(Commands.literal("saturation").redirect(node, (context) -> {
                           return storeIntoSaturation(context.getSource(),
                                 EntityArgument.getPlayer(context, "player"), requestResult);
                        })).then(Commands.literal("exhaustion").redirect(node, (context) -> {
                           return storeIntoExhaustion(context.getSource(),
                                 EntityArgument.getPlayer(context, "player"), requestResult);
                        })).then(Commands.literal("air").redirect(node, (context) -> {
                           return storeIntoAir(context.getSource(), EntityArgument.getPlayer(context, "player"),
                                 requestResult);
                        })).then(Commands.literal("fire").redirect(node, (context) -> {
                           return storeIntoBurnTime(context.getSource(),
                                 EntityArgument.getPlayer(context, "player"), requestResult);
                        })).then(Commands.literal("freeze").redirect(node, (context) -> {
                              return storeIntoFreezeTime(context.getSource(),
                                    EntityArgument.getPlayer(context, "player"), requestResult);
                           }))
                        .then(Commands.literal("effect")
                              .then(Commands.argument("effect", MobEffectArgument.effect())
                                    .then(Commands.literal("amplifier").redirect(node, (context) -> {
                                       return storeIntoEffectAmplifier(context.getSource(),
                                             MobEffectArgument.getEffect(context, "effect"),
                                             EntityArgument.getPlayer(context, "player"), requestResult);
                                    })).then(Commands.literal("duration").redirect(node, (context) -> {
                                       return storeIntoEffectDuration(context.getSource(),
                                             MobEffectArgument.getEffect(context, "effect"),
                                             EntityArgument.getPlayer(context, "player"), requestResult);
                                    }))))));

      return builder;
   }

   private static CommandSourceStack storeIntoItem(CommandSourceStack source, ServerPlayer target, int slot,
         IntFunction<Tag> tagConverter, NbtPath path, boolean storingResult) throws CommandSyntaxException {
      return source.withCallback((context, successful, result) -> {
            try {
                  SlotAccess stackReference = target.getSlot(slot);
                  if (stackReference != SlotAccess.NULL) {
                  throw new SimpleCommandExceptionType(
                        new TranslatableComponent("commands.devsdream.execute.store.player.item.failed.no_item")).create();
                  } else {
                        ItemStack stack = stackReference.get();
                        CompoundTag tag = stack.getTag();
                        int i = storingResult ? result : (successful ? 1 : 0);
                        path.set(tag, () -> {
                              return tagConverter.apply(i);
                        });
                        stack.setTag(stack.getTag().merge(tag));
                        target.containerMenu.broadcastChanges();
                  }
            }
            catch (CommandSyntaxException e) {
            }
      });
   }

   private static CommandSourceStack storeIntoHealth(CommandSourceStack source, ServerPlayer target,
         boolean storingResult) {
      return source.withCallback((context, successful, result) -> {
         int i = storingResult ? result : (successful ? 1 : 0);
         target.setHealth((float) i);
      });
   }

   private static CommandSourceStack storeIntoFood(CommandSourceStack source, ServerPlayer target,
         boolean storingResult) {
      return source.withCallback((context, successful, result) -> {
         int i = storingResult ? result : (successful ? 1 : 0);
         target.getFoodData().setFoodLevel(i);
      });
   }

   private static CommandSourceStack storeIntoSaturation(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
      return source.withCallback((context, successful, result) -> {
          int i = storingResult ? result : (successful ? 1 : 0);
          target.getFoodData().setSaturation((float) i);
      });
  }

  private static CommandSourceStack storeIntoExhaustion(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       int i = storingResult ? result : (successful ? 1 : 0);
       target.getFoodData().addExhaustion((float) i);
   });
}

private static CommandSourceStack storeIntoAir(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       int i = storingResult ? result : (successful ? 1 : 0);
       target.setAirSupply(i);
   });
}

private static CommandSourceStack storeIntoBurnTime(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       int i = storingResult ? result : (successful ? 1 : 0);
       target.setRemainingFireTicks(i);
   });
}

private static CommandSourceStack storeIntoFreezeTime(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
      return source.withCallback((context, successful, result) -> {
          int i = storingResult ? result : (successful ? 1 : 0);
          target.setTicksFrozen(i);
      });
   }

private static CommandSourceStack storeIntoEffectAmplifier(CommandSourceStack source, MobEffect effect, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       try {
           int i = storingResult ? result : (successful ? 1 : 0);
           MobEffectInstance targetEffect = target.getEffect(effect);
           if (targetEffect == null) {
               throw new SimpleCommandExceptionType(
                   new TranslatableComponent("commands.devsdream.execute.store.player.item.failed.no_item"))
                           .create();
           } else {
               MobEffectInstance newEffect = new MobEffectInstance(targetEffect.getEffect(), targetEffect.getDuration(), i, targetEffect.isAmbient(), targetEffect.isVisible(), targetEffect.showIcon());
               newEffect.update(targetEffect);
               target.removeEffect(effect);
               target.addEffect(newEffect);
           }
       } catch (CommandSyntaxException e) {
       }
   });
}

private static CommandSourceStack storeIntoEffectDuration(CommandSourceStack source, MobEffect effect, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       try {
           int i = storingResult ? result : (successful ? 1 : 0);
           MobEffectInstance targetEffect = target.getEffect(effect);
           if (targetEffect == null) {
               throw new SimpleCommandExceptionType(
                   new TranslatableComponent("commands.devsdream.execute.store.player.item.failed.no_item"))
                           .create();
           } else {
               MobEffectInstance newEffect = new MobEffectInstance(targetEffect.getEffect(), i, targetEffect.getDuration(), targetEffect.isAmbient(), targetEffect.isVisible(), targetEffect.showIcon());
               newEffect.update(targetEffect);
               target.removeEffect(effect);
               target.addEffect(newEffect);
           }
       } catch (CommandSyntaxException e) {
       }
   });
}

private static CommandSourceStack storeIntoXMotion(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       int i = storingResult ? result : (successful ? 1 : 0);
       double motionY = target.getDeltaMovement().y;
       double motionZ = target.getDeltaMovement().z;
       target.setDeltaMovement((double) i, motionY, motionZ);
       target.hurtMarked = true;
   });
}

private static CommandSourceStack storeIntoYMotion(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       int i = storingResult ? result : (successful ? 1 : 0);
       double motionX = target.getDeltaMovement().x;
       double motionZ = target.getDeltaMovement().z;
       target.setDeltaMovement(motionX, (double) i, motionZ);
       target.hurtMarked = true;
   });
}

private static CommandSourceStack storeIntoZMotion(CommandSourceStack source, ServerPlayer target, boolean storingResult) {
   return source.withCallback((context, successful, result) -> {
       int i = storingResult ? result : (successful ? 1 : 0);
       double motionX = target.getDeltaMovement().x;
       double motionY = target.getDeltaMovement().y;
       target.setDeltaMovement(motionX, motionY, (double) i);
       target.hurtMarked = true;
   });
}
}
