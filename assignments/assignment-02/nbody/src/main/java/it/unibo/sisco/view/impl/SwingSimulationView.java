package it.unibo.sisco.view.impl;

import it.unibo.sisco.controller.api.Configuration;
import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.controller.api.SimulationEvents;
import it.unibo.sisco.model.api.Body;
import it.unibo.sisco.view.api.SimulationView;
import it.unibo.sisco.view.impl.panel.BodyShape;
import it.unibo.sisco.view.impl.panel.InitPanel;
import it.unibo.sisco.view.impl.panel.SimulationPanel;

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Swing implementation of {@link SimulationView}: a window that draws the
 * bodies and shows the virtual time, the number of steps and steps per second
 * of computation time.
 *
 * <p>
 * Every Swing component is created and updated on the Event Dispatch Thread.
 * {@link #display(List, long, double, long)} copies the positions of the bodies in
 * the calling thread and waits until the Event Dispatch Thread has received
 * them, so the simulation never runs ahead of the view. Closing the window
 * terminates the application.
 */
public final class SwingSimulationView implements SimulationView {

    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    private final Controller controller;
    private final JFrame frame;
    private JPanel currentPanel;
    /**
     * Builds and shows the window of the view. It blocks until the window is
     * created.
     *
     * @param controller the controller used to coordinate the simulation
     * @param w the width of the window
     * @param h the height of the window
     */
    public SwingSimulationView(final Controller controller, final int w, final int h) {
        this.controller = controller;
        this.frame = new JFrame("Bodies Simulation");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setMinimumSize(new Dimension(700, 500));
        this.frame.setSize(w, h);
        this.frame.setLocationByPlatform(true);
        this.frame.setAlwaysOnTop(true);
    }

    @Override
    public void init() {
        runOnEdt(() -> {
            this.currentPanel = new InitPanel(config -> controller.handleEvent(
                SimulationEvents.SET_CONFIGURATION,
                Optional.of(config)
            ));
            this.frame.setContentPane(this.currentPanel);
            this.frame.setVisible(true);
        });
    }

    @Override
    public void selectedConfiguration(final Configuration config) {
        runOnEdt(() -> {
            this.currentPanel = new SimulationPanel(
                config,
                event -> this.controller.handleEvent(event, Optional.empty())
            );
            this.frame.setContentPane(this.currentPanel);
            this.frame.revalidate();
            this.frame.repaint();
            SwingUtilities.invokeLater(
                () -> ((SimulationPanel) this.currentPanel).getDrawPanel().requestFocusInWindow()
            );
        });
    }

    @Override
    public void display(final List<Body> bodies, final long nanos, final double vt, final long iteration) {
        final int stepsPerSecond = nanos != 0
            ? (int) (iteration * NANOS_PER_SECOND / nanos)
            : 0;
        final List<BodyShape> shapes = bodies.stream()
            .map(b -> new BodyShape(b.getPos().x(), b.getPos().y(), b.getRadius()))
            .toList();
        runOnEdt(() -> {
            ((SimulationPanel) this.currentPanel).display(shapes, nanos, vt, iteration, stepsPerSecond);
        });
    }

    private static void runOnEdt(final Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
            return;
        }
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (final InvocationTargetException e) {
            throw new IllegalStateException("Error while updating the view", e.getCause());
        }
    }

    @Override
    public void simulationOver(final long timeElapsedNanos) {
        runOnEdt(() -> {
            if (this.currentPanel instanceof SimulationPanel panel)
                panel.showSimulationOver(timeElapsedNanos);
        });
    }
}
