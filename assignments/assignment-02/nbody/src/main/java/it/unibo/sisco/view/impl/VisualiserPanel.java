package it.unibo.sisco.view.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;

/**
 * Panel that draws the bodies. It must be accessed only from the Event Dispatch Thread.
 */
@SuppressFBWarnings(
    value = "SE_TRANSIENT_FIELD_NOT_RESTORED",
    justification = "Swing components of this view are never serialized"
)
final class VisualiserPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final double ZOOM_IN = 1.1;
    private static final double ZOOM_OUT = 0.9;
    private static final int MARGIN = 20;
    private static final int TEXT_X = 2;
    private static final int FIRST_LINE_Y = 20;
    private static final int SECOND_LINE_Y = 40;

    private final long dx;
    private final long dy;
    private transient List<BodyShape> bodies = List.of();
    private long iteration;
    private int framePerSec;
    private double vt;
    private double scale = 1;

    /**
     * Builds the panel.
     *
     * @param w the width of the panel
     * @param h the height of the panel
     */
    VisualiserPanel(final int w, final int h) {
        super();
        setSize(w, h);
        dx = w / 2 - MARGIN;
        dy = h / 2 - MARGIN;
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    scale *= ZOOM_IN;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    scale *= ZOOM_OUT;
                }
            }
        });
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        requestFocusInWindow();
    }

    /**
     * Updates the stage to draw.
     *
     * @param shapes the bodies to draw
     * @param time the current virtual time
     * @param steps the number of steps executed so far
     * @param fps the frames rendered per second
     */
    void display(final List<BodyShape> shapes, final double time, final long steps, final int fps) {
        this.bodies = List.copyOf(shapes);
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
        g2.clearRect(0, 0, getWidth(), getHeight());
        for (final BodyShape b : bodies) {
            final int radius = Math.max(1, (int) (b.radius() * dx * scale));
            g2.drawOval(getXcoord(b.x()), getYcoord(b.y()), radius, radius);
        }
        final String time = String.format(Locale.ROOT, "%.2f", vt);
        g2.drawString("Bodies: " + bodies.size() + " - vt: " + time + " - nIter: " + iteration
            + " - frames/sec: " + framePerSec, TEXT_X, FIRST_LINE_Y);
        g2.drawString("Commands: UP for zoom in, DOWN for zoom out", TEXT_X, SECOND_LINE_Y);
    }

    private int getXcoord(final double x) {
        return (int) (dx + x * dx * scale);
    }

    private int getYcoord(final double y) {
        return (int) (dy - y * dy * scale);
    }
}
