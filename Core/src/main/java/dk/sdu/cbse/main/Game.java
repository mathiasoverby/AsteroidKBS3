package dk.sdu.cbse.main;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.GameKeys;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import dk.sdu.cbse.common.data.ScoreClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class Game {
    private final GameData gameData;
    private final World world;
    private final Map<Entity, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();
    private final List<IGamePluginService> gamePlugins;
    private final List<IEntityProcessingService> entityProcessors;
    private final List<IPostEntityProcessingService> postProcessors;


    private Text scoreText;
    private int currentScore = 0;

    public Game(GameData gameData, World world,
                List<IGamePluginService> gamePlugins,
                List<IEntityProcessingService> entityProcessors,
                List<IPostEntityProcessingService> postProcessors) {
        this.gameData = gameData;
        this.world = world;
        this.gamePlugins = gamePlugins;
        this.entityProcessors = entityProcessors;
        this.postProcessors = postProcessors;
    }

    public void start(Stage window) {
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.RED);
        scoreText.setTranslateX(10);
        scoreText.setTranslateY(20);
        gameWindow.getChildren().add(scoreText);


        scoreTextReference = scoreText;


        connectScoreTextToSystems();

        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());

        Scene scene = new Scene(gameWindow);
        setupKeyListeners(scene);

        gamePlugins.forEach(plugin -> plugin.start(gameData, world));

        world.getEntities().forEach(entity -> {
            if (entity.getPolygonCoordinates() != null) {
                Polygon polygon = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity, polygon);
                gameWindow.getChildren().add(polygon);
            }
        });

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }


    public static Text getScoreText() {
        return scoreTextReference;
    }

    private static Text scoreTextReference = null;


    private void connectScoreTextToSystems() {

        try {
            Class<?> collisionSystemClass = Class.forName("dk.sdu.cbse.collision.CollisionSystem");
            java.lang.reflect.Method setScoreTextMethod = collisionSystemClass.getMethod("setScoreText", Text.class);
            setScoreTextMethod.invoke(null, scoreText);
            System.out.println("Connected score text to CollisionSystem via direct class loading");
        } catch (Exception e) {
            System.out.println("Direct class loading failed: " + e.getMessage());

            // Fallback: Try to find CollisionSystem instances in postProcessors
            System.out.println("Trying to find CollisionSystem in postProcessors...");
            for (IPostEntityProcessingService processor : postProcessors) {
                System.out.println("Found processor: " + processor.getClass().getName());
                if (processor.getClass().getName().contains("CollisionSystem")) {
                    try {
                        // Get the setScoreText method and invoke it
                        java.lang.reflect.Method setScoreTextMethod = processor.getClass().getMethod("setScoreText", Text.class);
                        setScoreTextMethod.invoke(null, scoreText);
                        System.out.println("Connected score text to CollisionSystem via processor lookup");
                        return;
                    } catch (Exception ex) {
                        System.out.println("Could not connect via processor: " + ex.getMessage());
                    }
                }
            }
            System.out.println("No CollisionSystem found in " + postProcessors.size() + " postProcessors");
        }
    }

    private void setupKeyListeners(Scene scene) {
        scene.setOnKeyPressed(event -> {
            GameKeys keys = gameData.getKeys();
            if (event.getCode() == KeyCode.LEFT) keys.setKey(GameKeys.LEFT, true);
            if (event.getCode() == KeyCode.RIGHT) keys.setKey(GameKeys.RIGHT, true);
            if (event.getCode() == KeyCode.UP) keys.setKey(GameKeys.UP, true);
            if (event.getCode() == KeyCode.SPACE) keys.setKey(GameKeys.SPACE, true);
        });

        scene.setOnKeyReleased(event -> {
            GameKeys keys = gameData.getKeys();
            if (event.getCode() == KeyCode.LEFT) keys.setKey(GameKeys.LEFT, false);
            if (event.getCode() == KeyCode.RIGHT) keys.setKey(GameKeys.RIGHT, false);
            if (event.getCode() == KeyCode.UP) keys.setKey(GameKeys.UP, false);
            if (event.getCode() == KeyCode.SPACE) keys.setKey(GameKeys.SPACE, false);
        });
    }

    public void render() {
        new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                gameData.setTime(now / 1_000_000_000.0);

                if (lastUpdate != 0) {
                    double delta = (now - lastUpdate) / 1_000_000_000.0;
                    gameData.setDeltaTime(delta);
                }
                lastUpdate = now;

                update();
                draw();
                gameData.getKeys().update();
            }
        }.start();
    }

    private void update() {
        entityProcessors.forEach(processor -> processor.process(gameData, world));
        postProcessors.forEach(processor -> processor.process(gameData, world));
    }

    private void draw() {
        polygons.keySet().removeIf(entity -> {
            if (!world.getEntities().contains(entity)) {
                gameWindow.getChildren().remove(polygons.get(entity));
                return true;
            }
            return false;
        });

        world.getEntities().forEach(entity -> {
            Polygon polygon = polygons.computeIfAbsent(entity, e -> {
                Polygon newPoly = new Polygon(e.getPolygonCoordinates());
                gameWindow.getChildren().add(newPoly);
                return newPoly;
            });
            polygon.setTranslateX(entity.getX());
            polygon.setTranslateY(entity.getY());
            polygon.setRotate(entity.getRotation());
        });
    }


    public void updateScoreText(int newScore) {
        this.currentScore = newScore;
        scoreText.setText("Score: " + currentScore);
    }


    public void incrementScore(int increment) {
        try {

            int updatedScore = new ScoreClient().updateScore(increment);
            updateScoreText(updatedScore);
        } catch (IOException | InterruptedException e) {
            System.out.println("Failed to update score");
        }
    }
}