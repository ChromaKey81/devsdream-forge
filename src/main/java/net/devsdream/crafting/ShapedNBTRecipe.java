package net.devsdream.crafting;

import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.Ingredient;

import net.devsdream.util.ChromaJsonHelper;

public class ShapedNBTRecipe extends ShapedRecipe {

    public ShapedNBTRecipe(ResourceLocation id, String group, int width, int height, NonNullList<Ingredient> input,
            ItemStack output) {
        super(id, group, width, height, input, output);
    }

    public RecipeSerializer<?> getSerializer() {
        return Serializers.CRAFTING_SHAPED_NBT;
    }

    public static ItemStack outputFromJson(JsonObject json) {
      ItemStack base = ShapedRecipe.itemStackFromJson(json);
      base.setTag(ChromaJsonHelper.getNbt(json, "nbt"));
      return base;
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ShapedNBTRecipe> {
      private static final ResourceLocation NAME = new ResourceLocation("devsdream", "crafting_shaped_nbt");
      public ShapedNBTRecipe fromJson(ResourceLocation p_44236_, JsonObject p_44237_) {
         String s = GsonHelper.getAsString(p_44237_, "group", "");
         Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(p_44237_, "key"));
         String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(p_44237_, "pattern")));
         int i = astring[0].length();
         int j = astring.length;
         NonNullList<Ingredient> nonnulllist = ShapedRecipe.dissolvePattern(astring, map, i, j);
         ItemStack itemstack = ShapedNBTRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_44237_, "result"));
         return new ShapedNBTRecipe(p_44236_, s, i, j, nonnulllist, itemstack);
      }

      public ShapedNBTRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
         int i = buf.readVarInt();
         int j = buf.readVarInt();
         String s = buf.readUtf();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

         for(int k = 0; k < nonnulllist.size(); ++k) {
            nonnulllist.set(k, Ingredient.fromNetwork(buf));
         }

         ItemStack itemstack = buf.readItem();
         return new ShapedNBTRecipe(id, s, i, j, nonnulllist, itemstack);
      }

      public void toNetwork(FriendlyByteBuf buf, ShapedNBTRecipe recipe) {
         new ShapedRecipe.Serializer().toNetwork(buf, recipe);
      }
   }
    
}