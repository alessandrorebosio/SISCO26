package it.unibo.sisco.view.impl;

import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.model.api.SimulatorTypes;
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
            frame = new JFrame(TITLE);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            panel = new VisualiserPanel();
            panel.setPreferredSize(new Dimension(w, h));
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
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
        final int framePerSec = (int) (nFramesRendered * MILLIS_PER_SECOND / elapsed);
        // The snapshot is taken by the simulation thread, so the EDT never reads the live model
        final List<BodyShape> shapes = bodies.stream()
                .map(b -> new BodyShape(b.getPos().x(), b.getPos().y(), b.getRadius()))
                .toList();
        runOnEdt(() -> {
            panel.display(shapes, vt, iteration, framePerSec);
            panel.repaint();
        });
    }

    @Override
    public void selectedSimulator(final SimulatorTypes simulator) {
        runOnEdt(() -> frame.setTitle(TITLE + " - " + simulator));
        // TODO
    }

    @Override
    public void simulationOver(final long timeElapsed) {
        runOnEdt(() -> frame.setTitle(frame.getTitle() + " - over in " + timeElapsed + " ms"));
        // TODO
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
