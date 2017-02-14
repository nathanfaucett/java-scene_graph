package io.faucette.scene_graph;


import io.faucette.event_emitter.EventEmitter;


public class Component extends EventEmitter implements Comparable<Component> {
    protected Entity entity;
    protected ComponentManager componentManager;


    public Component() {
        componentManager = null;
        entity = null;
    }

    public Entity getEntity() {
        return entity;
    }
    public boolean hasEntity() {
        return entity != null;
    }

    public boolean hasComponentManager() {
        return componentManager != null;
    }
    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public Class<? extends ComponentManager> getComponentManagerClass() {
        return ComponentManager.class;
    }
    public ComponentManager createComponentManager() {
        return new ComponentManager();
    }

    public Component init() {
        return this;
    }
    public Component clear() {
        return this;
    }
    public Component update() {
        return this;
    }

    public Component destroy() {
        if (entity != null) {
            entity.removeComponent(this);
        }
        clear();
        return this;
    }

    public int compareTo(Component component) {
        return 0;
    }
}
