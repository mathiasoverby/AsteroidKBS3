package dk.sdu.cbse.asteroids;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.commonasteroids.Asteroids;

import java.util.Random;

public class AsteroidsProcessor implements IEntityProcessingService {
    private Random random = new Random();
    @Override
    public void process(GameData gameData, World world) {
        for (Entity asteroid : world.getEntities(Asteroids.class)) {
            // Move asteroid based on its rotation
            double changeX = Math.cos(Math.toRadians(asteroid.getRotation()));
            double changeY = Math.sin(Math.toRadians(asteroid.getRotation()));
            asteroid.setX(asteroid.getX() + changeX * 0.5);
            asteroid.setY(asteroid.getY() + changeY * 0.5);


            if (asteroid.getX() < 0) asteroid.setX(gameData.getDisplayWidth());
            if (asteroid.getX() > gameData.getDisplayWidth()) asteroid.setX(0);
            if (asteroid.getY() < 0) asteroid.setY(gameData.getDisplayHeight());
            if (asteroid.getY() > gameData.getDisplayHeight()) asteroid.setY(0);

        }

        int randomInt = random.nextInt(50);

        if (randomInt == 1) {

            createAsteroid(gameData, world);
        }
    }

    public void createAsteroid(GameData gameData, World world) {
        Entity asteroid = new Asteroids();
        asteroid.setPolygonCoordinates(
                15, 0,   10, 10,
                0, 15,   -10, 10,
                -15, 0,  -10, -10,
                0, -15,  10, -10
        );
        asteroid.setX(random.nextDouble() * gameData.getDisplayWidth());
        asteroid.setY(random.nextDouble() * gameData.getDisplayHeight());
        asteroid.setRadius(15); // Same size as initial asteroids
        asteroid.setRotation(random.nextDouble() * 360);
        world.addEntity(asteroid);
    }

}
