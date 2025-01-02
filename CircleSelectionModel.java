package selector;

import java.awt.Point;
import java.util.LinkedList;

/**
 * A selection model that selects a circular region of an image.
 * The first control point is the center, and the second control point lies on the circumference.
 */
public class CircleSelectionModel extends SelectionModel {

    /**
     * Create a CircleSelectionModel with the given notification policy.
     */
    public CircleSelectionModel(boolean notifyOnEdt) {
        super(notifyOnEdt);
    }

    @Override
    public SelectionState state() {
        if (controlPoints.size() == 0) {
            return CircleState.NO_SELECTION;
        } else if (controlPoints.size() == 1) {
            return CircleState.SELECTING;
        } else {
            return CircleState.SELECTED;
        }
    }

    @Override
    public PolyLine liveWire(Point p) {
        if (controlPoints.size() == 1) {
            Point center = controlPoints.getFirst();
            return approximateCircle(center, p);
        }
        return null;
    }

    @Override
    protected void appendToSelection(Point p) {
        if (controlPoints.size() == 1) {
            // Create a circle segment when the second point is added
            Point center = controlPoints.getFirst();
            PolyLine circle = approximateCircle(center, p);
            segments.add(circle);
            controlPoints.add(new Point(p)); // Add the second point
        } else {
            throw new IllegalStateException("Cannot add more than two points in CircleSelectionModel");
        }
    }

    @Override
    protected void undoPoint() {
        if (controlPoints.size() == 1) {
            reset(); // Reset if only one point exists
        } else if (controlPoints.size() == 2) {
            controlPoints.removeLast();
            segments.clear();
            propSupport.firePropertyChange("selection", null, selection());
        } else {
            throw new IllegalStateException("Nothing to undo in CircleSelectionModel");
        }
    }

    @Override
    public void movePoint(int index, Point newPos) {
        if (!state().canEdit()) {
            throw new IllegalStateException("May not move point in state " + state());
        }
        if (index < 0 || index >= controlPoints.size()) {
            throw new IllegalArgumentException("Invalid point index " + index);
        }

        controlPoints.set(index, new Point(newPos)); // Update the control point
        segments.clear(); // Recalculate the circle
        if (controlPoints.size() == 2) {
            segments.add(approximateCircle(controlPoints.get(0), controlPoints.get(1)));
        }
        propSupport.firePropertyChange("selection", null, selection());
    }

    private PolyLine approximateCircle(Point center, Point edge) {
        LinkedList<Point> points = new LinkedList<>();
        int radius = (int) center.distance(edge);
        int numPoints = Math.max(12, (int) (2 * Math.PI * radius / 10)); // Approximate with at least 12 points
        double angleStep = 2 * Math.PI / numPoints;

        for (int i = 0; i < numPoints; i++) {
            double angle = i * angleStep;
            int x = center.x + (int) (radius * Math.cos(angle));
            int y = center.y + (int) (radius * Math.sin(angle));
            points.add(new Point(x, y));
        }

        int[] xs = points.stream().mapToInt(p -> p.x).toArray();
        int[] ys = points.stream().mapToInt(p -> p.y).toArray();
        return new PolyLine(xs, ys);
    }

    @Override
    public void finishSelection() {
        throw new IllegalStateException("Cannot explicitly finish a CircleSelectionModel");
    }

    @Override
    public int closestPoint(Point p, int maxDistanceSq) {
        // Allow dragging either the center or the circumference point
        for (int i = 0; i < controlPoints.size(); i++) {
            if (controlPoints.get(i).distanceSq(p) <= maxDistanceSq) {
                return i;
            }
        }
        return -1; // No close point
    }

    private enum CircleState implements SelectionState {
        NO_SELECTION, SELECTING, SELECTED;

        @Override
        public boolean isEmpty() {
            return this == NO_SELECTION;
        }

        @Override
        public boolean isFinished() {
            return this == SELECTED;
        }

        @Override
        public boolean canUndo() {
            return this != NO_SELECTION;
        }

        @Override
        public boolean canAddPoint() {
            return this != SELECTED;
        }

        @Override
        public boolean canFinish() {
            return false;
        }

        @Override
        public boolean canEdit() {
            return this == SELECTED;
        }

        @Override
        public boolean isProcessing() {
            return false;
        }
    }
}
