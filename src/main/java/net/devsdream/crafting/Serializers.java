package net.devsdream.crafting;

import net.minecraft.world.item.crafting.RecipeSerializer;

public class Serializers {
    public static RecipeSerializer<ShapedNBTRecipe> CRAFTING_SHAPED_NBT = new ShapedNBTRecipe.Serializer();
    public static RecipeSerializer<ShapelessNBTRecipe> CRAFTING_SHAPELESS_NBT = new ShapelessNBTRecipe.Serializer();
    public static RecipeSerializer<StonecuttingNBTRecipe> STONECUTTING_NBT = new StonecuttingNBTRecipe.Serializer();
    public static RecipeSerializer<SmithingNBTRecipe> SMITHING_NBT = new SmithingNBTRecipe.Serializer();
}
