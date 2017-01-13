package io.faucette.scene_graph;


public class Time {
    private double startTime;
    private double current;
    private double fps;
    private double delta;
    private long frame;

    private double scale;
    private double fixedDelta;
    private double fixedDeltaNoScale;

    private long fpsFrame;
    private double fpsLastTime;

    private static double MIN_DELTA = 0.000001d;
    private static double MAX_DELTA = 1d;


    public Time() {
        startTime = System.nanoTime() * 1e-9d;
        current = 0d;
        fps = 60d;
        delta = 1d / 60d;
        frame = 0;

        scale = 1d;
        fixedDelta = 1d / 60d;
        fixedDeltaNoScale = 1d / 60d;

        fpsFrame = 0;
        fpsLastTime = 0d;
    }

    public double getCurrent() { return current; }
    public double getFps() { return fps; }
    public double getDelta() { return delta; }
    public long getFrame() { return frame; }

    public void setScale(double newScale) {
        scale = newScale;
        fixedDelta = fixedDeltaNoScale * newScale;
    }
    public double getScale() { return scale; }

    public void setFixedDelta(double newFixedDelta) {
        fixedDeltaNoScale = newFixedDelta;
        fixedDelta = fixedDeltaNoScale * scale;
    }
    public double getFixedDelta() { return fixedDelta; }

    public double start() {
        return startTime;
    }

    public double now() {
        return (System.nanoTime() * 1e-9d) - startTime;
    }

    public void update() {
        frame++;

        double lastTime = current;
        double currentTime = now();

        fpsFrame++;
        if (fpsLastTime + 1d < currentTime) {
            fps = fpsFrame / (currentTime - fpsLastTime);
            fpsLastTime = currentTime;
            fpsFrame = 0;
        }

        delta = (currentTime - lastTime) * scale;
        delta = delta < MIN_DELTA ? MIN_DELTA : delta > MAX_DELTA ? MAX_DELTA : delta;

        current = currentTime;
    }
}
