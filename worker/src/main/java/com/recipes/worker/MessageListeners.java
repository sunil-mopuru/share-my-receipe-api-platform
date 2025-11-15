package com.recipes.worker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.recipes.entities.Recipe;

@Component
public class MessageListeners {

    @RabbitListener(queues = "${recipe.queue.created}")
    public void handleRecipeCreated(Recipe recipe) {
        System.out.println("Processing created recipe: " + recipe.getTitle());
        // Process the created recipe (e.g., index for search, send notifications, etc.)
        // This is where you would implement the actual processing logic
        processRecipe(recipe);
    }

    @RabbitListener(queues = "${recipe.queue.updated}")
    public void handleRecipeUpdated(Recipe recipe) {
        System.out.println("Processing updated recipe: " + recipe.getTitle());
        // Process the updated recipe
        processRecipe(recipe);
    }

    @RabbitListener(queues = "${recipe.queue.published}")
    public void handleRecipePublished(Recipe recipe) {
        System.out.println("Processing published recipe: " + recipe.getTitle());
        // Process the published recipe (e.g., send notifications to followers)
        processRecipe(recipe);
    }

    @RabbitListener(queues = "${recipe.queue.deleted}")
    public void handleRecipeDeleted(String recipeId) {
        System.out.println("Processing deleted recipe with ID: " + recipeId);
        // Process the deleted recipe (e.g., remove from search index)
        // This is where you would implement the actual processing logic
    }

    private void processRecipe(Recipe recipe) {
        // Simulate processing time
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // In a real application, you would do actual processing here such as:
        // - Indexing the recipe for search
        // - Generating thumbnails for images
        // - Sending notifications to followers
        // - Updating analytics
        System.out.println("Finished processing recipe: " + recipe.getTitle());
    }
}