package selector;

import static org.junit.jupiter.api.Assertions.*;
import static selector.PointToPointSelectionModel.PointToPointState.*;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import selector.SelectionModel.SelectionState;

/**
 * A test suite for `PointToPointSelectionModel`, which also covers inherited methods defined in
 * `SelectionModel`.
 */
class PointToPointSelectionModelTest {

    // Note: All selection models are constructed with `notifyOnEdt=false` so that property change
    //  listeners will be notified immediately on the test thread, rather than asynchronously on
    //  Swing's EDT.

    @DisplayName("WHEN a new model is constructed without providing a previous selection, THEN it "
            + "will be in the NO_SELECTION state, AND its selection will be empty")
    @Test
    void testDefaultConstruction() {
        SelectionModel model = new PointToPointSelectionModel(false);
        assertEquals(NO_SELECTION, model.state());
        assertTrue(model.selection().isEmpty());
    }


    @DisplayName(
            "[Task 2A] GIVEN a model in the NO_SELECTION state, WHEN a point is added, THEN the model "
                    + "will transition to the SELECTING state, notifying listeners that its 'state' "
                    + "property has changed, AND the selection will still be empty, AND the model's last "
                    + "point will be the provided point.")
    @Test
    void testStart() {
        // Set up the test scenario and start listening for events
        SelectionModel model = new PointToPointSelectionModel(false);
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        // Perform the test action
        Point newPoint = new Point(0, 0);
        model.addPoint(newPoint);

        // Verify the consequences
        // Note: A point-to-point model should never enter PROCESSING, so no need to wait
        observer.assertChangedTo("state", SELECTING);
        assertEquals(SELECTING, model.state());

        observer.assertNotChanged("selection");
        assertTrue(model.selection().isEmpty());

        assertEquals(newPoint, model.lastPoint());
    }


    @DisplayName(
            "[Task 2B] GIVEN a model whose start point has been chosen but whose selection is currently "
                    + "empty, WHEN a live wire is requested to a location, THEN it will return a straight "
                    + "line segment from its start to the location.")
    @Test
    void testLiveWireStarting() {
        // Set up the test scenario
        SelectionModel model = new PointToPointSelectionModel(false);
        Point startPoint = new Point(0, 0);
        model.addPoint(startPoint);

        // Perform the test action
        Point mouseLocation = new Point(1, 2);
        PolyLine wire = model.liveWire(mouseLocation);

        // Verify the consequences
        PolyLine expectedWire = new PolyLine(startPoint, mouseLocation);
        assertEquals(expectedWire, wire);
    }

    @DisplayName(
            "[Task 2E] GIVEN a model whose start point has been chosen but whose selection is currently "
                    + "empty, WHEN an undo is requested, THEN it will transition to the NO_SELECTION "
                    + "state AND its selection will still be empty.")
    @Test
    void testUndoStarting() {
        // Set up the test scenario
        SelectionModel model = new PointToPointSelectionModel(false);
        model.addPoint(new Point(0, 0));

        // Only listen for events after we are done with test setup
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        // Perform the test action
        model.undo();

        // Verify the consequences
        observer.assertChangedTo("state", NO_SELECTION);
        assertEquals(NO_SELECTION, model.state());

        assertTrue(model.selection().isEmpty());
    }

    @DisplayName(
            "[Task 2C] GIVEN a model in the SELECTING state, WHEN a point is added, THEN the model "
                    + "will remain in the SELECTING state, AND listeners will be notified that the "
                    + "selection has changed, AND the selection will end with a straight line segment to "
                    + "the new point, AND the model's last point will be the provided point.")
    @Test
    void testAppend() {
        // Set up the test scenario
        SelectionModel model = new PointToPointSelectionModel(false);
        model.addPoint(new Point(0, 0));

        // Only listen for events after we are done with test setup
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        // Perform the test action
        Point newPoint = new Point(1, 2);
        model.addPoint(newPoint);

        // Verify the consequences
        observer.assertNotChanged("state");
        assertEquals(SELECTING, model.state());

        observer.assertChanged("selection");
        assertEquals(1, model.selection().size());
        PolyLine lastSegment = model.selection().getLast();
        // A straight segment should only have two points
        assertEquals(2, lastSegment.size());
        assertEquals(newPoint, lastSegment.end());

        assertEquals(newPoint, model.lastPoint());
    }

    // TODO 2D: Add a test case covering `liveWire()` when the selection path is non-empty.  Check
    //  returned value.

    @Test
    void testLiveWireWithSegments() {
        PointToPointSelectionModel model = new PointToPointSelectionModel(true);
        Point start = new Point(10, 10);
        Point mid = new Point(20, 20);
        Point end = new Point(30, 30);

        model.addPoint(start);
        model.addPoint(mid);

        PolyLine liveWire = model.liveWire(end);
        assertEquals(mid, liveWire.start(), "Live wire should start at the last control point");
        assertEquals(end, liveWire.end(), "Live wire should end at the new point");
    }

    // TODO 2F: Add a test case covering `undo()` when the selection path is non-empty.  Check
    //  expected state, absence of state change notification, expected selection size, occurrence of
    //  selection change notification, and expected last point.  See `testUndoSelected()` for
    //  inspiration.


    @Test
    void testUndoInProgress() {
        // Set up a selection model with points added (but not finished)
        PointToPointSelectionModel model = new PointToPointSelectionModel(true);
        model.addPoint(new Point(0, 0));  // Starting point
        model.addPoint(new Point(10, 0)); // First segment
        model.addPoint(new Point(10, 10)); // Second segment

        // Verify initial state
        assertTrue(model.state().canAddPoint()); // Indicates the state is SELECTING
        assertFalse(model.state().isFinished()); // Not yet finished
        assertEquals(2, model.selection().size());
        assertEquals(new Point(10, 10), model.lastPoint());

        // Add a listener for property change notifications
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        // Perform undo action
        model.undo();

        // Verify that the state remains SELECTING (no state change notification)
        observer.assertNotChanged("state");
        assertTrue(model.state().canAddPoint()); // Still SELECTING

        // Verify that the selection path size decreases by 1
        observer.assertChanged("selection");
        assertEquals(1, model.selection().size());

        // Verify that the last point is updated correctly
        assertEquals(new Point(10, 0), model.lastPoint());
    }

    @DisplayName("GIVEN a model in the SELECTING state with a non-empty selection path, WHEN the "
            + "selection is finished, THEN it will transition to the SELECTED state, notifying "
            + "listeners that its 'state' property has changed, AND its selection path will have "
            + "one additional segment, ending at its start point, AND listeners will be notified "
            + "that its selection has changed")
    @Test
    void testFinishSelection() {
        // Set up the test scenario
        PointToPointSelectionModel model = new PointToPointSelectionModel(false);
        model.addPoint(new Point(0, 0));
        model.addPoint(new Point(10, 0));
        model.addPoint(new Point(10, 10));
        model.addPoint(new Point(0, 10));

        // Only listen for events after we are done with test setup
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        // Perform the test action
        model.finishSelection();

        // Verify the consequences
        observer.assertChangedTo("state", SELECTED);
        assertEquals(SELECTED, model.state());

        observer.assertChanged("selection");
        assertEquals(4, model.selection().size());
        assertEquals(new Point(0, 0), model.lastPoint());


    }


    // Now that we've tested `finishSelection()`, we can define a helper method that uses it.

    /**
     * Return a selection model in the SELECTED state whose selection path consists of 4
     * straight-line segments forming a square.  The path starts and ends at (0,0), the first
     * segments connects the start to (10,0), and the last segment connects (0,10) back to the
     * start.
     */
    static PointToPointSelectionModel makeSquareSelection() {
        PointToPointSelectionModel model = new PointToPointSelectionModel(false);
        model.addPoint(new Point(0, 0));
        model.addPoint(new Point(10, 0));
        model.addPoint(new Point(10, 10));
        model.addPoint(new Point(0, 10));
        model.finishSelection();
        return model;
    }

    @DisplayName(
            "[Task 2E] GIVEN a selection, WHEN an undo is requested, THEN it will transition to the "
                    + "SELECTING state, notifying listeners that its 'state' property has changed, AND its "
                    + "selection path will have one fewer segment, ending at its previous penultimate "
                    + "point, AND listeners will be notified that its selection has changed.")
    @Test
    void testUndoSelected() {
        // Set up the test scenario
        SelectionModel model = makeSquareSelection();

        // Only listen for events after we are done with test setup
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        // Perform the test action
        model.undo();

        // Verify the consequences
        observer.assertChangedTo("state", SELECTING);
        assertEquals(SELECTING, model.state());

        observer.assertChanged("selection");
        assertEquals(3, model.selection().size());
        assertEquals(new Point(0, 10), model.lastPoint());
    }


    /* Tests of movePoint() */

    @DisplayName(
            "[Task 4C] GIVEN a selection, WHEN a point in the middle of the selection path is moved, "
                    + "THEN the two segments joined at that point will have their start or end moved to "
                    + "the new location as appropriate.")
    @Test
    void testMovePointMiddle() {
        SelectionModel model = makeSquareSelection();
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        model.movePoint(1, new Point(11, 12));
        observer.assertChanged("selection");
        PolyLine beforeSegment = model.selection().get(0);
        PolyLine afterSegment = model.selection().get(1);
        assertEquals(new Point(0, 0), beforeSegment.start());
        assertEquals(new Point(11, 12), beforeSegment.end());
        assertEquals(new Point(11, 12), afterSegment.start());
        assertEquals(new Point(10, 10), afterSegment.end());

        model.movePoint(0, new Point(5, 6));
        observer.assertChanged("selection");
        beforeSegment = model.selection().get(0);
        afterSegment = model.selection().get(1);
        PolyLine finalSegment = model.selection().get(3);
        assertEquals(new Point(5, 6), beforeSegment.start());
        assertEquals(new Point(11, 12), beforeSegment.end());
        assertEquals(new Point(5, 6), finalSegment.end());
        assertEquals(new Point(0, 10), finalSegment.start());
    }

    // TODO 4C: Write at least one additional test case for `movePoint()` that moves the starting
    //  point of the selection.

    @DisplayName(
            "[Task 4C] GIVEN a selection, WHEN the starting point of the selection path is moved, "
                    + "THEN the first segment will have its start moved to the new location, "
                    + "AND the last segment will have its end moved to the same new location."
    )
    @Test
    void testMovePointStartingPoint() {
        // Set up a square selection
        SelectionModel model = makeSquareSelection(); // Assume this creates a square with control points
        PclTester observer = new PclTester();
        model.addPropertyChangeListener(observer);

        // Move the starting point
        model.movePoint(0, new Point(5, 6));

        // Verify that the selection has been updated
        observer.assertChanged("selection");

        // Verify the first segment is updated correctly
        PolyLine firstSegment = model.selection().get(0);
        assertEquals(new Point(5, 6), firstSegment.start(), "The first segment's start should be updated.");
        assertEquals(new Point(10, 0), firstSegment.end(), "The first segment's end should remain unchanged.");

        // Verify the last segment is updated to loop back to the moved starting point
        PolyLine lastSegment = model.selection().get(3);
        assertEquals(new Point(0, 10), lastSegment.start(), "The last segment's start should remain unchanged.");
        assertEquals(new Point(5, 6), lastSegment.end(), "The last segment's end should be updated to the moved starting point.");
    }


    /* Tests of closestPoint() */

    @DisplayName(
            "[Task 4D] GIVEN a selection (with no duplicate control points), WHEN querying for the "
                    + "closest point to a location equal to one of the control points, THEN the index of "
                    + "that control point will be returned.")
    @Test
    void testClosestPointCentered() {
        SelectionModel model = makeSquareSelection();
        assertEquals(1, model.closestPoint(new Point(10, 0), 4));
    }

    @DisplayName(
            "[Task 4D] GIVEN a selection, WHEN querying for the closest point to a location farther to "
                    + "all of the control points than the max distance, THEN -1 will be returned.")
    @Test
    void testClosestPointTooFar() {
        SelectionModel model = makeSquareSelection();
        assertEquals(-1, model.closestPoint(new Point(100, -100), 9));
    }

    // TODO 4E: Write at least one additional test case for `closestPoint()` where the queried
    //  location is within the maximum distance of an unambiguously closest point but not directly
    //  on top of it.

} // class PointToPointSelectionModelTest
