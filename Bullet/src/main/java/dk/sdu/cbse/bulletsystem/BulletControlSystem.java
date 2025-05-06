package dk.sdu.cbse.bulletsystem;

import dk.sdu.cbse.commonbullet.Bullet;
import dk.sdu.cbse.commonbullet.BulletSPI;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;

import java.util.HashMap;
import java.util.Map;

public class BulletControlSystem implements IEntityProcessingService, BulletSPI {
private static final double bulletSpeed = 5.0;
private static final double bulletLifeTime =1.5;

private Map<Entity, Double> bulletCreationTimes = new HashMap<>();
    @Override
    public void process(GameData gameData, World world) {

        for (Entity bullet : world.getEntities(Bullet.class)) {
            if(!bulletCreationTimes.containsKey(Bullet.class)){
                bulletCreationTimes.put(bullet, gameData.getTime());
            }
            updateBulletPosition(bullet);
            checkBulletLifeTime(bullet, gameData, world);
        }
    }

    private void updateBulletPosition(Entity bullet){
        double radians = Math.toRadians(bullet.getRotation());
        bullet.setX(bullet.getX() + Math.cos(radians) * bulletSpeed);
        bullet.setY(bullet.getY() + Math.sin(radians) * bulletSpeed);
    }

    private void checkBulletLifeTime(Entity bullet, GameData gameData, World world){
       double creationTime = bulletCreationTimes.get(bullet);
       if(gameData.getTime() - creationTime > bulletLifeTime){
           world.removeEntity(bullet);
           bulletCreationTimes.remove(bullet);
       }
    }

    @Override
    public Entity createBullet(Entity shooter, GameData gameData) {
        Entity bullet = new Bullet();
        bullet.setPolygonCoordinates(1, -1, 1, 1, -1, 1, -1, -1);

        double radians = Math.toRadians(shooter.getRotation());
        double offsetX = Math.cos(radians) * 15;
        double offsetY = Math.sin(radians) * 15;

        bullet.setX(shooter.getX() + offsetX);
        bullet.setY(shooter.getY() + offsetY);
        bullet.setRotation(shooter.getRotation());
        bulletCreationTimes.put(bullet, gameData.getTime());
        return bullet;
    }
}
