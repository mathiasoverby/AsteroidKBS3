package dk.sdu.cbse.main;

import dk.sdu.cbse.common.services.IEntityProcessingService;
import dk.sdu.cbse.common.services.IGamePluginService;
import dk.sdu.cbse.common.services.IPostEntityProcessingService;
import dk.sdu.cbse.common.data.GameData;
import dk.sdu.cbse.common.data.World;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

@Configuration
public class ModuleConfig {


    @Bean
    public GameData gameData() {
        return new GameData();
    }

    @Bean
    public World world() {
        return new World();
    }

    @Bean
    public List<IEntityProcessingService> entityProcessingServiceList() {
        return ServiceLoader.load(IEntityProcessingService.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }


    @Bean
    public List<IGamePluginService> gamePluginServices() {
        return ServiceLoader.load(IGamePluginService.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }


    @Bean
    public List<IPostEntityProcessingService> postEntityProcessingServices() {
        return ServiceLoader.load(IPostEntityProcessingService.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .collect(Collectors.toList());
    }

    @Bean
    public Game game(GameData gameData, World world,
                     List<IGamePluginService> gamePluginServices,
                     List<IEntityProcessingService> entityProcessingServiceList,
                     List<IPostEntityProcessingService> postEntityProcessingServices) {
        return new Game(gameData, world, gamePluginServices, entityProcessingServiceList, postEntityProcessingServices);
    }
}
