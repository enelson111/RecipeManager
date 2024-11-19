package application;

import java.util.List;

//recipe class representing a single recipe with name, ingredients, instructions, and category
class Recipe {
    protected String name;  //recipe name
    protected List<String> ingredients;  //list of ingredients
    protected String instructions;  //instructions for preparing the recipe
    protected String category;  //category of the recipe (e.g., breakfast, lunch)

    public Recipe(String name, List<String> ingredients, String instructions, String category) {  //constructor to initialize recipe object
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.category = category;
    }

    public String getName() {  //getter for recipe name
        return name;
    }

    public String getCategory() {  //getter for recipe category
        return category;
    }

    public List<String> getIngredients() {  //getter for recipe ingredients
        return ingredients;
    }

    public String getInstructions() {  //getter for recipe instructions
        return instructions;
    }

    @Override
    public String toString() {  //override toString to display the recipe's name and category
        return name + " (" + category + ")";
    }

    //method to check if the recipe matches a search keyword
    public boolean matchesKeyword(String keyword) {
        return name.toLowerCase().contains(keyword.toLowerCase()) || ingredients.stream()
                .anyMatch(ingredient -> ingredient.toLowerCase().contains(keyword.toLowerCase()));  //check if keyword is in name or ingredients
    }

    //method to check if the recipe matches a selected category
    public boolean matchesCategory(String category) {
        return category.isEmpty() || this.category.equalsIgnoreCase(category);  //check if category matches or is empty
    }
}
