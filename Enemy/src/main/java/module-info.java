import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;


module Enemy {
    requires Common;
    requires CommonBullet;
    uses dk.sdu.cbse.commonbullet.BulletSPI;
    provides IGamePluginService with dk.sdu.cbse.enemy.EnemyPlugin;
    provides IEntityProcessingService with dk.sdu.cbse.enemy.EnemyControlSystem;
}