package dk.sdu.cbse.bulletsystem;
        import dk.sdu.cbse.commonbullet.Bullet;
        import dk.sdu.cbse.common.data.Entity;
        import dk.sdu.cbse.common.data.GameData;
        import dk.sdu.cbse.common.data.World;
        import dk.sdu.cbse.common.services.IGamePluginService;

public class BulletPlugin implements IGamePluginService {
    private Entity bullet;

    @Override
    public void start(GameData gameData, World world) {
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity e : world.getEntities()) {
            if (e.getClass() == Bullet.class) {
                world.removeEntity(e);
            }
        }
    }
}