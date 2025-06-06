package dk.sdu.cbse.enemy;

import dk.sdu.cbse.commonbullet.BulletSPI;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;
import java.util.Collection;
import java.util.Random;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class EnemyControlSystem implements IEntityProcessingService {
private Random random = new Random();
private static final double shootProbability = 0.01;
    @Override
    public void process(GameData gameData, World world) {

        for (Entity enemy : world.getEntities(Enemy.class)) {
            updateMovement(enemy, gameData);
            tryShooting(enemy, gameData, world);
            handleScreenBounds(enemy, gameData);
        }
    }


    private void updateMovement(Entity enemy, GameData gameData){
        if(random.nextDouble() < 0.02) {
        enemy.setRotation(enemy.getRotation() + (random.nextBoolean() ? 5 : -5));
        }

        double radians = Math.toRadians(enemy.getRotation());
        enemy.setX(enemy.getX() + Math.cos(radians));
        enemy.setY(enemy.getY() + Math.sin(radians));
    }

    private void tryShooting(Entity enemy, GameData gameData, World world){
        if(random.nextDouble() < shootProbability){
            getBulletSPIs().stream().findFirst().ifPresent(
                    spi -> world.addEntity(spi.createBullet(enemy, gameData))
            );

        }
    }
    private void handleScreenBounds(Entity enemy, GameData gameData){
        if (enemy.getX() < 0) enemy.setX(gameData.getDisplayWidth());
        if (enemy.getX() > gameData.getDisplayWidth()) enemy.setX(0);
        if (enemy.getY() < 0) enemy.setY(gameData.getDisplayHeight());
        if (enemy.getY() > gameData.getDisplayHeight()) enemy.setY(0);
    }
    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

}