package seedu.address.ui;

import java.util.Objects;
import java.util.function.Consumer;

import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

/** Provides dragging and edge resizing for a window with a custom title bar. */
final class WindowFrame {
    private static final double RESIZE_MARGIN = 5;

    private final Stage stage;
    private final Consumer<Boolean> maximizeStateListener;
    private boolean left;
    private boolean right;
    private boolean top;
    private boolean bottom;
    private boolean resizing;
    private boolean maximized;
    private boolean hasLastRestoredBounds;
    private double pressX;
    private double pressY;
    private double windowX;
    private double windowY;
    private double windowWidth;
    private double windowHeight;
    private double restoreX;
    private double restoreY;
    private double restoreWidth;
    private double restoreHeight;
    private double lastRestoredX;
    private double lastRestoredY;
    private double lastRestoredWidth;
    private double lastRestoredHeight;

    WindowFrame(Stage stage, Region dragArea) {
        this(stage, dragArea, maximized -> { });
    }

    WindowFrame(Stage stage, Region dragArea, Consumer<Boolean> maximizeStateListener) {
        this.stage = stage;
        this.maximizeStateListener = Objects.requireNonNull(maximizeStateListener);
        configureDragArea(dragArea);
        configureResizeHandlers();
    }

    /**
     * Configures dragging and double-click maximize behavior for the title bar.
     */
    private void configureDragArea(Region dragArea) {
        dragArea.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY && !maximized) {
                rememberBounds(event);
            }
        });
        dragArea.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && !maximized) {
                stage.setX(windowX + event.getScreenX() - pressX);
                stage.setY(windowY + event.getScreenY() - pressY);
            }
        });
        dragArea.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                toggleMaximize();
            }
        });
    }

    /**
     * Configures scene-level handlers for detecting and performing edge resizing.
     */
    private void configureResizeHandlers() {
        Scene scene = stage.getScene();
        scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> updateResizeEdges(event));
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            updateResizeEdges(event);
            resizing = event.getButton() == MouseButton.PRIMARY && (left || right || top || bottom);
            if (resizing) {
                rememberBounds(event);
                event.consume();
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> {
            if (resizing) {
                resize(event);
                event.consume();
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (resizing) {
                resizing = false;
                scene.setCursor(Cursor.DEFAULT);
                event.consume();
            }
        });
    }

    /** Toggles between the saved window size and the current screen's usable bounds. */
    void toggleMaximize() {
        if (maximized) {
            restoreWindow();
        } else {
            maximizeWindow();
        }
    }

    boolean isMaximized() {
        return maximized;
    }

    /** Returns the bounds that should be saved when the application closes. */
    Rectangle2D getWindowBounds() {
        if (maximized) {
            return new Rectangle2D(restoreX, restoreY, restoreWidth, restoreHeight);
        }
        return new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
    }

    private void maximizeWindow() {
        if (!hasLastRestoredBounds || !matchesLastRestoredBounds()) {
            rememberRestoreBounds();
        }
        Rectangle2D screenBounds = Screen.getScreensForRectangle(
                stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight())
                .stream()
                .findFirst()
                .orElse(Screen.getPrimary())
                .getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        maximized = true;
        resetResizeState();
        maximizeStateListener.accept(true);
    }

    private void restoreWindow() {
        stage.setX(restoreX);
        stage.setY(restoreY);
        stage.setWidth(restoreWidth);
        stage.setHeight(restoreHeight);
        lastRestoredX = restoreX;
        lastRestoredY = restoreY;
        lastRestoredWidth = restoreWidth;
        lastRestoredHeight = restoreHeight;
        hasLastRestoredBounds = true;
        maximized = false;
        resetResizeState();
        maximizeStateListener.accept(false);
    }

    private void rememberRestoreBounds() {
        restoreX = stage.getX();
        restoreY = stage.getY();
        restoreWidth = stage.getWidth();
        restoreHeight = stage.getHeight();
        hasLastRestoredBounds = false;
    }

    private boolean matchesLastRestoredBounds() {
        return Math.abs(stage.getX() - lastRestoredX) < 0.01
                && Math.abs(stage.getY() - lastRestoredY) < 0.01
                && Math.abs(stage.getWidth() - lastRestoredWidth) < 0.01
                && Math.abs(stage.getHeight() - lastRestoredHeight) < 0.01;
    }

    private void resetResizeState() {
        resizing = false;
        stage.getScene().setCursor(Cursor.DEFAULT);
    }

    private void rememberBounds(MouseEvent event) {
        pressX = event.getScreenX();
        pressY = event.getScreenY();
        windowX = stage.getX();
        windowY = stage.getY();
        windowWidth = stage.getWidth();
        windowHeight = stage.getHeight();
    }

    private void updateResizeEdges(MouseEvent event) {
        Scene scene = stage.getScene();
        boolean enabled = stage.isResizable() && !maximized && !stage.isFullScreen();
        left = enabled && event.getSceneX() < RESIZE_MARGIN;
        right = enabled && event.getSceneX() > scene.getWidth() - RESIZE_MARGIN;
        top = enabled && event.getSceneY() < RESIZE_MARGIN;
        bottom = enabled && event.getSceneY() > scene.getHeight() - RESIZE_MARGIN;
        Cursor cursor = Cursor.DEFAULT;
        if (top && left || bottom && right) {
            cursor = Cursor.NW_RESIZE;
        } else if (top && right || bottom && left) {
            cursor = Cursor.NE_RESIZE;
        } else if (left || right) {
            cursor = Cursor.H_RESIZE;
        } else if (top || bottom) {
            cursor = Cursor.V_RESIZE;
        }
        scene.setCursor(cursor);
    }

    private void resize(MouseEvent event) {
        double deltaX = event.getScreenX() - pressX;
        double deltaY = event.getScreenY() - pressY;
        if (left || right) {
            double width = Math.min(stage.getMaxWidth(),
                    Math.max(stage.getMinWidth(), windowWidth + (left ? -deltaX : deltaX)));
            stage.setWidth(width);
            if (left) {
                stage.setX(windowX + windowWidth - width);
            }
        }
        if (top || bottom) {
            double height = Math.min(stage.getMaxHeight(),
                    Math.max(stage.getMinHeight(), windowHeight + (top ? -deltaY : deltaY)));
            stage.setHeight(height);
            if (top) {
                stage.setY(windowY + windowHeight - height);
            }
        }
    }
}
