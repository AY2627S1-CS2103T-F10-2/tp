package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import seedu.address.MainApp;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseclass.CourseClass;
import seedu.address.model.courseclass.CourseCode;
import seedu.address.model.courseclass.Name;
import seedu.address.model.student.Note;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.Telehandle;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.GuiTestProcess;
import seedu.address.testutil.TypicalPersons;

public class CourseClassUiTest {
    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void startToolkit() {
        if (!GuiTestProcess.isRequired()) {
            Platform.startup(() -> Platform.setImplicitExit(false));
        }
    }

    /** Entry point for running GUI assertions under a virtual display in CI. */
    public static void main(String[] args) throws Exception {
        CourseClassUiTest test = new CourseClassUiTest();
        test.temporaryFolder = Path.of(args[1]);
        startToolkit();
        try {
            CourseClassUiTest.class.getMethod(args[0]).invoke(test);
        } finally {
            Platform.exit();
        }
    }

    @Test
    public void applicationLifecycle_initializesAndStartsFromIsolatedFiles() throws Exception {
        if (!Boolean.getBoolean("courseclass.ui.child")) {
            GuiTestProcess.run(getClass(), "applicationLifecycle_initializesAndStartsFromIsolatedFiles",
                    temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            MainApp app = new MainApp();
            app.init();
            Stage stage = new Stage();
            try {
                app.start(stage);
                assertTrue(stage.isShowing());
                assertNotNull(stage.getScene().lookup("#courseClassListView"));
                app.stop();
                assertTrue(Files.exists(Path.of("preferences.json")));
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void mainWindow_commandsErrorsHelpAndExit_updateUiAndSaveSettings() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "mainWindow_commandsErrorsHelpAndExit_updateUiAndSaveSettings",
                    temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Model model = new ModelManager();
            model.setGuiSettings(new GuiSettings(800, 650, 20, 30));
            Logic logic = new LogicManager(model, new StorageManager(
                    new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json")),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, logic, temporaryFolder.resolve("classes.json"));
            assertSame(stage, window.getPrimaryStage());
            assertEquals(20, stage.getX());
            assertEquals(30, stage.getY());
            window.fillInnerParts();
            window.show();
            TextField input = (TextField) stage.getScene().lookup("#commandTextField");
            TextArea feedback = (TextArea) stage.getScene().lookup("#resultDisplay");
            enterCommand(input, "aclass n/F10-2 c/CS2103T");
            assertEquals(1, model.getFilteredCourseClassList().size());
            assertFalse(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            enterCommand(input, "unknown-command");
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertTrue(feedback.getText().contains("Unknown command"));
            enterCommand(input, "dclass 9");
            assertTrue(input.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
            assertTrue(feedback.getText().contains("invalid"));
            enterCommand(input, "help");
            assertTrue(Window.getWindows().stream().anyMatch(other -> other != stage && other.isShowing()));
            window.handleHelp();
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F2, false, false, false, false));
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, false, false, false, false));
            stage.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, false, false, false, false));
            enterCommand(input, "exit");
            assertFalse(stage.isShowing());
            assertEquals((int) stage.getX(), model.getGuiSettings().getWindowCoordinates().x);
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    private static void enterCommand(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    @Test
    public void mainWindow_customTitleBar_stylesAndWindowControls() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "mainWindow_customTitleBar_stylesAndWindowControls", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Model model = new ModelManager();
            model.setGuiSettings(new GuiSettings(800, 650, 20, 30));
            Logic logic = new LogicManager(model, new StorageManager(
                    new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json")),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
            Stage stage = new Stage();
            try {
                MainWindow window = new MainWindow(stage, logic, temporaryFolder.resolve("classes.json"));
                window.show();
                stage.getScene().getRoot().applyCss();
                stage.getScene().getRoot().layout();
                HBox titleBar = (HBox) stage.getScene().lookup("#titleBar");
                assertEquals(StageStyle.UNDECORATED, stage.getStyle());
                assertEquals(40, titleBar.getHeight(), 0.01);
                assertEquals(Color.web("#0B111E"), titleBar.getBackground().getFills().get(0).getFill());
                assertEquals(Color.web("#2D3B4E"), titleBar.getBorder().getStrokes().get(0).getBottomStroke());
                assertEquals("Course Classes", ((Label) stage.getScene().lookup("#windowTitle")).getText());
                stage.setTitle("Updated title");
                assertEquals("Updated title", ((Label) stage.getScene().lookup("#windowTitle")).getText());

                Button close = (Button) stage.getScene().lookup("#closeButton");
                assertEquals(5, titleBar.getWidth() - close.getBoundsInParent().getMaxX(), 1);
                PseudoClass hover = PseudoClass.getPseudoClass("hover");
                close.pseudoClassStateChanged(hover, true);
                close.applyCss();
                assertEquals(Color.web("#D94C4C"), close.getBackground().getFills().get(0).getFill());
                close.pseudoClassStateChanged(hover, false);
                close.applyCss();
                assertEquals(Color.web("#0B111E"), close.getBackground().getFills().get(0).getFill());

                HBox dragArea = (HBox) stage.getScene().lookup("#titleDragArea");
                double initialX = stage.getX();
                double initialY = stage.getY();
                dragArea.fireEvent(mouseEvent(MouseEvent.MOUSE_PRESSED, 20, 20, 100, 100));
                dragArea.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, 20, 20, 130, 120));
                assertEquals(initialX + 30, stage.getX(), 0.01);
                assertEquals(initialY + 20, stage.getY(), 0.01);

                double width = stage.getWidth();
                stage.getScene().getRoot().fireEvent(
                        mouseEvent(MouseEvent.MOUSE_PRESSED, width - 1, 200, 900, 200));
                stage.getScene().getRoot().fireEvent(
                        mouseEvent(MouseEvent.MOUSE_DRAGGED, width - 1, 200, 920, 200));
                assertEquals(width + 20, stage.getWidth(), 0.01);
                stage.getScene().getRoot().fireEvent(
                        mouseEvent(MouseEvent.MOUSE_RELEASED, width - 1, 200, 920, 200));

                // Verify the controller's requests without native window-manager feedback.
                // A visible stage's maximized/iconified state may be overridden by the platform.
                stage.hide();
                Button maximize = (Button) stage.getScene().lookup("#maximizeButton");
                maximize.fire();
                assertTrue(stage.isMaximized());
                assertEquals("Restore window", maximize.getAccessibleText());
                assertEquals("\u2750", maximize.getText());
                maximize.fire();
                assertFalse(stage.isMaximized());
                assertEquals("Maximize window", maximize.getAccessibleText());
                assertEquals("\u25a1", maximize.getText());
                Button minimize = (Button) stage.getScene().lookup("#minimizeButton");
                minimize.fire();
                assertTrue(stage.isIconified());
                stage.setIconified(false);
                window.show();
                assertTrue(stage.isShowing());
                close.fire();
                assertFalse(stage.isShowing());
                assertEquals((int) stage.getX(), model.getGuiSettings().getWindowCoordinates().x);
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    private static MouseEvent mouseEvent(EventType<MouseEvent> type, double x, double y,
                                        double screenX, double screenY) {
        return new MouseEvent(type, x, y, screenX, screenY, MouseButton.PRIMARY, 1,
                false, false, false, false, type != MouseEvent.MOUSE_RELEASED,
                false, false, false, false, false, null);
    }

    @Test
    public void windowFrame_edgesResizeAndRespectLimits() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "windowFrame_edgesResizeAndRespectLimits", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Stage stage = new Stage();
            HBox title = new HBox();
            VBox root = new VBox(title);
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            new WindowFrame(stage, title);
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.setMaxWidth(1000);
            stage.setMaxHeight(900);
            double[][] edges = {
                {1, 1}, {799, 599}, {799, 1}, {1, 599},
                {1, 200}, {799, 200}, {200, 1}, {200, 599}, {200, 200}
            };
            Cursor[] cursors = {
                Cursor.NW_RESIZE, Cursor.NW_RESIZE, Cursor.NE_RESIZE, Cursor.NE_RESIZE,
                Cursor.H_RESIZE, Cursor.H_RESIZE, Cursor.V_RESIZE, Cursor.V_RESIZE, Cursor.DEFAULT
            };
            try {
                for (int i = 0; i < edges.length; i++) {
                    double x = edges[i][0];
                    double y = edges[i][1];
                    root.fireEvent(mouseEvent(MouseEvent.MOUSE_MOVED, x, y, 100, 100));
                    assertEquals(cursors[i], scene.getCursor());
                    stage.setX(50);
                    stage.setY(60);
                    stage.setWidth(800);
                    stage.setHeight(600);
                    root.fireEvent(mouseEvent(MouseEvent.MOUSE_PRESSED, x, y, 100, 100));
                    root.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, x, y, 120, 130));
                    boolean leftEdge = x == 1;
                    boolean rightEdge = x == 799;
                    boolean topEdge = y == 1;
                    boolean bottomEdge = y == 599;
                    assertEquals(leftEdge ? 780 : rightEdge ? 820 : 800, stage.getWidth(), 0.01);
                    assertEquals(topEdge ? 570 : bottomEdge ? 630 : 600, stage.getHeight(), 0.01);
                    assertEquals(leftEdge ? 70 : 50, stage.getX(), 0.01);
                    assertEquals(topEdge ? 90 : 60, stage.getY(), 0.01);
                    root.fireEvent(mouseEvent(MouseEvent.MOUSE_RELEASED, x, y, 120, 130));
                    assertEquals(Cursor.DEFAULT, scene.getCursor());
                }
                // The top-left corner must keep the opposite corner fixed when clamped.
                stage.setX(50);
                stage.setY(60);
                stage.setWidth(800);
                stage.setHeight(600);
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_PRESSED, 1, 1, 100, 100));
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, 1, 1, 2100, 2100));
                assertEquals(400, stage.getWidth());
                assertEquals(300, stage.getHeight());
                assertEquals(450, stage.getX());
                assertEquals(360, stage.getY());
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, 1, 1, -1900, -1900));
                assertEquals(1000, stage.getWidth());
                assertEquals(900, stage.getHeight());
                assertEquals(-150, stage.getX());
                assertEquals(-240, stage.getY());
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_RELEASED, 1, 1, -1900, -1900));
                double releasedWidth = stage.getWidth();
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, 1, 1, 100, 100));
                assertEquals(releasedWidth, stage.getWidth());
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void windowFrame_titleBarAndDisabledResizeStates() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "windowFrame_titleBarAndDisabledResizeStates", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Stage stage = new Stage();
            HBox title = new HBox();
            VBox root = new VBox(title);
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            new WindowFrame(stage, title);
            stage.setX(50);
            stage.setY(60);
            stage.setWidth(800);
            stage.setHeight(600);
            try {
                MouseEvent secondary = new MouseEvent(MouseEvent.MOUSE_PRESSED, 200, 200, 100, 100,
                        MouseButton.SECONDARY, 1, false, false, false, false,
                        false, false, true, false, false, false, null);
                title.fireEvent(secondary);
                title.fireEvent(new MouseEvent(MouseEvent.MOUSE_DRAGGED, 200, 200, 130, 120,
                        MouseButton.SECONDARY, 1, false, false, false, false,
                        false, false, true, false, false, false, null));
                assertEquals(50, stage.getX());
                assertEquals(60, stage.getY());
                title.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 200, 200, 100, 100,
                        MouseButton.SECONDARY, 2, false, false, false, false,
                        false, false, false, false, false, false, null));
                assertFalse(stage.isMaximized());
                title.fireEvent(mouseEvent(MouseEvent.MOUSE_CLICKED, 200, 200, 100, 100));
                assertFalse(stage.isMaximized());
                MouseEvent doubleClick = new MouseEvent(MouseEvent.MOUSE_CLICKED, 200, 200, 100, 100,
                        MouseButton.PRIMARY, 2, false, false, false, false,
                        false, false, false, false, false, false, null);
                title.fireEvent(doubleClick);
                assertTrue(stage.isMaximized());
                title.fireEvent(mouseEvent(MouseEvent.MOUSE_PRESSED, 200, 200, 100, 100));
                title.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, 200, 200, 130, 120));
                assertEquals(50, stage.getX());
                assertEquals(60, stage.getY());
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_MOVED, 1, 1, 100, 100));
                assertEquals(Cursor.DEFAULT, scene.getCursor());
                title.fireEvent(doubleClick.copyFor(title, title));
                assertFalse(stage.isMaximized());

                stage.setResizable(false);
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_MOVED, 1, 1, 100, 100));
                assertEquals(Cursor.DEFAULT, scene.getCursor());
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_PRESSED, 1, 1, 100, 100));
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, 1, 1, 130, 120));
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_RELEASED, 1, 1, 130, 120));
                assertEquals(800, stage.getWidth());
                assertEquals(600, stage.getHeight());
                stage.setResizable(true);
                stage.setFullScreen(true);
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_MOVED, 1, 1, 100, 100));
                assertEquals(Cursor.DEFAULT, scene.getCursor());
                stage.setFullScreen(false);
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_MOVED, 1, 1, 100, 100));
                assertEquals(Cursor.NW_RESIZE, scene.getCursor());
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_PRESSED, 1, 1, 100, 100));
                stage.setMaximized(true);
                assertEquals(Cursor.DEFAULT, scene.getCursor());
                root.fireEvent(mouseEvent(MouseEvent.MOUSE_DRAGGED, 1, 1, 130, 120));
                assertEquals(800, stage.getWidth());
                assertEquals(600, stage.getHeight());
            } finally {
                stage.close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void theme_actionButtonsAndHelpWindow_renderInteractionStates() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "theme_actionButtonsAndHelpWindow_renderInteractionStates", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Button primary = new Button("Save");
            primary.getStyleClass().add("primary-action");
            Button destructive = new Button("Delete");
            destructive.getStyleClass().add("destructive-action");
            Button normal = new Button("Other");
            VBox root = new VBox(primary, destructive, normal);
            Scene scene = new Scene(root, 400, 300);
            scene.getStylesheets().add(MainApp.class.getResource("/view/DarkTheme.css").toExternalForm());
            root.applyCss();
            assertEquals(Color.web("#0B111E"), root.getBackground().getFills().get(0).getFill());
            assertEquals(Color.web("#FCBF3A"), primary.getBackground().getFills().get(0).getFill());
            assertEquals(Color.web("#0B111E"), primary.getTextFill());
            assertEquals(Color.TRANSPARENT, destructive.getBackground().getFills().get(0).getFill());
            assertEquals(Color.web("#D94C4C"), destructive.getTextFill());
            assertEquals(Color.web("#D94C4C"), destructive.getBorder().getStrokes().get(0).getTopStroke());
            for (String state : List.of("hover", "pressed")) {
                PseudoClass pseudoClass = PseudoClass.getPseudoClass(state);
                destructive.pseudoClassStateChanged(pseudoClass, true);
                destructive.applyCss();
                assertEquals(Color.web("#D94C4C"), destructive.getBackground().getFills().get(0).getFill());
                assertEquals(Color.WHITE, destructive.getTextFill());
                destructive.pseudoClassStateChanged(pseudoClass, false);
            }
            PseudoClass hover = PseudoClass.getPseudoClass("hover");
            normal.pseudoClassStateChanged(hover, true);
            normal.applyCss();
            assertEquals(Color.web("#2A7B82"), normal.getBackground().getFills().get(0).getFill());
            normal.pseudoClassStateChanged(hover, false);
            normal.setDefaultButton(true);
            normal.applyCss();
            assertEquals(Color.web("#FCBF3A"), normal.getBackground().getFills().get(0).getFill());
            normal.setDefaultButton(false);
            normal.setDisable(true);
            normal.applyCss();
            assertEquals(0.4, normal.getOpacity());
            assertNull(primary.getEffect());
            assertNull(destructive.getEffect());
            HelpWindow help = new HelpWindow();
            try {
                help.getRoot().getScene().getRoot().applyCss();
                assertNull(help.getRoot().getScene().lookup("#copyButton").getEffect());
                assertNull(help.getRoot().getScene().lookup("#helpMessage").getEffect());
            } finally {
                help.getRoot().close();
            }
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void mainWindow_displaysCourseClassesAndTracksCommands() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "mainWindow_displaysCourseClassesAndTracksCommands", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Model model = new ModelManager();
            CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"),
                    Set.of(new Tag("tutorial"), new Tag("lab")));
            model.addCourseClass(courseClass);
            Logic logic = new LogicManager(model, new StorageManager(
                    new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json")),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, logic, temporaryFolder.resolve("classes.json"));
            window.fillInnerParts();
            window.show();
            stage.getScene().getRoot().applyCss();
            stage.getScene().getRoot().layout();
            assertNotNull(window.getCourseClassListPanel());
            ListView<?> list = (ListView<?>) window.getCourseClassListPanel().getRoot().lookup("#courseClassListView");
            assertSame(logic.getFilteredCourseClassList(), list.getItems());

            Region bottom = (Region) stage.getScene().lookup("#bottomMenu");
            Region inputPane = (Region) stage.getScene().lookup("#commandBoxPlaceholder");
            Region statusPane = (Region) stage.getScene().lookup("#statusbarPlaceholder");
            TextField input = (TextField) stage.getScene().lookup("#commandTextField");
            assertSame(bottom, inputPane.getParent());
            assertSame(bottom, statusPane.getParent());
            assertTrue(bottom.localToScene(0, 0).getY() >= list.localToScene(list.getBoundsInLocal()).getMaxY());
            assertEquals(Color.web("#0B111E"), inputPane.getBackground().getFills().get(0).getFill());
            assertEquals(Color.web("#151E2E"), input.getBackground().getFills().get(0).getFill());
            assertEquals(50, input.getPrefHeight());
            assertEquals(50, input.getHeight(), 1);
            Label saveLocation = (Label) statusPane.lookup("#saveLocationStatus");
            assertEquals(Pos.CENTER, saveLocation.getAlignment());
            assertEquals(Color.web("#9CA3AF"), saveLocation.getTextFill());
            assertNull(input.getEffect());
            enterCommand(input, "unknown-command");
            input.applyCss();
            Text commandText = (Text) input.lookupAll(".text").stream()
                    .filter(node -> node instanceof Text text && text.getText().equals("unknown-command"))
                    .findFirst().orElseThrow();
            assertEquals(Color.web("#D94C4C"), commandText.getFill());
            enterCommand(input, "lclass");
            input.applyCss();
            assertEquals(Color.WHITE, commandText.getFill());

            CourseClassCard card = new CourseClassCard(courseClass, 1);
            assertEquals("F10-2", ((Label) card.getRoot().lookup("#name")).getText());
            assertEquals("CS2103T", ((Label) card.getRoot().lookup("#courseCode")).getText());
            assertEquals("Students: 0", ((Label) card.getRoot().lookup("#studentCount")).getText());
            assertEquals("lab", ((Label) ((FlowPane) card.getRoot().lookup("#tags"))
                    .getChildren().get(0)).getText());
            logic.execute("aclass n/F10-3 c/CS2103T");
            assertEquals(2, list.getItems().size());
            logic.execute("cclass");
            assertEquals(0, list.getItems().size());
            stage.close();
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void card_displaysStudentCardsWithTheirOwnIndices() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "card_displaysStudentCardsWithTheirOwnIndices", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of());
            courseClass.getStudents().addPerson(TypicalPersons.ALICE);
            courseClass.getStudents().addPerson(TypicalPersons.BOB);
            CourseClassCard card = new CourseClassCard(courseClass, 3);
            VBox students = (VBox) card.getRoot().lookup("#studentsBox");
            assertEquals(2, students.getChildren().size());
            assertEquals("Students: 2", ((Label) card.getRoot().lookup("#studentCount")).getText());
            assertEquals("3. ", ((Label) card.getRoot().lookup("#id")).getText());
            assertEquals(TypicalPersons.ALICE.getName().fullName, ((Label) students.getChildren().get(0)
                    .lookup("#name")).getText());
            assertEquals("1. ", ((Label) students.getChildren().get(0).lookup("#id")).getText());
            assertEquals(TypicalPersons.BOB.getName().fullName, ((Label) students.getChildren().get(1)
                    .lookup("#name")).getText());
            assertEquals("2. ", ((Label) students.getChildren().get(1).lookup("#id")).getText());
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void studentCard_rowsAlignColumnsAndGrowForNotes() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "studentCard_rowsAlignColumnsAndGrowForNotes", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Student student = new Student(new StudentName("Aaron Tan"), new Telehandle("aarontan"));
            Note note = new Note("A longer note below the student name that makes the row taller.",
                    LocalDateTime.of(2026, 10, 8, 14, 30));
            Student other = new Student(new StudentName("Beth"), new Telehandle("bethtan"), List.of(note));
            Region first = new StudentCard(student, 1).getRoot();
            Region second = new StudentCard(other, 2).getRoot();
            VBox rows = new VBox(first, second);
            Scene scene = new Scene(rows, 700, 300);
            scene.getStylesheets().add(MainApp.class.getResource("/view/DarkTheme.css").toExternalForm());
            rows.applyCss();
            rows.layout();

            assertEquals(first.getBoundsInParent().getMaxY(), second.getLayoutY(), 0.01);
            assertEquals(first.lookup("#name").localToScene(0, 0).getX(),
                    second.lookup("#name").localToScene(0, 0).getX(), 0.01);
            assertEquals(first.lookup("#telehandle").localToScene(0, 0).getX(),
                    second.lookup("#telehandle").localToScene(0, 0).getX(), 0.01);
            assertTrue(second.getHeight() > first.getHeight());
            VBox notes = (VBox) second.lookup("#notesBox");
            assertEquals(second.lookup("#id").localToScene(0, 0).getX(),
                    notes.localToScene(0, 0).getX(), 0.01);
            assertTrue(notes.localToScene(0, 0).getY() >= second.lookup("#name")
                    .localToScene(second.lookup("#name").getBoundsInLocal()).getMaxY());
            assertEquals(Color.web("#0F1626"), first.getBackground().getFills().get(0).getFill());
            assertEquals(Color.web("#161F30"), second.getBackground().getFills().get(0).getFill());
            assertEquals(Color.web("#1E293B"), first.getBorder().getStrokes().get(0).getBottomStroke());
            assertEquals(1, first.getBorder().getStrokes().get(0).getWidths().getBottom());
            assertEquals(0, first.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius());
            assertEquals(0, first.getBorder().getStrokes().get(0).getRadii().getTopLeftHorizontalRadius());
            Label noteLabel = (Label) notes.getChildren().get(0);
            assertEquals(notes.getWidth(), noteLabel.getWidth(), 0.01);
            assertEquals(Pos.CENTER_LEFT, noteLabel.getAlignment());
            assertEquals(Color.web("#202C40"), noteLabel.getBackground().getFills().get(0).getFill());
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void studentCard_displaysNotesInStoredOrderWithReadableTimestamps() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "studentCard_displaysNotesInStoredOrderWithReadableTimestamps",
                    temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Note firstNote = new Note("Needs follow-up", LocalDateTime.of(2026, 10, 8, 14, 30));
            Note secondNote = new Note("A long observation that should wrap instead of widening the student card.",
                    LocalDateTime.of(2026, 10, 9, 9, 5));
            Student student = new Student(new StudentName("Aaron Tan"), new Telehandle("aarontan"),
                    List.of(firstNote, secondNote));

            StudentCard card = new StudentCard(student, 1);
            VBox notesBox = (VBox) card.getRoot().lookup("#notesBox");
            assertTrue(notesBox.isManaged());
            assertTrue(notesBox.isVisible());
            assertEquals(2, notesBox.getChildren().size());

            Label firstNoteLabel = (Label) notesBox.getChildren().get(0);
            Label secondNoteLabel = (Label) notesBox.getChildren().get(1);
            assertEquals("1. [08 Oct 2026, 14:30] Needs follow-up", firstNoteLabel.getText());
            assertEquals("2. [09 Oct 2026, 09:05] " + secondNote.getText(), secondNoteLabel.getText());
            assertTrue(firstNoteLabel.isWrapText());
            assertTrue(secondNoteLabel.isWrapText());
            assertEquals(0, firstNoteLabel.getMinWidth());
            assertEquals(0, firstNoteLabel.getPrefWidth());
            assertEquals(Double.MAX_VALUE, firstNoteLabel.getMaxWidth());
            assertEquals(Double.MAX_VALUE, secondNoteLabel.getMaxWidth());
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void courseClassCard_refreshesNotesWhenStudentChanges() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "courseClassCard_refreshesNotesWhenStudentChanges", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            CourseClass courseClass = new CourseClass(new Name("T05"), new CourseCode("CS2103T"), Set.of());
            Student student = new Student(new StudentName("Aaron Tan"), new Telehandle("aarontan"));
            courseClass.addStudent(student);
            CourseClassCard card = new CourseClassCard(courseClass, 1);
            VBox studentsBox = (VBox) card.getRoot().lookup("#studentsBox");
            VBox initialNotesBox = (VBox) studentsBox.getChildren().get(0).lookup("#notesBox");
            assertFalse(initialNotesBox.isManaged());
            Scene scene = new Scene(card.getRoot(), 700, 300);
            scene.getStylesheets().add(MainApp.class.getResource("/view/DarkTheme.css").toExternalForm());
            card.getRoot().applyCss();
            card.getRoot().layout();
            Rectangle clip = (Rectangle) studentsBox.getClip();
            assertEquals(8, clip.getArcWidth());
            assertEquals(8, clip.getArcHeight());
            assertEquals(studentsBox.getWidth(), clip.getWidth());
            assertEquals(studentsBox.getHeight(), clip.getHeight());
            assertEquals(4, studentsBox.getBackground().getFills().get(0).getRadii().getTopLeftHorizontalRadius());
            Region outline = (Region) studentsBox.getParent();
            assertEquals(5, outline.getBorder().getStrokes().get(0).getRadii().getTopLeftHorizontalRadius());
            assertEquals(Color.web("#1E293B"), outline.getBorder().getStrokes().get(0).getBottomStroke());
            assertEquals(1, outline.getBorder().getStrokes().get(0).getWidths().getBottom());
            assertEquals(1, studentsBox.getLayoutX(), 0.01);
            Region studentRow = (Region) studentsBox.getChildren().get(0);
            assertEquals(0, studentRow.getBorder().getStrokes().get(0).getWidths().getBottom());
            double initialHeight = studentsBox.getHeight();

            Note note = new Note("Needs follow-up", LocalDateTime.of(2026, 10, 8, 14, 30));
            courseClass.removeStudent(student.getTelehandle());
            courseClass.addStudent(new Student(student.getName(), student.getTelehandle(), List.of(note)));

            VBox refreshedNotesBox = (VBox) studentsBox.getChildren().get(0).lookup("#notesBox");
            assertTrue(refreshedNotesBox.isManaged());
            assertEquals(1, refreshedNotesBox.getChildren().size());
            Label refreshedNoteLabel = (Label) refreshedNotesBox.getChildren().get(0);
            assertEquals("1. [08 Oct 2026, 14:30] Needs follow-up", refreshedNoteLabel.getText());
            card.getRoot().applyCss();
            card.getRoot().layout();
            assertTrue(studentsBox.getHeight() > initialHeight);
            assertEquals(studentsBox.getHeight(), clip.getHeight());
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void studentCard_withoutNotes_hidesNotesContainer() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "studentCard_withoutNotes_hidesNotesContainer", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Student student = new Student(new StudentName("Aaron Tan"), new Telehandle("aarontan"));

            StudentCard card = new StudentCard(student, 1);
            VBox notesBox = (VBox) card.getRoot().lookup("#notesBox");
            assertFalse(notesBox.isManaged());
            assertFalse(notesBox.isVisible());
            assertTrue(notesBox.getChildren().isEmpty());
            assertEquals("Aaron Tan", ((Label) card.getRoot().lookup("#name")).getText());
            assertEquals("@aarontan", ((Label) card.getRoot().lookup("#telehandle")).getText());
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void card_updatesStudentCountWhenStudentIsAdded() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "card_updatesStudentCountWhenStudentIsAdded", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            CourseClass courseClass = new CourseClass(new Name("T05"), new CourseCode("CS2103T"), Set.of());
            CourseClassCard card = new CourseClassCard(courseClass, 1);
            Label count = (Label) card.getRoot().lookup("#studentCount");

            assertEquals("Students: 0", count.getText());

            courseClass.addStudent(new Student(new StudentName("Aaron Tan"), new Telehandle("aarontan")));

            assertEquals("Students: 1", count.getText());
            VBox students = (VBox) card.getRoot().lookup("#studentsBox");
            assertEquals(1, students.getChildren().size());
            assertEquals("Aaron Tan", ((Label) students.getChildren().get(0).lookup("#name")).getText());
            assertEquals("@aarontan", ((Label) students.getChildren().get(0)
                    .lookup("#telehandle")).getText());

            courseClass.getStudents().addPerson(TypicalPersons.ALICE);
            assertEquals("Students: 2", count.getText());
            assertEquals(2, students.getChildren().size());
            assertEquals("1. ", ((Label) students.getChildren().get(0).lookup("#id")).getText());
            assertEquals("2. ", ((Label) students.getChildren().get(1).lookup("#id")).getText());
            assertFalse(students.getChildren().get(0).getStyleClass().contains("student-row-last"));
            assertTrue(students.getChildren().get(1).getStyleClass().contains("student-row-last"));
            PersonListPanel legacyPanel = new PersonListPanel(courseClass.getStudents().getPersonList());
            ListView<?> legacyList = (ListView<?>) legacyPanel.getRoot().lookup("#personListView");
            assertSame(courseClass.getStudents().getPersonList(), legacyList.getItems());
            courseClass.removeStudent(new Telehandle("aarontan"));
            assertEquals("Students: 1", count.getText());
            assertEquals(1, students.getChildren().size());
            assertTrue(students.getChildren().get(0).getStyleClass().contains("student-row-last"));
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }

    @Test
    public void listCell_recycledForEmptyItem_clearsPreviousCard() throws Exception {
        if (GuiTestProcess.isRequired()) {
            GuiTestProcess.run(getClass(), "listCell_recycledForEmptyItem_clearsPreviousCard", temporaryFolder);
            return;
        }
        FutureTask<Void> task = new FutureTask<>(() -> {
            Model model = new ModelManager();
            CourseClassListPanel panel = new CourseClassListPanel(model.getFilteredCourseClassList());
            CourseClassListPanel.CourseClassListViewCell cell = panel.new CourseClassListViewCell();
            CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"), Set.of());
            cell.updateItem(courseClass, false);
            assertNotNull(cell.getGraphic());
            cell.updateItem(null, true);
            assertNull(cell.getGraphic());
            assertNull(cell.getText());
            cell.updateItem(null, false);
            assertNull(cell.getGraphic());
            return null;
        });
        Platform.runLater(task);
        task.get(15, TimeUnit.SECONDS);
    }
}
