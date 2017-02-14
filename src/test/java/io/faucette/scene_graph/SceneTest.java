package io.faucette.scene_graph;


import java.util.Iterator;
import io.faucette.event_emitter.Emitter;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
import org.junit.*;


public class SceneTest {
    @Test
    public void testChildrenAddedToEntityAddedToSceneAndRemoved() {
        Scene scene = new Scene();
        Entity parent = new Entity();
        Entity child = new Entity();

        parent.addChild(child);
        assertTrue(parent.hasChild(child));

        scene.addEntity(parent);
        assertTrue(scene.hasEntity(parent));
        assertTrue(scene.hasEntity(child));

        assertEquals(0, parent.getDepth());
        assertEquals(1, child.getDepth());
        assertEquals(parent, child.getRoot());
        assertEquals(parent, child.getParent());

        parent.removeChild(child);
        assertTrue(scene.hasEntity(parent));
        assertTrue(!scene.hasEntity(child));
    }
    @Test
    public void testEvents() {
        Scene scene = new Scene();
        Entity entity = new Entity("name");

        entity.addComponent(new TestComponent());
        scene.addEntity(entity);

        final AtomicBoolean called = new AtomicBoolean(false);

        scene.on("event", new Emitter.Callback() {
            public void call(Emitter emitter, Object[] args) {
                called.set(true);
            }
        });
        entity.on("event", new Emitter.Callback() {
            public void call(Emitter emitter, Object[] args) {
                Entity entity = (Entity) emitter;
                entity.getScene().emit("event");
            }
        });

        entity.emit("event");
        assertTrue(called.get());
    }
    @Test
    public void testComponentsAddedToEntityAddedToSceneAndRemoved() {
        Scene scene = new Scene();
        Entity entityA = new Entity();
        Entity entityB = new Entity();

        entityA.addComponent(new TestComponent());
        entityB.addComponent(new Component());

        assertTrue(entityA.hasComponent(TestComponent.class));
        assertTrue(entityB.hasComponent(Component.class));

        scene.addEntity(entityA);
        scene.addEntity(entityB);

        assertTrue(entityA.hasScene());
        assertTrue(entityB.hasScene());

        assertTrue(scene.hasComponentManager(TestComponentManager.class));
        assertTrue(scene.hasComponentManager(ComponentManager.class));

        scene.init();

        TestComponentManager testManager = scene.getComponentManager(TestComponentManager.class);
        assertTrue(testManager.initted);

        Iterator<? extends Component> it = testManager.iterator();
        while (it.hasNext()) {
            TestComponent test = (TestComponent) it.next();
            assertTrue(test != null);
        }

        scene.addPlugin(new TestPlugin());
        assertTrue(scene.hasPlugin(TestPlugin.class));

        TestPlugin testPlugin = scene.getPlugin(TestPlugin.class);
        assertTrue(testPlugin.initted);

        scene.update();

        assertTrue(testManager.updated);
        assertTrue(testPlugin.updated);

        assertEquals(1, scene.time.getFrame());
    }
}

class TestComponent extends Component {
    public TestComponent() {
        super();
    }
    @Override
    public Class<? extends ComponentManager> getComponentManagerClass() {
        return TestComponentManager.class;
    }
    @Override
    public ComponentManager createComponentManager() {
        return new TestComponentManager();
    }
}

class TestComponentManager extends ComponentManager {
    public boolean initted;
    public boolean updated;


    public TestComponentManager() {
        super();
        initted = false;
        updated = false;
    }
    @Override
    public TestComponentManager init() {
        initted = true;
        return this;
    }
    @Override
    public TestComponentManager update() {
        updated = true;
        return this;
    }
    public int getOrder() {
        return 1;
    }
}

class TestPlugin extends Plugin {
    public boolean initted;
    public boolean updated;


    public TestPlugin() {
        super();
        initted = false;
        updated = false;
    }
    @Override
    public TestPlugin init() {
        initted = true;
        return this;
    }
    @Override
    public TestPlugin update() {
        updated = true;
        return this;
    }
}
