package dk.sdu.cbse.collision;

import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CollisionSystemTest {
    private CollisionSystem collisionSystem;

    @BeforeEach
    void setUp() {
        collisionSystem = new CollisionSystem();
    }


    @Test
    void collides_WhenEntitiesOverlap_ReturnsTrue() {
        Entity a = createEntity(100, 100, 10);
        Entity b = createEntity(100, 100, 10);
        assertTrue(collisionSystem.collides(a, b));
    }

    @Test
    void collides_WhenEntitiesFarApart() {
        Entity a = createEntity(100, 100, 5);
        Entity b = createEntity(200, 200, 5);
        assertFalse(collisionSystem.collides(a, b));
    }


    @Test
    void collides_AtExactCollisionDistance() {
        Entity a = createEntity(100, 100, 10);
        Entity b = createEntity(100 + 10 + 10, 100, 10); // Exactly at sum of radii
        assertTrue(collisionSystem.collides(a, b));
    }


    private Entity createEntity(double x, double y, float radius) {
        Entity e = new Entity();
        e.setX(x);
        e.setY(y);
        e.setRadius(radius);
        return e;
    }
}
