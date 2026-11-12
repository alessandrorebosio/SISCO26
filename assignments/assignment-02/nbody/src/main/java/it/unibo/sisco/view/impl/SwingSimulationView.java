package it.unibo.sisco.view.impl;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.view.api.SimulationView;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Swing implementation of {@link SimulationView}. Every Swing component is created
 * and updated on the Event Dispatch Thread.
 */
public final class SwingSimulationView implements SimulationView {

    private static final double MILLIS_PER_SECOND = 1000.0;

    private VisualiserPanel panel;
    private long nFramesRendered;
    private long startTime;

    /**
     * Builds and shows the window of the view.
     *
     * @param w the width of the window
     * @param h the height of the window
     */
    public SwingSimulationView(final int w, final int h) {
        runOnEdt(() -> {
            final JFrame frame = new JFrame("Bodies Simulation");
            frame.setSize(w, h);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel = new VisualiserPanel(w, h);
            frame.getContentPane().add(panel);
            frame.setVisible(true);
            panel.requestFocusInWindow();
        });
    }

    @Override
    public void init() {
        startTime = System.currentTimeMillis();
        nFramesRendered = 0;
    }

    @Override
    public void display(final List<Body> bodies, final double vt, final long iteration) {
        nFramesRendered++;
        final long elapsed = Math.max(1, System.currentTimeMillis() - startTime);
        final int framePerSec = (int) (nFramesRendered / (double) elapsed * MILLIS_PER_SECOND);
        /* the snapshot is taken by the simulation thread, so the EDT never reads the live model */
        final List<BodyShape> shapes = bodies.stream()
            .map(b -> new BodyShape(b.getPos().x(), b.getPos().y(), b.getRadius()))
            .toList();
        runOnEdt(() -> {
            panel.display(shapes, vt, iteration, framePerSec);
            panel.repaint();
        });
    }

    private static void runOnEdt(final Runnable action) {
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final InvocationTargetException e) {
            throw new IllegalStateException("Error while updating the view", e.getCause());
        }
    }
}
