package dk.sdu.cbse.collision;

import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.data.ScoreClient;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javafx.application.Platform;
import javafx.scene.text.Text;

public class CollisionSystem implements IPostEntityProcessingService {
    private static Text scoreText;
    private static int currentScore = 0;

    @Override
    public void process(GameData gameData, World world) {
        Set<String> entitiesToRemove = new HashSet<>();
        Entity[] entities = world.getEntities().toArray(new Entity[0]);

        for (int i = 0; i < entities.length; i++) {
            Entity entity1 = entities[i];
            if (entitiesToRemove.contains(entity1.getID())) continue;

            for (int j = i + 1; j < entities.length; j++) {
                Entity entity2 = entities[j];
                if (entitiesToRemove.contains(entity2.getID())) continue;

                if (this.collides(entity1, entity2)) {
                    if (isBullet(entity1) || isBullet(entity2)) {
                        entitiesToRemove.add(entity1.getID());
                        entitiesToRemove.add(entity2.getID());
                        updateScore(1);
                    }
                }
            }
        }

        entitiesToRemove.forEach(world::removeEntity);
    }

    boolean isBullet(Entity entity) {
        return entity.getRadius() <= 2;
    }

    public Boolean collides(Entity entity1, Entity entity2) {
        float dx = (float) (entity1.getX() - entity2.getX());
        float dy = (float) (entity1.getY() - entity2.getY());
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        float tolerance = 1.1f;
        return distance < (entity1.getRadius() + entity2.getRadius()) * tolerance;
    }


    public static void setScoreText(Text scoreText) {
        System.out.println("CollisionSystem: setScoreText called with: " + scoreText);
        CollisionSystem.scoreText = scoreText;
        updateScoreText(currentScore);  // Initialize the score text when it's first set
        System.out.println("CollisionSystem: Score text initialized");
    }

    public static void updateScoreText(int newScore) {
        System.out.println("CollisionSystem: updateScoreText called with score: " + newScore);
        // Update the score variable
        currentScore = newScore;


        if (scoreText == null) {
            try {
                Class<?> gameClass = Class.forName("dk.sdu.cbse.main.Game");
                java.lang.reflect.Method getScoreTextMethod = gameClass.getMethod("getScoreText");
                scoreText = (Text) getScoreTextMethod.invoke(null);
                System.out.println("CollisionSystem: Retrieved scoreText via Game.getScoreText()");
            } catch (Exception e) {
                System.out.println("CollisionSystem: Could not retrieve scoreText via Game.getScoreText(): " + e.getMessage());
            }
        }


        if (scoreText != null) {
            Platform.runLater(() -> {
                scoreText.setText("Score: " + currentScore);
                System.out.println("CollisionSystem: UI updated with score: " + currentScore);
            });
        } else {
            System.out.println("CollisionSystem: scoreText is still null, cannot update UI");
        }
    }

    // Method to update the score, including making a call to the ScoreClient
    private void updateScore(int increment) {
        try {

            ScoreClient scoreClient = new ScoreClient();
            int updatedScore = scoreClient.updateScore(increment);
            System.out.println("Updated Score: " + updatedScore);


            Platform.runLater(() -> updateScoreText(updatedScore));

        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to update score: " + e.getMessage());
        }
    }
}