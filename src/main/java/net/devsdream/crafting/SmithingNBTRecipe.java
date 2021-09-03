package net.devsdream.crafting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.devsdream.util.ChromaJsonHelper;

import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.commands.arguments.NbtPathArgument.NbtPath;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;

public class SmithingNBTRecipe extends UpgradeRecipe {

    private final Collection<NbtPath> overrides;

    public SmithingNBTRecipe(ResourceLocation id, Ingredient base, Ingredient addition, ItemStack result, Collection<NbtPath> overrides) {
        super(id, base, addition, result);
        this.overrides = overrides;
    }

    public Collection<NbtPath> getOverrides() {
        return this.overrides;
    }

    @Override
    public ItemStack assemble(Container inventory) {
        ItemStack out = super.assemble(inventory).copy();
        out.setTag(this.getResultItem().copy().getTag());
        this.getOverrides().forEach((path) -> {
            ItemStack base = inventory.getItem(0).copy();
            if (base.getTag() != null) {
                try {
                    List<Tag> source = path.get(base.getTag().copy());
                    if (!source.isEmpty()) {
                    CopyNbtFunction.MergeStrategy.REPLACE.merge(out.getOrCreateTag(), path, source);
                    }
                } catch (CommandSyntaxException e) {
                }
            }
        });
        return out;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<SmithingNBTRecipe> {
        public SmithingNBTRecipe fromJson(ResourceLocation id, JsonObject object) {
           Ingredient ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(object, "base"));
           Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getAsJsonObject(object, "addition"));
           JsonObject resultObj = GsonHelper.getAsJsonObject(object, "result");
           ItemStack itemstack = ShapedNBTRecipe.itemStackFromJson(resultObj);
           List<NbtPath> overrides = new ArrayList<NbtPath>();
           ChromaJsonHelper.getArrayOrDefault(resultObj, "overrides", new JsonArray()).forEach((element) -> {
               String override = GsonHelper.convertToString(element, "every override");
               try {
                overrides.add(new NbtPathArgument().parse(new StringReader(override)));
               } catch (CommandSyntaxException e) {
                   throw new JsonSyntaxException("Invalid NBT path: " + e.getMessage());
               }
           });
           return new SmithingNBTRecipe(id, ingredient, ingredient1, itemstack, overrides);
        }
  
        public SmithingNBTRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
           Ingredient ingredient = Ingredient.fromNetwork(buf);
           Ingredient ingredient1 = Ingredient.fromNetwork(buf);
           ItemStack itemstack = buf.readItem();
           List<NbtPath> overrides = buf.readList((buffy) -> {
                try {
                    return new NbtPathArgument().parse(new StringReader(buffy.readUtf()));
                } catch (CommandSyntaxException e) {
                    throw new JsonSyntaxException("if you see this error, you've encountered a bug. please report it ASAP to the github repo at https://github.com/ChromaKey81/devsdream-fabric");
                }
            });
           return new SmithingNBTRecipe(id, ingredient, ingredient1, itemstack, overrides);
        }
  
        public void toNetwork(FriendlyByteBuf buf, SmithingNBTRecipe recipe) {
           recipe.getIngredients().get(0).toNetwork(buf);
           recipe.getIngredients().get(1).toNetwork(buf);
           buf.writeItem(recipe.getResultItem());
           buf.writeCollection(recipe.getOverrides(), (buffy, element) -> {
               buffy.writeUtf(element.toString());
           });
        }
     }
    
}
