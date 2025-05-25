package dk.sdu.cbse.collision;

import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollisionSystem implements IPostEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {

        Set<String> entitiesToRemove = new HashSet<>();


        Entity[] entities = world.getEntities().toArray(new Entity[0]);

        for (int i = 0; i < entities.length; i++) {
            Entity entity1 = entities[i];
            if (entitiesToRemove.contains(entity1.getID())) continue;

            for (int j = i + 1; j < entities.length; j++) {
                Entity entity2 = entities[j];
                if (entitiesToRemove.contains(entity2.getID())) continue;

                if (this.collides(entity1, entity2)) {

                    if (isBullet(entity1) || isBullet(entity2)) {
                        entitiesToRemove.add(entity1.getID());
                        entitiesToRemove.add(entity2.getID());
                    }
                }
            }
        }


        entitiesToRemove.forEach(world::removeEntity);
    }

    boolean isBullet(Entity entity) {

        return entity.getRadius() <= 2;
    }

    public Boolean collides(Entity entity1, Entity entity2) {
        float dx = (float) (entity1.getX() - entity2.getX());
        float dy = (float) (entity1.getY() - entity2.getY());
        float distance = (float) Math.sqrt(dx * dx + dy * dy);


        float tolerance = 1.1f;
        return distance < (entity1.getRadius() + entity2.getRadius()) * tolerance;
    }
}