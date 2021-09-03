package net.devsdream.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class DamageCommand {

                private static Map<String, Boolean> damageOptions = mapDamageOptions();
                private static Map<String, Boolean> pierceOptions = mapPierceOptions();
                private static List<Object> sourceEntityThings = makeSourceEntityThings();

        static Map<String, Boolean> mapDamageOptions() {
                Map<String, Boolean> map = new HashMap<String, Boolean>();
                map.put("fire", false);
                map.put("magic", false);
                map.put("explosion", false);
                map.put("projectile", false);
                map.put("aggroless", false);
                map.put("headhurt", false);
                map.put("scaled", false);
                map.put("fall", false);
                return map;
        }
        static Map<String, Boolean> mapPierceOptions() {
                Map<String, Boolean> map = new HashMap<String, Boolean>();
                map.put("armor", false);
                map.put("invulnerability", false);
                map.put("magic", false);
                return map;
        }

        static List<Object> makeSourceEntityThings() {
                List<Object> list = new ArrayList<Object>();
                list.add(null);
                list.add(false);
                return list;
        }


        private static final SimpleCommandExceptionType DAMAGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
                        new TranslatableComponent("commands.devsdream.damage.failed"));

        public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

                

                LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("damage").requires((source) -> {
                        return source.hasPermission(2);
                });
                builder.then(Commands.literal("run").then(Commands.argument("targets", EntityArgument.entities()).then(Commands
                                .argument("amount", FloatArgumentType.floatArg(0))
                                .then(Commands.argument("sourceString", StringArgumentType.string()).executes(context -> {
                                        DamageSource damage = setDamageProperties(StringArgumentType.getString(context, "sourceString"), damageOptions.get("fire"), pierceOptions.get("armor"), damageOptions.get("magic"), pierceOptions.get("invulnerability"), damageOptions.get("explosion"), damageOptions.get("projectile"), damageOptions.get("headhurt"), !(damageOptions.get("aggroless")), pierceOptions.get("magic"), damageOptions.get("fall"), damageOptions.get("scaled"));
                                        damageOptions = mapDamageOptions();
                                        pierceOptions = mapPierceOptions();
                                        if (sourceEntityThings.get(0) == null) {
                                                sourceEntityThings = makeSourceEntityThings();
                                                return damageEntity(context.getSource(), EntityArgument.getEntities(context, "targets"), damage, FloatArgumentType.getFloat(context, "amount"));
                                        } else {
                                                EntityDamageSource entityDamage = upgradeToEntitySource((Entity) (sourceEntityThings.get(0)), (boolean) (sourceEntityThings.get(1)), damage);
                                                sourceEntityThings = makeSourceEntityThings();
                                                return damageEntity(context.getSource(), EntityArgument.getEntities(context, "targets"), entityDamage, FloatArgumentType.getFloat(context, "amount"));
                                        }
                                })))));
                LiteralCommandNode<CommandSourceStack> node = dispatcher
                .register(builder);
                                
                damageOptions.forEach((string, bool) -> {
                        builder.then(Commands.literal(string).redirect(node, (context) -> {
                                damageOptions.put(string, true);
                                return context.getSource();
                        }));
                });
                LiteralArgumentBuilder<CommandSourceStack> pierce = Commands.literal("pierce");
                pierceOptions.forEach((string, bool) -> {
                        pierce.then(Commands.literal(string).redirect(node, (context) -> {
                                pierceOptions.put(string, true);
                                return context.getSource();
                        }));
                });

                builder.then(pierce);

                builder.then(Commands.literal("from").then(Commands.argument("source", EntityArgument.entity()).then(Commands.argument("thorns", BoolArgumentType.bool()).redirect(node, (context) -> {
                        sourceEntityThings.set(0, EntityArgument.getEntity(context, "source"));
                        sourceEntityThings.set(1, BoolArgumentType.getBool(context, "thorns"));
                        return context.getSource();
                }))));
                
                dispatcher.register(builder);
        }

        private static DamageSource setDamageProperties(String sourceString, boolean isFire, boolean pierceArmor, boolean isMagic, boolean bypassInvulnerability, boolean isExplosion,
                        boolean isProjectile, boolean damageHelmet, boolean aggro, boolean pierceMagic, boolean fromFalling, 
                        boolean difficultyScaled) {

                DamageSource damage = new DamageSource(sourceString);
               

                if (isFire) {
                        damage.setIsFire();
                }

                if (pierceArmor) {
                        damage.bypassArmor();
                }

                if (damageHelmet) {
                        damage.damageHelmet();
                }

                if (difficultyScaled) {
                        damage.setScalesWithDifficulty();
                }

                if (isMagic) {
                        damage.setMagic();
                }

                if (bypassInvulnerability) {
                        damage.bypassInvul();
                }

                if (isExplosion) {
                        damage.setExplosion();
                }

                if (isProjectile) {
                        damage.setProjectile();
                }

                if (pierceMagic) {
                        damage.bypassMagic();
                }

                if (!aggro) {
                        damage.setNoAggro();
                }

                if (fromFalling) {
                        damage.setIsFall();
                }

                return damage;
        }

        private static EntityDamageSource upgradeToEntitySource(Entity sourceEntity, boolean thorns, DamageSource base) {
                EntityDamageSource damage = new EntityDamageSource(base.getMsgId(), sourceEntity);
                if (thorns) {
                        damage.setThorns();
                }
                if (damage.isFire()) {
                        damage.setIsFire();
                }
                if (damage.isBypassArmor()) {
                        damage.bypassArmor();
                }
                if (damage.isDamageHelmet()) {
                        damage.damageHelmet();
                }
                if (damage.isMagic()) {
                        damage.setMagic();
                }
                if (damage.isBypassInvul()) {
                        damage.bypassInvul();
                }
                if (damage.isExplosion()) {
                        damage.setExplosion();
                }
                if (damage.isProjectile()) {
                        damage.setProjectile();
                }
                if (damage.isBypassMagic()) {
                        damage.bypassMagic();
                }
                if (damage.isNoAggro()) {
                        damage.setNoAggro();
                }
                if (damage.isFall()) {
                        damage.setIsFall();
                }
                return damage;
        }

        private static int damageEntity(CommandSourceStack source, Collection<? extends Entity> targets,
                        DamageSource damageSource, float amount) throws CommandSyntaxException {
                List<Entity> list = Lists.newArrayListWithCapacity(targets.size());

                for (Entity entity : targets) {
                        if (entity instanceof LivingEntity) {
                                ((LivingEntity) entity).hurt(damageSource, amount);
                                list.add(entity);
                        }
                }

                if (list.isEmpty()) {
                        throw DAMAGE_FAILED_EXCEPTION.create();
                } else {
                        if (list.size() == 1) {
                                source.sendSuccess(
                                                new TranslatableComponent("commands.devsdream.damage.success.single",
                                                                list.iterator().next().getDisplayName(), amount),
                                                true);
                        } else {
                                source.sendSuccess(new TranslatableComponent(
                                                "commands.devsdream.damage.success.multiple", list.size(), amount),
                                                true);
                        }

                        return (int) amount;
                }
        }
}