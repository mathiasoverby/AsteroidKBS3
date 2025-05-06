package dk.sdu.cbse.playersystem;

import dk.sdu.cbse.commonbullet.BulletSPI;
import dk.sdu.cbse.common.data.Entity;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.GameKeys;
import dk.sdu.cbse.common.data.World;
import dk.sdu.cbse.common.services.IEntityProcessingService;

import java.util.Collection;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class PlayerControlSystem implements IEntityProcessingService {
    private static final double ROTATION_SPEED = 5.0;
    private static final double MOVEMENT_SPEED = 2.0;

    @Override
    public void process(GameData gameData, World world) {
        for (Entity player : world.getEntities(Player.class)) {
            handleRotation(player, gameData);
            handleMovement(player, gameData);
            handleShooting(player, gameData, world);
            keepInBounds(player, gameData);
        }
    }

    private void handleRotation(Entity player, GameData gameData) {
        if (gameData.getKeys().isDown(GameKeys.LEFT)) {
            player.setRotation(player.getRotation() - ROTATION_SPEED);
        }
        if (gameData.getKeys().isDown(GameKeys.RIGHT)) {
            player.setRotation(player.getRotation() + ROTATION_SPEED);
        }
    }

    private void handleMovement(Entity player, GameData gameData) {
        if (gameData.getKeys().isDown(GameKeys.UP)) {
            double radians = Math.toRadians(player.getRotation());
            player.setX(player.getX() + Math.cos(radians) * MOVEMENT_SPEED);
            player.setY(player.getY() + Math.sin(radians) * MOVEMENT_SPEED);
        }
    }

    private void handleShooting(Entity player, GameData gameData, World world) {
        if (gameData.getKeys().isPressed(GameKeys.SPACE)) {
            getBulletSPIs().stream().findFirst().ifPresent(
                    spi -> world.addEntity(spi.createBullet(player, gameData))
            );
        }
    }

    private void keepInBounds(Entity player, GameData gameData) {
        if (player.getX() < 0) player.setX(0);
        if (player.getX() > gameData.getDisplayWidth()) player.setX(gameData.getDisplayWidth());
        if (player.getY() < 0) player.setY(0);
        if (player.getY() > gameData.getDisplayHeight()) player.setY(gameData.getDisplayHeight());
    }

    private Collection<? extends BulletSPI> getBulletSPIs() {
        return ServiceLoader.load(BulletSPI.class).stream()
                .map(ServiceLoader.Provider::get)
                .collect(toList());
    }
}