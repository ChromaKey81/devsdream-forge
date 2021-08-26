package net.devsdream.util;

import java.util.Map;
import java.util.NoSuchElementException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.world.item.Item;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ChromaJsonHelper extends GsonHelper {

    /**
     * 
     * Reimplementing these methods with better names for more readability and easier access.
     * 
     */
    public static float getFloatOrDefault(JsonObject object, String element, float defaultValue) throws JsonSyntaxException {
        return GsonHelper.getAsFloat(object, element, defaultValue);
    }
    public static int getIntOrDefault(JsonObject object, String element, int defaultValue) throws JsonSyntaxException {
        return GsonHelper.getAsInt(object, element, defaultValue);
    }
    public static long getLongOrDefault(JsonObject object, String element, long defaultValue) throws JsonSyntaxException {
        return GsonHelper.getAsLong(object, element, defaultValue);
    }
    public static String getStringOrDefault(JsonObject object, String element, String defaultValue) throws JsonSyntaxException {
        return GsonHelper.getAsString(object, element, defaultValue);
    }
    public static boolean getBooleanOrDefault(JsonObject object, String element, boolean defaultValue) throws JsonSyntaxException {
        return GsonHelper.getAsBoolean(object, element, defaultValue);
    }
    public static JsonArray getArrayOrDefault(JsonObject object, String element, JsonArray defaultValue) throws JsonSyntaxException {
        return GsonHelper.getAsJsonArray(object, element, defaultValue);
    }
    public static Item getItemOrDefault(JsonObject object, String element, Item defaultValue) throws JsonSyntaxException {
        return GsonHelper.getAsItem(object, element, defaultValue);
    }

    /**
     * 
     * New methods for similar functionality to {@link GsonHelper#asItem(JsonElement, String)} and {@link GsonHelper#getItem(JsonObject, String)}
     * 
     */
    public static Block asBlock(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (Block)Registry.BLOCK.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be a block, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a block, was " + getType(element));
         }
    }
    public static Block getBlock(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asBlock(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a block");
         }
    }
    public static Block getBlockOrDefault(JsonObject object, String key, Block defaultBlock) throws JsonSyntaxException {
        return object.has(key) ? asBlock(object.get(key), key) : defaultBlock;
    }

    public static SoundEvent asSound(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (SoundEvent)Registry.SOUND_EVENT.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be a sound event, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a sound event, was " + getType(element));
         }
    }
    public static SoundEvent getSound(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asSound(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a sound event");
         }
    }
    public static SoundEvent getSoundOrDefault(JsonObject object, String key, SoundEvent defaultSound) throws JsonSyntaxException {
        return object.has(key) ? asSound(object.get(key), key) : defaultSound;
    }

    public static EntityType<?> asEntity(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (EntityType<?>)Registry.ENTITY_TYPE.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be an entity type, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be an entity type, was " + getType(element));
         }
    }
    public static EntityType<?> getEntity(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asEntity(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find an entity type");
         }
    }
    public static EntityType<?> getEntityOrDefault(JsonObject object, String key, EntityType<?> defaultEntity) throws JsonSyntaxException {
        return object.has(key) ? asEntity(object.get(key), key) : defaultEntity;
    }

    public static DyeColor asDyeColor(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            DyeColor color = DyeColor.byName(string, null);
            if (color == null) {
                throw new JsonSyntaxException("Expected " + name + " to be a dye color, was unknown string '" + "'");
            } else {
                return color;
            }
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a dye color, was " + getType(element));
         }
    }
    public static DyeColor getDyeColor(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asDyeColor(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find an entity type");
         }
    }
    public static DyeColor getDyeColorOrDefault(JsonObject object, String key, DyeColor defaultColor) throws JsonSyntaxException {
        return object.has(key) ? asDyeColor(object.get(key), key) : defaultColor;
    }

    
    public static BlockEntityType<?> asBlockEntity(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (BlockEntityType<?>)Registry.BLOCK_ENTITY_TYPE.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be a block entity type, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a block entity type, was " + getType(element));
         }
    }
    public static BlockEntityType<?> getBlockEntity(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asBlockEntity(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a block entity type");
         }
    }
    public static BlockEntityType<?> getBlockEntityOrDefault(JsonObject object, String key, BlockEntityType<?> defaultBlockEntity) throws JsonSyntaxException {
        return object.has(key) ? asBlockEntity(object.get(key), key) : defaultBlockEntity;
    }

    public static MobEffect asEffect(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (MobEffect)Registry.MOB_EFFECT.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be a status effect, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a status effect, was " + getType(element));
         }
    }
    public static MobEffect getEffect(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asEffect(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a status effect");
         }
    }
    public static MobEffect getEffectOrDefault(JsonObject object, String key, MobEffect defaultEffect) throws JsonSyntaxException {
        return object.has(key) ? asEffect(object.get(key), key) : defaultEffect;
    }

    public static MobEffectInstance effectInstance(JsonObject statusEffect) {
        return new MobEffectInstance(ChromaJsonHelper.getEffect(statusEffect, "type"),
                ChromaJsonHelper.getIntOrDefault(statusEffect, "duration", 0),
                ChromaJsonHelper.getIntOrDefault(statusEffect, "amplifier", 0),
                ChromaJsonHelper.getBooleanOrDefault(statusEffect, "ambient", false),
                ChromaJsonHelper.getBooleanOrDefault(statusEffect, "show_particles", true),
                ChromaJsonHelper.getBooleanOrDefault(statusEffect, "show_icon", true));
    }

    public static <T, S> T getFromMapSafe(S key, String propertyName, Map<S, T> map) throws JsonSyntaxException {
        T result = map.get(key);
        if (result == null) {
            throw new JsonSyntaxException("Unknown " + propertyName + " '" + key + "'");
        } else {
            return result;
        }
    }

    public static Fluid asFluid(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (Fluid)Registry.FLUID.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be a fluid, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a fluid, was " + getType(element));
         }
    }
    public static Fluid getFluid(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asFluid(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a fluid");
         }
    }
    public static Fluid getFluidOrDefault(JsonObject object, String key, Fluid defaultFluid) throws JsonSyntaxException {
        return object.has(key) ? asFluid(object.get(key), key) : defaultFluid;
    }

    public static Feature<? extends FeatureConfiguration> asFeature(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (Feature<? extends FeatureConfiguration>)Registry.FEATURE.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be a feature, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a feature, was " + getType(element));
         }
    }
    public static Feature<? extends FeatureConfiguration> getFeature(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asFeature(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a feature");
         }
    }
    public static Feature<? extends FeatureConfiguration> getFeatureOrDefault(JsonObject object, String key, Feature<?> defaultFeature) throws JsonSyntaxException {
        return object.has(key) ? asFeature(object.get(key), key) : defaultFeature;
    }

    public static CompoundTag getNbt(JsonObject object, String key) throws JsonSyntaxException {
        try {
            CompoundTag nbtCompound = TagParser.parseTag(GsonHelper.getAsString(object, key));
            return nbtCompound;
         } catch (CommandSyntaxException e) {
            throw new JsonSyntaxException(e.getMessage());
         }
    }

    public static ParticleType<?> asParticleType(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            return (ParticleType<?>)Registry.PARTICLE_TYPE.getOptional(new ResourceLocation(string)).orElseThrow(() -> {
               return new JsonSyntaxException("Expected " + name + " to be a particle type, was unknown string '" + string + "'");
            });
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a particle type, was " + getType(element));
         }
    }
    public static ParticleType<?> getParticleType(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asParticleType(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find a particle type");
         }
    }
    public static ParticleType<?> getParticleTypeOrDefault(JsonObject object, String key, ParticleType<?> defaultParticleType) throws JsonSyntaxException {
        return object.has(key) ? asParticleType(object.get(key), key) : defaultParticleType;
    }

    public static EquipmentSlot asEquipmentSlot(JsonElement element, String name) throws JsonSyntaxException {
        if (element.isJsonPrimitive()) {
            String string = element.getAsString();
            EquipmentSlot slot = EquipmentSlot.byName(string);
            if (slot == null) {
                throw new JsonSyntaxException("Expected " + name + " to be an equipment slot, was unknown string '" + "'");
            } else {
                return slot;
            }
         } else {
            throw new JsonSyntaxException("Expected " + name + " to be a equipment slot, was " + getType(element));
         }
    }
    public static EquipmentSlot getEquipmentSlot(JsonObject object, String key) throws JsonSyntaxException {
        if (object.has(key)) {
            return asEquipmentSlot(object.get(key), key);
         } else {
            throw new JsonSyntaxException("Missing " + key + ", expected to find an equipment slot");
         }
    }
    public static EquipmentSlot getEquipmentSlotOrDefault(JsonObject object, String key, EquipmentSlot defaultSlot) throws JsonSyntaxException {
        return object.has(key) ? asEquipmentSlot(object.get(key), key) : defaultSlot;
    }

    /**
     * methods to get datapack-related things from JSON, these should only be used on the logical server
     */
    public class ServerOnly {
        
        public static CommandFunction getFunction(JsonObject object, Level world, String key) {
            try {
                ServerFunctionManager manager = world.getServer().getFunctions();
                return manager.get(new ResourceLocation(GsonHelper.getAsString(object, key))).get();
            } catch (NoSuchElementException e) {
                return null;
            }
        }

        public static void getAndExecuteFunction(JsonObject object, Level world, String key, CommandSourceStack source) throws NoSuchElementException {
            ServerFunctionManager manager = world.getServer().getFunctions();
            source.withSuppressedOutput();
            manager.execute(manager.get(new ResourceLocation(GsonHelper.getAsString(object, key))).get(), source);
        }

    }
}
