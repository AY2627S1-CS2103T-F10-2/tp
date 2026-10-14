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

                Button maximize = (Button) stage.getScene().lookup("#maximizeButton");
                maximize.fire();
                assertTrue(stage.isMaximized());
                assertEquals("Restore window", maximize.getAccessibleText());
                maximize.fire();
                assertFalse(stage.isMaximized());
                Button minimize = (Button) stage.getScene().lookup("#minimizeButton");
                minimize.fire();
                assertTrue(stage.isIconified());
                stage.setIconified(false);
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
            assertEquals("[08 Oct 2026, 14:30] Needs follow-up", firstNoteLabel.getText());
            assertEquals("[09 Oct 2026, 09:05] " + secondNote.getText(), secondNoteLabel.getText());
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
            assertEquals("[08 Oct 2026, 14:30] Needs follow-up", refreshedNoteLabel.getText());
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
