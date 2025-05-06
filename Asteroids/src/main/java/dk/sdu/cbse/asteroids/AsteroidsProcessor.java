package dk.sdu.cbse.asteroids;

import dk.sdu.cbse.commonasteroids.Asteroids;
import dk.sdu.cbse.commonasteroids.IAsteroidsSplitter;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;

public class AsteroidsProcessor implements IEntityProcessingService {
    IAsteroidsSplitter asteroidsSplitter = new AsteroidsSplitter();
    @Override
    public void process(GameData gameData, World world) {
    for(Entity asteroid : world.getEntities(Asteroids.class)){
        moveAsteroid(asteroid);
        handleScreenWrapping(asteroid, gameData);
    }
    }

    private void moveAsteroid(Entity asteroid){
        double radians = Math.toRadians(asteroid.getRotation());
        double speed = 0.8;
        asteroid.setX(asteroid.getX()+ Math.cos(radians) * speed);
        asteroid.setY(asteroid.getY()+ Math.sin(radians)*speed);
    }


    private void handleScreenWrapping(Entity asteroid, GameData gameData){
        if(asteroid.getX() < 0) asteroid.setX(gameData.getDisplayWidth());
        if(asteroid.getX() > gameData.getDisplayWidth()) asteroid.setX(0);
        if(asteroid.getY() < 0) asteroid.setY(gameData.getDisplayHeight());
        if(asteroid.getY() > gameData.getDisplayHeight()) asteroid.setY(0);
    }
    public void setAsteroidSplitter(IAsteroidsSplitter asteroidSplitter) {
        this.asteroidsSplitter = asteroidSplitter;
    }

    public void removeAsteroidSplitter(IAsteroidsSplitter asteroidSplitter) {
        this.asteroidsSplitter = null;
    }


}
