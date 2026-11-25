package it.unibo.sisco.view.impl.panel;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.ParseException;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import it.unibo.sisco.controller.api.Configuration;
import it.unibo.sisco.model.api.SimulatorTypes;

/**
 * Panel displayed while the simulation is being initialized.
 */
public final class InitPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_ITERATIONS = 100;
    private static final int DEFAULT_BODIES = 5000;
    private static final int FIELD_COLUMNS = 10;
    private static final int PADDING = 8;
    private static final int SECTION_GAP = 16;
    private static final int MIN_VALUE = 1;
    private static final int STEP = 1;
    private static final float TITLE_FONT_SIZE = 24f;

    private final JComboBox<SimulatorTypes> simulators;
    private final JSpinner bodiesNumber;
    private final JSpinner iterationsNumber;
    private final transient Consumer<Configuration> onConfirm;

    /**
     * Creates the initialization panel.
     *
     * @param onConfirm callback invoked with the entered data when the confirm button is pressed
     */
    public InitPanel(final Consumer<Configuration> onConfirm) {
        this.onConfirm = Objects.requireNonNull(onConfirm);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.simulators = new JComboBox<>(SimulatorTypes.values());
        this.bodiesNumber = createNumberSpinner();
        this.bodiesNumber.setValue(DEFAULT_BODIES);
        this.iterationsNumber = createNumberSpinner();
        this.iterationsNumber.setValue(DEFAULT_ITERATIONS);

        final JLabel title = new JLabel("N-Body graphical simulator", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, TITLE_FONT_SIZE));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPanel form = new JPanel(new GridLayout(0, 2, PADDING, PADDING));
        form.add(new JLabel("Simulator:"));
        form.add(this.simulators);
        form.add(new JLabel("Bodies number:"));
        form.add(this.bodiesNumber);
        form.add(new JLabel("Iterations number:"));
        form.add(this.iterationsNumber);
        // Prevents BoxLayout from stretching the form to the full panel width
        form.setMaximumSize(form.getPreferredSize());
        form.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JButton confirmButton = new JButton("Confirm");
        confirmButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmButton.addActionListener(e -> confirm());

        // Glue at both ends keeps the content vertically centered
        this.add(Box.createVerticalGlue());
        this.add(title);
        this.add(Box.createVerticalStrut(SECTION_GAP));
        this.add(form);
        this.add(Box.createVerticalStrut(SECTION_GAP));
        this.add(confirmButton);
        this.add(Box.createVerticalGlue());
    }

    private static JSpinner createNumberSpinner() {
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(MIN_VALUE, MIN_VALUE, Integer.MAX_VALUE, STEP));
        final JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinner, "#");
        editor.getTextField().setColumns(FIELD_COLUMNS);
        editor.getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        spinner.setEditor(editor);
        return spinner;
    }

    private void confirm() {
        commit(this.bodiesNumber);
        commit(this.iterationsNumber);
        this.onConfirm.accept(new Configuration(
            (SimulatorTypes) this.simulators.getSelectedItem(),
            ((Number) this.bodiesNumber.getValue()).intValue(),
            ((Number) this.iterationsNumber.getValue()).intValue())
        );
    }

    /*
     * Applies the text currently typed in the spinner. If it is not valid,
     * the spinner keeps its last valid value.
     */
    private static void commit(final JSpinner spinner) {
        try {
            spinner.commitEdit();
        } catch (final ParseException e) {
            // Invalid text: the last valid value is kept
        }
    }
}