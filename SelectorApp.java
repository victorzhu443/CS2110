package selector;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import scissors.ScissorsSelectionModel;
import selector.SelectionModel.SelectionState;

/**
 * A graphical application for selecting and extracting regions of images.
 */
public class SelectorApp implements PropertyChangeListener {

    // New in A6
    /**
     * Progress bar to indicate the progress of a model that needs to do long calculations in a
     * "processing" state.
     */
    private JProgressBar processingProgress;

    /**
     * Our application window. Disposed when application exits.
     */
    private final JFrame frame;

    /**
     * Component for displaying the current image and selection tool.
     */
    private final ImagePanel imgPanel;

    /**
     * The current state of the selection tool. Must always match the model used by `imgPanel`.
     */
    private SelectionModel model;

    /* Components whose state must be changed during the selection process. */
    private JMenuItem saveItem;
    private JMenuItem undoItem;
    private JButton cancelButton;
    private JButton undoButton;
    private JButton resetButton;
    private JButton finishButton;
    private final JLabel statusLabel;

    /**
     * Construct a new application instance. Initializes GUI components, so must be invoked on the
     * Swing Event Dispatch Thread. Does not show the application window (call `start()` to do
     * that).
     */
    public SelectorApp() {
        // Initialize application window
        frame = new JFrame("Selector");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add status bar
        statusLabel = new JLabel();
        frame.add(statusLabel, BorderLayout.SOUTH);
        statusLabel.setText("Ready"); // Initial message

        // Add progress bar
        processingProgress = new JProgressBar();
        processingProgress.setStringPainted(true); // Show percentage as text
        frame.add(processingProgress, BorderLayout.PAGE_START);

        // Add image component with scrollbars
        imgPanel = new ImagePanel();
        JScrollPane scrollPane = new JScrollPane(imgPanel);
        scrollPane.setPreferredSize(new Dimension(600, 600)); // Set preferred size
        frame.add(scrollPane, BorderLayout.CENTER);

        // Add menu bar
        frame.setJMenuBar(makeMenuBar());

        // Add control buttons
        frame.add(makeControlPanel(), BorderLayout.EAST);

        // Controller: Set initial selection tool and update components to reflect its state
        setSelectionModel(new PointToPointSelectionModel(true));
    }

    /**
     * Create and populate a menu bar with our application's menus and items and attach listeners.
     * Should only be called from constructor, as it initializes menu item fields.
     */
    private JMenuBar makeMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create and populate File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        JMenuItem openItem = new JMenuItem("Open...");
        fileMenu.add(openItem);
        saveItem = new JMenuItem("Save...");
        fileMenu.add(saveItem);
        JMenuItem closeItem = new JMenuItem("Close");
        fileMenu.add(closeItem);
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);

        // Create and populate Edit menu
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
        undoItem = new JMenuItem("Undo");
        editMenu.add(undoItem);

        // Assign keyboard shortcuts
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));

        // Controller: Attach menu item listeners
        openItem.addActionListener(e -> openImage());
        closeItem.addActionListener(e -> imgPanel.setImage(null));
        saveItem.addActionListener(e -> saveSelection());
        exitItem.addActionListener(e -> frame.dispose());
        undoItem.addActionListener(e -> model.undo());

        return menuBar;
    }

    /**
     * Return a panel containing buttons for controlling image selection. Should only be called
     * from constructor, as it initializes button fields.
     */
    private JPanel makeControlPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 5, 5)); // Adjust grid for buttons and combo box

        // Initialize control buttons
        cancelButton = new JButton("Cancel");
        undoButton = new JButton("Undo");
        resetButton = new JButton("Reset");
        finishButton = new JButton("Finish");

        // Add action listeners
        cancelButton.addActionListener(e -> model.cancelProcessing());
        undoButton.addActionListener(e -> model.undo());
        resetButton.addActionListener(e -> model.reset());
        finishButton.addActionListener(e -> model.finishSelection());

        panel.add(cancelButton);
        panel.add(undoButton);
        panel.add(resetButton);
        panel.add(finishButton);

        // Add combo box for selection models
        JComboBox<String> modelSelector = new JComboBox<>(new String[]{
                "Point-to-point", "Spline", "Intelligent Scissors: Gray", "Intelligent Scissors: Color"
        });

        // Add listener to handle model selection
        modelSelector.addActionListener(e -> {
            String selection = (String) modelSelector.getSelectedItem();
            switch (selection) {
                case "Point-to-point" -> setSelectionModel(new PointToPointSelectionModel(true));
                case "Spline" -> setSelectionModel(new SplineSelectionModel(true));
                case "Intelligent Scissors: Gray" ->
                        setSelectionModel(new ScissorsSelectionModel("CrossGradMono", true));
                case "Intelligent Scissors: Color" -> // New "CrossGradColor" option
                        setSelectionModel(new ScissorsSelectionModel("CrossGradColor", true));
            }
        });

        panel.add(modelSelector); // Add combo box to the panel

        return panel;
    }

    /**
     * Start the application by showing its window.
     */
    public void start() {
        // Compute ideal window size
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * React to property changes in an observed model. Supported properties include:
     * * "state": Update components to reflect the new selection state.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();

        if ("state".equals(propertyName)) {
            reflectSelectionState(model.state());

            // Manage progress bar visibility based on state
            if (model.state().isProcessing()) {
                processingProgress.setIndeterminate(true);
                processingProgress.setVisible(true); // Ensure visibility
            } else {
                processingProgress.setIndeterminate(false);
                processingProgress.setValue(0);
                processingProgress.setVisible(false);
            }
        } else if ("progress".equals(propertyName)) {
            // Update progress bar value
            int progress = (int) evt.getNewValue();
            processingProgress.setIndeterminate(false);
            processingProgress.setValue(progress);
        }
    }

    /**
     * Update components to reflect a selection state of `state`. Disable buttons and menu items
     * whose actions are invalid in that state, and update the status bar.
     */
    private void reflectSelectionState(SelectionState state) {
        statusLabel.setText(state.toString());
        cancelButton.setEnabled(state.isProcessing());
        undoButton.setEnabled(state.canUndo());
        resetButton.setEnabled(!state.isEmpty());
        finishButton.setEnabled(state.canFinish());
        saveItem.setEnabled(state.isFinished());
    }

    /**
     * Return the model of the selection tool currently in use.
     */
    public SelectionModel getSelectionModel() {
        return model;
    }

    /**
     * Use `newModel` as the selection tool and update our view to reflect its state. This
     * application will no longer respond to changes made to its previous selection model and will
     * instead respond to property changes from `newModel`.
     */
    public void setSelectionModel(SelectionModel newModel) {
        if (model != null) {
            model.removePropertyChangeListener(this);
        }

        imgPanel.setSelectionModel(newModel);
        model = imgPanel.selection();
        model.addPropertyChangeListener("state", this);
        reflectSelectionState(model.state());

        // New in A6: Listen for "progress" events
        model.addPropertyChangeListener("progress", this);
    }

    /**
     * Start displaying and selecting from `img` instead of any previous image. Argument may be
     * null, in which case no image is displayed and the current selection is reset.
     */
    public void setImage(BufferedImage img) {
        imgPanel.setImage(img);
    }

    /**
     * Allow the user to choose a new image from an "open" dialog. If they do, start displaying and
     * selecting from that image. Show an error message dialog (and retain any previous image) if
     * the chosen image could not be opened.
     */
    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setFileFilter(new FileNameExtensionFilter("Image files",
                ImageIO.getReaderFileSuffixes()));

        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedFile);
                if (img == null) {
                    throw new IOException("File is not a valid image");
                }
                setImage(img);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame,
                        "Failed to open image: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Save the selected region of the current image to a file selected from a "save" dialog.
     * Show an error message dialog if the image could not be saved.
     */
    private void saveSelection() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        chooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));

        while (true) {
            int result = chooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = chooser.getSelectedFile();
                    if (!file.getName().endsWith(".png")) {
                        file = new File(file.getAbsolutePath() + ".png");
                    }
                    if (file.exists()) {
                        int overwrite = JOptionPane.showConfirmDialog(frame,
                                "The file already exists. Overwrite?", "Confirm Overwrite",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (overwrite != JOptionPane.YES_OPTION) {
                            continue;
                        }
                    }
                    try (OutputStream out = new FileOutputStream(file)) {
                        model.saveSelection(out);
                    }
                    return; // Success
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Could not save the file: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    /**
     * Run an instance of SelectorApp. No program arguments are expected.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {
                /* If the Nimbus theme isn't available, just use the platform default. */
            }

            // Create and start the app
            SelectorApp app = new SelectorApp();
            app.start();
        });
    }
}
