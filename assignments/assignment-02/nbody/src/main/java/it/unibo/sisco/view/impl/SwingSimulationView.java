package it.unibo.sisco.view.impl;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.view.api.SimulationView;
import it.unibo.sisco.view.impl.panel.BodyShape;
import it.unibo.sisco.view.impl.panel.VisualiserPanel;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Swing implementation of {@link SimulationView}: a window that draws the
 * bodies and shows the virtual time, the number of steps and the frames
 * rendered per second.
 *
 * <p>
 * Every Swing component is created and updated on the Event Dispatch Thread.
 * {@link #display(List, double, long)} copies the positions of the bodies in
 * the calling thread and waits until the Event Dispatch Thread has received
 * them, so the simulation never runs ahead of the view. Closing the window
 * terminates the application.
 */
public final class SwingSimulationView implements SimulationView {

    private static final String TITLE = "Bodies Simulation";
    private static final double MILLIS_PER_SECOND = 1000.0;

    private JFrame frame;
    private VisualiserPanel panel;
    private long nFramesRendered;
    private long startTime;

    /**
     * Builds and shows the window of the view, centred on the screen. It blocks
     * until the window is created.
     *
     * @param w the width of the drawing area
     * @param h the height of the drawing area
     */
    public SwingSimulationView(final int w, final int h) {
        runOnEdt(() -> {
            this.frame = new JFrame(TITLE);
            this.frame.setResizable(false);
            this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.panel = new VisualiserPanel();
            this.panel.setPreferredSize(new Dimension(w, h));
            this.frame.add(this.panel);
            this.frame.pack();
            this.frame.setLocationRelativeTo(null);
            this.frame.setVisible(true);
            this.panel.requestFocusInWindow();
        });
    }

    @Override
    public void init() {
        this.startTime = System.currentTimeMillis();
        this.nFramesRendered = 0;
    }

    @Override
    public void display(final List<Body> bodies, final double vt, final long iteration) {
        this.nFramesRendered++;
        final long elapsed = Math.max(1, System.currentTimeMillis() - this.startTime);
        final int framePerSec = (int) (this.nFramesRendered * MILLIS_PER_SECOND / elapsed);
        // The snapshot is taken by the simulation thread, so the EDT never reads the live model
        final List<BodyShape> shapes = bodies.stream()
                .map(b -> new BodyShape(b.getPos().x(), b.getPos().y(), b.getRadius()))
                .toList();
        runOnEdt(() -> {
            this.panel.display(shapes, vt, iteration, framePerSec);
            this.panel.repaint();
        });
    }

    @Override
    public void simulationOver(final long timeElapsed) {
        runOnEdt(() -> this.frame.setTitle(TITLE + " - over in " + timeElapsed + " ms"));
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
