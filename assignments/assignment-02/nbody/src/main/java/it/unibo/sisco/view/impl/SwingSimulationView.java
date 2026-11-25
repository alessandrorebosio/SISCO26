package it.unibo.sisco.view.impl;

import it.unibo.sisco.controller.api.ControlState;
import it.unibo.sisco.controller.api.Controller;
import it.unibo.sisco.model.api.SimulationState;
import it.unibo.sisco.view.api.SimulationView;
import it.unibo.sisco.view.impl.panel.VisualiserPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Swing implementation of {@link SimulationView}: a window that draws the
 * bodies and shows the virtual time, the number of steps and the frames
 * rendered per second. The Start, Pause/Resume, Step and Stop buttons below
 * the drawing send the commands to the controller, and only the ones allowed in
 * the current state are enabled.
 *
 * <p>
 * Every Swing component is created and updated on the Event Dispatch Thread.
 * {@link #display(SimulationState)} hands the snapshot of the bodies to the
 * Event Dispatch Thread and waits until it has received it, so the simulation
 * never runs ahead of the view. Closing the window terminates the
 * application.
 */
public final class SwingSimulationView implements SimulationView {

    private static final String TITLE = "Bodies Simulation";
    private static final double MILLIS_PER_SECOND = 1000.0;
    private static final int GAP = 8;

    private JFrame frame;
    private VisualiserPanel panel;
    // The controller and the buttons are used only on the Event Dispatch Thread
    private Controller controller;
    private ControlState controlState = ControlState.READY;
    private JButton start;
    private JButton pause;
    private JButton step;
    private JButton stop;
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
            this.frame.add(this.panel, BorderLayout.CENTER);

            // One row of buttons of the same width, enabled by the state of the simulation
            this.start = new JButton("Start");
            this.pause = new JButton("Pause");
            this.step = new JButton("Step");
            this.stop = new JButton("Stop");
            this.start.addActionListener(e -> this.controller.start());
            this.pause.addActionListener(e -> {
                if (this.controlState == ControlState.PAUSED) {
                    this.controller.resume();
                } else {
                    this.controller.pause();
                }
            });
            this.step.addActionListener(e -> this.controller.step());
            this.stop.addActionListener(e -> this.controller.stop());
            final JPanel buttons = new JPanel(new GridLayout(1, 0, GAP, 0));
            buttons.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
            for (final JButton b : List.of(this.start, this.pause, this.step, this.stop)) {
                // The buttons never take the focus, which stays on the panel for the zoom keys
                b.setFocusable(false);
                b.setEnabled(false);
                buttons.add(b);
            }
            this.frame.add(buttons, BorderLayout.SOUTH);
            this.frame.pack();
            this.frame.setLocationRelativeTo(null);
            this.frame.setVisible(true);
            this.panel.requestFocusInWindow();
        });
    }

    @Override
    public void setController(final Controller newController) {
        runOnEdt(() -> this.controller = newController);
    }

    @Override
    public void controlChanged(final ControlState newState) {
        // invokeLater, not invokeAndWait: the caller may be the Event Dispatch Thread or hold the monitor lock
        SwingUtilities.invokeLater(() -> {
            this.controlState = newState;
            this.start.setEnabled(newState == ControlState.READY);
            this.pause.setEnabled(newState == ControlState.RUNNING || newState == ControlState.PAUSED);
            this.pause.setText(newState == ControlState.PAUSED ? "Resume" : "Pause");
            this.step.setEnabled(newState == ControlState.READY || newState == ControlState.PAUSED);
            this.stop.setEnabled(newState != ControlState.STOPPED);
        });
    }

    @Override
    public void init() {
        this.startTime = System.currentTimeMillis();
        this.nFramesRendered = 0;
    }

    @Override
    public void display(final SimulationState state) {
        this.nFramesRendered++;
        final long elapsed = Math.max(1, System.currentTimeMillis() - this.startTime);
        final int framePerSec = (int) (this.nFramesRendered * MILLIS_PER_SECOND / elapsed);
        runOnEdt(() -> {
            this.panel.display(state.bodies(), state.vt(), state.iteration(), framePerSec);
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
