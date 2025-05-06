package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;


public class EnemyPlugin implements IGamePluginService {
    private Entity enemy;

    public EnemyPlugin() {
    }

    @Override
    public void start(GameData gameData, World world) {

        // Add entities to the world
        enemy = createEnemyShip(gameData);
        world.addEntity(enemy);
    }

    private Entity createEnemyShip(GameData gameData) {

        Entity enemyShip = new Enemy();
        enemyShip.setPolygonCoordinates(-5,-5,10,0,-5,5);
        enemyShip.setX(gameData.getDisplayHeight() *0.75);
        enemyShip.setY(gameData.getDisplayWidth()/2);



        return enemyShip;
    }

    @Override
    public void stop(GameData gameData, World world) {
        // Remove entities
       if(enemy != null){
           world.removeEntity(enemy);
       }

    }
}
