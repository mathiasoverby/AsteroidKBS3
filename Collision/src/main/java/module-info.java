import dk.sdu.cbse.common.services.IPostEntityProcessingService;

module Collision {
    requires Common;
    requires javafx.graphics;
    provides IPostEntityProcessingService with dk.sdu.cbse.collision.CollisionSystem;
}