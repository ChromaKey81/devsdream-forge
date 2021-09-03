package net.devsdream.crafting;

import com.google.gson.JsonObject;

import net.devsdream.util.ChromaJsonHelper;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.StonecutterRecipe;

public class StonecuttingNBTRecipe extends StonecutterRecipe {
    
    public StonecuttingNBTRecipe(ResourceLocation id, String group, Ingredient input, ItemStack output) {
       super(id, group, input, output);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return Serializers.STONECUTTING_NBT;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<StonecuttingNBTRecipe> {

      public StonecuttingNBTRecipe fromJson(ResourceLocation id, JsonObject object) {
         String s = ChromaJsonHelper.getAsString(object, "group", "");
         Ingredient ingredient;
         if (GsonHelper.isArrayNode(object, "ingredient")) {
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonArray(object, "ingredient"));
         } else {
            ingredient = Ingredient.fromJson(GsonHelper.getAsJsonObject(object, "ingredient"));
         }

         String s1 = GsonHelper.getAsString(object, "result");
         int i = GsonHelper.getAsInt(object, "count");
         ItemStack itemstack = new ItemStack(Registry.ITEM.get(new ResourceLocation(s1)), i);
         itemstack.setTag(ChromaJsonHelper.getNbt(object, "nbt"));
         return new StonecuttingNBTRecipe(id, s, ingredient, itemstack);
      }

      public StonecuttingNBTRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffy) {
         String s = buffy.readUtf();
         Ingredient ingredient = Ingredient.fromNetwork(buffy);
         ItemStack itemstack = buffy.readItem();
         return new StonecuttingNBTRecipe(id, s, ingredient, itemstack);
      }

      public void toNetwork(FriendlyByteBuf buf, StonecuttingNBTRecipe recipe) {
         buf.writeUtf(recipe.group);
         recipe.ingredient.toNetwork(buf);
         buf.writeItem(recipe.result);
      }
   }
 }