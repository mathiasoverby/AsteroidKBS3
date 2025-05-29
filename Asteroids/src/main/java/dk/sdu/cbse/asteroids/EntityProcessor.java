package dk.sdu.cbse.asteroids;

import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;

public class EntityProcessor implements IEntityProcessingService {
    @Override
    public void process(GameData gameData, World world) {
        System.out.println("Hey i am a Asteroid Entityprocessor");
    }
}
