package it.unibo.sisco.view.impl.panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.swing.JPanel;

/**
 * Panel that draws the bodies of the simulation as circles, centred on the
 * origin. The UP and DOWN arrow keys zoom in and out.
 *
 * <p>
 * Positions and radii are normalized: the range [-1, 1] covers the smaller
 * side of the panel (minus a margin) at zoom 1. It must be accessed only from
 * the Event Dispatch Thread.
 *
 * @serial exclude
 */
public final class DrawPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final double ZOOM_IN = 1.1;
    private static final double ZOOM_OUT = 0.9;
    private static final int MARGIN = 20;
    private static final int TEXT_X = 2;
    private static final int FIRST_LINE_Y = 20;
    private static final int SECOND_LINE_Y = 40;

    private transient List<BodyShape> shapes = List.of();
    private long iteration;
    private int framePerSec;
    private double vt;
    private double scale = 1;

    /**
     * Creates the simulation panel.
     */
    public DrawPanel() {
        super();
        this.setBackground(Color.WHITE);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    scale *= ZOOM_IN;
                    repaint();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    scale *= ZOOM_OUT;
                    repaint();
                }
            }
        });
        this.setFocusable(true);
        this.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Updates the stage to draw. The caller is responsible for repainting.
     *
     * @param newShapes the bodies to draw
     * @param time      the current virtual time
     * @param steps     the number of steps executed so far
     * @param fps       the frames rendered per second
     */
    public void display(final List<BodyShape> newShapes, final double time,
            final long steps, final int fps) {
        this.shapes = List.copyOf(Objects.requireNonNull(newShapes));
        this.vt = time;
        this.iteration = steps;
        this.framePerSec = fps;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setColor(Color.BLACK);

        final double half = Math.min(this.getWidth(), this.getHeight()) / 2.0 - MARGIN;
        final double centerX = this.getWidth() / 2.0;
        final double centerY = this.getHeight() / 2.0;
        final double k = half * this.scale;

        for (final BodyShape b : this.shapes) {
            final int radius = Math.max(1, (int) (b.radius() * k));
            final int x = (int) (centerX + b.x() * k) - radius;
            final int y = (int) (centerY - b.y() * k) - radius;
            g2.drawOval(x, y, radius * 2, radius * 2);
        }

        final String time = String.format(Locale.ROOT, "%.2f", this.vt);
        g2.drawString("Bodies: " + this.shapes.size() + " - vt: " + time
                + " - nIter: " + this.iteration + " - frames/sec: " + this.framePerSec,
                TEXT_X, FIRST_LINE_Y);
        g2.drawString("Commands: UP for zoom in, DOWN for zoom out", TEXT_X, SECOND_LINE_Y);
    }
}