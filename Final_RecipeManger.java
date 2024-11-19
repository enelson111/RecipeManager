package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


class RecipeManager {
    protected List<Recipe> recipes;  //list to store recipes

    public RecipeManager() {  //constructor to initialize the recipemanager
        recipes = new ArrayList<>();  //initialize the list of recipes
    }

    public void addRecipe(Recipe recipe) {  //method to add a recipe to the manager
        recipes.add(recipe);
    }

    public List<Recipe> getRecipes() {  //method to get all recipes
        return recipes;
    }

    //method to search recipes by keyword and category
    public List<Recipe> searchRecipesByKeywordAndCategory(String keyword, String category) {
        return recipes.stream()  //stream through all recipes
                .filter(recipe -> recipe.matchesKeyword(keyword) && recipe.matchesCategory(category))  //filter by keyword and category
                .collect(Collectors.toList());  //collect and return the filtered recipes
    }
}
