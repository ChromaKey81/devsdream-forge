package net.devsdream.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import java.util.Collection;
import java.util.List;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;

public class DamageCommand {
        private static final SimpleCommandExceptionType DAMAGE_FAILED_EXCEPTION = new SimpleCommandExceptionType(
                        new TranslatableComponent("commands.devsdream.damage.failed"));

        public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
                dispatcher.register(Commands.literal("damage").requires((user) -> {
                        return user.hasPermission(2);
                }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands
                                .argument("amount", FloatArgumentType.floatArg(0))
                                .then(Commands.argument("sourceString", StringArgumentType.string())
                                                                .then(Commands.argument("isFire",
                                                                                BoolArgumentType.bool())
                                                                                .executes((damage) -> {
                                                                                        return damageEntity(damage
                                                                                                        .getSource(),
                                                                                                        EntityArgument.getEntities(
                                                                                                                        damage,
                                                                                                                        "targets"),
                                                                                                        setDamageProperties(
                                                                                                                        StringArgumentType
                                                                                                                                        .getString(damage,
                                                                                                                                                        "sourceString"),
                                                                                                                        BoolArgumentType.getBool(
                                                                                                                                        damage,
                                                                                                                                        "isFire"),
                                                                                                                        false, false, false, false, false, false, false, false, false, false, null, false),
                                                                                                        FloatArgumentType
                                                                                                                        .getFloat(damage,
                                                                                                                                        "amount"));
                                                                                })
                                                                                .then(Commands.argument("pierceArmor",
                                                                                                BoolArgumentType.bool())
                                                                                                .executes((damage) -> {
                                                                                                        return damageEntity(
                                                                                                                        damage.getSource(),
                                                                                                                        EntityArgument.getEntities(
                                                                                                                                        damage,
                                                                                                                                        "targets"),
                                                                                                                        setDamageProperties(
                                                                                                                                StringArgumentType
                                                                                                                                                .getString(damage,
                                                                                                                                                                "sourceString"),
                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                damage,
                                                                                                                                                "isFire"),
                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), false, false, false, false, false, false, false, false, false, null, false),
                                                                                                                        FloatArgumentType
                                                                                                                                        .getFloat(damage,
                                                                                                                                                        "amount"));
                                                                                                }).then(Commands.argument(
                                                                                                                                "isMagic",
                                                                                                                                BoolArgumentType.bool())
                                                                                                                                .executes((damage) -> {
                                                                                                                                        return damageEntity(
                                                                                                                                                        damage.getSource(),
                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                        damage,
                                                                                                                                                                        "targets"),
                                                                                                                                                        setDamageProperties(
                                                                                                                                                                StringArgumentType
                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                damage,
                                                                                                                                                                                "isFire"),
                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), false, false, false, false, false, false, false, false, null, false),
                                                                                                                                                        FloatArgumentType
                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                        "amount"));
                                                                                                                                })
                                                                                                                                .then(Commands.argument(
                                                                                                                                                "bypassInvulnerability",
                                                                                                                                                BoolArgumentType.bool())
                                                                                                                                                .executes((damage) -> {
                                                                                                                                                        return damageEntity(
                                                                                                                                                                        damage.getSource(),
                                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                                        damage,
                                                                                                                                                                                        "targets"),
                                                                                                                                                                        setDamageProperties(
                                                                                                                                                                                StringArgumentType
                                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                                damage,
                                                                                                                                                                                                "isFire"),
                                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), false, false, false, false, false, false, false, null, false),
                                                                                                                                                                        FloatArgumentType
                                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                                        "amount"));
                                                                                                                                                })
                                                                                                                                                .then(Commands.argument(
                                                                                                                                                                "isExplosion",
                                                                                                                                                                BoolArgumentType.bool())
                                                                                                                                                                .executes((damage) -> {
                                                                                                                                                                        return damageEntity(
                                                                                                                                                                                        damage.getSource(),
                                                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                                                        damage,
                                                                                                                                                                                                        "targets"),
                                                                                                                                                                                        setDamageProperties(
                                                                                                                                                                                                StringArgumentType
                                                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                "isFire"),
                                                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), false, false, false, false, false, false, null, false),
                                                                                                                                                                                        FloatArgumentType
                                                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                                                        "amount"));
                                                                                                                                                                })
                                                                                                                                                                .then(Commands.argument(
                                                                                                                                                                                "isProjectile",
                                                                                                                                                                                BoolArgumentType.bool())
                                                                                                                                                                                .executes((damage) -> {
                                                                                                                                                                                        return damageEntity(
                                                                                                                                                                                                        damage.getSource(),
                                                                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                        "targets"),
                                                                                                                                                                                                        setDamageProperties(
                                                                                                                                                                                                                StringArgumentType
                                                                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                "isFire"),
                                                                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), false, false, false, false, false, null, false),
                                                                                                                                                                                                        FloatArgumentType
                                                                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                                                                        "amount"));
                                                                                                                                                                                })
                                                                                                                                                                                .then(Commands.argument(
                                                                                                                                                                                                "damageHelmet",
                                                                                                                                                                                                BoolArgumentType.bool())
                                                                                                                                                                                                .executes((damage) -> {
                                                                                                                                                                                                        return damageEntity(
                                                                                                                                                                                                                        damage.getSource(),
                                                                                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                                        "targets"),
                                                                                                                                                                                                                        setDamageProperties(
                                                                                                                                                                                                                                StringArgumentType
                                                                                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                                "isFire"),
                                                                                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), BoolArgumentType.getBool(damage, "damageHelmet"), false, false, false, false, null, false),
                                                                                                                                                                                                                        FloatArgumentType
                                                                                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                                                                                        "amount"));
                                                                                                                                                                                                })
                                                                                                                                                                                                .then(Commands.argument(
                                                                                                                                                                                                                "aggro",
                                                                                                                                                                                                                BoolArgumentType.bool())
                                                                                                                                                                                                                .executes((damage) -> {
                                                                                                                                                                                                                        return damageEntity(
                                                                                                                                                                                                                                        damage.getSource(),
                                                                                                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                                                        "targets"),
                                                                                                                                                                                                                                        setDamageProperties(
                                                                                                                                                                                                                                                StringArgumentType
                                                                                                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                                                "isFire"),
                                                                                                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), BoolArgumentType.getBool(damage, "damageHelmet"), BoolArgumentType.getBool(damage, "aggro"), false, false, false, null, false),
                                                                                                                                                                                                                                        FloatArgumentType
                                                                                                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                                                                                                        "amount"));
                                                                                                                                                                                                                }).then(Commands.argument(
                                                                                                                                                                                                                        "bypassMagic",
                                                                                                                                                                                                                        BoolArgumentType.bool())
                                                                                                                                                                                                                        .executes((damage) -> {
                                                                                                                                                                                                                                return damageEntity(
                                                                                                                                                                                                                                                damage.getSource(),
                                                                                                                                                                                                                                                EntityArgument.getEntities(
                                                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                                                "targets"),
                                                                                                                                                                                                                                                setDamageProperties(
                                                                                                                                                                                                                                                        StringArgumentType
                                                                                                                                                                                                                                                                        .getString(damage,
                                                                                                                                                                                                                                                                                        "sourceString"),
                                                                                                                                                                                                                                                        BoolArgumentType.getBool(
                                                                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                                                                        "isFire"),
                                                                                                                                                                                                                                                        BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), BoolArgumentType.getBool(damage, "damageHelmet"), BoolArgumentType.getBool(damage, "aggro"), BoolArgumentType.getBool(damage, "bypassMagic"), false, false, null, false),
                                                                                                                                                                                                                                                FloatArgumentType
                                                                                                                                                                                                                                                                .getFloat(damage,
                                                                                                                                                                                                                                                                                "amount"));
                                                                                                                                                                                                                        }).then(Commands.argument(
                                                                                                                                                                                                                                "fromFalling",
                                                                                                                                                                                                                                BoolArgumentType.bool())
                                                                                                                                                                                                                                .executes((damage) -> {
                                                                                                                                                                                                                                        return damageEntity(
                                                                                                                                                                                                                                                        damage.getSource(),
                                                                                                                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                                                                        "targets"),
                                                                                                                                                                                                                                                        setDamageProperties(
                                                                                                                                                                                                                                                                StringArgumentType
                                                                                                                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                                                                "isFire"),
                                                                                                                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), BoolArgumentType.getBool(damage, "damageHelmet"), BoolArgumentType.getBool(damage, "aggro"), BoolArgumentType.getBool(damage, "bypassMagic"), BoolArgumentType.getBool(damage, "fromFalling"), false, null, false),
                                                                                                                                                                                                                                                        FloatArgumentType
                                                                                                                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                                                                                                                        "amount"));
                                                                                                                                                                                                                                }).then(Commands.argument(
                                                                                                                                                                                                                                        "difficultyScaled",
                                                                                                                                                                                                                                        BoolArgumentType.bool())
                                                                                                                                                                                                                                        .executes((damage) -> {
                                                                                                                                                                                                                                                return damageEntity(
                                                                                                                                                                                                                                                                damage.getSource(),
                                                                                                                                                                                                                                                                EntityArgument.getEntities(
                                                                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                                                                "targets"),
                                                                                                                                                                                                                                                                setDamageProperties(
                                                                                                                                                                                                                                                                        StringArgumentType
                                                                                                                                                                                                                                                                                        .getString(damage,
                                                                                                                                                                                                                                                                                                        "sourceString"),
                                                                                                                                                                                                                                                                        BoolArgumentType.getBool(
                                                                                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                                                                                        "isFire"),
                                                                                                                                                                                                                                                                        BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), BoolArgumentType.getBool(damage, "damageHelmet"), BoolArgumentType.getBool(damage, "aggro"), BoolArgumentType.getBool(damage, "bypassMagic"), BoolArgumentType.getBool(damage, "fromFalling"), BoolArgumentType.getBool(damage, "difficultyScaled"), null, false),
                                                                                                                                                                                                                                                                FloatArgumentType
                                                                                                                                                                                                                                                                                .getFloat(damage,
                                                                                                                                                                                                                                                                                                "amount"));
                                                                                                                                                                                                                                        })).then(Commands.argument(
                                                                                                                                                                                                                                                "sourceEntity",
                                                                                                                                                                                                                                                BoolArgumentType.bool())
                                                                                                                                                                                                                                                .executes((damage) -> {
                                                                                                                                                                                                                                                        return damageEntity(
                                                                                                                                                                                                                                                                        damage.getSource(),
                                                                                                                                                                                                                                                                        EntityArgument.getEntities(
                                                                                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                                                                                        "targets"),
                                                                                                                                                                                                                                                                        setDamageProperties(
                                                                                                                                                                                                                                                                                StringArgumentType
                                                                                                                                                                                                                                                                                                .getString(damage,
                                                                                                                                                                                                                                                                                                                "sourceString"),
                                                                                                                                                                                                                                                                                BoolArgumentType.getBool(
                                                                                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                                                                                "isFire"),
                                                                                                                                                                                                                                                                                BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), BoolArgumentType.getBool(damage, "damageHelmet"), BoolArgumentType.getBool(damage, "aggro"), BoolArgumentType.getBool(damage, "bypassMagic"), BoolArgumentType.getBool(damage, "fromFalling"), false, EntityArgument.getEntity(damage, "sourceEntity"), false),
                                                                                                                                                                                                                                                                        FloatArgumentType
                                                                                                                                                                                                                                                                                        .getFloat(damage,
                                                                                                                                                                                                                                                                                                        "amount"));
                                                                                                                                                                                                                                                }).then(Commands.argument(
                                                                                                                                                                                                                                                        "thorns",
                                                                                                                                                                                                                                                        BoolArgumentType.bool())
                                                                                                                                                                                                                                                        .executes((damage) -> {
                                                                                                                                                                                                                                                                return damageEntity(
                                                                                                                                                                                                                                                                                damage.getSource(),
                                                                                                                                                                                                                                                                                EntityArgument.getEntities(
                                                                                                                                                                                                                                                                                                damage,
                                                                                                                                                                                                                                                                                                "targets"),
                                                                                                                                                                                                                                                                                setDamageProperties(
                                                                                                                                                                                                                                                                                        StringArgumentType
                                                                                                                                                                                                                                                                                                        .getString(damage,
                                                                                                                                                                                                                                                                                                                        "sourceString"),
                                                                                                                                                                                                                                                                                        BoolArgumentType.getBool(
                                                                                                                                                                                                                                                                                                        damage,
                                                                                                                                                                                                                                                                                                        "isFire"),
                                                                                                                                                                                                                                                                                        BoolArgumentType.getBool(damage, "pierceArmor"), BoolArgumentType.getBool(damage, "isMagic"), BoolArgumentType.getBool(damage, "bypassInvulnerability"), BoolArgumentType.getBool(damage, "isExplosion"), BoolArgumentType.getBool(damage, "isProjectile"), BoolArgumentType.getBool(damage, "damageHelmet"), BoolArgumentType.getBool(damage, "aggro"), BoolArgumentType.getBool(damage, "bypassMagic"), BoolArgumentType.getBool(damage, "fromFalling"), false, EntityArgument.getEntity(damage, "sourceEntity"), BoolArgumentType.getBool(damage, "thorns")),
                                                                                                                                                                                                                                                                                FloatArgumentType
                                                                                                                                                                                                                                                                                                .getFloat(damage,
                                                                                                                                                                                                                                                                                                                "amount"));
                                                                                                                                                                                                                                                        })))))))))))))))));
        }

        private static DamageSource setDamageProperties(String sourceString, boolean isFire, boolean pierceArmor, boolean isMagic, boolean bypassInvulnerability, boolean isExplosion,
                        boolean isProjectile, boolean damageHelmet, boolean aggro, boolean pierceMagic, boolean fromFalling, 
                        boolean difficultyScaled, @Nullable Entity sourceEntity, boolean thorns) {

                DamageSource damage;

                if (sourceEntity == null) {
                        damage = new EntityDamageSource(sourceString, sourceEntity);
                } else {
                        damage = new DamageSource(sourceString);
                }
               

                if (isFire == true) {
                        damage.setIsFire();
                }

                if (pierceArmor == true) {
                        damage.bypassArmor();
                }

                if (damageHelmet == true) {
                        damage.damageHelmet();
                }

                if (difficultyScaled == true) {
                        damage.setScalesWithDifficulty();
                }

                if (isMagic == true) {
                        damage.setMagic();
                }

                if (bypassInvulnerability == true) {
                        damage.bypassInvul();
                }

                if (isExplosion == true) {
                        damage.setExplosion();
                }

                if (isProjectile == true) {
                        damage.setProjectile();
                }

                if (pierceMagic == true) {
                        damage.bypassMagic();
                }

                if (thorns == true) {
                        ((EntityDamageSource)damage).setThorns();
                }

                if (aggro == false) {
                        damage.setNoAggro();
                }

                if (fromFalling == true) {
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