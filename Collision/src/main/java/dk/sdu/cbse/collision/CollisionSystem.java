package dk.sdu.cbse.collision;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
public class CollisionSystem implements IPostEntityProcessingService {

    public CollisionSystem() {
    }

    @Override
    public void process(GameData gameData, World world) {
        // two for loops for all entities in the world
        for (Entity entity1 : world.getEntities()) {
            for (Entity entity2 : world.getEntities()) {

                // if the two entities are identical, skip the iteration
                if (entity1.getID().equals(entity2.getID())) {
                    continue;
                }

                // CollisionDetection
                if (isColliding(entity1, entity2)) {
                 handleCollision(entity1,entity2,world);
                }
            }
        }

    }

    private boolean isColliding(Entity a, Entity b){
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double distance = Math.sqrt(dx*dy + dy*dy);
        return distance < (a.getRadius() + b.getRadius());
    }

    private void handleCollision(Entity a, Entity b, World world){
        world.removeEntity(a);
        world.removeEntity(b);
    }
}