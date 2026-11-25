package it.unibo.sisco.view.impl.panel;

import it.unibo.sisco.model.api.BodyState;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;
import javax.swing.JPanel;

/**
 * Panel that draws the bodies, centred on the origin, below a header with the
 * statistics of the simulation. The UP and DOWN arrow keys zoom in and out.
 *
 * <p>
 * It must be accessed only from the Event Dispatch Thread.
 *
 * @serial exclude
 */
public final class VisualiserPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final double ZOOM_IN = 1.1;
    private static final double ZOOM_OUT = 0.9;
    private static final int HEADER_HEIGHT = 50;
    private static final int MARGIN = 10;
    private static final int TEXT_X = 5;
    private static final int FIRST_LINE_Y = 20;
    private static final int SECOND_LINE_Y = 40;

    private BodyState[] bodies = new BodyState[0];
    private long iteration;
    private int framePerSec;
    private double vt;
    private double scale = 1;

    /**
     * Builds the panel and enables the zoom keys.
     */
    public VisualiserPanel() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    VisualiserPanel.this.scale *= ZOOM_IN;
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    VisualiserPanel.this.scale *= ZOOM_OUT;
                }
                repaint();
            }
        });
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
    }

    /**
     * Updates the stage to draw; the panel is redrawn at the next repaint.
     *
     * @param states the bodies to draw, which are copied
     * @param time   the current virtual time
     * @param steps  the number of steps executed so far
     * @param fps    the frames rendered per second
     */
    public void display(final List<BodyState> states, final double time, final long steps, final int fps) {
        this.bodies = states.toArray(new BodyState[0]);
        this.vt = time;
        this.iteration = steps;
        this.framePerSec = fps;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBodies(g2);
        drawHeader(g2);
    }

    /*
     * The bodies are drawn in the area below the header: the origin is in its
     * centre and the square [-1, 1] x [-1, 1] fits in it, with a margin on every
     * side, when the zoom is 1.
     */
    private void drawBodies(final Graphics2D g2) {
        final int areaHeight = getHeight() - HEADER_HEIGHT;
        final double centreX = getWidth() / 2.0;
        final double centreY = HEADER_HEIGHT + areaHeight / 2.0;
        final double unit = (Math.min(getWidth(), areaHeight) / 2.0 - MARGIN) * this.scale;

        final Graphics2D canvas = (Graphics2D) g2.create();
        canvas.clipRect(0, HEADER_HEIGHT, getWidth(), areaHeight);
        for (final BodyState b : this.bodies) {
            // The radius of the model is drawn as the diameter, so the bodies are easier to tell apart
            final int diameter = Math.max(2, (int) Math.round(b.radius() * unit));
            final int x = (int) Math.round(centreX + b.x() * unit - diameter / 2.0);
            final int y = (int) Math.round(centreY - b.y() * unit - diameter / 2.0);
            canvas.drawOval(x, y, diameter, diameter);
        }
        canvas.dispose();
    }

    private void drawHeader(final Graphics2D g2) {
        g2.drawString(String.format(Locale.ROOT, "Bodies: %d - vt: %.2f - nIter: %d - frames/sec: %d",
                this.bodies.length, this.vt, this.iteration, this.framePerSec), TEXT_X, FIRST_LINE_Y);
        g2.drawString("Commands: UP for zoom in, DOWN for zoom out", TEXT_X, SECOND_LINE_Y);
    }
}
