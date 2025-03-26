
import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;

module Player {
    requires Common;
   /* requires CommonBullet;
    uses dk.sdu.cbse.common.bullet.BulletSPI;*/
    provides IGamePluginService with dk.sdu.cbse.playersystem.PlayerPlugin;
    provides IEntityProcessingService with dk.sdu.cbse.playersystem.PlayerControlSystem;

}
