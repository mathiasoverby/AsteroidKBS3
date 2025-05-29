package dk.sdu.cbse.main;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.GameKeys;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import static java.util.stream.Collectors.toList;

public class Main extends Application {
    private final GameData gameData = new GameData();
    private final World world = new World();
    private final Map<String, Polygon> polygons = new ConcurrentHashMap<>();
    private final Pane gameWindow = new Pane();

    private static ModuleLayer pluginLayer;

    public static void main(String[] args) {
        initializePluginLayer();
        launch(Main.class);
    }

    private static void initializePluginLayer() {
        try {
            Path pluginsDir = Paths.get("plugins");

            // Finding plugins in the plugins directory
            ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);

            // Getting plugin module names
            List<String> plugins = pluginsFinder
                    .findAll()
                    .stream()
                    .map(ModuleReference::descriptor)
                    .map(ModuleDescriptor::name)
                    .collect(Collectors.toList());

            System.out.println("Found plugins: " + plugins);

            // Creating configuration for plugins
            Configuration pluginsConfiguration = ModuleLayer
                    .boot()
                    .configuration()
                    .resolve(pluginsFinder, ModuleFinder.of(), plugins);

            // Create module layer for plugins
            pluginLayer = ModuleLayer
                    .boot()
                    .defineModulesWithOneLoader(pluginsConfiguration, ClassLoader.getSystemClassLoader());

            System.out.println("Plugin layer initialized successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize plugin layer: " + e.getMessage());
            e.printStackTrace();
            // Going back to boot layer
            pluginLayer = ModuleLayer.boot();
        }
    }

    @Override
    public void start(Stage window) throws Exception {
        Text text = new Text(10, 20, "Destroyed asteroids: 0");
        gameWindow.setPrefSize(gameData.getDisplayWidth(), gameData.getDisplayHeight());
        gameWindow.getChildren().add(text);

        Scene scene = new Scene(gameWindow);
        setupKeyHandlers(scene);

        // Start all plugins
        for (IGamePluginService plugin : getPluginServices()) {
            plugin.start(gameData, world);
        }

        // Initialize polygons for existing entities
        for (Entity entity : world.getEntities()) {
            if (entity.getPolygonCoordinates() != null) {
                Polygon polygon = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity.getID(), polygon);
                gameWindow.getChildren().add(polygon);
            }
        }

        render();

        window.setScene(scene);
        window.setTitle("ASTEROIDS");
        window.show();
    }

    private void setupKeyHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, true);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, true);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, true);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, true);
            }
        });

        scene.setOnKeyReleased(event -> {
            if (event.getCode().equals(KeyCode.LEFT)) {
                gameData.getKeys().setKey(GameKeys.LEFT, false);
            }
            if (event.getCode().equals(KeyCode.RIGHT)) {
                gameData.getKeys().setKey(GameKeys.RIGHT, false);
            }
            if (event.getCode().equals(KeyCode.UP)) {
                gameData.getKeys().setKey(GameKeys.UP, false);
            }
            if (event.getCode().equals(KeyCode.SPACE)) {
                gameData.getKeys().setKey(GameKeys.SPACE, false);
            }
        });
    }

    private void render() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameData.setTime(now / 1_000_000_000.0);
                update();
                draw();
                gameData.getKeys().update();
            }
        }.start();
    }

    private void update() {
        // Process entities
        for (IEntityProcessingService processor : getEntityProcessingServices()) {
            processor.process(gameData, world);
        }

        // Add polygons for new entities
        for (Entity entity : world.getEntities()) {
            if (polygons.get(entity.getID()) == null && entity.getPolygonCoordinates() != null) {
                Polygon polygon = new Polygon(entity.getPolygonCoordinates());
                polygons.put(entity.getID(), polygon);
                gameWindow.getChildren().add(polygon);
            }
        }

        // Post processing (collision detection)
        for (IPostEntityProcessingService postProcessor : getPostEntityProcessingServices()) {
            postProcessor.process(gameData, world);
        }
    }

    private void draw() {
        // Remove polygons for removed entities
        polygons.entrySet().removeIf(entry -> {
            String entityID = entry.getKey();
            if (world.getEntities().stream().noneMatch(e -> e.getID().equals(entityID))) {
                gameWindow.getChildren().remove(entry.getValue());
                return true;
            }
            return false;
        });

        // Update polygon positions and rotations
        for (Entity entity : world.getEntities()) {
            Polygon polygon = polygons.get(entity.getID());
            if (polygon != null) {
                polygon.setTranslateX(entity.getX());
                polygon.setTranslateY(entity.getY());
                polygon.setRotate(entity.getRotation());
            }
        }
    }

    // Service loading methods using plugin layer
    private Collection<? extends IGamePluginService> getPluginServices() {
        return ServiceLoader.load(pluginLayer, IGamePluginService.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(toList());
    }

    private Collection<? extends IEntityProcessingService> getEntityProcessingServices() {
        return ServiceLoader.load(pluginLayer, IEntityProcessingService.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(toList());
    }

    private Collection<? extends IPostEntityProcessingService> getPostEntityProcessingServices() {
        return ServiceLoader.load(pluginLayer, IPostEntityProcessingService.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(toList());
    }
}