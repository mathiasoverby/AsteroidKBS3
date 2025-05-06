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
        for(int i = 0; i<5; i++)
            world.addEntity(createRandomAsteroid(gameData));
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove entities
        for (Entity asteroid : world.getEntities(Asteroids.class)) {
            world.removeEntity(asteroid);
        }
    }

    private Entity createRandomAsteroid(GameData gameData){
        Entity asteroid = new Asteroids();
        int size = random.nextInt(15)+10;
        int sides = random.nextInt(5)+5;

        double[] coordinates = new double[sides *2];
        for(int i = 0; i < sides; i++){
            double angle = 2*Math.PI/sides;
            double radiusVariation = size * (0.7 + random.nextDouble() *0.6);
            coordinates[2*i] = radiusVariation + Math.cos(angle);
            coordinates[2*i+1] = radiusVariation + Math.sin(angle);
        }

        asteroid.setPolygonCoordinates(coordinates);
        asteroid.setX(random.nextDouble() * gameData.getDisplayWidth());
        asteroid.setY(random.nextDouble() * gameData.getDisplayHeight());
        asteroid.setRadius(size);
        asteroid.setRotation(random.nextDouble()*360);
        return asteroid;

    }
}