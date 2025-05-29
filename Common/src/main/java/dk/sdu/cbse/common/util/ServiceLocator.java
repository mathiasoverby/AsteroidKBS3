package dk.sdu.cbse.common.util;

import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public enum ServiceLocator {

    INSTANCE;

    private static final Map<Class, ServiceLoader> serviceLoaderMap = new HashMap<>();
    private final ModuleLayer moduleLayer;

    ServiceLocator() {
        try {
            Path pluginDirectory = Paths.get("plugins"); // Directory containing plugin JAR files

            // Locate plugin modules in the specified directory
            ModuleFinder pluginModuleFinder = ModuleFinder.of(pluginDirectory);

            // Extract the names of all plugin modules
            List<String> pluginNames = pluginModuleFinder
                    .findAll()
                    .stream()
                    .map(ModuleReference::descriptor)
                    .map(ModuleDescriptor::name)
                    .collect(Collectors.toList());

            // Resolve the plugin modules and ensure correct graph configuration
            Configuration moduleConfiguration = ModuleLayer
                    .boot()
                    .configuration()
                    .resolve(pluginModuleFinder, ModuleFinder.of(), pluginNames);

            // Define a new module layer for plugins using the system class loader
            moduleLayer = ModuleLayer
                    .boot()
                    .defineModulesWithOneLoader(moduleConfiguration, ClassLoader.getSystemClassLoader());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize plugin module layer", e);
        }
    }

    public <T> List<T> locateServices(Class<T> serviceType) {
        ServiceLoader<T> svloader = serviceLoaderMap.get(serviceType);

        if (svloader == null) {
            svloader = ServiceLoader.load(moduleLayer, serviceType);
            serviceLoaderMap.put(serviceType, svloader);
        }

        List<T> services = new ArrayList<>();

        if (svloader != null) {
            try {
                for (T service : svloader) {
                    services.add(service);
                }
            } catch (ServiceConfigurationError error) {
                error.printStackTrace();
            }
        }

        return services;
    }
}
