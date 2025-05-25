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

    private static final Map<Class<?>, ServiceLoader<?>> loadermap = new HashMap<>();
    private final ModuleLayer layer;

    ServiceLocator() {
        try {
            Path pluginsDir = Paths.get("plugins"); // Directory with plugins JARs

            // Search for plugins in the plugins directory
            ModuleFinder pluginsFinder = ModuleFinder.of(pluginsDir);

            // Find all names of all found plugin modules
            List<String> plugins = pluginsFinder
                    .findAll()
                    .stream()
                    .map(ModuleReference::descriptor)
                    .map(ModuleDescriptor::name)
                    .collect(Collectors.toList());

            // Create configuration that will resolve plugin modules
            Configuration pluginsConfiguration = ModuleLayer
                    .boot()
                    .configuration()
                    .resolve(pluginsFinder, ModuleFinder.of(), plugins);

            // Create a module layer for plugins
            layer = ModuleLayer
                    .boot()
                    .defineModulesWithOneLoader(pluginsConfiguration, ClassLoader.getSystemClassLoader());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize ServiceLocator", e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> locateAll(Class<T> service) {
        ServiceLoader<T> loader = (ServiceLoader<T>) loadermap.get(service);

        if (loader == null) {
            loader = ServiceLoader.load(layer, service);
            loadermap.put(service, loader);
        }

        List<T> list = new ArrayList<>();
        if (loader != null) {
            try {
                loader.iterator().forEachRemaining(list::add);
            } catch (ServiceConfigurationError serviceError) {
                System.err.println("Error loading service " + service.getName());
                serviceError.printStackTrace();
            }
        }
        return list;
    }
}