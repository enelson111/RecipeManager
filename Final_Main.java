package application;  
//import needed packages
import javafx.application.Application; 
import javafx.beans.value.ObservableValue; 
import javafx.collections.FXCollections;  
import javafx.collections.ObservableList; 
import javafx.scene.Scene;  
import javafx.scene.control.*; 
import javafx.scene.layout.*;  
import javafx.stage.Stage;  
import java.io.*;  
import java.util.*; 



public class Main extends Application {  
    private static final String FILE_PATH = "C:\\Users\\Aidan\\Downloads\\recipes1.txt";  //path to the csv file storing recipes
    private RecipeManager recipeManager = new RecipeManager();  //initialize recipemanager 
    private ObservableList<Recipe> recipeList = FXCollections.observableArrayList();  //observable list to hold recipes
    private ListView<Recipe> recipeListView = new ListView<>(recipeList);  //listview used to display recipes
    private TextArea recipeDetailsArea = new TextArea();  //textarea to display selected recipe details
    private TextField searchField = new TextField();  //textfield to enter search keywords
    private ComboBox<String> categoryComboBox = new ComboBox<>();  //combobox to select recipe category

    public static void main(String[] args) {  
        launch(args);  //launch javafx
    }

    @Override
    public void start(Stage primaryStage) {  
        primaryStage.setTitle("Recipe Manager");  //set the window title

        loadRecipesFromFile();  //load recipes from the csv file

        // create layout for the recipe manager
        VBox mainLayout = new VBox(10);  //vbox for vertical layout 
        HBox searchBox = new HBox(10);  //hbox for horizontal layout of the search
        HBox inputBox = new HBox(20);  //hbox for horizontal layout of input fields for new recipe
        inputBox.setSpacing(5);  //set spacing between the input boxes

        //setup recipe list view
        recipeListView.setPrefHeight(200);  //set height 
        //add listener to handle recipe selection and show its details
        recipeListView.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Recipe> observable, Recipe oldValue, Recipe newValue) -> {
            if (newValue != null) {  //show details of selected recipe
                showRecipeDetails(newValue);
            }
        }
        );

        //set up textarea to display the recipe details
        recipeDetailsArea.setEditable(false); //make the textarea non-editable
        recipeDetailsArea.setPrefHeight(200); //set height

        searchBox.getChildren().addAll(new Label("Search: "), searchField);  //add a label and search field

        //add the labels for the combo box
        categoryComboBox.setItems(FXCollections.observableArrayList("All", "Breakfast", "Lunch", "Dinner", "Dessert", "Snack"));
        categoryComboBox.getSelectionModel().selectFirst();  //default select to all
        
        //add search button to search the recipes
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchRecipe());  //perform the search on click
        searchBox.getChildren().addAll(categoryComboBox, searchButton);  //add combobox and button to searchbox

        //set up input fields for adding new recipes
        TextField nameField = new TextField();  //field for entering recipe name
        nameField.setPromptText("Recipe Name");  //set prompt text for name field
        TextField categoryField = new TextField();  //field for entering category
        categoryField.setPromptText("Category (e.g. Breakfast)");  //set prompt text for category field
        TextField ingredientsField = new TextField();  //field for entering ingredients
        ingredientsField.setPromptText("Ingredients (separate by a comma)");  //set prompt text for ingredients field
        ingredientsField.setPrefHeight(200); // set height
        ingredientsField.setPrefWidth(300); //set width
        TextArea instructionsArea = new TextArea();  //textarea for entering recipe instructions
        instructionsArea.setPromptText("Instructions");  //set prompt text for instructions area
        instructionsArea.setPrefHeight(200);//set height
        instructionsArea.setPrefWidth(300); //set width

        //button to add a new recipe
        Button addButton = new Button("Add Recipe");
        addButton.setOnAction(e -> addRecipe(nameField.getText(), categoryField.getText(), ingredientsField.getText(), instructionsArea.getText()));

        //add input fields and button
        inputBox.getChildren().addAll( nameField, categoryField, ingredientsField, instructionsArea, addButton);

        //button to list all recipes
        Button listButton = new Button("List All Recipes");
        listButton.setOnAction(e -> listRecipes());  //list all recipes when button is pressed

        //add all ui components to the main layout
        mainLayout.getChildren().addAll(searchBox, recipeListView, recipeDetailsArea, inputBox, listButton);

        //create a scene and set it to the primarystage
        Scene scene = new Scene(mainLayout, 1050, 600);  //define scene size
        primaryStage.setScene(scene);  //set the scene for the stage
        primaryStage.show();  //show the window
    }

    private void loadRecipesFromFile() {  //method to load recipes from a csv file
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {  //open a bufferedreader to read the file
            String line;
            br.readLine();//skip title line
            
            while ((line = br.readLine()) != null) {  //read each line in the file
                String[] data = line.split("\t");  //split the line by tab character
                if (data.length >= 4) {  //if the line has sufficient data (category, name, ingredients, instructions)
                    List<String> ingredients = Arrays.asList(data[2].split(","));  //split ingredients by comma
                    Recipe recipe = new Recipe(data[1], ingredients, data[3], data[0]);  //create a new recipe object. order = recipeName, ingredients, steps, recipeType
                    recipeManager.addRecipe(recipe);  //add the recipe to the recipemanager
                }
            }
        } catch (IOException e) {  //handle any ioexception that may occur
            e.printStackTrace();
        }
    }

    private void searchRecipe() {  //method to search for recipes
        String keyword = searchField.getText();  //get the search keyword from the search field
        String category = categoryComboBox.getSelectionModel().getSelectedItem();  //get the selected category from the combobox

        //search for recipes by keyword and category
        //"category.equals("All")=> if the category is "all" the field is left blank
        List<Recipe> searchResults = recipeManager.searchRecipesByKeywordAndCategory(keyword, category.equals("All") ? "" : category); 

        if (searchResults.isEmpty()) {  //show an alert if no results are found
            showAlert("No recipes found for the search criteria");
        } else {
            recipeList.setAll(searchResults);  //update the recipe list with search results
        }
    }

    private void addRecipe(String name, String category, String ingredientsText, String instructions) {  //method to add a new recipe
        if (name.isEmpty() || category.isEmpty() || ingredientsText.isEmpty() || instructions.isEmpty()) {  //check if all fields are filled
            showAlert("All fields must be filled out");  //show an alert if any field is empty
            return;
        }

        List<String> ingredients = Arrays.asList(ingredientsText.split(","));  //split ingredients by commas
        Recipe newRecipe = new Recipe(name, ingredients, instructions, category);  //create a new recipe object
        recipeManager.addRecipe(newRecipe);  //add the new recipe to the recipemanager
        recipeList.add(newRecipe);  //add the new recipe to the observable recipe list
        appendRecipeToFile(newRecipe);  //write the new recipe to the csv file
        clearInputFields();  //clear input fields after adding the recipe
        showAlert("Recipe added successfully!");  //show a success alert
    }

    private void appendRecipeToFile(Recipe recipe) {  //method to write a new recipe to the file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {  //open a bufferedwriter in append mode
            String ingredients = String.join(",", recipe.getIngredients());  //join ingredients with commas
            String line = recipe.getCategory() + "\t" + recipe.getName() + "\t" + ingredients + "\t" + recipe.getInstructions();  //prepare the line to be written
            bw.write(line);  //write the line to the file
            bw.newLine();  //add a new line after the entry
        } catch (IOException e) {  //handle any ioexception that may occur
            e.printStackTrace();
        }
    }

    private void showRecipeDetails(Recipe recipe) {  //method to display recipe details in the textarea
        String details = "Name: " + recipe.getName() + "\nCategory: " + recipe.getCategory() + "\nIngredients: " + String.join(", ", recipe.getIngredients()) + "\n\nInstructions: \n" + recipe.getInstructions();
        recipeDetailsArea.setText(details);  //set the recipe details in the textarea
    }

    private void listRecipes() {  //method to list all recipes
        recipeList.clear();  //clear the current list of recipes
        recipeList.addAll(recipeManager.getRecipes());  //add all recipes from recipemanager to the list
    }

    private void showAlert(String message) {  //method to show an informational alert with a message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);  //create a new alert of type INFORMATION
        alert.setContentText(message);  //set the message in the alert
        alert.showAndWait();  //display the alert and wait for the user to close it
    }

    private void clearInputFields() {  //method to clear the input fields
        searchField.clear();  //clear the search field
    }


}
