package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.courseClass.CourseClass;
import seedu.address.model.courseClass.CourseCode;
import seedu.address.model.courseClass.Name;
import seedu.address.model.tag.Tag;
import seedu.address.storage.JsonCourseClassBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

public class CourseClassUiTest {
    @TempDir
    public Path temporaryFolder;

    @BeforeAll
    public static void startToolkit() {
        Platform.startup(() -> Platform.setImplicitExit(false));
    }

    @Test
    public void mainWindow_displaysCourseClassesAndTracksCommands() throws Exception {
        FutureTask<Void> task = new FutureTask<>(() -> {
            Model model = new ModelManager();
            CourseClass courseClass = new CourseClass(new Name("F10-2"), new CourseCode("CS2103T"),
                    Set.of(new Tag("tutorial")));
            model.addCourseClass(courseClass);
            Logic logic = new LogicManager(model, new StorageManager(
                    new JsonCourseClassBookStorage(temporaryFolder.resolve("classes.json")),
                    new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
            Stage stage = new Stage();
            MainWindow window = new MainWindow(stage, logic, temporaryFolder.resolve("classes.json"));
            window.fillInnerParts();
            assertNotNull(window.getCourseClassListPanel());
            ListView<?> list = (ListView<?>) window.getCourseClassListPanel().getRoot().lookup("#courseClassListView");
            assertSame(logic.getFilteredCourseClassList(), list.getItems());

            CourseClassCard card = new CourseClassCard(courseClass, 1);
            assertEquals("F10-2", ((Label) card.getRoot().lookup("#name")).getText());
            assertEquals("CS2103T", ((Label) card.getRoot().lookup("#courseCode")).getText());
            assertEquals("Students: 0", ((Label) card.getRoot().lookup("#studentCount")).getText());
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
}
