package io.faucette.scene_graph;


import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;


public class ComponentManager {
    protected Scene scene;
    protected List<Component> components;


    public ComponentManager() {
        scene = null;
        components = new ArrayList<>();
    }

    public boolean isEmpty() {
        return components.isEmpty();
    }

    public int getOrder() {
       return 0;
    }

    public ComponentManager init() {
       for (Component component: components) {
           component.init();
       }
       return this;
    }

    public ComponentManager update() {
       for (Component component: components) {
           component.update();
       }
       return this;
    }

    public ComponentManager sort() {
        Collections.sort(components);
        return this;
    }

    public Iterator<? extends Component> iterator() {
        return components.iterator();
    }

    public boolean hasComponent(Component component) {
        return components.contains(component);
    }
    public <T extends Component> ComponentManager addComponent(T component) {
        if (!components.contains(component)) {
            components.add(component);
        }
        return this;
    }
    public <T extends Component> ComponentManager removeComponent(T component) {
        components.remove(component);
        return this;
    }
}
