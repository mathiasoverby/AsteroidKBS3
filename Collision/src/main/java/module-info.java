import dk.sdu.cbse.common.services.IPostEntityProcessingService;

module Collision {
    requires Common;
    provides IPostEntityProcessingService with dk.sdu.cbse.collision.CollisionSystem;
}