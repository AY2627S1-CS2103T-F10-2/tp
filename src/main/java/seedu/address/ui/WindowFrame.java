package seedu.address.ui;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/** Provides dragging and edge resizing for a window with a custom title bar. */
final class WindowFrame {
    private static final double RESIZE_MARGIN = 5;

    private final Stage stage;
    private boolean left;
    private boolean right;
    private boolean top;
    private boolean bottom;
    private boolean resizing;
    private double pressX;
    private double pressY;
    private double windowX;
    private double windowY;
    private double windowWidth;
    private double windowHeight;

    WindowFrame(Stage stage, Region dragArea) {
        this.stage = stage;
        configureDragArea(dragArea);
        configureResizeHandlers();
        configureMaximizeHandler();
    }

    /**
     * Configures dragging and double-click maximize behavior for the title bar.
     */
    private void configureDragArea(Region dragArea) {
        dragArea.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY && !stage.isMaximized()) {
                rememberBounds(event);
            }
        });
        dragArea.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && !stage.isMaximized()) {
                stage.setX(windowX + event.getScreenX() - pressX);
                stage.setY(windowY + event.getScreenY() - pressY);
            }
        });
        dragArea.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
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

    /**
     * Resets resize state when the window is maximized or restored.
     */
    private void configureMaximizeHandler() {
        Scene scene = stage.getScene();
        stage.maximizedProperty().addListener((observable, previous, maximized) -> {
            resizing = false;
            scene.setCursor(Cursor.DEFAULT);
        });
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
        boolean enabled = stage.isResizable() && !stage.isMaximized() && !stage.isFullScreen();
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
