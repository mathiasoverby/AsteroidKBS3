package dk.sdu.cbse.enemy;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IGamePluginService;

import java.util.ArrayList;
import java.util.List;

public class EnemyPlugin implements IGamePluginService {

    private List<Entity> enemies = new ArrayList<>();

    @Override
    public void start(GameData gameData, World world) {

       Entity enemy = createEnemy(gameData);
        world.addEntity(enemy);

    }

    private Entity createEnemy(GameData gameData) {
        Entity enemy = new Enemy();
        enemy.setPolygonCoordinates(-5, -5, 10, 0, -5, 5);
        enemy.setX(gameData.getDisplayWidth() / 2);
        enemy.setY(gameData.getDisplayHeight() / 3);
        enemy.setRotation(90);
        return enemy;
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity enemy : enemies) {
            world.removeEntity(enemy);
        }
    }
}
