package net.devsdream.crafting;

import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.core.NonNullList;

public class ShapelessNBTRecipe extends ShapelessRecipe {

   public ShapelessNBTRecipe(ResourceLocation id, String group, ItemStack output, NonNullList<Ingredient> input) {
      super(id, group, output, input);
   }

   public RecipeSerializer<?> getSerializer() {
      return Serializers.CRAFTING_SHAPELESS_NBT;
   }

   public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<RecipeSerializer<?>>
         implements RecipeSerializer<ShapelessNBTRecipe> {
      private static final ResourceLocation NAME = new ResourceLocation("minecraft", "crafting_shapeless");

      public ShapelessNBTRecipe fromJson(ResourceLocation p_44290_, JsonObject p_44291_) {
         String s = GsonHelper.getAsString(p_44291_, "group", "");
         NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(p_44291_, "ingredients"));
         if (nonnulllist.isEmpty()) {
            throw new JsonParseException("No ingredients for shapeless recipe");
         } else if (nonnulllist.size() > 9) {
            throw new JsonParseException("Too many ingredients for shapeless recipe");
         } else {
            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(p_44291_, "result"));
            return new ShapelessNBTRecipe(p_44290_, s, itemstack, nonnulllist);
         }
      }

      private static NonNullList<Ingredient> itemsFromJson(JsonArray array) {
         return ShapelessRecipe.Serializer.itemsFromJson(array);
      }

      public ShapelessNBTRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
         String s = buf.readUtf();
         int i = buf.readVarInt();
         NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

         for (int j = 0; j < nonnulllist.size(); ++j) {
            nonnulllist.set(j, Ingredient.fromNetwork(buf));
         }

         ItemStack itemstack = buf.readItem();
         return new ShapelessNBTRecipe(id, s, itemstack, nonnulllist);
      }

      public void toNetwork(FriendlyByteBuf buf, ShapelessNBTRecipe recipe) {
         new ShapelessRecipe.Serializer().toNetwork(buf, recipe);
      }
   }

}
