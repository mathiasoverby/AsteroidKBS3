import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.asteroids.EntityProcessor;



module Asteroids {
    requires Common;
    requires CommonAsteroids;
    provides IGamePluginService with dk.sdu.cbse.asteroids.AsteroidsPlugin;
    provides IEntityProcessingService with dk.sdu.cbse.asteroids.AsteroidsProcessor, EntityProcessor;
}