package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
