package it.unibo.sisco.view.impl.panel;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * Main panel shown while the simulation is running.
 */
public final class SimulationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int OPTIONS_PANEL_WIDTH = 300;

    private final DrawPanel drawPanel;

    /**
     * Creates the simulation panel with a reserved area for future options.
     */
    public SimulationPanel() {
        super(new BorderLayout());
        this.drawPanel = new DrawPanel();

        final JPanel optionsPanel = new JPanel();
        optionsPanel.setPreferredSize(new Dimension(OPTIONS_PANEL_WIDTH, 0));

        this.add(optionsPanel, BorderLayout.WEST);
        this.add(this.drawPanel, BorderLayout.CENTER);
    }

    /**
     * Returns the panel that draws the simulation.
     *
     * @return the drawing panel
     */
    public DrawPanel getDrawPanel() {
        return this.drawPanel;
    }
}
