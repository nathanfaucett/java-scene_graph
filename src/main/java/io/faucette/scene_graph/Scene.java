package io.faucette.scene_graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;
import java.util.Comparator;


public class Scene {
    private UUID id;
    private String name;

    public final Time time;

    private List<Entity> entities;
    private Map<UUID, Entity> entityHash;
    private Map<String, Entity> entityNameHash;

    private List<ComponentManager> componentManagers;
    private Map<Class<? extends ComponentManager>, ComponentManager> componentManagerHash;

    private List<Plugin> plugins;
    private Map<Class<? extends Plugin>, Plugin> pluginHash;

    private boolean initted;

    private Comparator<ComponentManager> componentManagerComparator = new Comparator<ComponentManager>() {
        @Override
        public int compare(ComponentManager a, ComponentManager b) {
            return new Integer(a.getOrder()).compareTo(b.getOrder());
        }
    };
    private Comparator<Plugin> pluginComparator = new Comparator<Plugin>() {
        @Override
        public int compare(Plugin a, Plugin b) {
            return new Integer(a.getOrder()).compareTo(b.getOrder());
        }
    };


    public Scene(String name) {

        id = UUID.randomUUID();
        this.name = name;

        time = new Time();

        entities = new ArrayList<>();
        entityHash = new HashMap<>();
        entityNameHash = new HashMap<>();

        componentManagers = new ArrayList<>();
        componentManagerHash = new HashMap<>();

        plugins = new ArrayList<>();
        pluginHash = new HashMap<>();

        initted = false;
    }
    public Scene() {
        this("");
    }

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Time getTime() {
        return time;
    }
    public boolean hasName() {
        return !name.equals("");
    }

    public Scene init() {
        if (!initted) {
            initted = true;

            sortPlugins();

            for (int i = 0, il = plugins.size(); i < il; i++ ) {
                plugins.get(i).init();
            }

            sortComponentManagers();

            for (int i = 0, il = componentManagers.size(); i < il; i++ ) {
                componentManagers.get(i).init();
            }
        }
        return this;
    }

    public Scene update() {

        time.update();

        for (int i = 0, il = plugins.size(); i < il; i++ ) {
            plugins.get(i).update();
        }
        for (int i = 0, il = componentManagers.size(); i < il; i++ ) {
            componentManagers.get(i).update();
        }

        return this;
    }

    public Scene clear() {
        for (int i = entities.size() - 1; i >= 0; i--) {
            entities.get(i).destroy();
        }
        for (int i = plugins.size() - 1; i >= 0; i--) {
            plugins.get(i).destroy();
        }
        return this;
    }

    public Scene destroy() {
        return clear();
    }

    public Entity getEntity(String name) {
        return entityNameHash.get(name);
    }
    public Entity getEntity(UUID uuid) {
        return entityHash.get(uuid);
    }
    public boolean hasEntity(Entity entity) {
        return entityHash.containsKey(entity.getId());
    }
    public Scene addEntity(Entity entity) {
        if (!entityHash.containsKey(entity.getId())) {
            entity.scene = this;
            entities.add(entity);
            entityHash.put(entity.getId(), entity);

            if (entity.hasName()) {
                entityNameHash.put(entity.getName(), entity);
            }

            for (Component component: entity.components) {
                addComponent(component);
            }
            for (Entity child: entity.children) {
                addEntity(child);
            }
        }
        return this;
    }

    public Scene removeEntity(Entity entity) {
        if (entityHash.containsKey(entity.getId())) {
            entity.scene = null;
            entities.remove(entity);
            entityHash.remove(entity.getId());

            if (entity.hasName()) {
                entityNameHash.remove(entity.getName());
            }

            for (Component component: entity.components) {
                removeComponent(component);
            }
            for (Entity child: entity.children) {
                removeEntity(child);
            }
        }
        return this;
    }

    public <T extends ComponentManager> boolean hasComponentManager(Class<T> componentManagerClass) {
        return componentManagerHash.containsKey(componentManagerClass);
    }
    @SuppressWarnings("unchecked")
    public <T extends ComponentManager> T getComponentManager(Class<T> componentManagerClass) {
        return (T) componentManagerHash.get(componentManagerClass);
    }

    protected <T extends Component> Scene addComponent(T component) {
        Class<? extends ComponentManager> componentManagerClass = component.getComponentManagerClass();
        ComponentManager componentManager;

        if (!componentManagerHash.containsKey(componentManagerClass)) {
            componentManager = component.createComponentManager();

            componentManager.scene = this;
            componentManagers.add(componentManager);
            componentManagerHash.put(componentManagerClass, componentManager);

            if (initted) {
                componentManager.init();
            }
        } else {
            componentManager = componentManagerHash.get(componentManagerClass);
        }

        componentManager.addComponent(component);
        component.componentManager = componentManager;

        if (initted) {
            sortComponentManagers();
            componentManager.sort();
            component.init();
        }

        return this;
    }
    protected <T extends Component> Scene removeComponent(T component) {
        Class<? extends ComponentManager> componentManagerClass = component.getComponentManagerClass();

        if (componentManagerHash.containsKey(componentManagerClass)) {
            ComponentManager componentManager = componentManagerHash.get(componentManagerClass);

            componentManager.removeComponent(component);
            component.componentManager = null;

            if (componentManager.isEmpty()) {
                componentManager.scene = null;
                componentManagers.remove(componentManager);
                componentManagerHash.remove(componentManagerClass);
            }
        }

        return this;
    }

    private void sortComponentManagers() {
        Collections.sort(componentManagers, componentManagerComparator);
    }
    private void sortPlugins() {
        Collections.sort(plugins, pluginComparator);
    }

    public <T extends Plugin> boolean hasPlugin(Class<T> pluginClass) {
        return pluginHash.containsKey(pluginClass);
    }
    @SuppressWarnings("unchecked")
    public <T extends Plugin> T getPlugin(Class<T> pluginClass) {
        return (T) pluginHash.get(pluginClass);
    }

    public <T extends Plugin> Scene addPlugin(T plugin) {
        if (!pluginHash.containsKey(plugin.getClass())) {
            plugin.scene = this;
            plugins.add(plugin);
            pluginHash.put(plugin.getClass(), plugin);

            if (initted) {
                sortPlugins();
                plugin.init();
            }
        }
        return this;
    }

    public <T extends Plugin> Scene removePlugin(T plugin) {
        if (pluginHash.containsKey(plugin.getClass())) {
            plugin.scene = null;
            plugins.remove(plugin);
            pluginHash.remove(plugin.getClass());
        }
        return this;
    }
}
