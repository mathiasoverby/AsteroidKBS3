package dk.sdu.cbse.asteroids;
import dk.sdu.cbse.commonasteroids.Asteroids;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;
import java.util.Random;
public class AsteroidsPlugin implements IGamePluginService{
    private Random random = new Random();
    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < 5; i++) {
            Entity asteroid = createRandomAsteroid(gameData);
            world.addEntity(asteroid);
        }
    }

    @Override
    public void stop(GameData gameData, World world) {

        for (Entity asteroid : world.getEntities(Asteroids.class)) {
            world.removeEntity(asteroid);
        }
    }

    private Entity createRandomAsteroid(GameData gameData) {
        Entity asteroid = new Asteroids();
        int size = 15;


        asteroid.setPolygonCoordinates(
                15, 0,   10, 10,
                0, 15,   -10, 10,
                -15, 0,  -10, -10,
                0, -15,  10, -10
        );

        asteroid.setX(random.nextDouble() * gameData.getDisplayWidth());
        asteroid.setY(random.nextDouble() * gameData.getDisplayHeight());
        asteroid.setRadius(size); // Consistent radius
        asteroid.setRotation(random.nextDouble() * 360);
        return asteroid;
    }
}