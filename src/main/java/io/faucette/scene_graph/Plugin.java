package io.faucette.scene_graph;


public class Plugin {
    protected Scene scene;


    public Plugin() {
        scene = null;
    }

    public int getOrder() {
        return 0;
    }

    public Scene getScene() {
        return scene;
    }

    public Plugin init() {
        return this;
    }
    public Plugin clear() {
        return this;
    }
    public Plugin update() {
        return this;
    }

    public Plugin destroy() {
        if (scene != null) {
            scene.removePlugin(this);
        }
        clear();
        return this;
    }
}
