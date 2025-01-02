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
import selector.SelectionModel.SelectionState;

/**
 * A graphical application for selecting and extracting regions of images.
 */
public class SelectorApp implements PropertyChangeListener {


    /**
     * Our application window.  Disposed when application exits.
     */
    private final JFrame frame;

    /**
     * Component for displaying the current image and selection tool.
     */
    private final ImagePanel imgPanel;

    /**
     * The current state of the selection tool.  Must always match the model used by `imgPanel`.
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
     * Construct a new application instance.  Initializes GUI components, so must be invoked on the
     * Swing Event Dispatch Thread.  Does not show the application window (call `start()` to do
     * that).
     */
    public SelectorApp() {
        // Initialize application window
        frame = new JFrame("Selector");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Add status bar
        statusLabel = new JLabel();
        // TODO 1A: Add `statusLabel` to the bottom of our window.  Stylistic alteration of the
        //  label (i.e., custom fonts and colors) is allowed.
        //  See the BorderLayout tutorial [1] for example code that you can adapt.
        //  [1]: https://docs.oracle.com/javase/tutorial/uiswing/layout/border.html

        // Add status bar at the bottom of the window
        frame.add(statusLabel, BorderLayout.SOUTH);
        statusLabel.setText("Ready"); // Initial message

        // Add image component with scrollbars
        imgPanel = new ImagePanel();
        // TODO 1B: Replace the following line with code to put scroll bars around `imgPanel` while
        //  otherwise keeping it in the center of our window.  The scroll pane should also be given
        //  a moderately large preferred size (e.g., between 400 and 700 pixels wide and tall).
        //  The Swing Tutorial has lots of info on scrolling [1], but for this task you only need
        //  the basics from lecture.
        //  [1] https://docs.oracle.com/javase/tutorial/uiswing/components/scrollpane.html

        // Add image component with scrollbars
        JScrollPane scrollPane = new JScrollPane(imgPanel);
        scrollPane.setPreferredSize(new Dimension(600, 600)); // Set preferred size
        frame.add(scrollPane, BorderLayout.CENTER);


        // Add menu bar
        frame.setJMenuBar(makeMenuBar());

        // Add control buttons
        // TODO 3E: Call `makeControlPanel()`, then add the result to the window next to the image.
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

        // TODO (embellishment): Assign keyboard shortcuts to menu items [1].  (0.5 points)
        //  [1] https://docs.oracle.com/javase/tutorial/uiswing/components/menu.html#mnemonic

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
     * Return a panel containing buttons for controlling image selection.  Should only be called
     * from constructor, as it initializes button fields.
     */
    private JPanel makeControlPanel() {
        // TODO 3D: Create and return a panel containing the Cancel, Undo, Reset, and Finish
        //  buttons (remember that these buttons are fields).  Activating the buttons should call
        //  `cancelProcessing()`, `undo()`, `reset()`, and `finishSelection()` on the selection
        //  model, respectively.  You may arrange and style the buttons however you like (so long as
        //  they are usable); a vertical grid [2] is a good place to start.  See `makeMenuBar()`
        //  above for inspiration.
        //  The JPanel tutorial [1] shows how to set a layout manager and add components to a panel.
        //  You are welcome to add borders, labels, and subpanels to improve its appearance.
        //  The Visual Guide to Layout Managers [3] might give you other ideas for how to arrange
        //  the buttons.
        //  [1] https://docs.oracle.com/javase/tutorial/uiswing/components/panel.html
        //  [2] https://docs.oracle.com/javase/tutorial/uiswing/layout/grid.html
        //  [3] https://docs.oracle.com/javase/tutorial/uiswing/layout/visual.html
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5)); // Grid layout with 4 rows

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
        JComboBox<String> modelSelector = new JComboBox<>(new String[]{"Point-to-point", "Spline", "Circle"});
        modelSelector.addActionListener(e -> {
            String selection = (String) modelSelector.getSelectedItem();
            if (selection.equals("Point-to-point")) {
                setSelectionModel(new PointToPointSelectionModel(model)); // Switch to Point-to-point model
            } else if (selection.equals("Spline")) {
                setSelectionModel(new SplineSelectionModel(model)); // Switch to Spline model
            } else if (selection.equals("Circle")) {
                setSelectionModel(new CircleSelectionModel(true)); // Switch to Circle model
            }
        });
        panel.add(modelSelector); // Add combo box to the panel


        return panel;
        // TODO 3G: Add a JComboBox to your control panel that allows the user to choose between
        //  different selection models, including "Point-to-point" and "Spline" (and "Circle" if you
        //  choose to attempt the challenge extension).  Add a listener to the box that will call
        //  `setSelectionModel()` with a new instance of the selected model.  Pass the previous
        //  `model` object as the constructor argument so that the new model copies any state it can
        //  from the old one.
        //  The JComboBox tutorial [1] will show you how to add items and listeners to the combo
        //  box.  You may label the items however you like as long as it's clear which selection
        //  model they refer to.
        //  [1] https://docs.oracle.com/javase/tutorial/uiswing/components/combobox.html

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
     * React to property changes in an observed model.  Supported properties include:
     * * "state": Update components to reflect the new selection state.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
                reflectSelectionState(model.state());
        }
    }

    /**
     * Update components to reflect a selection state of `state`.  Disable buttons and menu items
     * whose actions are invalid in that state, and update the status bar.
     */
    private void reflectSelectionState(SelectionState state) {
        // Update status bar to show current state
        statusLabel.setText(state.toString());

        // TODO 3F: Enable/disable components (both buttons and menu items) as follows:
        //  * Cancel is only allowed when the selection is processing
        //  * Reset is not allowed when there is no selection
        //  * Adding, undoing, and finishing are only allowed is states that allow them
        //  * Saving is only allowed when the selection is complete
        //  The JButton tutorial [1] shows an example of enabling buttons in an event handler.
        //  [1] https://docs.oracle.com/javase/tutorial/uiswing/components/button.html

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
     * Use `newModel` as the selection tool and update our view to reflect its state.  This
     * application will no longer respond to changes made to its previous selection model and will
     * instead respond to property changes from `newModel`.
     */
    public void setSelectionModel(SelectionModel newModel) {
        // Stop listening to old model
        if (model != null) {
            model.removePropertyChangeListener(this);
        }

        imgPanel.setSelectionModel(newModel);
        model = imgPanel.selection();
        model.addPropertyChangeListener("state", this);

        // Since the new model's initial state may be different from the old model's state, manually
        //  trigger an update to our state-dependent view.
        reflectSelectionState(model.state());
    }

    /**
     * Start displaying and selecting from `img` instead of any previous image.  Argument may be
     * null, in which case no image is displayed and the current selection is reset.
     */
    public void setImage(BufferedImage img) {
        imgPanel.setImage(img);
    }

    /**
     * Allow the user to choose a new image from an "open" dialog.  If they do, start displaying and
     * selecting from that image.  Show an error message dialog (and retain any previous image) if
     * the chosen image could not be opened.
     */
    private void openImage() {
        JFileChooser chooser = new JFileChooser();
        // Start browsing in current directory
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        // Filter for file extensions supported by Java's ImageIO readers
        chooser.setFileFilter(new FileNameExtensionFilter("Image files",
                ImageIO.getReaderFileSuffixes()));

        // TODO 1C: Complete this method as specified by performing the following tasks:
        //  * Show an "open file" dialog using the above chooser [1].
        //  * If the user selects a file, read it into a BufferedImage [2], then set that as the
        //    current image (by calling `this.setImage()`).
        //  * If a problem occurs when reading the file (either an exception is thrown or null is
        //    returned), show an error dialog with a descriptive title and message [3].
        //  [1] https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
        //  [2] https://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
        //  [3] https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
        // TODO (embellishment): After a problem, re-show the open dialog.  By reusing the same
        //  chooser, the dialog will show the same directory as before the problem. (0.5 points)
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            try {
                BufferedImage img = ImageIO.read(selectedFile);
                if (img == null) {
                    throw new IOException("File is not a valid image");
                }
                setImage(img); // Display the image in the application
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
        // Start browsing in current directory
        chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        // We always save in PNG format, so only show existing PNG files
        chooser.setFileFilter(new FileNameExtensionFilter("PNG images", "png"));

        // TODO 3H: Complete this method as specified by performing the following tasks:
        //  * Show a "save file" dialog using the above chooser [1].
        //  * If the user selects a file, write an image containing the selected pixels to the file.
        //  * If a problem occurs when opening or writing to the file, show an error dialog with the
        //    class of the exception as its title and the exception's message as its text [2].
        //  [1] https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
        //  [2] https://docs.oracle.com/javase/tutorial/uiswing/components/dialog.html
        // TODO (embellishment; 1 point):
        //  * If the selected filename does not end in ".png", append that extension.
        //  * Prompt with a yes/no/cancel dialog before overwriting a file.
        //  * After an IOException, or after user selects "No" (instead of "Cancel") when prompted,
        //    re-show the save dialog.  By reusing the same chooser, the dialog will show the same
        //    directory as before the problem.
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
                break; // User canceled
            }
        }
    }

    /**
     * Run an instance of SelectorApp.  No program arguments are expected.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Set Swing theme to look the same (and less old) on all operating systems.
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
