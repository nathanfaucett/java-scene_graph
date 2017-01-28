package io.faucette.scene_graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Collections;


public class Entity {
    private UUID id;
    private String name;
    private String tag;
    private int depth;
    protected Scene scene;
    private Entity root;
    private Entity parent;
    protected List<Entity> children;

    protected List<Component> components;
    protected HashMap<Class<? extends Component>, Component> componentHash;


    public Entity(String name) {
        id = UUID.randomUUID();

        this.name = name == null ? "" : name;
        tag = "";

        depth = 0;
        scene = null;
        root = this;
        parent = null;
        children = new ArrayList<>();

        components = new ArrayList<>();
        componentHash = new HashMap<>();
    }
    public Entity() {
        this("");
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public boolean hasName() {
        return !name.equals("");
    }

    public Entity setTag(String tag) {
        this.tag = tag;
        return this;
    }
    public String getTag() {
        return tag;
    }
    public boolean compareTag(String tag) {
        return this.tag.equals(tag);
    }
    public boolean hasTag() {
        return tag != null;
    }

    public int getDepth() {
        return depth;
    }

    public Scene getScene() {
        return scene;
    }
    public boolean hasScene() {
        return scene != null;
    }

    public Entity getRoot() {
        return root;
    }

    public Entity getParent() {
        return parent;
    }
    public boolean hasParent() {
        return parent != null;
    }

    public Entity clear() {

        detach();

        for (int i = components.size(); i >= 0; i--) {
            components.get(i).destroy();
        }
        for (int i = children.size(); i >= 0; i--) {
            children.get(i).destroy();
        }
        return this;
    }

    public Entity destroy() {
        return clear();
    }

    public Entity detach() {
        if (parent != null) {
            parent.removeChild(this);
        }
        return this;
    }

    public <T extends Component> boolean hasComponent(Class<T> componentClass) {
        return componentHash.containsKey(componentClass);
    }
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) componentHash.get(componentClass);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> Entity addComponent(T component) {
        if (!componentHash.containsKey(component.getClass())) {
            Component c = (Component) component;

            c.entity = this;

            components.add(c);
            componentHash.put((Class<Component>) component.getClass(), c);

            if (scene != null) {
                scene.addComponent(component);
            }
        }
        return this;
    }
    public <T extends Component> Entity removeComponent(T component) {
        if (componentHash.containsKey(component.getClass())) {
            Component c = (Component) component;

            if (scene != null) {
                scene.removeComponent(component);
            }

            component.entity = null;

            components.remove(c);
            componentHash.remove(component.getClass());
        }
        return this;
    }

    public boolean hasChild(Entity entity) {
        return children.contains(entity);
    }
    public Entity addChild(Entity entity) {
        return addChild(entity, true);
    }
    private Entity addChild(Entity entity, boolean addToScene) {
        if (!children.contains(entity)) {
            if (entity.parent != null) {
                entity.parent.removeChild(entity);
            }

            children.add(entity);
            entity.parent = this;
            entity.root = this.root;

            Entity.updateDepth(entity, depth + 1);

            if (addToScene && scene != null) {
                scene.addEntity(entity);
            }
        }
        return this;
    }

    public Entity removeChild(Entity entity) {
        int index = children.indexOf(entity);

        if (index != -1) {
            children.remove(index);
            entity.parent = null;
            entity.root = entity;

            Entity.updateDepth(entity, 0);

            if (scene != null) {
                scene.removeEntity(entity);
            }
        }
        return this;
    }

    public List<Entity> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    private static void updateDepth(Entity entity, int depth) {
        entity.depth = depth;
        for (Entity child: entity.children) {
            Entity.updateDepth(child, depth + 1);
        }
    }
}
